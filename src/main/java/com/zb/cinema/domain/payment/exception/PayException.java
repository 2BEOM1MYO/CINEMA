package com.zb.cinema.domain.payment.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

@Getter
public class PayException extends RuntimeException{

	private final PayError payError;
	private final int status;
	private static final ObjectMapper mapper = new ObjectMapper();


	public PayException(PayError payError) {
		super(payError.getDescription());
		this.payError = payError;
		this.status = payError.getHttpStatus().value();
	}
}
