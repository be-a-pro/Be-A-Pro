package com.beer.BeAPro.Repository;

import com.beer.BeAPro.Domain.Project;
import com.beer.BeAPro.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByUserAndIsTemporary(User user, Boolean isTemporary);
}
