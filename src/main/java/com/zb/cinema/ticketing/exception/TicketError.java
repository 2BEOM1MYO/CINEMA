package com.zb.cinema.ticketing.exception;

import com.zb.cinema.admin.entity.Auditorium;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TicketError {

	MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST,"상품을 찾을 수 없습니다."),
	AUDITORIUM_NOT_FOUND(HttpStatus.BAD_REQUEST, "상영관을 찾을 수 없습니다."),
	MOVIE_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 영화를 찾을 수 없습니다."),
	TICKET_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 예매내역을 찾을 수 없습니다."),
	SCHEDULE_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 상영일자 내역을 찾을 수 없습니다."),
	SEAT_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 좌석을 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String description;
}
