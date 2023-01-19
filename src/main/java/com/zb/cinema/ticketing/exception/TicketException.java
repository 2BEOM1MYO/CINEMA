package com.zb.cinema.ticketing.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

@Getter
public class TicketException extends RuntimeException{

	private final TicketError ticketError;
	private final int status;
	private static final ObjectMapper mapper = new ObjectMapper();


	public TicketException(TicketError ticketError) {
		super(ticketError.getDescription());
		this.ticketError = ticketError;
		this.status = ticketError.getHttpStatus().value();
	}
}
