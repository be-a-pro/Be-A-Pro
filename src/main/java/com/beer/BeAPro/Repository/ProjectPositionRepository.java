package com.beer.BeAPro.Repository;

import com.beer.BeAPro.Domain.Project;
import com.beer.BeAPro.Domain.ProjectPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectPositionRepository extends JpaRepository<ProjectPosition, Long> {
    List<ProjectPosition> findAllByProject(Project project);
}
