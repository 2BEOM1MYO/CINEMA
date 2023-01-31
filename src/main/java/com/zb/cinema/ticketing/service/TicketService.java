package com.zb.cinema.ticketing.service;

import com.zb.cinema.admin.entity.Auditorium;
import com.zb.cinema.admin.entity.Schedule;
import com.zb.cinema.admin.entity.Seat;
import com.zb.cinema.admin.repository.AuditoriumRepository;
import com.zb.cinema.admin.repository.ScheduleRepository;
import com.zb.cinema.admin.repository.SeatRepository;
import com.zb.cinema.payment.entity.KakaoPayApproval;
import com.zb.cinema.payment.entity.KakaoPayCancel;
import com.zb.cinema.payment.exception.PayError;
import com.zb.cinema.payment.exception.PayException;
import com.zb.cinema.payment.model.KakaoPayCancelInput;
import com.zb.cinema.payment.repository.KakaoPayCancelRepository;
import com.zb.cinema.payment.service.KakaoPayService;
import com.zb.cinema.ticketing.entity.Ticket;
import com.zb.cinema.ticketing.exception.TicketError;
import com.zb.cinema.ticketing.exception.TicketException;
import com.zb.cinema.ticketing.model.TicketInput;
import com.zb.cinema.payment.repository.PaymentRepository;
import com.zb.cinema.ticketing.repository.TicketRepository;
import java.security.Principal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {

	private final TicketRepository ticketRepository;
	private final PaymentRepository paymentRepository;
	private final AuditoriumRepository auditoriumRepository;
	private final ScheduleRepository scheduleRepository;
	private final SeatRepository seatRepository;
	private final KakaoPayCancelRepository kakaoPayCancelRepository;
	private final KakaoPayService kakaoPayService;


	public Ticket readyTicketing(TicketInput parameter, Principal principal) {

		Schedule schedule = scheduleRepository.findById(parameter.getScheduleId()).orElseThrow(
			() -> new TicketException(TicketError.SCHEDULE_NOT_FOUND));

		Auditorium auditorium = auditoriumRepository.findById(schedule.getAuditorium().getId())
			.orElseThrow(() -> new TicketException(TicketError.AUDITORIUM_NOT_FOUND));


		// 좌석 선점
		Seat seat = seatRepository.findBySeatNumAndAuditoriumId(
			parameter.getSeat(), auditorium.getId()).orElseThrow(() -> new TicketException(TicketError.SEAT_NOT_FOUND));
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

	public void savePay(KakaoPayApproval parameter) {
		paymentRepository.save(parameter);
	}

	public void saveTicket(Ticket parameter) {

		parameter.setBookDt(LocalDateTime.now());

		ticketRepository.save(parameter);

	}

	public KakaoPayCancel cancelTicket(Long ticketId) {

		Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new TicketException(TicketError.TICKET_NOT_FOUND));

		// 이미 취소된 티켓이면?
		if (ticket.isStatus()) {
			ticket.setStatus(false);
			ticketRepository.save(ticket);
		} else {
			throw new TicketException(TicketError.TICKET_ALREADY_CANCEL);
		}

		// 결제 취소, 취소 정보 저장
		KakaoPayApproval kakaoPayApproval = paymentRepository.findByTid(ticket.getTid()).orElseThrow(() -> new PayException(
			PayError.PAY_NOT_FOUND));

		KakaoPayCancel kakaoPayCancel = kakaoPayService.cancelPay(
			KakaoPayCancelInput.builder()
				.cid(kakaoPayApproval.getCid())
				.tid(ticket.getTid())
				.build());

		kakaoPayCancelRepository.save(kakaoPayCancel);

		// 좌석 풀기
		Seat seat = seatRepository.findBySeatNumAndAuditoriumId(
			ticket.getSeat(), ticket.getAuditoriumId()).orElseThrow(() -> new TicketException(TicketError.SEAT_NOT_FOUND));
		seat.setUsing(false);
		seatRepository.save(seat);

		return  kakaoPayCancel;
	}

}
