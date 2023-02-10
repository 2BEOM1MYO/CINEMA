package com.zb.cinema.domain.member.model;

import com.zb.cinema.domain.member.type.MemberType;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginMember {

	@NotBlank(message = "사용자 ID는 필수 항목입니다.")
	private String email;

	@NotBlank(message = "사용자 비밀번호는 필수 항목입니다.")
	private String password;

	private MemberType type;
}
