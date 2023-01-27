package com.zb.cinema.ticketing.service;

import com.zb.cinema.admin.entity.Auditorium;
import com.zb.cinema.admin.repository.AuditoriumRepository;
import com.zb.cinema.movie.entity.Movie;
import com.zb.cinema.movie.model.response.ResponseMessage;
import com.zb.cinema.movie.type.ErrorCode;
import com.zb.cinema.payment.model.KakaoPayApprovalVO;
import com.zb.cinema.payment.service.KakaoPayService;
import com.zb.cinema.ticketing.entity.Ticket;
import com.zb.cinema.ticketing.exception.TicketError;
import com.zb.cinema.ticketing.exception.TicketException;
import com.zb.cinema.ticketing.model.TicketInput;
import com.zb.cinema.ticketing.repository.TicketRepository;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {

	private final TicketRepository ticketRepository;
	private final AuditoriumRepository auditoriumRepository;
	private final KakaoPayService kakaoPayService;


	public Ticket readyTicketing(TicketInput parameter, Principal principal) {

		Optional<Auditorium> optionalAuditorium = auditoriumRepository.findById(
			parameter.getAuditoriumId());

		if(optionalAuditorium.isEmpty()) {
			throw new TicketException(TicketError.AUDITORIUM_NOT_FOUND);
		}
		Auditorium auditorium = optionalAuditorium.get();

		// 프론트에서 예매가 진행된 좌석은 선택하지 못하므로 중복 좌석 예매의 경우는 제외
		// 같은 사람이 예매를 몇 번 진행할 수 있다고 가정하고 진행
		return Ticket.builder()
			//.memberId(Long.valueOf(principal.getName()))
			.memberId(12L)
			.movieCode(auditorium.getMovie().getCode())
			.theaterId(auditorium.getTheater().getId())
			.auditoriumId(auditorium.getId())
			.status(true)
			.seat(parameter.getSeat())
			.startDt(auditorium.getStartDt())
			.endDt(auditorium.getEndDt())
			.build();
	}

	public String saveTicket(Ticket parameter) {

		parameter.setBookDt(LocalDateTime.now());

		ticketRepository.save(parameter);

		return "su";
	}

	public void cancelTicket(Long ticketId) {
		Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
		if(optionalTicket.isEmpty()) {
			throw new TicketException(TicketError.TICKET_NOT_FOUND);
		}

		Ticket ticket = optionalTicket.get();
		ticket.setStatus(false);
		ticketRepository.save(ticket);
	}

}
