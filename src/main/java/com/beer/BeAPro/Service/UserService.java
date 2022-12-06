package com.beer.BeAPro.Service;

import com.beer.BeAPro.Domain.*;
import com.beer.BeAPro.Dto.AuthDto;
import com.beer.BeAPro.Dto.OAuth2NaverUserDto;
import com.beer.BeAPro.Dto.RequestDto;
import com.beer.BeAPro.Dto.UserDto;
import com.beer.BeAPro.Exception.ErrorCode;
import com.beer.BeAPro.Exception.RestApiException;
import com.beer.BeAPro.Repository.*;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.beer.BeAPro.Domain.QApply.apply;
import static com.beer.BeAPro.Domain.QProjectMember.*;
import static com.beer.BeAPro.Domain.QProjectPosition.projectPosition;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final JPAQueryFactory jpaQueryFactory;
    private final UserRepository userRepository;
    private final UserInterestKeywordRepository userInterestKeywordRepository;
    private final UserToolRepository userToolRepository;
    private final UserPositionRepository userPositionRepository;
    private final PositionRepository positionRepository;
    private final ProjectMemberRepository projectMemberRepository;

    private final ProjectService projectService;


    // ===== 생성 및 삭제 ===== //
    @Transactional
    public User registerUser(String email) {
        User user = User.registerUserTestOnly(email);
        userRepository.save(user);
        return user;
    }

    @Transactional
    public User registerUserByNaver(OAuth2NaverUserDto oAuth2NaverUserDto) {
        User user = User.registerUserByNaver(oAuth2NaverUserDto);
        userRepository.save(user);
        return user;
    }

    @Transactional
    public void connectNaver(User user, String naverId) {
        user.connectNaver(naverId);
    }

    @Transactional
    public void disconnectNaver(User user) {
        user.disconnectNaver();
    }

    // OAuthType 검사
    public void checkOAuthType(User user, OAuthType oAuthType) {
        if (user.getOAuthType() == oAuthType) { // 회원 가입시 사용한 소셜 로그인 API일 경우
            switch (oAuthType) {
                case NAVER:
//                    if (다른 소셜 로그인 API와 연동되어 있을 경우) {
//                        throw new RestApiException(ErrorCode.CANNOT_DISCONNECT);
//                        break;
//                    }
                default:
                    break;
            }
        }
    }

    // User 탈퇴 처리
    @Transactional
    public void withdrawal(User user) {
        // TODO: 재삭제 불가능
        if (user.getIsWithdrawal()) {
            throw new RestApiException(ErrorCode.USER_NOT_FOUND);
        }

        // 사용자가 지원한 목록이 있을 경우, Apply.user 값을 null로 변환
        jpaQueryFactory
                .update(apply)
                .setNull(apply.user)
                .where(apply.user.eq(user))
                .execute();


        // 사용자가 팀장인 Project, 사용자의 ProjectMember
        List<ProjectMember> projectMembersWhoseUserIsLeader = jpaQueryFactory
                .selectFrom(projectMember)
                .join(projectMember.project)
                .fetchJoin()
                .where(projectMember.user.eq(user),
                        projectMember.teamPosition.eq(TeamPosition.LEADER))
                .fetch();
        // 사용자 ProjectMember 삭제
        projectMemberRepository.deleteAll(projectMembersWhoseUserIsLeader);
        // 사용자가 팀장인 Project 삭제 처리
        for (ProjectMember projectMember : projectMembersWhoseUserIsLeader) {
            projectService.deleteProcessingProject(projectMember.getProject());
        }

        // 사용자가 팀원인 프로젝트 중 지원 마감(구인 완료)된 Project, 그리고 Project의 ProjectMember 및 사용자의 Position 데이터
        List<ProjectMember> projectMembersWhoseUserIsMemberOnClosed = jpaQueryFactory
                .selectFrom(projectMember)
                .join(projectMember.project)
                .fetchJoin()
                .join(projectMember.position) // 팀장의 경우 Position 존재X
                .fetchJoin()
                .where(projectMember.user.eq(user),
                        projectMember.project.isApplyPossible.eq(false))
                .fetch();
        // 사용자 ProjectMember 삭제
        projectMemberRepository.deleteAll(projectMembersWhoseUserIsMemberOnClosed);
        // 삭제되는 ProjectMember의 Position을 가진 팀원이 더이상 없는 경우, Position 칼럼 삭제
        for (ProjectMember projectMember : projectMembersWhoseUserIsMemberOnClosed) {
            if (projectMemberRepository.countByPosition(projectMember.getPosition()) <= 0) {
                positionRepository.delete(projectMember.getPosition());
            }
        }

        // 사용자가 팀원인 프로젝트 중 구인중인 프로젝트의 사용자의 ProjectMember, ProjectPosition 불러오기
        List<Tuple> dataWhoseUserIsMemberDuringRecruiting = jpaQueryFactory
                .select(projectMember, projectPosition)
                .from(projectMember)
                .join(projectPosition) // 팀장의 경우 Position 존재X
                .on(projectPosition.project.eq(projectMember.project), // 사용자가 팀원인 프로젝트
                        projectPosition.position.eq(projectMember.position)) // 사용자의 Position의 ProjectPosition
                .fetchJoin()
                .where(projectMember.user.eq(user),
                        projectMember.project.isApplyPossible.eq(true))
                .fetch();
        // 사용자 ProjectMember 삭제
        List<ProjectMember> toBeDeletedProjectMembers = new ArrayList<>();
        // 현재 모집된 인원 카운트 다운
        for (Tuple data : dataWhoseUserIsMemberDuringRecruiting) {
            toBeDeletedProjectMembers.add(data.get(projectMember));
            Objects.requireNonNull(data.get(projectPosition)).withdrawalDuringRecruiting();
        }
        if (toBeDeletedProjectMembers.size() != 0) {
            projectMemberRepository.deleteAll(toBeDeletedProjectMembers);
        }

        // 탈퇴 처리
        user.setToWithdrawal();
    }

    // User 데이터 삭제
    public void deleteData(List<User> usersToBeDeleted) {
        userRepository.deleteAll(usersToBeDeleted);
    }


    // ===== 조회 ===== //

    public List<User> findUserToInactive() {
        return userRepository.findAll().stream()
                .filter(user -> user.getIsInactive().equals(false))
                .filter(user -> user.getToInactiveDate().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public List<User> findUsersToBeDeleted() {
        return userRepository.findAll().stream()
                .filter(user -> user.getToBeDeletedDate().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
    }


    // ===== 세부 설정 ===== //

    @Transactional
    public void login(User user) { // 로그인
        user.login();
    }

    @Transactional
    public void setInactive(User user) { // 휴면 계정 전환
        user.setInactive();
    }

    @Transactional
    public void setEnable(User user, Boolean bool) { // 비활성화(정지) 여부 결정
        user.setEnable(bool);
    }


    // ===== 비즈니스 로직 ===== //

    // 약관 동의 여부 값 설정
    @Transactional
    public void setTermsAgree(User user, AuthDto.AgreeDto agreeDto) {
        user.setTermsAgree(agreeDto);
    }

    // 추가 정보 저장
    @Transactional
    public void saveUserAdditionalInfo(User user, RequestDto.SignUpAdditionalInfoDto signUpAdditionalInfoDto) {
        // 데이터 가공
        List<Position> positions = signUpAdditionalInfoDto.getUserPositions().stream()
                .map(Position::createPosition) // Position 생성
                .collect(Collectors.toList());
        List<UserPosition> userPositions = positions.stream()
                .map(position -> UserPosition.createUserPosition(user, position)) // UserPosition 생성
                .collect(Collectors.toList());
        List<UserInterestKeyword> userInterestKeywords = signUpAdditionalInfoDto.getUserInterestKeywords().stream()
                .map(keyword -> UserInterestKeyword.createUserInterestKeyword(user, keyword)) // UserInterestKeyword 생성
                .collect(Collectors.toList());
        List<UserTool> userTools = signUpAdditionalInfoDto.getUserTools().stream()
                .map(userTool -> UserTool.createUserTool(user, userTool)) // UserTool 생성
                .collect(Collectors.toList());
        String portfolioLinks = String.join(",", signUpAdditionalInfoDto.getPortfolioLinks());

        // DTO 생성
        UserDto.SignUpAdditionalInfoDto info = UserDto.SignUpAdditionalInfoDto.builder()
                .mobileIsPublic(signUpAdditionalInfoDto.getMobileIsPublic())
                .userPositions(userPositions)
                .userInterestKeywords(userInterestKeywords)
                .userTools(userTools)
                .portfolioIsPublic(signUpAdditionalInfoDto.getPortfolioIsPublic())
                .portfolioLinks(portfolioLinks)
                .build();

        // 저장
        positionRepository.saveAll(positions);
        userPositionRepository.saveAll(userPositions);
        userInterestKeywordRepository.saveAll(userInterestKeywords);
        userToolRepository.saveAll(userTools);
        user.saveUserAdditionalInfo(info);
    }

    // 포트폴리오 파일 업로드
    @Transactional
    public void setPortfolioFile(User user, PortfolioFile portfolioFile) {
        user.setPortfolioFile(portfolioFile);
    }
}
