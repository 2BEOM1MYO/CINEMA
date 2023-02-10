package com.zb.cinema.domain.admin.repository;

import com.zb.cinema.domain.admin.entity.Auditorium;
import com.zb.cinema.domain.admin.entity.Theater;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditoriumRepository extends JpaRepository<Auditorium, Long> {
    Optional<Auditorium> findByTheaterAndAndName(Theater theater, String name);
}
