package com.zb.cinema.payment.model;

import com.zb.cinema.payment.entity.Amount;
import com.zb.cinema.payment.entity.Card;
import java.util.Date;
import lombok.Data;

@Data
public class KakaoPayApprovalVO {
	private String aid;
	private String tid;
	private String cid;
	private String sid;
	private String partner_order_id;
	private String partner_user_id;
	private String payment_method_type;
	private Amount amount;
	private Card card_info;
	private String item_name, item_code, payload;
	private Integer quantity, tax_free_amount, vat_amount;
	private Date created_at;
	private Date approved_at;
}
