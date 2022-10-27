package com.beer.BeAPro.Repository;

import com.beer.BeAPro.Domain.PortfolioFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioFileRepository extends JpaRepository<PortfolioFile, Long> {
}
