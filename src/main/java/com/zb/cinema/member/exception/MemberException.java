package com.zb.cinema.member.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberException extends RuntimeException{

	private MemberError memberError;
	private String error;

	public MemberException(MemberError memberError) {
		super(memberError.getDescription());
		this.memberError = memberError;
	}
}
