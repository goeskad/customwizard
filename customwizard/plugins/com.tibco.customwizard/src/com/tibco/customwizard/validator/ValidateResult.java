package com.tibco.customwizard.validator;

public class ValidateResult {
    public static final ValidateResult VALID = new ValidateResult();

    private boolean valid;

    private String errorMessage;

    private ValidateResult() {
        valid = true;
    }

    public ValidateResult(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isValid() {
        return valid;
    }
}
