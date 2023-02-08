package com.zb.cinema.member.model;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ModifyMember {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Request {

		@NotBlank(message = "수정 할 비밀번호를 입력해주세요.")
		private String password;

	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Response {

		private String password;
		private LocalDateTime updateDt;

		public static ModifyMember.Response from(MemberDto memberDto) {

			return Response.builder().password(memberDto.getPassword())
				.updateDt(memberDto.getUpdateDt()).build();
		}

	}

}
