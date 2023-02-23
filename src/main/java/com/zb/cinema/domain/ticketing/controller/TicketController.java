package com.zb.cinema.domain.ticketing.controller;

import com.zb.cinema.domain.member.service.MemberService;
import com.zb.cinema.domain.payment.entity.KakaoPayApproval;
import com.zb.cinema.domain.payment.entity.KakaoPayCancel;
import com.zb.cinema.domain.payment.model.KakaoPayReady;
import com.zb.cinema.domain.payment.service.KakaoPayService;
import com.zb.cinema.domain.ticketing.entity.Ticket;
import com.zb.cinema.domain.ticketing.model.TicketInput;
import com.zb.cinema.domain.ticketing.service.TicketService;
import com.zb.cinema.global.jwt.TokenProvider;
import com.zb.cinema.waiting.service.WatingService;
import io.swagger.annotations.Api;
import java.util.HashMap;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Ticketing-Api")
@RestController
@RequestMapping("/ticketing")
@RequiredArgsConstructor
public class TicketController {

	private final TicketService ticketService;

	private final KakaoPayService kakaoPayService;
	// DB에 저장 해버리기
	// pg_token으로 추적할 수 있을 것, 이것은 결제 전에 추적
	// tid는 결제 후로 추적하는것
	private KakaoPayReady kakaoPayReady;
	private final TokenProvider tokenProvider;
	private static HashMap<String, TicketInput> inputMap;
	private static HashMap<String, Ticket> ticketMap;
	private final WatingService watingService;
	private final MemberService memberService;

	// 1. 대기열 큐에 고객 Insert
	// 2. Batch에서 일정 시간마다 작업업열로 이동 가능한 Capability 확인
	// 3. 가능한 Capability 있으면 대기열에서 작업열로 이동
	// 4. 작업열에 유효한 Key 값인지 확인
	// 5. 결제 후 작업열에서 삭제
	@GetMapping("readyTicketing")
	public void readyTicketing(@RequestParam String token) {
		// key를 토큰으로 쓰는것이 맞을까
		watingService.addWatingQueue(token, memberService.getName(token));
	}


	@PostMapping("/ticket")
	public KakaoPayReady ticketing(@RequestBody @Valid TicketInput parameter) {
		// 예매 내역 사전 정보 생성
		String email = tokenProvider.getUserPk(parameter.getToken());

		inputMap.put(email, parameter);
		Ticket ticket = ticketService.readyTicketing(parameter);
		ticketMap.put(email, ticket);

		// 결제
		kakaoPayReady = kakaoPayService.kakaoPayReadyUrl(parameter, ticket);

		return kakaoPayReady;
	}

	// 예매 내역 저장
	@GetMapping("/ticketingSuccess")
	@ResponseBody
	public KakaoPayApproval paymentSuccess(@RequestParam String pg_token, @RequestParam String token) {
		String email = tokenProvider.getUserPk(token);
		TicketInput ticketInput = inputMap.get(email);

		KakaoPayApproval result = kakaoPayService.kakaoPayApprovalUrl(pg_token, ticketInput,
			kakaoPayReady);
		ticketService.savePay(result);

		Ticket ticket = ticketMap.get(email);
		ticket.setTid(result.getTid());
		ticketService.saveTicket(ticket);

		// return "redirect:ticketResult";
		return result;
	}


	@GetMapping("/ticketCancel")
	public KakaoPayCancel ticketCancel(@RequestParam Long ticketId) {
		return ticketService.cancelTicket(ticketId);
	}


}
