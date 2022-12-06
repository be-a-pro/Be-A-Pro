package com.beer.BeAPro.Controller;

import com.beer.BeAPro.Domain.PortfolioFile;
import com.beer.BeAPro.Domain.User;
import com.beer.BeAPro.Dto.FileUploadDto;
import com.beer.BeAPro.Dto.RequestDto;
import com.beer.BeAPro.Exception.ErrorCode;
import com.beer.BeAPro.Exception.RestApiException;
import com.beer.BeAPro.Service.AuthService;
import com.beer.BeAPro.Service.FileUploadService;
import com.beer.BeAPro.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    // 회원가입 절차 중 사용자 추가 정보 입력
    @PostMapping(value = "/signup", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> signupUser(@RequestPart MultipartFile portfolioFile,
                                             @RequestPart @Valid RequestDto.SignUpAdditionalInfoDto requestDto,
                                             @RequestHeader("Authorization") String requestAccessTokenInHeader) {
        User findUser = extractUserFromAccessToken(requestAccessTokenInHeader);

        // 약관 동의 여부가 저장되지 않았을 경우
        if (findUser.getProvideToThirdParties() == null
                || findUser.getMarketingEmail() == null || findUser.getMarketingSMS() == null) {
            throw new RestApiException(ErrorCode.TERMS_AGREEMENT_REQUIRED);
        }

        // 사용자 정보 저장
        userService.saveUserAdditionalInfo(findUser, requestDto);

        // 포트폴리오 파일 생성 및 S3에 업로드
        if (!portfolioFile.isEmpty()) {
            long sizeLimit = 30000000; // 30MB
            FileUploadDto fileUploadDto = fileUploadService.uploadFile(portfolioFile, sizeLimit, "file");

            // 포트폴리오 파일 정보 저장
            PortfolioFile savedPortfolioFile = fileUploadService.savePortfolioFile(fileUploadDto);
            userService.setPortfolioFile(findUser, savedPortfolioFile);
        }

        return ResponseEntity.ok().build();
    }

    // 사용자 계정 삭제
    @PostMapping("/withdrawal")
    public ResponseEntity<String> deleteUser(@RequestHeader("Authorization") String requestAccessTokenInHeader) {
        User user = extractUserFromAccessToken(requestAccessTokenInHeader);
        userService.withdrawal(user);

        return ResponseEntity.ok().build();
    }
}
