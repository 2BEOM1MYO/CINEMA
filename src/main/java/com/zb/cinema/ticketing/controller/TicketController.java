package com.zb.cinema.ticketing.controller;

import com.zb.cinema.payment.model.KakaoPayApprovalVO;
import com.zb.cinema.payment.model.KakaoPayInput;
import com.zb.cinema.payment.model.KakaoPayReadyVO;
import com.zb.cinema.payment.service.KakaoPayService;
import com.zb.cinema.ticketing.entity.Ticket;
import com.zb.cinema.ticketing.model.TicketInput;
import com.zb.cinema.ticketing.service.TicketService;
import java.security.Principal;
import java.time.LocalDateTime;
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
	private TicketInput ticketInput;
	private Ticket saTicket;
	private KakaoPayReadyVO kakaoPayReadyVO;


	@PostMapping("/ticket")
	public KakaoPayReadyVO ticketing(@RequestBody @Valid TicketInput parameter, Principal principal) {
		// 예매 내역 사전 정보 생성
		ticketInput = parameter;
		Ticket ticket = ticketService.readyTicketing(parameter, principal);
		saTicket = ticket;
		// 결제
		kakaoPayReadyVO = kakaoPayService.kakaoPayReadyUrl(parameter, ticket);

		return kakaoPayReadyVO;
	}

	// 예매 내역 저장
	@GetMapping("/ticketingSuccess")
	@ResponseBody
	public KakaoPayApprovalVO paymentSuccess(@RequestParam String pg_token) {

		KakaoPayApprovalVO result = kakaoPayService.kakaoPayApprovalUrl(pg_token, ticketInput, kakaoPayReadyVO);
		ticketService.savePay(result);
		saTicket.setTid(result.getTid());
		ticketService.saveTicket(saTicket);

		return result;
	}

	@GetMapping("/ticketCancel")
	public void ticketCancel(@RequestParam Long ticketId) {
		ticketService.cancelTicket(ticketId);
	}
}
