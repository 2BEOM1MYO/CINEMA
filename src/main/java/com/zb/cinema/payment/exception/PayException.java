package com.zb.cinema.payment.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zb.cinema.ticketing.exception.TicketError;
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
