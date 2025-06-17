package com.sjp.flow.CustomException;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private CustomErrorCode errorCode;
    private String errorMessage;
    private String targetView;

    public CustomException(CustomErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getMessage();
    }
}
