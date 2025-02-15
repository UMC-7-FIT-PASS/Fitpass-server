package com.example.fitpassserver.domain.member.controller;


import com.example.fitpassserver.domain.member.annotation.CurrentMember;
import com.example.fitpassserver.domain.member.converter.MemberConverter;
import com.example.fitpassserver.domain.member.dto.MemberRequestDTO;
import com.example.fitpassserver.domain.member.dto.MemberResponseDTO;
import com.example.fitpassserver.domain.member.entity.Member;
import com.example.fitpassserver.domain.member.exception.MemberErrorCode;
import com.example.fitpassserver.domain.member.exception.MemberException;
import com.example.fitpassserver.domain.member.service.command.MemberCommandService;
import com.example.fitpassserver.domain.member.service.query.MemberQueryService;
import com.example.fitpassserver.domain.member.validation.validator.CheckLoginIdValidator;
import com.example.fitpassserver.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "일반 회원 API", description = "일반 회원 관련 API입니다.")
public class MemberController {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;
    private final CheckLoginIdValidator checkLoginIdValidator; //중복 아이디 체크

    @InitBinder("joinDTO")
    public void validatorBinder(WebDataBinder binder) {
        binder.addValidators(checkLoginIdValidator);
    }

    @Operation(summary = "회원가입 api", description = "회원가입을 위한 api입니다.")
    @PostMapping("/register")
    public ApiResponse<MemberResponseDTO.JoinResultDTO> join(@RequestBody @Valid MemberRequestDTO.JoinDTO request) {
        Member member = memberCommandService.joinMember(request);
        return ApiResponse.onSuccess(MemberConverter.toJoinResultDTO(member));
    }

    @Operation(summary = "아이디 중복 확인 api", description = "중복 아이디 확인을 위한 api입니다.")
    @GetMapping("/check/login-id")
    public ApiResponse<?> checkLoginId(@RequestParam("loginId") String loginId) {
        boolean isDuplicate = memberQueryService.checkLoginId(loginId);
        return ApiResponse.onSuccess(isDuplicate);
    }

    @Operation(summary = "로그인 api", description = "로그인을 위한 api입니다.")
    @Parameters({
            @Parameter(name = "dto", description = "로그인을 위한 아이디와 비밀번호 입력 DTO")
    })
    @PostMapping("/login")
    public ApiResponse<MemberResponseDTO.MemberTokenDTO> login(@RequestBody MemberRequestDTO.LoginDTO dto) {
        return ApiResponse.onSuccess(memberCommandService.login(dto));
    }

    @Operation(summary = "리프레시 토큰 갱신 api", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰 발급하는 api입니다.")
    @Parameters({
            @Parameter(name = "Refresh-Token", description = "리프레시 토큰")
    })
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토큰 재발급에 성공하였습니다.",
                    content = @Content(schema = @Schema(implementation = io.swagger.v3.oas.annotations.responses.ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "유효하지 않은 토큰입니다.",
                    content = @Content(schema = @Schema(implementation = io.swagger.v3.oas.annotations.responses.ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰이 만료되었습니다.",
                    content = @Content(schema = @Schema(implementation = io.swagger.v3.oas.annotations.responses.ApiResponse.class)))
    })
    @PostMapping("/refresh")
    public ApiResponse<MemberResponseDTO.MemberTokenDTO> refreshToken(
            @RequestHeader(value = "Refresh-Token", required = true) String refreshToken) {

        // Refresh Token이 비어 있거나 잘못된 경우 처리
        if (refreshToken == null || refreshToken.isBlank()) {
            log.error("Refresh token is missing or invalid.");
            throw new MemberException(MemberErrorCode.INVALID_TOKEN);
        }

        // 토큰 재발급
        MemberResponseDTO.MemberTokenDTO tokenResponse = memberCommandService.refreshToken(refreshToken);
        return ApiResponse.onSuccess(tokenResponse);
    }

    @Operation(summary = "사용자 탈퇴 api", description = "사용자 탈퇴시 사용하는 api입니다..")
    @DeleteMapping("/withdraw")
    public ApiResponse<Void> deleteMember(@CurrentMember Member member) {
        memberCommandService.deactivateAccount(member); // 회원 탈퇴 처리
        return ApiResponse.onSuccess(null); // HTTP 204 No Content 응답
    }

    @Operation(summary = "소셜 로그인 후 추가 정보 입력받아 회원가입하는 api", description = "소셜 로그인 이후 추가 정보를 입력받아 회원가입하는 api입니다.")
    @PostMapping("/oauth2/register")
    public ApiResponse<?> oAuth2Join(@RequestBody @Valid MemberRequestDTO.SocialJoinDTO request, @CookieValue(value = "accessToken") String accessToken) {
        Member updatedMember = memberCommandService.socialJoinMember(request, accessToken);
        return ApiResponse.onSuccess(MemberConverter.toJoinResultDTO(updatedMember));
    }

    @Operation(summary = "사용자 위치 설정 api", description = "위도 경도를 받아 사용자의 위치를 설정합니다.")
    @PatchMapping("/location")
    public ApiResponse<String> setLocation(@CurrentMember Member member, @RequestBody @Valid MemberRequestDTO.LocationDTO dto) {
        memberCommandService.setLocation(member.getLoginId(), dto);
        return ApiResponse.onSuccess("사용자의 위치가 변경되었습니다.");
    }

    @Operation(summary = "전화번호 변경 api", description = "인증된 전화번호를 변경하는 api입니다.")
    @PatchMapping("/change/phone-number")
    public ApiResponse<?> changePhoneNumber(@CurrentMember Member member, @RequestBody @Valid MemberRequestDTO.ChangePhoneNumberDTO request) {
        memberCommandService.changePhoneNumber(member, request);
        return ApiResponse.onSuccess("전화번호가 변경되었습니다.");
    }

    @Operation(summary = "아이디 찾기 api", description = "아이디 찾기 api입니다.")
    @PostMapping("/find-id")
    public ApiResponse<?> findId(@RequestBody @Valid MemberRequestDTO.FindLoginIdDTO request) {
        String loginId = memberQueryService.getLoginId(request);
        return ApiResponse.onSuccess(loginId);
    }

    @Operation(summary = "비밀번호 찾기 api", description = "비밀번호 찾기 api입니다.")
    @PostMapping("/find-password")
    public ApiResponse<?> findPassword(@RequestBody @Valid MemberRequestDTO.FindPasswordDTO request) {
        return ApiResponse.onSuccess(memberQueryService.findPassword(request));
    }

    @Operation(summary = "비밀번호 리셋 api", description = "비밀번호 찾기 후 비밀번호를 리셋하는 api입니다.")
    @PatchMapping("/reset-password")
    public ApiResponse<?> resetPassword(@RequestBody @Valid MemberRequestDTO.ResetPasswordDTO request) {
        memberCommandService.resetPassword(request);
        return ApiResponse.onSuccess("비밀번호 변경 완료");
    }

    @Operation(summary = "비밀번호 변경 api", description = "비밀번호를 변경하는 api입니다.")
    @PatchMapping("/change/password")
    public ApiResponse<?> resetPassword(@CurrentMember Member member, @RequestBody @Valid MemberRequestDTO.ChangePasswordDTO request) {
        memberCommandService.changePassword(member, request);
        return ApiResponse.onSuccess("비밀번호 변경 완료");
    }


}
