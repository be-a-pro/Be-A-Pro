package com.beer.BeAPro.Repository;

import com.beer.BeAPro.Domain.User;
import com.beer.BeAPro.Domain.UserTool;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserToolRepository extends JpaRepository<UserTool, Long> {
    List<UserTool> findAllByUser(User user);
}
