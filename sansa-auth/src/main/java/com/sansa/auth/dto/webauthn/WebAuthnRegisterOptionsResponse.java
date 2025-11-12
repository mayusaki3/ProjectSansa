package com.sansa.auth.dto.webauthn;

/**
 * setOptionsJson(...) を要求されているため setter を用意。
 */
public class WebAuthnRegisterOptionsResponse {
    private String optionsJson;

    public String getOptionsJson() { return optionsJson; }
    public void setOptionsJson(String optionsJson) { this.optionsJson = optionsJson; }
}
