package com.mtn.madapi.payments.occ.occcharger.cca;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorHolder {

    private String errorMessage;
    private Throwable error;

    /**
     * @param error
     */
    public ErrorHolder(Throwable error) {
        super();
        this.error = error;
    }

    /**
     * @param errorMessage
     */
    public ErrorHolder(String errorMessage) {
        super();
        this.errorMessage = errorMessage;
    }

    /**
     * @param errorMessage
     * @param error
     */
    public ErrorHolder(String errorMessage, Throwable error) {
        super();
        this.errorMessage = errorMessage;
        this.error = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Throwable getError() {
        return error;
    }

    @Override
    public String toString() {
        // for now. add StackTrace gen
        StringBuilder sb = new StringBuilder(" Msg: ");
        if (errorMessage != null) {
            sb.append(errorMessage).append(", stack trace: \n");
        }
        else {
            sb.append("EMPTY, stack trace: \n");
        }

        if (error != null) {
            error.fillInStackTrace();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            error.printStackTrace(pw);
            sb.append(sw.getBuffer().toString());
        }

        return sb.toString();
    }

}