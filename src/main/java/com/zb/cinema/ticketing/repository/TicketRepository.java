package com.zb.cinema.ticketing.repository;

import com.zb.cinema.ticketing.entity.Ticket;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

	Optional<Ticket> findByMemberId(String memberId);

}
