package com.zb.cinema.admin.repository;

import com.zb.cinema.admin.entity.Auditorium;
import com.zb.cinema.admin.entity.Theater;
import com.zb.cinema.movie.entity.Movie;
import java.time.LocalDateTime;
import java.util.List;
import net.bytebuddy.asm.Advice.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditoriumRepository extends JpaRepository<Auditorium, Long> {

    List<Auditorium> findAllByStartDtBetweenAndEndDtBetweenAndTheater(LocalDateTime startDt1,
        LocalDateTime endDt1, LocalDateTime startDt2, LocalDateTime endDt2, Theater theater);

    List<Auditorium> findAllByStartDtBetweenAndTheaterOrEndDtBetweenAndTheater(
        LocalDateTime startDt1, LocalDateTime endDt1, Theater theater1,
        LocalDateTime startDt2, LocalDateTime endDt2, Theater theater);

    List<Auditorium> findAllByMovie(Movie movie);
}
