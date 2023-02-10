package com.zb.cinema.domain.payment.service;

import com.zb.cinema.domain.admin.entity.Auditorium;
import com.zb.cinema.domain.admin.entity.Schedule;
import com.zb.cinema.domain.admin.entity.Seat;
import com.zb.cinema.domain.admin.repository.AuditoriumRepository;
import com.zb.cinema.domain.admin.repository.ScheduleRepository;
import com.zb.cinema.domain.admin.repository.SeatRepository;
import com.zb.cinema.domain.movie.entity.Movie;
import com.zb.cinema.domain.movie.repository.MovieRepository;
import com.zb.cinema.domain.payment.model.KakaoPayCancelInput;
import com.zb.cinema.domain.payment.model.KakaoPayReady;
import com.zb.cinema.domain.payment.repository.PaymentRepository;
import com.zb.cinema.domain.payment.entity.Amount;
import com.zb.cinema.domain.payment.entity.KakaoPayCancel;
import com.zb.cinema.domain.payment.exception.PayError;
import com.zb.cinema.domain.payment.exception.PayException;
import com.zb.cinema.domain.payment.entity.KakaoPayApproval;
import com.zb.cinema.domain.ticketing.entity.Ticket;
import com.zb.cinema.domain.ticketing.exception.TicketError;
import com.zb.cinema.domain.ticketing.exception.TicketException;
import com.zb.cinema.domain.ticketing.model.TicketInput;
import com.zb.cinema.domain.payment.repository.AmountRepository;
import com.zb.cinema.domain.ticketing.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@PropertySource("classpath:config.properties")
@RequiredArgsConstructor
public class KakaoPayService {

	private static final String HOST = "https://kapi.kakao.com";
	@Value("${ADMIN_KEY}")
	private String admin_key;
	private KakaoPayReady kakaoPayReady;
	private KakaoPayApproval kakaoPayApproval;
	private KakaoPayCancel kakaoPayCancel;
	private final MovieRepository movieRepository;
	private final AuditoriumRepository auditoriumRepository;
	private final ScheduleRepository scheduleRepository;
	private final SeatRepository seatRepository;
	private final TicketRepository ticketRepository;
	private final PaymentRepository paymentRepository;
	private final AmountRepository amountRepository;


	public KakaoPayReady kakaoPayReadyUrl(TicketInput parameter, Ticket ticket) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "KakaoAK " + admin_key);
		headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

		// 결제 상품 이름 찾기
		Movie movie = movieRepository.findById(ticket.getMovieCode()).orElseThrow(() -> new TicketException(TicketError.MOVIE_NOT_FOUND));

		// 가격 찾기
		Schedule schedule = scheduleRepository.findById(parameter.getScheduleId()).orElseThrow(() -> new TicketException(TicketError.SCHEDULE_NOT_FOUND));

		Auditorium auditorium = auditoriumRepository.findById(schedule.getAuditorium().getId()).orElseThrow(() -> new TicketException(TicketError.AUDITORIUM_NOT_FOUND));

		Seat seat = seatRepository.findBySeatNumAndAuditoriumId(parameter.getSeat(), auditorium.getId())
			.orElseThrow(() ->  new TicketException(TicketError.SEAT_NOT_FOUND));



		// 서버로 요청할 Body
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("cid", parameter.getCid());
		params.add("partner_order_id", parameter.getPartner_order_id());
		params.add("partner_user_id", parameter.getPartner_user_id());
		params.add("item_name", movie.getTitle());
		params.add("quantity", String.valueOf(1));
		params.add("total_amount", String.valueOf(seat.getPrice()));
		params.add("tax_free_amount", String.valueOf(seat.getPrice()));
		params.add("approval_url", "http://localhost:8080/ticketing/ticketingSuccess");
		params.add("cancel_url", "http://localhost:8080/kakaoPayCancel");
		params.add("fail_url", "http://localhost:8080/kakaoPaySuccessFail");

		return getKakaoPayUrl(headers, params);
	}


	public KakaoPayReady getKakaoPayUrl(HttpHeaders headers, MultiValueMap<String, String> params) {
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<MultiValueMap<String, String>>(params, headers);

		try {
			kakaoPayReady = restTemplate.postForObject(HOST + "/v1/payment/ready", body, KakaoPayReady.class);

			return kakaoPayReady != null ? kakaoPayReady : null;
		} catch (RestClientException e) {
			e.printStackTrace();
		}

		return null;
	}




	public KakaoPayApproval kakaoPayApprovalUrl(String pg_token, TicketInput parameter, KakaoPayReady kakaoPayReady) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "KakaoAK " + admin_key);
		headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

		Schedule schedule = scheduleRepository.findById(parameter.getScheduleId()).orElseThrow(() -> new TicketException(TicketError.SCHEDULE_NOT_FOUND));

		Auditorium auditorium = auditoriumRepository.findById(schedule.getAuditorium().getId()).orElseThrow(() -> new TicketException(TicketError.AUDITORIUM_NOT_FOUND));

		Seat seat = seatRepository.findBySeatNumAndAuditoriumId(
			parameter.getSeat(), auditorium.getId()).orElseThrow(()-> new TicketException(TicketError.SEAT_NOT_FOUND));


		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("cid", parameter.getCid());
		params.add("tid", kakaoPayReady.getTid());
		params.add("partner_order_id", parameter.getPartner_order_id());
		params.add("partner_user_id", parameter.getPartner_user_id());
		params.add("pg_token", pg_token);
		params.add("total_amount", String.valueOf(seat.getPrice()));

		return kakaoPayApprovalInfo(pg_token ,headers, params);
	}

	public KakaoPayApproval kakaoPayApprovalInfo(String pg_token, HttpHeaders headers, MultiValueMap<String, String> params) {
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<MultiValueMap<String, String>>(params, headers);

		try {
			kakaoPayApproval = restTemplate.postForObject(HOST + "/v1/payment/approve", body, KakaoPayApproval.class);

			return kakaoPayApproval;

		} catch (RestClientException e) {
			e.printStackTrace();
		}
		return null;
	}



	// 결제 취소
	public KakaoPayCancel cancelPay(KakaoPayCancelInput parameter) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "KakaoAK " + admin_key);
		headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

		// 예매한 티켓 찾기
		Ticket ticket = ticketRepository.findByTid(parameter.getTid()).orElseThrow(() -> new TicketException(TicketError.TICKET_NOT_FOUND));

		// 가격 찾기
		KakaoPayApproval kakaoPayApproval = paymentRepository.findByTid(ticket.getTid()).orElseThrow(() -> new PayException(PayError.PAY_NOT_FOUND));
		Amount amount = amountRepository.findById(Long.valueOf(kakaoPayApproval.getTid())).orElseThrow(() -> new PayException(
			PayError.AMOUNT_NOT_FOUND));

		// 서버로 요청할 Body
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("cid", parameter.getCid());
		params.add("tid", parameter.getTid());
		params.add("cancel_amount", String.valueOf(amount.getTotal()));
		params.add("cancel_tax_free_amount", String.valueOf(amount.getTax_free()));

		return cancel(headers, params);
	}

	public KakaoPayCancel cancel(HttpHeaders headers, MultiValueMap<String, String> params) {
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<MultiValueMap<String, String>>(params, headers);

		try {
			kakaoPayCancel = restTemplate.postForObject(HOST + "/v1/payment/cancel", body, KakaoPayCancel.class);

			return kakaoPayCancel != null ? kakaoPayCancel : null;
		} catch (RestClientException e) {
			e.printStackTrace();
		}

		return null;
	}



}
