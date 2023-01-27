package com.zb.cinema.admin.repository;

import com.zb.cinema.admin.entity.Auditorium;
import com.zb.cinema.admin.entity.Schedule;
import com.zb.cinema.admin.entity.Theater;
import com.zb.cinema.movie.entity.Movie;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findAllByStartDtBetweenAndAuditoriumOrEndDtBetweenAndAuditorium(
        LocalDateTime startDt1, LocalDateTime endDt1, Auditorium auditorium1,
        LocalDateTime startDt2, LocalDateTime endDt2, Auditorium auditorium2);

    List<Schedule> findAllByMovie(Movie movie);
    List<Schedule> findAllByMovieAndEndDtAfter(Movie movie, LocalDateTime time);
}
