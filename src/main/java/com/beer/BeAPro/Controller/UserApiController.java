package com.beer.BeAPro.Controller;

import com.beer.BeAPro.Domain.PortfolioFile;
import com.beer.BeAPro.Domain.User;
import com.beer.BeAPro.Dto.AuthDto;
import com.beer.BeAPro.Dto.FileUploadDto;
import com.beer.BeAPro.Dto.RequestDto;
import com.beer.BeAPro.Exception.ErrorCode;
import com.beer.BeAPro.Exception.RestApiException;
import com.beer.BeAPro.Service.AuthService;
import com.beer.BeAPro.Service.FileUploadService;
import com.beer.BeAPro.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;


@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApiController {

    private final FileUploadService fileUploadService;
    private final UserService userService;
    private final AuthService authService;

    
    // AT로부터 사용자 추출
    public User extractUserFromAccessToken(String requestAccessTokenInHeader) {
        String requestAccessToken = authService.resolveToken(requestAccessTokenInHeader);
        String principal = authService.getPrincipal(requestAccessToken);
        return userService.findByEmail(principal);
    }

    // 회원가입 절차 중 약관 동의
    @Operation(summary = "회원가입 중 약관 동의")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 중 약관 동의 완료"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없을 경우"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/agree")
    public ResponseEntity<String> setUserTermsAgree(
            @Parameter(description = "Access Token", example = "Bearer {access-token}") @RequestHeader("Authorization") String requestAccessTokenInHeader,
            @Parameter(description = "약관 동의 DTO") @RequestBody AuthDto.AgreeDto agreeDto) {
        // AT로부터 사용자 추출
        String requestAccessToken = authService.resolveToken(requestAccessTokenInHeader);
        String principal = authService.getPrincipal(requestAccessToken);
        User findUser = userService.findByEmail(principal);
        if (findUser == null) {
            throw new RestApiException(ErrorCode.USER_NOT_FOUND);
        }

        // 약관 동의 여부 값 설정
        userService.setTermsAgree(findUser, agreeDto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // 회원가입 절차 중 사용자 추가 정보 입력
    @Operation(summary = "회원가입 중 사용자 추가 정보 입력(Swagger에서 실행X, Postman에서 정상 실행됨)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 중 사용자 추가 정보 입력 완료"),
            @ApiResponse(responseCode = "401", description = "약관 동의 여부(POST api/auth/agree)가 저장되어 있지 않을 경우"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없을 경우"),
            @ApiResponse(responseCode = "413", description = "파일 크기가 제한보다 클 경우"),
            @ApiResponse(responseCode = "415", description = "지원하지 않는 확장자일 경우"),
            @ApiResponse(responseCode = "500", description = "서버 문제로 파일 업로드 실패 혹은 서버 에러")
    })
    @PostMapping(value = "/signup", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> signupUser(
            @Parameter(description = "Access Token", example = "Bearer {access-token}")
            @RequestHeader("Authorization") String requestAccessTokenInHeader,
            @Parameter(description = "포트폴리오 파일(multipart/form-data 타입으로 전송 필요)")
            @RequestParam(required = false) MultipartFile portfolioFile,
            @Parameter(description = "회원가입 중 사용자 추가 데이터 입력 DTO(application/json 타입으로 전송 필요)")
            @RequestPart @Valid RequestDto.SignUpAdditionalInfoDto requestDto) {
        User findUser = extractUserFromAccessToken(requestAccessTokenInHeader);

        // 약관 동의 여부가 저장되지 않았을 경우
        if (findUser.getProvideToThirdParties() == null
                || findUser.getMarketingEmail() == null || findUser.getMarketingSMS() == null) {
            throw new RestApiException(ErrorCode.TERMS_AGREEMENT_REQUIRED);
        }

        // 사용자 정보 저장
        userService.saveUserAdditionalInfo(findUser, requestDto);

        // 포트폴리오 파일 생성 및 S3에 업로드
        if (portfolioFile != null) {
            long sizeLimit = 30000000; // 30MB
            FileUploadDto fileUploadDto = fileUploadService.uploadFile(portfolioFile, sizeLimit, "file");

            // 포트폴리오 파일 정보 저장
            PortfolioFile savedPortfolioFile = fileUploadService.savePortfolioFile(fileUploadDto);
            userService.setPortfolioFile(findUser, savedPortfolioFile);
        }

        return ResponseEntity.ok().build();
    }

    // 사용자 탈퇴 처리
    @Operation(summary = "사용자 탈퇴 처리")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 탈퇴 처리 완료. Access Token, Refresh Token 삭제 완료."),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없을 경우"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/withdrawal")
    public ResponseEntity<String> deleteUser(
            @Parameter(description = "Access Token", example = "Bearer {access-token}")
            @RequestHeader("Authorization") String requestAccessTokenInHeader) {
        User user = extractUserFromAccessToken(requestAccessTokenInHeader);
        userService.withdrawal(user);

        ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                .maxAge(0)
                .path("/")
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .build();
    }
}
