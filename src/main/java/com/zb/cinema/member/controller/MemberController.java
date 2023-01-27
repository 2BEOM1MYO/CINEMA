package com.zb.cinema.member.controller;

import com.zb.cinema.config.jwt.JwtAuthenticationFilter;
import com.zb.cinema.config.jwt.TokenProvider;
import com.zb.cinema.member.entity.Member;
import com.zb.cinema.member.model.LoginMember;
import com.zb.cinema.member.model.RegisterMember;
import com.zb.cinema.member.model.TokenDto;
import com.zb.cinema.member.service.MemberService;
import com.zb.cinema.member.type.MemberType;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
			memberService.register(request));
	}

	@PostMapping("/login")
	public ResponseEntity<TokenDto> signIn(@RequestBody @Valid LoginMember loginMember) {

		Member member = memberService.login(loginMember);
		String email = member.getEmail();
		MemberType role = member.getType();

		String token = tokenProvider.generatedToken(email, role);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(JwtAuthenticationFilter.TOKEN_HEADER,
			JwtAuthenticationFilter.TOKEN_PREFIX + token);

		return new ResponseEntity<>(new TokenDto(token), httpHeaders, HttpStatus.OK);

	}
}