package com.sansa.auth.annotations;

import java.lang.annotation.*;

/**
 * TEST-ONLY: テストから直接呼ぶ／触ることを意図した要素に付ける注釈。
 * 可視性は package-private を推奨。ビルド成果物には残さない。
 */
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface VisibleForTesting {}
