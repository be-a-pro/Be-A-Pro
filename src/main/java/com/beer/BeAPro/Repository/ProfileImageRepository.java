package com.beer.BeAPro.Repository;

import com.beer.BeAPro.Domain.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImageRepository  extends JpaRepository<ProfileImage, Long> {
}
