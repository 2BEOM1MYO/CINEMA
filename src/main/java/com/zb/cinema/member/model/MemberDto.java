package com.zb.cinema.member.model;

import com.zb.cinema.member.entity.Member;
import com.zb.cinema.member.type.MemberType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MemberDto {

	private String name;
	private String phone;
	private String email;
	private String password;
	private MemberType type;
	private LocalDateTime regDt;
	private LocalDateTime updateDt;

	public static MemberDto fromEntity(Member member) {
		return MemberDto.builder().email(member.getEmail()).password(member.getPassword())
			.name(member.getName()).phone(member.getPhone()).type(member.getType())
			.regDt(member.getRegDt()).updateDt(member.getUpdateDt()).build();
	}

}
