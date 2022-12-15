package com.beer.BeAPro.Service;

import com.beer.BeAPro.Domain.*;
import com.beer.BeAPro.Dto.ProjectDto;
import com.beer.BeAPro.Dto.RequestDto;
import com.beer.BeAPro.Dto.ResponseDto;
import com.beer.BeAPro.Exception.ErrorCode;
import com.beer.BeAPro.Exception.RestApiException;
import com.beer.BeAPro.Repository.*;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.beer.BeAPro.Domain.QProfileImage.profileImage;
import static com.beer.BeAPro.Domain.QProject.*;
import static com.beer.BeAPro.Domain.QProjectPosition.projectPosition;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final JPAQueryFactory jpaQueryFactory;
    private final ProjectRepository projectRepository;
    private final ProjectPositionRepository projectPositionRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final PositionRepository positionRepository;

    private final ApplyService applyService;


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
        List<Long> closingCountPerPosition = projectDto.getClosingCountPerPosition();
        if (closingCountPerPosition.stream().mapToLong(Long::longValue).sum() > 10)
            throw new RestApiException(ErrorCode.BAD_REQUEST);

        List<ProjectPosition> projectPositions = new ArrayList<>();
        for (int i = 0; i < positions.size(); i++) {
            ProjectPosition projectPosition =
                    ProjectPosition.createProjectPosition
                            (project, positions.get(i), closingCountPerPosition.get(i));
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
        List<Long> closingCountPerPosition = projectDto.getClosingCountPerPosition();
        if (closingCountPerPosition.stream().mapToLong(Long::longValue).sum() > 10)
            throw new RestApiException(ErrorCode.BAD_REQUEST);

        List<ProjectPosition> projectPositions = new ArrayList<>();
        for (int i = 0; i < positions.size(); i++) {
            ProjectPosition projectPosition =
                    ProjectPosition.createProjectPosition
                            (project, positions.get(i), closingCountPerPosition.get(i));
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
        // Apply 삭제
        applyService.deleteApplyByDeletingProject(project);

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
        // 이미 삭제 처리된 프로젝트일 경우
        if (project.getRestorationDate() != null) {
            throw new RestApiException(ErrorCode.CONFLICT_REQUEST);
        }

        // 팀장 ProjectMember 삭제
        projectMemberRepository.findByProjectAndTeamPosition(project, TeamPosition.LEADER)
                .ifPresent(projectMemberRepository::delete);

        // 팀장 제외 팀원이 있는지
        if (projectMemberRepository.findByProjectAndTeamPosition(project, TeamPosition.MEMBER).isPresent()) {
            project.setDeleteProcessing(); // 복구 가능 기간 설정
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
    public ResponseDto.GetProjectDataDto getProjectData(Project project,
                                                        boolean update) { // 지원자가 있는지 확인해야할 때 true
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
        List<Boolean> isApplicants = new ArrayList<>();
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
            if (update) {
                Apply apply = applyService.findByProjectAndPosition(project, position);
                if (apply != null) {
                    isApplicants.add(true);
                } else {
                    isApplicants.add(false);
                }
            }
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

        if (!update) {
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
        } else {
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
                    .isApplicants(isApplicants)
                    .build();
        }
    }

    // 프로젝트 목록 페이지에서 보일 프로젝트 데이터 불러오기
    public ResponseDto.ProjectDataOfProjectListDto getProjectDataOfProjectList(Project project) {
        // 삭제 처리된 프로젝트일 경우
        if(project.getRestorationDate() != null)
            throw new RestApiException(ErrorCode.PROJECT_AWAITING_DELETION);

        // 데이터 요청 쿼리
        List<Tuple> contents = jpaQueryFactory
                .select(projectPosition, projectPosition.position)
                .from(projectPosition)
                .where(projectPosition.project.id.eq(project.getId()))
                .fetch();

        // 데이터 가공
        List<ResponseDto.PositionDto> projectPositions = new ArrayList<>();
        List<Long> currentCountPerPosition = new ArrayList<>();
        List<Long> closingCountPerPosition = new ArrayList<>();
        for (Tuple content : contents) {
            // Position
            Position position = content.get(projectPosition.position);
            assert position != null;
            projectPositions.add(ResponseDto.PositionDto.builder()
                    .category(position.getCategory())
                    .development(position.getDevelopment())
                    .design(position.getDesign())
                    .planning(position.getPlanning())
                    .etc(position.getEtc())
                    .build());
            ProjectPosition projectPosition = content.get(QProjectPosition.projectPosition);
            assert projectPosition != null;
            currentCountPerPosition.add(projectPosition.getCurrentCount());
            closingCountPerPosition.add(projectPosition.getClosingCount());
        }
        // ProjectImage
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
        ResponseDto.GetProjectDataDto projectData = getProjectData(project, false);
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

    // 프로젝트 목록 페이지에서 보일 작성자 데이터 불러오기
    public ResponseDto.ProjectWriterDto getUserDataOfProjectList(User projectWriter) {
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
        return projectWriterDto;
    }

    // 프로젝트 목록 페이지에서 보일 전체 데이터 불러오기
    public ResponseDto.TotalDataOfProjectListDto getTotalDataOfProjectList(Project project) {
        // GetProjectDataOfListDto 생성
        ResponseDto.ProjectDataOfProjectListDto projectDataOfProjectListDto = getProjectDataOfProjectList(project);
        // ProjectWriterDto 생성
        ResponseDto.ProjectWriterDto projectWriterDto = getUserDataOfProjectList(project.getUser());

        // 생성 날짜 리포맷
        String createDateTime = project.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return ResponseDto.TotalDataOfProjectListDto.builder()
                .project(projectDataOfProjectListDto)
                .user(projectWriterDto)
                .createdDateTime(createDateTime)
                .views(project.getViews())
                .isApplyPossible(project.getIsApplyPossible())
                .build();
    }
    
    // 프로젝트 목록 페이지 페이징
    public Slice<Project> pagingProjectList(Long lastId, boolean sortByView, boolean isRecruitmentCompletionExcluded, Category category) {
        // Page 사이즈 설정
        Pageable pageable = setPageSize(lastId);

        // 프로젝트 페이징
        JPAQuery<Project> query = jpaQueryFactory
                .selectFrom(project)
                // 프로젝트 대표 이미지
                .leftJoin(project.projectImage)
                .fetchJoin()
                // 작성자
                .join(project.user)
                .fetchJoin()
                // 작성자 프로필 이미지
                .leftJoin(profileImage)
                .on(profileImage.id.eq(project.user.profileImage.id))
                .fetchJoin()
                .where(
                        // no-offset 페이징 처리
                        pagingByLastId(lastId),

                        // 공통 조건
                        project.isTemporary.eq(false), // 임시저장된 프로젝트 제외
                        project.restorationDate.isNull(), // 삭제 예정된 프로젝트 제외

                        // 모집 완료 필터링
                        filterRecruitmentCompletion(isRecruitmentCompletionExcluded), // true = 모집 완료된 프로젝트 제외

                        // 모집 포지션 필터링
                        filterProjectPosition(category)
                );
        // 정렬: 조회순
        if (sortByView) {
            query = query.orderBy(project.views.desc());
        }
        List<Project> contents = query
                .orderBy(project.createdDate.desc()) // 정렬: 최신순(default)
                .orderBy(project.id.desc()) // 생성 날짜가 같을 경우
                .limit(pageable.getPageSize() + 1) // 뒤에 페이지가 더 있는지 확인
                .fetch();

        // 무한 스크롤 처리
        boolean hasNext = false;
        // 조회한 결과 개수가 요청한 페이지 사이즈보다 크면, 뒤에 데이터가 더 있음을 의미
        if (contents.size() > pageable.getPageSize()) {
            contents.remove(pageable.getPageSize()); // 확인용으로 추가한 데이터 삭제
            hasNext = true; // 다음 페이지 존재
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }

    // Page 사이즈 설정
    public Pageable setPageSize(Long lastId) {
        if (lastId == null) { // 처음 조회할 경우
            return PageRequest.ofSize(18);
        }
        return PageRequest.ofSize(9);
    }

    // 마지막 조회 id를 기준으로 no-offset 페이징 처리
    public BooleanExpression pagingByLastId(Long lastId) {
        if (lastId == null) { // 처음 조회할 경우
            return null;
        }
        return project.id.lt(lastId);
    }

    // 모집이 완료된 프로젝트 제외 필터
    public BooleanExpression filterRecruitmentCompletion(Boolean isExcluded) {
        if (!isExcluded) { // 제외X, 모집 완료된 프로젝트 포함(default)
            return null;
        }
        return project.isApplyPossible.isTrue(); // 모집 중인 프로젝트만
    }

    // 모집 포지션별 필터
    public BooleanExpression filterProjectPosition(Category category) {
        if (category == null) { // 전체(default)
            return null;
        }
        return project.projectPositions.any().position.category.eq(category);
    }

    // Index 화면에서 보일 NEW 프로젝트 목록(최신순, 카테고리 필터링)
    public List<Project> pagingProjectListInIndex(Category category) {
        // 프로젝트 페이징
        return jpaQueryFactory
                .selectFrom(project)
                // 프로젝트 대표 이미지
                .leftJoin(project.projectImage)
                .fetchJoin()
                // 작성자
                .join(project.user)
                .fetchJoin()
                // 작성자 프로필 이미지
                .leftJoin(profileImage)
                .on(profileImage.id.eq(project.user.profileImage.id))
                .fetchJoin()
                .where(
                        // 공통 조건
                        project.isTemporary.eq(false), // 임시저장된 프로젝트 제외
                        project.restorationDate.isNull(), // 삭제 예정된 프로젝트 제외

                        // 모집 포지션 필터링
                        filterProjectPosition(category)
                )
                .orderBy(project.createdDate.desc()) // 최신순 정렬
                .orderBy(project.id.desc()) // 생성 날짜가 같을 경우
                .limit(9) // 가져올 개수
                .fetch();
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
        if (isProjectMember(writer, project)) {
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
