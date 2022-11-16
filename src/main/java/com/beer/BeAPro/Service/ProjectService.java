package com.beer.BeAPro.Service;

import com.beer.BeAPro.Domain.*;
import com.beer.BeAPro.Dto.ProjectDto;
import com.beer.BeAPro.Dto.RequestDto;
import com.beer.BeAPro.Exception.ErrorCode;
import com.beer.BeAPro.Exception.RestApiException;
import com.beer.BeAPro.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    // ProjectPosition 객체와 연관된 Position 삭제
    @Transactional
    public void deletePosition(Project project) {
        List<ProjectPosition> findProjectPositions = projectPositionRepository.findAllByProject(project);
        if (findProjectPositions != null) {
            findProjectPositions.forEach(findProjectPosition -> positionRepository.delete(findProjectPosition.getPosition()));
        }
    }


    // ===== 조회 ===== //
    public Project findTemporaryProjectByUser(User user) {
        return projectRepository.findByUserAndIsTemporary(user, false).orElse(null);
    }

    public Project findById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    
    // ===== 비즈니스 로직 ===== //
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
}
