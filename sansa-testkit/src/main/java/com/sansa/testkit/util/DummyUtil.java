package com.sansa.testkit.util;

import com.sansa.testkit.annotations.DummyImplementation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * DummyImplementation アノテーションを用いたダミー判定ユーティリティ。
 *
 * 役割:
 * - クラス／インスタンス／メソッドに @DummyImplementation が付与されているか判定する。
 * - テストコード側から簡単に「対象がダミーか？」を確認できるようにする。
 */
public final class DummyUtil {

    private DummyUtil() {
        // インスタンス化禁止
    }

    /**
     * クラスに @DummyImplementation が付与されているかを判定する。
     *
     * @param type 判定対象のクラス
     * @return true: DummyImplementation が付与されている / false: それ以外
     */
    public static boolean isDummy(Class<?> type) {
        if (type == null) {
            return false;
        }
        return hasDummyAnnotation(type);
    }

    /**
     * インスタンスのクラスに @DummyImplementation が付与されているかを判定する。
     *
     * @param instance 判定対象インスタンス
     * @return true: DummyImplementation が付与されている / false: それ以外
     */
    public static boolean isDummy(Object instance) {
        if (instance == null) {
            return false;
        }
        return isDummy(instance.getClass());
    }

    /**
     * メソッドまたはその宣言クラスに @DummyImplementation が付与されているかを判定する。
     *
     * 優先順位:
     * 1. メソッド自体に付与されているか
     * 2. クラス（宣言元）に付与されているか
     *
     * @param method 判定対象メソッド
     * @return true: DummyImplementation が付与されている / false: それ以外
     */
    public static boolean isDummy(Method method) {
        if (method == null) {
            return false;
        }
        if (hasDummyAnnotation(method)) {
            return true;
        }
        return hasDummyAnnotation(method.getDeclaringClass());
    }

    /**
     * 複数のクラスの中に 1つでもダミーが含まれているかを判定する。
     *
     * @param types 判定対象クラスの可変長引数
     * @return true: いずれかがダミー / false: すべて本番実装
     */
    public static boolean hasAnyDummy(Class<?>... types) {
        if (types == null) {
            return false;
        }
        for (Class<?> type : types) {
            if (isDummy(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 複数インスタンスの中に 1つでもダミーが含まれているかを判定する。
     *
     * @param instances 判定対象インスタンスの可変長引数
     * @return true: いずれかがダミー / false: すべて本番実装
     */
    public static boolean hasAnyDummy(Object... instances) {
        if (instances == null) {
            return false;
        }
        for (Object instance : instances) {
            if (isDummy(instance)) {
                return true;
            }
        }
        return false;
    }

    /**
     * AnnotatedElement（クラス／メソッドなど）に @DummyImplementation が付与されているかを判定する。
     *
     * @param element 判定対象
     * @return true: DummyImplementation が付与されている / false: それ以外
     */
    private static boolean hasDummyAnnotation(AnnotatedElement element) {
        if (Objects.isNull(element)) {
            return false;
        }
        return element.isAnnotationPresent(DummyImplementation.class);
    }
}
