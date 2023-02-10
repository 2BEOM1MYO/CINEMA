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
public class KakaoPayCancel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String aid;
	private String tid;
	private String cid;

	private Status status;

	private String partner_order_id;
	private String partner_user_id;
	private String payment_method_type;

	@OneToOne(cascade = CascadeType.ALL)
	private Amount amount;
	@OneToOne(cascade = CascadeType.ALL)
	ApprovedCancelAmount approved_cancel_amount;
	@OneToOne(cascade = CascadeType.ALL)
	CanceledAmount canceled_amount;
	@OneToOne(cascade = CascadeType.ALL)
	CancelAvailableAmount cancel_available_amount;

	private String item_name;
	private String item_code;
	private Integer quantity;
	private Date created_at;
	private Date approved_at;
	private Date canceled_at;
	private String payload;
}
