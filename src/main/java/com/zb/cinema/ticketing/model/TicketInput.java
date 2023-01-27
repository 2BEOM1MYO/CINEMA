package com.zb.cinema.ticketing.model;

import java.time.LocalDateTime;
import java.util.Random;
import lombok.Getter;

@Getter
public class TicketInput {
	private String cid;
	private String partner_order_id;
	private String partner_user_id;

	private Long scheduleId;		// 나머지 정보는 auditorium에
	private String seat;
}
