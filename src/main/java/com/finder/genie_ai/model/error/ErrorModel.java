package com.finder.genie_ai.model.error;

import com.google.gson.annotations.SerializedName;

public class ErrorModel {

    @SerializedName("code")
    private int code;
    @SerializedName("message")
    private String message;

    public ErrorModel(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
