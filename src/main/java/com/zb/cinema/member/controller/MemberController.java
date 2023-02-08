package com.zb.cinema.member.controller;

import com.zb.cinema.config.jwt.JwtAuthenticationFilter;
import com.zb.cinema.config.jwt.TokenProvider;
import com.zb.cinema.member.entity.Member;
import com.zb.cinema.member.model.LoginMember;
import com.zb.cinema.member.model.MemberInfo;
import com.zb.cinema.member.model.ModifyMember;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

		return RegisterMember.Response.from(memberService.register(request));
	}

	@PostMapping("/signin")
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

	@GetMapping("{memberId}")
	public MemberInfo getMemberInfo(@PathVariable Long memberId,
		@RequestHeader("Authorization") String token) {
		return MemberInfo.from(memberService.getMemberInfo(memberId, token));
	}

	@PatchMapping("{memberId}")
	@PreAuthorize("hasRole('READWRITE')")
	public ModifyMember.Response modifyMember(@PathVariable Long memberId,
		@RequestHeader("Authorization") String token,
		@RequestBody @Valid ModifyMember.Request request) {
		return ModifyMember.Response.from(memberService.modifyMember(memberId, token, request));
	}
}