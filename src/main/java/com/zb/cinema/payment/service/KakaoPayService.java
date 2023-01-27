package com.zb.cinema.payment.service;

import com.zb.cinema.admin.entity.Auditorium;
import com.zb.cinema.admin.entity.Schedule;
import com.zb.cinema.admin.entity.Seat;
import com.zb.cinema.admin.repository.AuditoriumRepository;
import com.zb.cinema.admin.repository.ScheduleRepository;
import com.zb.cinema.admin.repository.SeatRepository;
import com.zb.cinema.movie.entity.Movie;
import com.zb.cinema.movie.repository.MovieCodeRepository;
import com.zb.cinema.movie.repository.MovieRepository;
import com.zb.cinema.payment.model.KakaoPayApprovalVO;
import com.zb.cinema.payment.model.KakaoPayInput;
import com.zb.cinema.payment.model.KakaoPayReadyVO;
import com.zb.cinema.ticketing.entity.Ticket;
import com.zb.cinema.ticketing.exception.TicketError;
import com.zb.cinema.ticketing.exception.TicketException;
import com.zb.cinema.ticketing.model.TicketInput;
import java.util.Optional;
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
	private KakaoPayReadyVO kakaoPayReadyVO;
	private KakaoPayApprovalVO kakaoPayApprovalVO;
	private final MovieRepository movieRepository;
	private final AuditoriumRepository auditoriumRepository;
	private final ScheduleRepository scheduleRepository;
	private final SeatRepository seatRepository;


	public KakaoPayReadyVO kakaoPayReadyUrl(TicketInput parameter, Ticket ticket) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "KakaoAK " + admin_key);
		headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

		// 결제 상품 이름 찾기
		Optional<Movie> optionalMovie = movieRepository.findById(ticket.getMovieCode());
		if(optionalMovie.isEmpty()) {
			throw new TicketException(TicketError.MOVIE_NOT_FOUND);
		}
		Movie movie = optionalMovie.get();

		// 가격 찾기
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

		Optional<Seat> optionalSeat = seatRepository.findBySeatNumAndAuditoriumId(
			parameter.getSeat(), auditorium.getId());
		if(optionalSeat.isEmpty()) {
			throw new TicketException(TicketError.SEAT_NOT_FOUND);
		}
		Seat seat = optionalSeat.get();


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


	public KakaoPayReadyVO getKakaoPayUrl(HttpHeaders headers, MultiValueMap<String, String> params) {
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<MultiValueMap<String, String>>(params, headers);

		try {
			kakaoPayReadyVO = restTemplate.postForObject(HOST + "/v1/payment/ready", body, KakaoPayReadyVO.class);

			return kakaoPayReadyVO != null ? kakaoPayReadyVO : null;
		} catch (RestClientException e) {
			e.printStackTrace();
		}

		return null;
	}




	public KakaoPayApprovalVO kakaoPayApprovalUrl(String pg_token, TicketInput parameter, KakaoPayReadyVO kakaoPayReadyVO) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "KakaoAK " + admin_key);
		headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

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

		Optional<Seat> optionalSeat = seatRepository.findBySeatNumAndAuditoriumId(
			parameter.getSeat(), auditorium.getId());
		if(optionalSeat.isEmpty()) {
			throw new TicketException(TicketError.SEAT_NOT_FOUND);
		}
		Seat seat = optionalSeat.get();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("cid", parameter.getCid());
		params.add("tid", kakaoPayReadyVO.getTid());
		params.add("partner_order_id", parameter.getPartner_order_id());
		params.add("partner_user_id", parameter.getPartner_user_id());
		params.add("pg_token", pg_token);
		params.add("total_amount", String.valueOf(seat.getPrice()));

		return kakaoPayApprovalInfo(pg_token ,headers, params);
	}

	public KakaoPayApprovalVO kakaoPayApprovalInfo(String pg_token, HttpHeaders headers, MultiValueMap<String, String> params) {
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<MultiValueMap<String, String>>(params, headers);

		try {
			kakaoPayApprovalVO = restTemplate.postForObject(HOST + "/v1/payment/approve", body, KakaoPayApprovalVO.class);

			return kakaoPayApprovalVO;

		} catch (RestClientException e) {
			e.printStackTrace();
		}
		return null;
	}



}
