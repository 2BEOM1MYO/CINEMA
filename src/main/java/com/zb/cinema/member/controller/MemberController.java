package com.zb.cinema.member.controller;

import com.zb.cinema.member.model.RegisterMember;
import com.zb.cinema.member.service.MemberService;
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

	@PostMapping("/signup")
	public RegisterMember.Response signUp(@RequestBody @Valid RegisterMember.Request request) {

		return RegisterMember.Response.from(
			memberService.register(request.getEmail(), request.getPassword(), request.getName(),
				request.getPhone()));
	}
}