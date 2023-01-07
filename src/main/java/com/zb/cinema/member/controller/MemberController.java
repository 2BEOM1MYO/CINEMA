package com.zb.cinema.member.controller;

import com.zb.cinema.config.jwt.TokenProvider;
import com.zb.cinema.member.entity.Member;
import com.zb.cinema.member.model.LoginMember;
import com.zb.cinema.member.model.RegisterMember;
import com.zb.cinema.member.service.MemberService;
import com.zb.cinema.member.type.MemberType;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

	private final MemberService memberService;
	private final TokenProvider tokenProvider;

	@PostMapping("/signup")
	public RegisterMember.Response signUp(@RequestBody @Valid RegisterMember.Request request) {

		return RegisterMember.Response.from(
			memberService.register(request.getEmail(), request.getPassword(), request.getName(),
				request.getPhone()));
	}

	@PostMapping("/login")
	public String signIn(@RequestBody @Valid LoginMember loginMember,
		HttpServletResponse response) {

		Member member = memberService.login(loginMember);
		String email = member.getEmail();
		MemberType role = member.getType();

		String token = tokenProvider.generatedToken(email, role);
		response.setHeader("JWT", token);

		return token;

	}
}