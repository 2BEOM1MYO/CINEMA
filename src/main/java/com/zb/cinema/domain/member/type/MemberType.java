package com.zb.cinema.domain.member.type;

import lombok.Getter;

@Getter
public enum MemberType {
	//ROLE_READONLY("비회원"),
	ROLE_READWRITE("회원"), ROLE_UN_ACCESSIBLE("정지 회원"), ROLE_ADMIN("관리자");

	MemberType(String value) {
		this.value = value;
	}

	private String value;

}