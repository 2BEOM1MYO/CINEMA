package com.zb.cinema.payment.model;

import java.util.Date;
import lombok.Getter;

@Getter
public class KakaoPayReady {
	private String tid;
	private String next_redirect_pc_url;
	private Date created_at;
}
