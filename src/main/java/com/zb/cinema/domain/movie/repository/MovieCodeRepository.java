package com.zb.cinema.domain.movie.repository;

import com.zb.cinema.domain.movie.entity.MovieCode;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieCodeRepository extends JpaRepository<MovieCode, Long> {

    List<MovieCode> findByTitleContaining(String title);
    Optional<MovieCode> findByTitle(String title);

    MovieCode findByCode(Long movieCode);
}
