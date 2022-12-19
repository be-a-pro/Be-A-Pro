package com.beer.BeAPro.Repository;

import com.beer.BeAPro.Domain.Apply;
import com.beer.BeAPro.Domain.Position;
import com.beer.BeAPro.Domain.Project;
import com.beer.BeAPro.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplyRepository extends JpaRepository<Apply, Long> {
    Optional<Apply> findByUserAndProject(User user, Project project);
    Optional<Apply> findOneByProjectAndPosition(Project project, Position position);
}
