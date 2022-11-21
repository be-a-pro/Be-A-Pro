package com.beer.BeAPro.Service;

import com.beer.BeAPro.Domain.*;
import com.beer.BeAPro.Dto.ProjectDto;
import com.beer.BeAPro.Dto.RequestDto;
import com.beer.BeAPro.Dto.ResponseDto;
import com.beer.BeAPro.Exception.ErrorCode;
import com.beer.BeAPro.Exception.RestApiException;
import com.beer.BeAPro.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectPositionRepository projectPositionRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final PositionRepository positionRepository;


    // ===== 생성 및 업데이트 ===== //
    @Transactional
    public Project createProject(User user) {
        // Project 객체 생성(: 연관관계 설정을 위해 객체 생성 후 데이터 저장할 예정)
        ProjectDto.CreateDto createDto = ProjectDto.CreateDto.builder()
                .user(user)
                .build();
        return Project.createProject(createDto);
    }

    // DB에 저장
    @Transactional
    public void saveData(Project project, RequestDto.ProjectDto projectDto, ProjectImage projectImage) {
        // 데이터 가공
        List<ProjectHashtag> projectHashtags = projectDto.getProjectHashtags().stream()
                .map(projectHashtag -> ProjectHashtag.createProjectHashtag(project, projectHashtag))
                .collect(Collectors.toList());
        List<Position> positions = projectDto.getProjectPositions().stream()
                .map(Position::createPosition) // Position 생성
                .collect(Collectors.toList());
        List<Long> currentCountPerPosition = projectDto.getCurrentCountPerPosition();
        List<Long> closingCountPerPosition = projectDto.getClosingCountPerPosition();
        if (closingCountPerPosition.stream().mapToLong(Long::longValue).sum() > 10)
            throw new RestApiException(ErrorCode.BAD_REQUEST);

        List<ProjectPosition> projectPositions = new ArrayList<>();
        for (int i = 0; i < positions.size(); i++) {
            if (currentCountPerPosition.get(i) > closingCountPerPosition.get(i))
                throw new RestApiException(ErrorCode.BAD_REQUEST);

            ProjectPosition projectPosition =
                    ProjectPosition.createProjectPosition
                            (project, positions.get(i), currentCountPerPosition.get(i), closingCountPerPosition.get(i));
            projectPositions.add(projectPosition);
        }
        String usedStacks = String.join(",", projectDto.getUsedStacks());
        String referenceLinks = String.join(",", projectDto.getReferenceLinks());

        // DTO 생성
        ProjectDto.SaveDataDto saveDataDto = ProjectDto.SaveDataDto.builder()
                .title(projectDto.getTitle())
                .projectImage(projectImage)
                .projectHashtags(projectHashtags)
                .kakaoLink(projectDto.getKakaoLink())
                .info(projectDto.getInfo())
                .freeInfo(projectDto.getFreeInfo())
                .progressMethod(projectDto.getProgressMethod())
                .usedStacks(usedStacks)
                .referenceLinks(referenceLinks)
                .projectPositions(projectPositions)
                .isTemporary(projectDto.getIsTemporary())
                .build();

        // 저장
        positionRepository.saveAll(positions);
        Project savedProject = project.saveData(saveDataDto);
        projectRepository.save(savedProject);
    }

    // 업데이트
    @Transactional
    public Project update(Project project, RequestDto.ProjectDto projectDto, ProjectImage projectImage) {
        // 연관 Position 객체 삭제
        deletePosition(project);

        // 데이터 가공
        List<ProjectHashtag> projectHashtags = projectDto.getProjectHashtags().stream()
                .map(projectHashtag -> ProjectHashtag.createProjectHashtag(project, projectHashtag))
                .collect(Collectors.toList());
        List<Position> positions = projectDto.getProjectPositions().stream()
                .map(Position::createPosition) // Position 생성
                .collect(Collectors.toList());
        List<Long> currentCountPerPosition = projectDto.getCurrentCountPerPosition();
        List<Long> closingCountPerPosition = projectDto.getClosingCountPerPosition();
        if (closingCountPerPosition.stream().mapToLong(Long::longValue).sum() > 10)
            throw new RestApiException(ErrorCode.BAD_REQUEST);

        List<ProjectPosition> projectPositions = new ArrayList<>();
        for (int i = 0; i < positions.size(); i++) {
            if (currentCountPerPosition.get(i) > closingCountPerPosition.get(i))
                throw new RestApiException(ErrorCode.BAD_REQUEST);

            ProjectPosition projectPosition =
                    ProjectPosition.createProjectPosition
                            (project, positions.get(i), currentCountPerPosition.get(i), closingCountPerPosition.get(i));
            projectPositions.add(projectPosition);
        }
        String usedStacks = String.join(",", projectDto.getUsedStacks());
        String referenceLinks = String.join(",", projectDto.getReferenceLinks());

        // DTO 생성
        ProjectDto.SaveDataDto saveDataDto = ProjectDto.SaveDataDto.builder()
                .title(projectDto.getTitle())
                .projectImage(projectImage)
                .projectHashtags(projectHashtags)
                .kakaoLink(projectDto.getKakaoLink())
                .info(projectDto.getInfo())
                .freeInfo(projectDto.getFreeInfo())
                .progressMethod(projectDto.getProgressMethod())
                .usedStacks(usedStacks)
                .referenceLinks(referenceLinks)
                .projectPositions(projectPositions)
                .isTemporary(projectDto.getIsTemporary())
                .build();

        // 저장
        positionRepository.saveAll(positions);
        return project.update(saveDataDto);
    }


    // ===== 삭제 ===== //
    // 프로젝트 객체 삭제(영구 삭제)
    @Transactional
    public void deleteProject(Project project) {
        // ProjectMember 삭제
        List<ProjectMember> findProjectMembers = projectMemberRepository.findAllByProject(project);
        projectMemberRepository.deleteAll(findProjectMembers);
        // Position 삭제
        deletePosition(project);
        // Project 삭제
        projectRepository.delete(project);
    }

    // 프로젝트 삭제 처리(USER 권한으로 삭제할 경우)
    @Transactional
    public void deleteProcessingProject(Project project) {
        // 팀장 제외 팀원이 있는지
        if (projectMemberRepository.findByProjectAndTeamPosition(project, TeamPosition.MEMBER).isPresent()) {
            project.setRestorationDate(true); // 복구 가능 기간 가짐
        } else {
            deleteProject(project); // 영구 삭제
        }
    }

    // 삭제된 프로젝트 복구(삭제 예정 취소)
    @Transactional
    public void restorationProject(Project project) {
        project.setRestorationDate(false);
    }

    // ProjectPosition 객체와 연관된 Position 삭제
    @Transactional
    public void deletePosition(Project project) {
        List<ProjectPosition> findProjectPositions = projectPositionRepository.findAllByProject(project);
        if (findProjectPositions != null) {
            findProjectPositions.forEach(findProjectPosition -> positionRepository.delete(findProjectPosition.getPosition()));
        }
    }


    // ===== 조회 ===== //

    public Project findById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    public Project findTemporaryProjectByUser(User user) {
        return projectRepository.findByUserAndIsTemporary(user, true).orElse(null);
    }

    public Project findByUserAndId(User user, Long id) {
        return projectRepository.findByUserAndId(user, id).orElse(null);
    }

    // DB에서 삭제할 프로젝트 목록
    public List<Project> findProjectToDelete() {
        return projectRepository.findAll().stream()
                .filter(project -> project.getRestorationDate().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
    }


    // ===== 비즈니스 로직 ===== //

    // 프로젝트 데이터 불러오기
    public ResponseDto.GetProjectDataDto getProjectData(Project project) {
        // 삭제 처리된 프로젝트일 경우
        if(project.getRestorationDate() != null)
            throw new RestApiException(ErrorCode.PROJECT_AWAITING_DELETION);

        // 데이터 가공
        List<String> projectHashtags = project.getProjectHashtags().stream()
                .map(ProjectHashtag::getHashtag)
                .collect(Collectors.toList());
        List<ResponseDto.PositionDto> projectPositions = new ArrayList<>();
        List<Long> currentCountPerPosition = new ArrayList<>();
        List<Long> closingCountPerPosition = new ArrayList<>();
        for (ProjectPosition projectPosition : project.getProjectPositions()) {
            Position position = projectPosition.getPosition();
            projectPositions.add(ResponseDto.PositionDto.builder()
                    .category(position.getCategory())
                    .development(position.getDevelopment())
                    .design(position.getDesign())
                    .planning(position.getPlanning())
                    .etc(position.getEtc())
                    .build());
            currentCountPerPosition.add(projectPosition.getCurrentCount());
            closingCountPerPosition.add(projectPosition.getClosingCount());
        }
        List<String> usedStacks = Arrays.stream(project.getUsedStacks().split(","))
                .collect(Collectors.toList());
        List<String> referenceLinks = Arrays.stream(project.getReferenceLinks().split(","))
                .collect(Collectors.toList());
        ResponseDto.ImageDto projectImage = null;
        if (project.getProjectImage()!=null) {
            projectImage = ResponseDto.ImageDto.builder()
                    .filepath(project.getProjectImage().getFilepath())
                    .originalName(project.getProjectImage().getOriginalName())
                    .build();
        }

        return ResponseDto.GetProjectDataDto.builder()
                .title(project.getTitle())
                .projectImage(projectImage)
                .projectHashtags(projectHashtags)
                .kakaoLink(project.getKakaoLink())
                .info(project.getInfo())
                .freeInfo(project.getFreeInfo())
                .progressMethod(project.getProgressMethod())
                .usedStacks(usedStacks)
                .referenceLinks(referenceLinks)
                .projectPositions(projectPositions)
                .currentCountPerPosition(currentCountPerPosition)
                .closingCountPerPosition(closingCountPerPosition)
                .build();
    }

    // 프로젝트 목록 페이지에서 보일 프로젝트 데이터 불러오기
    public ResponseDto.ProjectDataOfProjectListDto getProjectDataOfProjectList(Project project) {
        // 삭제 처리된 프로젝트일 경우
        if(project.getRestorationDate() != null)
            throw new RestApiException(ErrorCode.PROJECT_AWAITING_DELETION);

        // 데이터 가공
        List<ResponseDto.PositionDto> projectPositions = new ArrayList<>();
        List<Long> currentCountPerPosition = new ArrayList<>();
        List<Long> closingCountPerPosition = new ArrayList<>();
        for (ProjectPosition projectPosition : project.getProjectPositions()) {
            Position position = projectPosition.getPosition();
            projectPositions.add(ResponseDto.PositionDto.builder()
                    .category(position.getCategory())
                    .development(position.getDevelopment())
                    .design(position.getDesign())
                    .planning(position.getPlanning())
                    .etc(position.getEtc())
                    .build());
            currentCountPerPosition.add(projectPosition.getCurrentCount());
            closingCountPerPosition.add(projectPosition.getClosingCount());
        }
        ResponseDto.ImageDto projectImage = null;
        if (project.getProjectImage()!=null) {
            projectImage = ResponseDto.ImageDto.builder()
                    .filepath(project.getProjectImage().getFilepath())
                    .originalName(project.getProjectImage().getOriginalName())
                    .build();
        }

        return ResponseDto.ProjectDataOfProjectListDto.builder()
                .id(project.getId())
                .title(project.getTitle())
                .projectImage(projectImage)
                .projectPositions(projectPositions)
                .currentCountPerPosition(currentCountPerPosition)
                .closingCountPerPosition(closingCountPerPosition)
                .build();
    }

    public ResponseDto.GetProjectDetailDto getProjectDetail(Project project) {
        // GetProjectDataDto 생성
        ResponseDto.GetProjectDataDto projectData = getProjectData(project);
        // ProjectWriterDto 생성
        User projectWriter = project.getUser();
        ResponseDto.ProjectWriterDto projectWriterDto = null; // 사용자가 탈퇴했을 경우 null
        if (projectWriter != null) {
            ResponseDto.ImageDto writerProfileImage = null; // 사용자 프로필 이미지가 없을 경우 null
            if (projectWriter.getProfileImage() != null) {
                writerProfileImage = ResponseDto.ImageDto.builder()
                        .originalName(projectWriter.getProfileImage().getOriginalName())
                        .filepath(projectWriter.getProfileImage().getFilepath())
                        .build();
            }
            projectWriterDto = ResponseDto.ProjectWriterDto.builder()
                    .name(projectWriter.getName())
                    .email(projectWriter.getEmail())
                    .profileImage(writerProfileImage)
                    .build();
        }
        // 생성 날짜 리포맷
        String createDateTime = project.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return ResponseDto.GetProjectDetailDto.builder()
                .project(projectData)
                .user(projectWriterDto)
                .createdDateTime(createDateTime)
                .views(project.getViews())
                .isApplyPossible(project.getIsApplyPossible())
                .build();
    }

    // 프로젝트 목록 페이지에서 보일 전체 데이터 불러오기
    public ResponseDto.GetProjectListDto getProjectList(Project project) {
        // GetProjectDataOfListDto 생성
        ResponseDto.ProjectDataOfProjectListDto projectSimple = getProjectDataOfProjectList(project);
        // ProjectWriterDto 생성
        User projectWriter = project.getUser();
        ResponseDto.ProjectWriterDto projectWriterDto = null; // 사용자가 탈퇴했을 경우 null
        if (projectWriter != null) {
            ResponseDto.ImageDto writerProfileImage = null; // 사용자 프로필 이미지가 없을 경우 null
            if (projectWriter.getProfileImage() != null) {
                writerProfileImage = ResponseDto.ImageDto.builder()
                        .originalName(projectWriter.getProfileImage().getOriginalName())
                        .filepath(projectWriter.getProfileImage().getFilepath())
                        .build();
            }
            projectWriterDto = ResponseDto.ProjectWriterDto.builder()
                    .name(projectWriter.getName())
                    .email(projectWriter.getEmail())
                    .profileImage(writerProfileImage)
                    .build();
        }
        // 생성 날짜 리포맷
        String createDateTime = project.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return ResponseDto.GetProjectListDto.builder()
                .project(projectSimple)
                .user(projectWriterDto)
                .createdDateTime(createDateTime)
                .views(project.getViews())
                .isApplyPossible(project.getIsApplyPossible())
                .build();
    }

    // 작성자(팀장)의 ProjectMember 객체 생성
    @Transactional
    public void createProjectLeader(User writer, Project project) {
        ProjectMember projectMember = ProjectMember.createProjectMember(writer, project, null, TeamPosition.LEADER);
        // 이미 팀장이 있는 프로젝트일 경우
        if (projectMemberRepository.findByProjectAndTeamPosition(project, TeamPosition.LEADER).isPresent()) {
            throw new RestApiException(ErrorCode.LEADER_ALREADY_EXISTS);
        }
        // 이미 프로젝트의 팀원일 경우
        if (projectMemberRepository.findByUserAndProject(writer, project).isPresent()) {
            throw new RestApiException(ErrorCode.CONFLICT_REQUEST);
        }
        projectMemberRepository.save(projectMember);
    }

    // 조회수 증가
    @Transactional
    public void increaseViews(Project project) {
        project.increaseViews();
    }
}
