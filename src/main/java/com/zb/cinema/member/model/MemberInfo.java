package com.zb.cinema.member.model;

import com.zb.cinema.member.type.MemberType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInfo {

	private String name;
	private String phone;
	private String email;
	private MemberType type;

	public static MemberInfo from(MemberDto memberDto) {

		return MemberInfo.builder().name(memberDto.getName()).phone(memberDto.getPhone())
			.email(memberDto.getEmail()).type(memberDto.getType()).build();
	}

}
