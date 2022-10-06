package me.snsservice.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /**
     * ErrorCode, HttpStatus, ErrorMessage
     * ex) 404,HttpStatus.NOT_FOUND, 존재하지 않은 회원
     */

    //common
    METHOD_NOT_ALLOWED(405, HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 Method 입력값"),
    INVALID_INPUT_VALUE(400, HttpStatus.BAD_REQUEST, "유효하지 않은 입력값"),
    BAD_REQUEST(400, HttpStatus.BAD_REQUEST, "bad request"),
    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "internal server error"),

    // member
    NOT_FOUND_MEMBER(404, HttpStatus.NOT_FOUND, "존재하지 않는 회원");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;
}