// apps/web/src/lib/db.ts
import { Dexie, Table } from 'dexie';

export interface Box {
  id: string;
  code: string;
  name: string;
  location?: string | null;
  tags?: string[] | null;
  thumbs?: string[];           // dataURL or blob url (thumb only)
  createdAt: string;           // ISO string
  updatedAt: string;           // ISO string
}

export interface Item {
  id: string;
  boxId: string;
  name: string;
  tags?: string[] | null;
  note?: string | null;
  thumbs?: string[];
  createdAt: string;
  updatedAt: string;
}

export interface BoxLocation {
  id: string;
  boxId: string;               // 1:1 を想定（ユニークに）
  thumbs?: string[];           // 置き場所サムネのみ（元画像は保持しない）
  note?: string | null;
  createdAt: string;
  updatedAt: string;
}

// 任意: 画面フロー用の下書きテーブル（必要なければ削除可）
export interface Draft {
  id: string;
  type: 'box' | 'item' | 'location';
  boxId?: string | null;
  itemId?: string | null;
  payload?: any;
  updatedAt: string;
}

const DB_NAME = 'hakomokuroku';
// 既存より必ず「大きい番号」にしてください。以前のバージョンが不明でもこのままでOKです。
const DB_VERSION = 5;

export class HKDB extends Dexie {
  boxes!: Table<Box, string>;
  items!: Table<Item, string>;
  boxLocations!: Table<BoxLocation, string>;
  drafts!: Table<Draft, string>;

  constructor() {
    super(DB_NAME);

    // v5: インデックス整備 & thumbs 統一
    this.version(DB_VERSION)
      .stores({
        // id 主キー, code ユニーク検索, updatedAt 差分PULL, tags は multiEntry
        boxes: 'id, &code, updatedAt, *tags',
        // boxId 参照, updatedAt 差分PULL, tags multiEntry
        items: 'id, boxId, updatedAt, *tags',
        // 1箱1レコードを想定 → boxId をユニーク化
        boxLocations: 'id, &boxId, updatedAt',
        // 任意の下書き
        drafts: 'id, type, boxId, itemId, updatedAt',
      })
      .upgrade(async (tx) => {
        // 旧スキーマからの移行（あれば）
        // photos -> thumbs への移行、tags の型を string[] に正規化など。
        const normalizeTags = (val: any): string[] | null => {
          if (val == null) return null;
          if (Array.isArray(val)) return val.map(String);
          if (typeof val === 'string' && val.trim() !== '') return [val];
          return null;
        };

        const migrateTable = async (name: string, opts: { hasPhotos?: boolean; hasTags?: boolean }) => {
          const tbl = tx.table(name) as Table<any, string>;
          try {
            const rows = await tbl.toArray();
            for (const r of rows) {
              let changed = false;

              // photos → thumbs へ移行
              if (opts.hasPhotos && r.photos && !r.thumbs) {
                r.thumbs = Array.isArray(r.photos) ? r.photos : [String(r.photos)];
                delete r.photos;
                changed = true;
              }

              // tags 正規化
              if (opts.hasTags) {
                const normalized = normalizeTags(r.tags);
                // Dexie の multiEntry インデックスを壊さないよう空配列にもできるが、
                // サーバー側(JSON)と整合を取り null で統一
                if (normalized !== r.tags) {
                  r.tags = normalized;
                  changed = true;
                }
              }

              if (changed) {
                await tbl.put(r);
              }
            }
          } catch {
            // テーブルがまだ無い等は無視（初回導入時など）
          }
        };

        await migrateTable('boxes', { hasPhotos: true, hasTags: true });
        await migrateTable('items', { hasPhotos: true, hasTags: true });
        await migrateTable('boxLocations', { hasPhotos: true, hasTags: false });
      });
  }
}

// HMR/SSRでも単一インスタンスに
declare global {
  // eslint-disable-next-line no-var
  var __hkdb: HKDB | undefined;
  interface Window {
    __hkdb?: HKDB;
  }
}

export const db: HKDB = globalThis.__hkdb ?? new HKDB();
if (typeof window !== 'undefined') {
  (window as any).__hkdb = db;
}
if (typeof globalThis !== 'undefined') {
  globalThis.__hkdb = db;
}

// 便宜 export
export type { Table };
