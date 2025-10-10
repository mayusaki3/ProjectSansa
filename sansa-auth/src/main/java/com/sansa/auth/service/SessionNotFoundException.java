package com.sansa.auth.service;

import lombok.experimental.StandardException;

/**
 * 指定されたセッションが見つからない場合に投げる例外。
 * Service層専用（RuntimeException）。
 */
@StandardException
public class SessionNotFoundException extends RuntimeException {}
