package com.zb.cinema.admin.repository;

import com.zb.cinema.admin.entity.Auditorium;
import com.zb.cinema.admin.entity.Schedule;
import com.zb.cinema.admin.entity.Theater;
import com.zb.cinema.movie.entity.Movie;
import io.netty.util.internal.ObjectPool;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditoriumRepository extends JpaRepository<Auditorium, Long> {
    Optional<Auditorium> findByTheaterAndAndName(Theater theater, String name);
}
