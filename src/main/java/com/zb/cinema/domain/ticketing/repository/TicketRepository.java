package com.zb.cinema.domain.ticketing.repository;

import com.zb.cinema.domain.ticketing.entity.Ticket;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

	Optional<Ticket> findByMemberId(String memberId);

	Optional<Ticket> findByTid(String tid);

}
