package com.gummar.boardgame.server.model;

/**
 * The type Error detail.
 *
 * @author Raj Gumma
 */
public class ErrorDetail {
    /**
     * The Error message.
     */
    String errorMessage;
    /**
     * The Cause.
     */
    String cause;

    public ErrorDetail(final String errorMessage, final String cause) {
        this.errorMessage = errorMessage;
        this.cause = cause;
    }

    /**
     * Gets error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
