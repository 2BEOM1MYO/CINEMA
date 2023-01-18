package com.zb.cinema.movie.repository;

import com.zb.cinema.movie.entity.MovieCode;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieCodeRepository extends JpaRepository<MovieCode, Long> {

    List<MovieCode> findByTitleContaining(String movieNm);
    Optional<MovieCode> findByTitle(String title);
}
