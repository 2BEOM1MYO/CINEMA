package com.zb.cinema.payment.model;

import java.util.Random;
import lombok.Getter;

@Getter
public class KakaoPayInput {
	private String cid;
	private String partner_order_id;
	private String partner_user_id;
	private String item_name;
	private Integer quantity;
	private Integer total_amount;
	private Integer tax_free_amount;

}
