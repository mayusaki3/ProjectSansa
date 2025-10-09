package com.sansa.auth.dto.sessions;

import lombok.*;
import java.util.List;

@Value @Builder
public class SessionsListResponse {
  List<SessionInfo> sessions;
}
