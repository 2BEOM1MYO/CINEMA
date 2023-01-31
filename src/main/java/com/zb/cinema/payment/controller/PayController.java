package com.zb.cinema.payment.controller;

import com.zb.cinema.payment.model.KakaoPayInput;
import com.zb.cinema.payment.model.KakaoPayReady;
import com.zb.cinema.payment.service.KakaoPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pay")
@RequiredArgsConstructor
public class PayController {
	private final KakaoPayService kakaoPayService;
	private KakaoPayInput kakaoPayInput;
	private KakaoPayReady kakaoPayReady;

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
