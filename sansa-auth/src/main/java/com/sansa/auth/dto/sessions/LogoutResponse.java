package com.sansa.auth.dto.sessions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogoutResponse {
    private boolean success;

    public static LogoutResponse ok() {
        return new LogoutResponse(true);
    }

    public static LogoutResponse fail() {
        return new LogoutResponse(false);
    }
}
