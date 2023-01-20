package com.zb.cinema.admin.repository;

import com.zb.cinema.admin.entity.Auditorium;
import com.zb.cinema.admin.entity.Seat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findAllByAuditorium(Auditorium auditorium);
}
