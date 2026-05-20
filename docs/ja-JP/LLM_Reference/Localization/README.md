[目次](../../目次.md) > LLM_Reference > Localization

# Localization

## 1. 概要

Localization は、Sansa ecosystem における multilingual policy / localization policy / semantic consistency policy を扱う reference directory である。

ProjectSansa は multilingual terminology authority として扱う。

---

## 2. 基本方針

### primary authoring language

Sansa ecosystem の primary authoring language は ja-JP とする。

以下を対象とする。

- architecture discussion
- governance discussion
- provenance discussion
- specification authoring
- LLM discussion
- repository coordination

---

### canonical identifier language

identifier / schema / semantic key は en-US を canonical language として固定する。

### examples

- term_id
- schema field
- API field
- enum
- policy id
- provenance id
- governance id
- error code
- event id

repository ごとに semantic identifier を独自翻訳しない。

---

## 3. multilingual policy

### embedded mandatory language

以下を embedded mandatory language とする。

- en-US

English は canonical semantic fallback として扱う。

---

### external localization

以下は external localization resource として扱う。

- ja-JP
- community translations
- future translations

OSS community が後から translation resource を追加可能な構造を推奨する。

---

## 4. localization structure example

```text
resources/
├─ en-US/
│  ├─ errors.json
│  ├─ governance.json
│  └─ provenance.json
│
├─ ja-JP/
│  ├─ errors.json
│  ├─ governance.json
│  └─ provenance.json
│
└─ community/
   ├─ fr-FR/
   └─ zh-CN/
```

---

## 5. fallback policy

localization fallback は以下順序を推奨する。

```text
1. requested language
2. fallback language
3. en-US
4. identifier
```

identifier fallback を維持し、semantic traceability を保持する。

---

## 6. API / Error policy

### structured identifier mandatory

error / audit / governance / provenance 系は structured identifier を mandatory とする。

### examples

```json
{
  "error_code": "governance_conflict",
  "message": "governance policy conflict detected",
  "locale": "en-US"
}
```

---

### localization example

```json
{
  "error_code": "governance_conflict",
  "message": "governance ポリシーが衝突しています",
  "locale": "ja-JP"
}
```

---

## 7. Important policy

strict message string matching を semantic authority としない。

以下を semantic authority とする。

- identifier
- schema key
- event id
- error code
- policy id

localized message は display layer として扱う。

---

## 8. Repository relationships

### SansaVRM

- governance localization
- provenance localization
- restriction localization

### SansaVRM-Studio-AI

- workflow localization
- diagnostics localization
- assembly localization

### SansaSphere

- audit localization
- verification localization
- dashboard localization

### SansaXR

- runtime localization
- networking diagnostics localization

---

[目次](../../目次.md) > LLM_Reference > Localization