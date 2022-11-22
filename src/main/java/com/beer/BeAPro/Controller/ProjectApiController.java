package com.beer.BeAPro.Controller;

import com.beer.BeAPro.Domain.Project;
import com.beer.BeAPro.Domain.ProjectImage;
import com.beer.BeAPro.Domain.User;
import com.beer.BeAPro.Dto.FileUploadDto;
import com.beer.BeAPro.Dto.RequestDto;
import com.beer.BeAPro.Dto.ResponseDto;
import com.beer.BeAPro.Exception.ErrorCode;
import com.beer.BeAPro.Exception.RestApiException;
import com.beer.BeAPro.Service.AuthService;
import com.beer.BeAPro.Service.FileUploadService;
import com.beer.BeAPro.Service.ProjectService;
import com.beer.BeAPro.Service.UserService;
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


    // ===== 프로젝트 생성 및 수정 ===== //

    // 프로젝트 객체 생성: 최초의 임시저장 or 임시저장 없이 글 작성 완료
    @PostMapping(value = "/write", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ResponseDto.GetProjectIdDto> createProject(@RequestHeader("Authorization") String requestAccessTokenInHeader,
                                                                     @RequestPart(required = false) MultipartFile projectImage,
                                                                     @RequestPart @Valid RequestDto.ProjectDto projectDto) {
        // 사용자 검증
        User findUser = extractUserFromAccessToken(requestAccessTokenInHeader);
        checkPortfolioIsPublic(findUser);

        // 프로젝트 대표 이미지 생성 및 S3에 업로드
        ProjectImage savedProjectImage = null;
        if (!projectImage.isEmpty()) {
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
        return ResponseEntity.status(HttpStatus.OK).body(getProjectIdDto);
    }

    // 프로젝트 객체 수정: 최초가 아닌 임시저장 or 임시저장 후 글 작성 완료 or 글 수정
    @PutMapping(value = "/write", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ResponseDto.GetProjectIdDto> printProjectBeingCreated(@RequestHeader("Authorization") String requestAccessTokenInHeader,
                                                                                @RequestParam Long id,
                                                                                @RequestPart(required = false) MultipartFile projectImage,
                                                                                @RequestPart @Valid RequestDto.ProjectDto projectDto) {
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
        return ResponseEntity.status(HttpStatus.OK).body(getProjectIdDto);
    }


    // ===== 프로젝트 삭제 ===== //

    // 임시저장된 프로젝트 영구 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteTemporaryProject(@RequestHeader("Authorization") String requestAccessTokenInHeader,
                                                         @RequestParam Long id) {
        // 사용자 검증
        User findUser = extractUserFromAccessToken(requestAccessTokenInHeader);
        Project findProject = findProjectByWriter(findUser, id);
        if (!findProject.getIsTemporary()) { // 임시저장된 프로젝트가 아닐경우
            throw new RestApiException(ErrorCode.METHOD_NOT_ALLOWED);
        }

        projectService.deleteProject(findProject);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 사용자 권한으로 프로젝트 삭제: 삭제 처리(복구 가능)
    @PutMapping("/delete")
    public ResponseEntity<String> deleteProject(@RequestHeader("Authorization") String requestAccessTokenInHeader,
                                                @RequestParam Long id) {
        // 사용자 검증
        User findUser = extractUserFromAccessToken(requestAccessTokenInHeader);
        Project findProject = findProjectByWriter(findUser, id);

        // 삭제 처리
        projectService.deleteProcessingProject(findProject);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    // ===== 프로젝트 조회 ===== //
    // 프로젝트 데이터 가져오기: 임시저장된 글 작성 또는 글 수정시
    @GetMapping("/write")
    public ResponseEntity<ResponseDto.GetProjectDataDto> getTemporaryProject(@RequestHeader("Authorization") String requestAccessTokenInHeader,
                                                                             @RequestParam Long id) {
        // 사용자 검증
        User findUser = extractUserFromAccessToken(requestAccessTokenInHeader);
        checkPortfolioIsPublic(findUser);
        Project findProject = findProjectByWriter(findUser, id);

        // 프로젝트 데이터 가져오기
        ResponseDto.GetProjectDataDto getProjectDataDto = projectService.getProjectData(findProject);
        return ResponseEntity.status(HttpStatus.OK).body(getProjectDataDto);
    }

    // 임시저장된 프로젝트가 있는지 체크
    @GetMapping("/temporary")
    public ResponseEntity<ResponseDto.GetProjectIdDto> checkTemporaryProject(@RequestHeader("Authorization") String requestAccessTokenInHeader) {
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
    @GetMapping
    public ResponseEntity<ResponseDto.GetProjectDetailDto> getProjectDetail(@RequestParam Long id) {
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
    @GetMapping("/list")
    public ResponseEntity<ResponseDto.GetProjectListDto> getProjectList(@RequestParam(value = "id", required = false) Long lastProjectId) {
        // 페이징 처리
        Slice<Project> projects = projectService.defaultPagingProjectList(lastProjectId);

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