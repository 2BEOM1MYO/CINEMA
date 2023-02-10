package com.zb.cinema.domain.admin.repository;

import com.zb.cinema.domain.admin.entity.Schedule;
import com.zb.cinema.domain.admin.entity.Seat;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findAllBySchedule(Schedule schedule);

    Optional<Seat> findBySeatNumAndAuditoriumId(String seatNum, Long auditoriumId);
}
