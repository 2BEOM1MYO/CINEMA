package com.zb.cinema.member.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	MEMBER_ALREADY_EMAIL("이미 등록된 e-mail 입니다.");

	private final String description;
}
