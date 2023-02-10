package com.zb.cinema.domain.payment.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PayError {

	AMOUNT_NOT_FOUND(HttpStatus.BAD_REQUEST,"가격을 찾을 수 없습니다."),
	PAY_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 결제 내역을 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String description;
}
