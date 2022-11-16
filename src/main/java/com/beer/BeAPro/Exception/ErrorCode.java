package com.beer.BeAPro.Exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /*
     * 400 BAD_REQUEST: 잘못된 요청
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request."),
    PARAMETER_REQUIRED(HttpStatus.BAD_REQUEST, "Parameter required."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "Invalid file name."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Validation failed for argument"),

    /*
     * 401 UNAUTHORIZED: 인증되지 않은 사용자의 요청
     */
    UNAUTHORIZED_REQUEST(HttpStatus.UNAUTHORIZED, "Unauthorized."),
    SOCIAL_LOGIN_ERROR(HttpStatus.UNAUTHORIZED, "Unauthorized."),
    TERMS_AGREEMENT_REQUIRED(HttpStatus.UNAUTHORIZED, "Agreement to the terms and conditions is required."),
    LOGOUT_FAILED(HttpStatus.UNAUTHORIZED, "Logout failed. Invalid user."),

    /*
     * 403 FORBIDDEN: 권한이 없는 사용자의 요청
     */
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "Forbidden."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Access Denied."),
    CREATING_PROJECT_DENIED(HttpStatus.FORBIDDEN, "Unable to create the project. The portfolio must be made public."),

    /*
     * 404 NOT_FOUND: 리소스를 찾을 수 없음
     */
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "Not found post."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Not found user."),
    PROJECT_AWAITING_DELETION(HttpStatus.NOT_FOUND, "Project awaiting deletion."),

    /*
     * 405 METHOD_NOT_ALLOWED: 허용되지 않은 Request Method 호출
     */
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Not allowed method."),
    CANNOT_DISCONNECT(HttpStatus.METHOD_NOT_ALLOWED, "The SNS account used for membership registration cannot be disconnected."),

    /*
     * 409 CONFLICT: 서버의 현재 상태와 요청이 충돌
     */
    CONFLICT_REQUEST(HttpStatus.CONFLICT, "Conflict request."),
    LEADER_ALREADY_EXISTS(HttpStatus.CONFLICT, "The team leader of the project already exists."),

    /*
     * 413 PAYLOAD_TOO_LARGE: 서버에서 지원하지 않는 미디어 포맷
     */
    FILE_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "File size is too large."),

    /*
     * 415 UNSUPPORTED_MEDIA_TYPE: 서버에서 지원하지 않는 미디어 포맷
     */
    INVALID_EXTENSION(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Invalid extension."),

    /*
     * 500 INTERNAL_SERVER_ERROR: 내부 서버 오류
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Server error.");


    private final HttpStatus httpStatus;
    private final String message;
}
