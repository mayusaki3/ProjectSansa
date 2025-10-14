package com.sansa.auth.it;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class ITBase {

  @Autowired protected MockMvc mvc;

  protected static final MediaType JSON = MediaType.APPLICATION_JSON;

  protected String at; // 発行された AccessToken を逐次格納
  protected String rt; // RefreshToken

  @BeforeEach
  void setupCommon() {
    at = null; rt = null;
  }

  protected String bearer() { return "Bearer " + at; }
}
