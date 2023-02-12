package com.zb.cinema.domain.member.model;

import io.swagger.annotations.ApiModel;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
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
	@ApiModel(value = "회원 수정 입력 정보")
	public static class Request {

		@NotBlank(message = "수정 할 비밀번호를 입력해주세요.")
		@Pattern(regexp = "[a-zA-Z1-9]{6,12}", message = "비밀번호는 영어와 숫자로 포함해서 6 ~ 12자리 이내로 입력해주세요.")
		private String password;

		@NotBlank
		@Pattern(regexp = "^\\d{2,3}\\d{3,4}\\d{4}$", message = " '-'를 제외한 전화번호만 입력해주세요. ")
		private String phone;

		@NotBlank(message = "수정 할 이메일을 입력해주세요.")
		@Pattern(regexp = "^[a-z0-9A-Z._-]*@[a-z0-9A-Z]*.[a-zA-Z.]*$", message = "이메일 형식을 올바르게 입력해주세요.")
		private String email;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@ApiModel(value = "회원 수정 응답 정보")
	public static class Response {

		private String password;
		private String phone;
		private String email;
		private LocalDateTime updateDt;

		public static ModifyMember.Response from(MemberDto memberDto) {

			return Response.builder().password(memberDto.getPassword()).phone(memberDto.getPhone())
				.email(memberDto.getEmail()).updateDt(memberDto.getUpdateDt()).build();
		}

	}

}
