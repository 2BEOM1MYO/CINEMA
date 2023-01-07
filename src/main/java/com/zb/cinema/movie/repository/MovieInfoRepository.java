package com.zb.cinema.movie.repository;

import com.zb.cinema.movie.entity.MovieInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieInfoRepository extends JpaRepository<MovieInfo, Long> {

    List<MovieInfo> findAllByTitleContaining(String movieNm);

    List<MovieInfo> findAllByGenreContaining(String genre);

    List<MovieInfo> findAllByDirectorsContaining(String director);

    List<MovieInfo> findAllByActorsContaining(String actor);
}
