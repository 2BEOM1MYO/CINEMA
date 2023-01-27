package com.zb.cinema.ticketing.service;

import com.zb.cinema.admin.entity.Auditorium;
import com.zb.cinema.admin.entity.Schedule;
import com.zb.cinema.admin.entity.Seat;
import com.zb.cinema.admin.repository.AuditoriumRepository;
import com.zb.cinema.admin.repository.ScheduleRepository;
import com.zb.cinema.admin.repository.SeatRepository;
import com.zb.cinema.payment.model.KakaoPayApprovalVO;
import com.zb.cinema.payment.service.KakaoPayService;
import com.zb.cinema.ticketing.entity.Ticket;
import com.zb.cinema.ticketing.exception.TicketError;
import com.zb.cinema.ticketing.exception.TicketException;
import com.zb.cinema.ticketing.model.TicketInput;
import com.zb.cinema.ticketing.repository.PaymentRepository;
import com.zb.cinema.ticketing.repository.TicketRepository;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {

	private final TicketRepository ticketRepository;
	private final PaymentRepository paymentRepository;
	private final AuditoriumRepository auditoriumRepository;
	private final ScheduleRepository scheduleRepository;
	private final SeatRepository seatRepository;
	private final KakaoPayService kakaoPayService;


	public Ticket readyTicketing(TicketInput parameter, Principal principal) {
		Optional<Schedule> optionalSchedule = scheduleRepository.findById(parameter.getScheduleId());

		if(optionalSchedule.isEmpty()) {
			throw new TicketException(TicketError.SCHEDULE_NOT_FOUND);
		}
		Schedule schedule = optionalSchedule.get();

		Optional<Auditorium> optionalAuditorium = auditoriumRepository.findById(schedule.getAuditorium().getId());
		if(optionalAuditorium.isEmpty()) {
			throw new TicketException(TicketError.AUDITORIUM_NOT_FOUND);
		}
		Auditorium auditorium = optionalAuditorium.get();

		// 좌석 선점
		Optional<Seat> seatOptional = seatRepository.findBySeatNumAndAuditoriumId(
			parameter.getSeat(), auditorium.getId());
		if(seatOptional.isEmpty()) {
			throw new TicketException(TicketError.SEAT_NOT_FOUND);
		}
		Seat seat = seatOptional.get();
		seat.setUsing(true);
		seatRepository.save(seat);

		// 프론트에서 예매가 진행된 좌석은 선택하지 못하므로 중복 좌석 예매의 경우는 제외
		// 같은 사람이 예매를 몇 번 진행할 수 있다고 가정하고 진행
		return Ticket.builder()
			//.memberId(Long.valueOf(principal.getName()))
			.memberId(12L)
			.movieCode(schedule.getMovie().getCode())
			.theaterId(auditorium.getTheater().getId())
			.auditoriumId(auditorium.getId())
			.status(true)
			.seat(parameter.getSeat())
			.startDt(schedule.getStartDt())
			.endDt(schedule.getEndDt())
			.build();
	}

	public void savePay(KakaoPayApprovalVO parameter) {
		paymentRepository.save(parameter);
	}

	public void saveTicket(Ticket parameter) {

		parameter.setBookDt(LocalDateTime.now());

		ticketRepository.save(parameter);

	}

	public void cancelTicket(Long ticketId) {
		Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
		if(optionalTicket.isEmpty()) {
			throw new TicketException(TicketError.TICKET_NOT_FOUND);
		}

		// 티켓 취소
		Ticket ticket = optionalTicket.get();
		ticket.setStatus(false);
		ticketRepository.save(ticket);

		// 결제 취소


		// 좌석 풀기
		Optional<Seat> seatOptional = seatRepository.findBySeatNumAndAuditoriumId(
			ticket.getSeat(), ticket.getAuditoriumId());
		if(seatOptional.isEmpty()) {
			throw new TicketException(TicketError.SEAT_NOT_FOUND);
		}
		Seat seat = seatOptional.get();
		seat.setUsing(false);
		seatRepository.save(seat);
	}

}
