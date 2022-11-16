package com.beer.BeAPro.Repository;

import com.beer.BeAPro.Domain.Project;
import com.beer.BeAPro.Domain.ProjectMember;
import com.beer.BeAPro.Domain.TeamPosition;
import com.beer.BeAPro.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findAllByProject(Project project);
    Optional<ProjectMember> findByUserAndProject(User user, Project project);
    Optional<ProjectMember> findByProjectAndTeamPosition(Project project, TeamPosition teamPosition);
}
