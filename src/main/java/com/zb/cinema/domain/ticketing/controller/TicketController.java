package com.zb.cinema.domain.ticketing.controller;

import com.zb.cinema.domain.payment.entity.KakaoPayApproval;
import com.zb.cinema.domain.payment.entity.KakaoPayCancel;
import com.zb.cinema.domain.payment.model.KakaoPayReady;
import com.zb.cinema.domain.payment.service.KakaoPayService;
import com.zb.cinema.domain.ticketing.entity.Ticket;
import com.zb.cinema.domain.ticketing.model.TicketInput;
import com.zb.cinema.domain.ticketing.service.TicketService;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ticketing")
@RequiredArgsConstructor
public class TicketController {

	private final TicketService ticketService;

	private final KakaoPayService kakaoPayService;
	// DB에 저장 해버리기
	// pg_token으로 추적할 수 있을 것, 이것은 결제 전에 추적
	// tid는 결제 후로 추적하는것
	private TicketInput ticketInput;
	private Ticket saTicket;
	private KakaoPayReady kakaoPayReady;


	@PostMapping("/ticket")
	public KakaoPayReady ticketing(@RequestBody @Valid TicketInput parameter, Principal principal) {
		// 예매 내역 사전 정보 생성
		ticketInput = parameter;
		Ticket ticket = ticketService.readyTicketing(parameter, principal);
		saTicket = ticket;
		// 결제
		kakaoPayReady = kakaoPayService.kakaoPayReadyUrl(parameter, ticket);

		return kakaoPayReady;
	}

	// 예매 내역 저장
	@GetMapping("/ticketingSuccess")
	@ResponseBody
	public KakaoPayApproval paymentSuccess(@RequestParam String pg_token) {

		KakaoPayApproval result = kakaoPayService.kakaoPayApprovalUrl(pg_token, ticketInput,
			kakaoPayReady);
		ticketService.savePay(result);
		saTicket.setTid(result.getTid());
		ticketService.saveTicket(saTicket);

		return result;
	}

	@GetMapping("/ticketCancel")
	public KakaoPayCancel ticketCancel(@RequestParam Long ticketId) {
		return ticketService.cancelTicket(ticketId);
	}
}
