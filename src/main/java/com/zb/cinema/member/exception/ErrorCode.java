package com.zb.cinema.member.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	MEMBER_ALREADY_EMAIL("이미 등록된 e-mail 입니다."),
	MEMBER_NOT_FOUND("존재 하지 않는 ID 입니다."),
	MEMBER_PASSWORD_NOT_SAME("등록된 비밀 번호가 다릅니다.");

	private final String description;
}
