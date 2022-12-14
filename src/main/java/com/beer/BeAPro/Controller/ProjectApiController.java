package com.beer.BeAPro.Controller;

import com.beer.BeAPro.Domain.Category;
import com.beer.BeAPro.Domain.Project;
import com.beer.BeAPro.Domain.ProjectImage;
import com.beer.BeAPro.Domain.User;
import com.beer.BeAPro.Dto.FileUploadDto;
import com.beer.BeAPro.Dto.RequestDto;
import com.beer.BeAPro.Dto.ResponseDto;
import com.beer.BeAPro.Exception.ErrorCode;
import com.beer.BeAPro.Exception.RestApiException;
import com.beer.BeAPro.Service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectApiController {

    private final ProjectService projectService;
    private final FileUploadService fileUploadService;
    private final UserService userService;
    private final AuthService authService;
    private final ApplyService applyService;


    // ===== 프로젝트 생성 및 수정 ===== //

    // 프로젝트 객체 생성: 최초의 임시저장 or 임시저장 없이 글 작성 완료
    @Operation(summary = "프로젝트 생성(Swagger에서 실행X, Postman에서 정상 실행됨)",
            description = "최초의 임시저장 / 임시저장 없이 글 작성 완료")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "프로젝트 생성 완료"),
            @ApiResponse(responseCode = "400", description = "프로젝트 총 구인 인원이 10명을 초과할 경우"),
            @ApiResponse(responseCode = "403", description = "포트폴리오 비공개 상태라 프로젝트 생성이 불가능한 사용자일 경우"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없을 경우"),
            @ApiResponse(responseCode = "409", description = "버그 리포트 바람"),
            @ApiResponse(responseCode = "413", description = "파일 크기가 제한보다 클 경우"),
            @ApiResponse(responseCode = "415", description = "지원하지 않는 확장자일 경우"),
            @ApiResponse(responseCode = "500", description = "서버 문제로 파일 업로드 실패 혹은 서버 에러"),
    })
    @PostMapping(value = "/write", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ResponseDto.GetProjectIdDto> createProject(
            @Parameter(description = "Access Token", example = "Bearer {access-token}")
            @RequestHeader("Authorization") String requestAccessTokenInHeader,
            @Parameter(description = "프로젝트 대표 이미지(multipart/form-data 타입으로 전송 필요)")
            @RequestParam(required = false) MultipartFile projectImage,
            @Parameter(description = "프로젝트 데이터 DTO(application/json 타입으로 전송 필요)")
            @RequestPart @Valid RequestDto.ProjectDto projectDto) {
        // 사용자 검증
        User findUser = extractUserFromAccessToken(requestAccessTokenInHeader);
        checkPortfolioIsPublic(findUser);

        // 프로젝트 대표 이미지 생성 및 S3에 업로드
        ProjectImage savedProjectImage = null;
        if (projectImage != null) {
            long sizeLimit = 5000000; // 5MB
            FileUploadDto fileUploadDto = fileUploadService.uploadFile(projectImage, sizeLimit, "image");

            // 프로젝트 대표 이미지 정보 저장
            savedProjectImage = fileUploadService.saveProjectImage(fileUploadDto); // 연관관계 매핑X
        }

        // 프로젝트 데이터 저장
        Project project = projectService.createProject(findUser); // 객체 생성
        projectService.saveData(project, projectDto, savedProjectImage); // 데이터 저장(연관관계 매핑)
        projectService.createProjectLeader(findUser, project); // 팀장 ProjectMember 생성

        ResponseDto.GetProjectIdDto getProjectIdDto = ResponseDto.GetProjectIdDto.builder()
                .projectId(project.getId())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(getProjectIdDto);
    }

    // 프로젝트 객체 수정: 최초가 아닌 임시저장 or 임시저장 후 글 작성 완료 or 글 수정
    @Operation(summary = "프로젝트 수정",
            description = "최초가 아닌 임시저장 / 임시저장 후 글 작성 완료 / (작성이 완료된) 프로젝트 글 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "프로젝트 수정 완료"),
            @ApiResponse(responseCode = "400", description = "프로젝트 총 구인 인원이 10명을 초과할 경우"),
            @ApiResponse(responseCode = "403", description = "포트폴리오 비공개 상태라 프로젝트 생성이 불가능한 사용자일 경우"),
            @ApiResponse(responseCode = "404", description = "사용자 혹은 프로젝트를 찾을 수 없을 경우"),
            @ApiResponse(responseCode = "409", description = "버그 리포트 바람"),
            @ApiResponse(responseCode = "413", description = "파일 크기가 제한보다 클 경우"),
            @ApiResponse(responseCode = "415", description = "지원하지 않는 확장자일 경우"),
            @ApiResponse(responseCode = "500", description = "서버 문제로 파일 업로드 실패"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PutMapping(value = "/write", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ResponseDto.GetProjectIdDto> printProjectBeingCreated(
            @Parameter(description = "Access Token", example = "Bearer {access-token}")
            @RequestHeader("Authorization") String requestAccessTokenInHeader,
            @Parameter(description = "프로젝트 id") @RequestParam Long id,
            @Parameter(description = "프로젝트 대표 이미지") @RequestPart(required = false) MultipartFile projectImage,
            @Parameter(description = "프로젝트 데이터 DTO") @RequestPart @Valid RequestDto.ProjectDto projectDto) {
        // 사용자 검증
        User findUser = extractUserFromAccessToken(requestAccessTokenInHeader);
        checkPortfolioIsPublic(findUser);

        Project findProject = findProjectByWriter(findUser, id);

        // 프로젝트 대표 이미지
        // AWS S3에서 기존 프로젝트 대표 이미지 삭제
        if (findProject.getProjectImage() != null) {
            fileUploadService.deleteFile(findProject.getProjectImage().getModifiedName());
        }
        // 생성 및 S3에 업로드
        ProjectImage savedProjectImage = null;
        if (!projectImage.isEmpty()) {
            long sizeLimit = 5000000; // 5MB
            FileUploadDto fileUploadDto = fileUploadService.uploadFile(projectImage, sizeLimit, "image");

            // 프로젝트 대표 이미지 정보 저장
            savedProjectImage = fileUploadService.saveProjectImage(fileUploadDto); // 연관관계 매핑X
        }

        // 프로젝트 데이터 저장
        projectService.update(findProject, projectDto, savedProjectImage);

        ResponseDto.GetProjectIdDto getProjectIdDto = ResponseDto.GetProjectIdDto.builder()
                .projectId(findProject.getId())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(getProjectIdDto);
    }


    // ===== 프로젝트 삭제 ===== //

    // 임시저장된 프로젝트 영구 삭제
    @Operation(summary = "임시저장된 프로젝트 영구 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "임시저장된 프로젝트 영구 삭제 완료"),
            @ApiResponse(responseCode = "404", description = "사용자 혹은 프로젝트를 찾을 수 없을 경우"),
            @ApiResponse(responseCode = "405", description = "임시저장된 프로젝트가 아닐 경우(잘못된 접근)"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteTemporaryProject(
            @Parameter(description = "Access Token", example = "Bearer {access-token}")
            @RequestHeader("Authorization") String requestAccessTokenInHeader,
            @Parameter(description = "프로젝트 id") @RequestParam Long id) {
        // 사용자 검증
        User findUser = extractUserFromAccessToken(requestAccessTokenInHeader);
        checkPortfolioIsPublic(findUser);

        Project findProject = findProjectByWriter(findUser, id);
        if (!findProject.getIsTemporary()) { // 임시저장된 프로젝트가 아닐경우
            throw new RestApiException(ErrorCode.METHOD_NOT_ALLOWED);
        }

        projectService.deleteProject(findProject);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 사용자 권한으로 프로젝트 삭제: 삭제 처리(복구 가능)
    @Operation(summary = "사용자 권한으로 프로젝트 삭제 처리")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 처리 완료(복구 기한을 가짐)"),
            @ApiResponse(responseCode = "404", description = "사용자 혹은 프로젝트를 찾을 수 없을 경우"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PutMapping("/delete")
    public ResponseEntity<String> deleteProject(
            @Parameter(description = "Access Token", example = "Bearer {access-token}")
            @RequestHeader("Authorization") String requestAccessTokenInHeader,
            @Parameter(description = "프로젝트 id") @RequestParam Long id) {
        // 사용자 검증
        User findUser = extractUserFromAccessToken(requestAccessTokenInHeader);
        Project findProject = findProjectByWriter(findUser, id);

        // 삭제 처리
        projectService.deleteProcessingProject(findProject);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    // ===== 프로젝트 조회 ===== //
    // 프로젝트 데이터 가져오기: 임시저장된 글 작성 또는 글 수정시
    @Operation(summary = "프로젝트 데이터 가져오기(임시저장된 글 작성 / 작성된 글 수정)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로젝트 데이터 가져오기 완료"),
            @ApiResponse(responseCode = "403", description = "포트폴리오 비공개 상태라 프로젝트 생성이 불가능한 사용자일 경우"),
            @ApiResponse(responseCode = "404", description = "사용자 혹은 프로젝트를 찾을 수 없을 경우"),
            @ApiResponse(responseCode = "409", description = "이미 삭제 처리된 프로젝트일 경우"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/write")
    public ResponseEntity<ResponseDto.GetProjectDataDto> getTemporaryProject(
            @Parameter(description = "Access Token", example = "Bearer {access-token}")
            @RequestHeader("Authorization") String requestAccessTokenInHeader,
            @Parameter(description = "프로젝트 id") @RequestParam Long id) {
        // 사용자 검증
        User findUser = extractUserFromAccessToken(requestAccessTokenInHeader);
        checkPortfolioIsPublic(findUser);
        Project findProject = findProjectByWriter(findUser, id);

        // 프로젝트 데이터 가져오기
        ResponseDto.GetProjectDataDto getProjectDataDto = projectService.getProjectData(findProject, true);
        return ResponseEntity.status(HttpStatus.OK).body(getProjectDataDto);
    }

    // 임시저장된 프로젝트가 있는지 체크
    @Operation(summary = "사용자가 임시저장한 프로젝트가 있는지 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "임시저장된 프로젝트가 있을 경우"),
            @ApiResponse(responseCode = "204", description = "임시저장된 프로젝트가 없을 경우"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없을 경우"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/temporary")
    public ResponseEntity<ResponseDto.GetProjectIdDto> checkTemporaryProject(
            @Parameter(description = "Access Token", example = "Bearer {access-token}")
            @RequestHeader("Authorization") String requestAccessTokenInHeader) {
        // 사용자 검증
        User findUser = extractUserFromAccessToken(requestAccessTokenInHeader);
        Project temporaryProject = projectService.findTemporaryProjectByUser(findUser);

        if (temporaryProject != null) {
            ResponseDto.GetProjectIdDto responseDto = ResponseDto.GetProjectIdDto.builder()
                    .projectId(temporaryProject.getId())
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    // 프로젝트 상세 내용 가져오기
    @Operation(summary = "프로젝트 상세 내용 가져오기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로젝트 상세 내용 가져오기 완료"),
            @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없을 경우"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping
    public ResponseEntity<ResponseDto.GetProjectDetailDto> getProjectDetail(
            @Parameter(description = "프로젝트 id") @RequestParam Long id) {
        Project findProject = projectService.findById(id);
        if (findProject != null) {
            projectService.increaseViews(findProject); // 조회수 증가
            ResponseDto.GetProjectDetailDto responseDto = projectService.getProjectDetail(findProject);
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);
        } else {
            throw new RestApiException(ErrorCode.POST_NOT_FOUND);
        }
    }

    // 프로젝트 목록 불러오기
    @Operation(summary = "프로젝트 목록 불러오기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로젝트 목록 불러오기 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 파라미터 입력시"),
            @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없을 경우"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/list")
    public ResponseEntity<ResponseDto.GetProjectListDto> getProjectList(
            @Parameter(description = "마지막으로 출력된 프로젝트 id") @RequestParam(value = "id", required = false) Long lastProjectId,
            @Parameter(description = "정렬: view(조회순)")  @RequestParam(required = false) String sort,
            @Parameter(description = "모집 완료 필터: recruiting(모집 완료 제외)") @RequestParam(required = false) String filter,
            @Parameter(description = "포지션 필터: development / design / planning / etc") @RequestParam(required = false) String position) {
        // 페이징 처리
        if ((sort != null && !sort.equals("view")) || (filter != null && !filter.equals("recruiting"))) {
            throw new RestApiException(ErrorCode.BAD_REQUEST);
        }
        boolean sortByView = sort != null; // 조회순 정렬일 경우
        boolean isRecruitmentCompletionExcluded = filter != null; // 모집 완료 제외 여부 필터링
        // 포지션 필터링
        Category category = null;
        if (position != null) {
            switch (position) {
                case "development":
                    category = Category.DEVELOPMENT;
                    break;
                case "design":
                    category = Category.DESIGN;
                    break;
                case "planning":
                    category = Category.PLANNING;
                    break;
                case "etc":
                    category = Category.ETC;
                    break;
                default:
                    throw new RestApiException(ErrorCode.INVALID_CATEGORY);
            }
        }
        Slice<Project> projects =
                projectService.pagingProjectList(lastProjectId, sortByView, isRecruitmentCompletionExcluded, category);

        // 프로젝트 목록에 보일 전체 데이터 DTO로 변환
        List<ResponseDto.TotalDataOfProjectListDto> projectList = new ArrayList<>();
        for (Project project : projects) {
            projectList.add(projectService.getTotalDataOfProjectList(project));
        }

        // 프로젝트 목록과 다음 컨텐츠 유무 여부
        ResponseDto.GetProjectListDto responseDto = ResponseDto.GetProjectListDto.builder()
                .projectList(projectList)
                .hasNext(projects.hasNext())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    // ===== 프로젝트 지원 ===== //
    @Operation(summary = "프로젝트 지원")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로젝트 지원 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 파라미터 입력시"),
            @ApiResponse(responseCode = "403", description = "작성자가 지원했을 경우"),
            @ApiResponse(responseCode = "404", description = "사용자 혹은 프로젝트를 찾을 수 없을 경우"),
            @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없거나 요청과 일치하는 포지션이 없는 경우"),
            @ApiResponse(responseCode = "409", description = "구인 마감 또는 지원 마감 / 프로젝트에 중복 지원한 경우"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/apply")
    public ResponseEntity<String> applyProject(
            @Parameter(description = "Access Token", example = "Bearer {access-token}")
            @RequestHeader("Authorization") String requestAccessTokenInHeader,
            @Parameter(description = "프로젝트 지원 데이터 DTO") @RequestBody RequestDto.ApplyDto applyDto) {
        // 사용자 추출
        User applicant = extractUserFromAccessToken(requestAccessTokenInHeader);
        // 작성자일 경우, 지원 불가
        if (projectService.findByUserAndId(applicant, applyDto.getProjectId()) != null) {
            throw new RestApiException(ErrorCode.ACCESS_DENIED);
        }

        // 프로젝트 지원
        Project project = projectService.findById(applyDto.getProjectId());
        if (project == null) {
            throw new RestApiException(ErrorCode.POST_NOT_FOUND);
        }
        applyService.applyToProject(project, applicant, applyDto.getPosition());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    // ===== 비즈니스 로직 ===== //

    // AT로부터 사용자 추출
    public User extractUserFromAccessToken(String requestAccessTokenInHeader) {
        String requestAccessToken = authService.resolveToken(requestAccessTokenInHeader);
        String principal = authService.getPrincipal(requestAccessToken);
        User findUser = userService.findByEmail(principal);
        if (findUser == null) {
            throw new RestApiException(ErrorCode.USER_NOT_FOUND);
        }
        return findUser;
    }

    // 접근한 사용자가 포트폴리오 공개 상태인지 확인
    public void checkPortfolioIsPublic(User accessUser) {
        if (!accessUser.getPortfolioIsPublic()) {
            throw new RestApiException(ErrorCode.CREATING_PROJECT_DENIED);
        }
    }

    // 사용자가 작성한 프로젝트 데이터 가져오기
    public Project findProjectByWriter(User accessUser, Long id) {
        Project findProject = projectService.findByUserAndId(accessUser, id);
        if (findProject == null) {
            throw new RestApiException(ErrorCode.POST_NOT_FOUND);
        }
        return findProject;
    }
}