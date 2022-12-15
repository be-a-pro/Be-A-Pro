package com.beer.BeAPro.Repository;

import com.beer.BeAPro.Domain.User;
import com.beer.BeAPro.Domain.UserInterestKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserInterestKeywordRepository extends JpaRepository<UserInterestKeyword, Long> {
    List<UserInterestKeyword> findAllByUser(User user);
}
