package com.andwis.travel_with_anna.email;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailTemplateName {

    ACTIVATE_ACCOUNT("activate-account"),
    RESET_PASSWORD("reset-password");

    private final String templateName;
}
