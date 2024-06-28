package com.andwis.travel_with_anna.email;

import lombok.Getter;

@Getter
public enum EmailTemplateName {
    ACTIVATE_ACCOUNT("activation-account");

    private final String templateName;

    EmailTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
