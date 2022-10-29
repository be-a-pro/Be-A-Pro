package com.beer.BeAPro.Repository;

import com.beer.BeAPro.Domain.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
}
