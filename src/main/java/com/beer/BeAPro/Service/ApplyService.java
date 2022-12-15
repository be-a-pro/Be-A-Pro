package com.beer.BeAPro.Service;

import com.beer.BeAPro.Domain.*;
import com.beer.BeAPro.Dto.RequestDto;
import com.beer.BeAPro.Exception.ErrorCode;
import com.beer.BeAPro.Exception.RestApiException;
import com.beer.BeAPro.Repository.ApplyRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.beer.BeAPro.Domain.QProjectPosition.projectPosition;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplyService {

    private final ApplyRepository applyRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Transactional
    public void applyToProject(Project project, User user, RequestDto.PositionDto positionDto) {
        // 프로젝트 지원 가능 여부 확인
        if (!project.getIsApplyPossible() || project.getRestorationDate() != null) {
            throw new RestApiException(ErrorCode.CANNOT_AVAILABLE);
        }

        // 중복 지원 여부 확인
        if (applyRepository.findByUserAndProject(user, project).isPresent()) {
            throw new RestApiException(ErrorCode.DUPLICATE_APPLICATION);
        }

        // 지원한 Project의 ProjectPosition, Position 목록
        List<ProjectPosition> projectPositions = jpaQueryFactory
                .select(projectPosition)
                .from(projectPosition)
                .join(projectPosition.position)
                .fetchJoin()
                .where(projectPosition.project.id.eq(project.getId()))
                .fetch();

        Position position = null;
        for (ProjectPosition projectPosition : projectPositions) {
            Position comparePosition = projectPosition.getPosition();
            // 지원한 포지션이 프로젝트에 있는지 확인. 존재하지 않을 경우 예외 발생.
            if (comparePosition.getCategory().equals(positionDto.getCategory())) {
                switch (comparePosition.getCategory()) {
                    case DESIGN:
                        if (!comparePosition.getDesign().equals(positionDto.getDesign()))
                            throw new RestApiException(ErrorCode.NOT_EXIST_POSITION);
                        break;
                    case DEVELOPMENT:
                        if (!comparePosition.getDevelopment().equals(positionDto.getDevelopment()))
                            throw new RestApiException(ErrorCode.NOT_EXIST_POSITION);
                        break;
                    case PLANNING:
                        if (!comparePosition.getPlanning().equals(positionDto.getPlanning()))
                            throw new RestApiException(ErrorCode.NOT_EXIST_POSITION);
                        break;
                    case ETC:
                        if (!comparePosition.getEtc().equals(positionDto.getEtc()))
                            throw new RestApiException(ErrorCode.NOT_EXIST_POSITION);
                        break;
                    default:
                        throw new RestApiException(ErrorCode.NOT_EXIST_POSITION);
                }
                // 포지셔의 구인 마감 상태 확인
                if (!projectPosition.getIsClosing()) {
                    position = comparePosition;
                    break;
                }
                // 구인 마감 또는 지원 마감
                throw new RestApiException(ErrorCode.CANNOT_AVAILABLE);
            }
        }
        Apply apply = Apply.createApply(user, project, position);
        applyRepository.save(apply);
    }

    public Apply findByProjectAndPosition(Project project, Position position) {
        return applyRepository.findOneByProjectAndPosition(project, position).orElse(null);
    }

    @Transactional
    public void deleteApplyByDeletingProject(Project project) {
        List<Apply> allByProject = applyRepository.findAllByProject(project);
        applyRepository.deleteAll(allByProject);
    }
}
