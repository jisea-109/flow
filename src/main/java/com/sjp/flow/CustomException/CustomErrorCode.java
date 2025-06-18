package com.sjp.flow.CustomException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomErrorCode {
    EXTENSION_NOT_FOUND("확장자를 찾을 수 없습니다."),
    CHARACTER_NOT_ALLOWED("확장자에는 알파벳과 숫자 그리고 단일로 '/' 만 사용할 수 있습니다."),
    EXTENSION_LENGTH("확장자 최대 길이는 20자리 입니다."),
    DUPLICATE_EXTENSION_FOUND("이미 추가된 확장자입니다."),
    EXTENSION_MAX("커스텀 확장자 최대 갯수(200개)에 도달했습니다."),
    NOT_ALLOWED_FILE_EXTENSION("업로드가 제한된 파일 유형입니다"),
    INAPPROPIRATE_EXTENSION_TYPE("커스텀 확장자만 삭제 가능합니다");

    private final String message;
}
