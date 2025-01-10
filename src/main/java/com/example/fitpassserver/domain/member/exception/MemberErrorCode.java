package com.example.fitpassserver.domain.member.exception;

import com.example.fitpassserver.global.apiPayload.code.BaseErrorCode;
import com.example.fitpassserver.global.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404", "사용자를 찾을 수 없습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "MEMBER400", "필수 동의사항에 모두 동의해야 회원가입이 가능합니다."),
    INCORRECT_PASSWORD(HttpStatus.UNAUTHORIZED, "MEMBER401", "비밀번호가 틀립니다."),
    INACTIVE_ACCOUNT(HttpStatus.FORBIDDEN, "MEMBER403", "비활성화된 계정입니다."),
    INVALID_VERIFICATION_CODE(HttpStatus.NOT_FOUND, "CODE400", "인증번호가 맞지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN401", "유효하지 않은 토큰입니다."),
    DUPLICATE_PHONE_NUMBER(HttpStatus.CONFLICT, "PHONE409", "이미 가입된 번호입니다.");
    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .httpStatus(this.status)
                .isSuccess(false) // 에러이므로 항상 false
                .code(this.code)
                .message(this.message)
                .build();
    }
}