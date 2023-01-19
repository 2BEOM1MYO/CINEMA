package com.zb.cinema.payment.entity;

import lombok.Getter;

@Getter
public class Amount {
	private Integer total;
	private Integer tax_free;
	private Integer vat;
	private Integer point;
	private Integer discount;
}
