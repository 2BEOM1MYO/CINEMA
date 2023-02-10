package com.zb.cinema.domain.member.model;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WithDrawMember {

	@NotBlank(message = "계정 비밀번호를 입력해주세요.")
	private String password;
}
