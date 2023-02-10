package com.zb.cinema.domain.payment.entity;

import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoPayApproval {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String aid;
	private String tid;
	private String cid;
	private String sid;
	private String partner_order_id;
	private String partner_user_id;
	private String payment_method_type;

	@OneToOne(cascade = CascadeType.ALL)
	private Amount amount;

	@OneToOne(cascade = CascadeType.ALL)
	private Card card_info;

	private String item_name, item_code, payload;
	private Integer quantity, tax_free_amount, vat_amount;
	private Date created_at;
	private Date approved_at;

}
