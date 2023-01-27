package com.zb.cinema.payment.controller;

import com.zb.cinema.payment.model.KakaoPayApprovalVO;
import com.zb.cinema.payment.model.KakaoPayInput;
import com.zb.cinema.payment.model.KakaoPayReadyVO;
import com.zb.cinema.payment.service.KakaoPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pay")
@RequiredArgsConstructor
public class PayController {
	private final KakaoPayService kakaoPayService;
	private KakaoPayInput kakaoPayInput;
	private KakaoPayReadyVO kakaoPayReadyVO;

	/*
	@PostMapping("/kakaopay")
	public KakaoPayReadyVO getKakaoPayUrl(@RequestBody KakaoPayInput parameter) {
		kakaoPayInput = parameter;
		kakaoPayReadyVO = kakaoPayService.kakaoPayReadyUrl(parameter);

		return kakaoPayReadyVO;
	}

	@GetMapping("/kakaoPaySuccess")
	@ResponseBody
	public KakaoPayApprovalVO paymentSuccess(@RequestParam String pg_token) {
		return kakaoPayService.kakaoPayApprovalUrl(pg_token, kakaoPayInput, kakaoPayReadyVO);
	}

	 */
}
