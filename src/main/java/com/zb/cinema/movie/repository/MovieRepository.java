package com.zb.cinema.movie.repository;

import com.zb.cinema.movie.entity.Movie;
import com.zb.cinema.movie.type.MovieStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findAllByTitleContaining(String movieNm);

    List<Movie> findAllByGenreContaining(String genre);

    List<Movie> findAllByDirectorsContaining(String director);

    List<Movie> findAllByActorsContaining(String actor);

    List<Movie> findAllByStatus(MovieStatus status);
}