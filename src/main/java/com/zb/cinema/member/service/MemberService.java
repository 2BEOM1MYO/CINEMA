package com.zb.cinema.member.service;

import static com.zb.cinema.config.jwt.JwtAuthenticationFilter.TOKEN_PREFIX;

import com.zb.cinema.config.jwt.TokenProvider;
import com.zb.cinema.member.entity.Member;
import com.zb.cinema.member.exception.MemberError;
import com.zb.cinema.member.exception.MemberException;
import com.zb.cinema.member.model.LoginMember;
import com.zb.cinema.member.model.MemberDto;
import com.zb.cinema.member.model.ModifyMember.Request;
import com.zb.cinema.member.model.RegisterMember;
import com.zb.cinema.member.repository.MemberRepository;
import com.zb.cinema.member.type.MemberType;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;

	public MemberDto register(RegisterMember.Request parameter) {

		Optional<Member> optionalMember = memberRepository.findByEmail(parameter.getEmail());

		if (optionalMember.isPresent()) {
			throw new MemberException(MemberError.MEMBER_ALREADY_EMAIL);
		}

		String pw = BCrypt.hashpw(parameter.getPassword(), BCrypt.gensalt());

		return MemberDto.from(memberRepository.save(
			Member.builder().email(parameter.getEmail()).password(pw).name(parameter.getName())
				.phone(parameter.getPhone()).regDt(LocalDateTime.now())
				.type(MemberType.ROLE_READWRITE).build()));
	}

	public Member login(LoginMember parameter) {

		Member member = memberRepository.findByEmail(parameter.getEmail())
			.orElseThrow(() -> new MemberException(MemberError.MEMBER_NOT_FOUND));

		if (!passwordEncoder.matches(parameter.getPassword(), member.getPassword())) {
			throw new MemberException(MemberError.MEMBER_PASSWORD_NOT_SAME);
		}

		if (MemberType.ROLE_UN_ACCESSIBLE.equals(member.getType())) {
			throw new MemberException(MemberError.MEMBER_ROLE_UN_ACCESSIBLE);
		}

		return member;
	}

	public MemberDto modifyMember(Long memberId, String token, Request request) {

		Member member = validateMember(token);

		if (!Objects.equals(member.getId(), memberId)) {
			throw new MemberException((MemberError.MODIFY_MEMBER_UN_MATCH));
		}
		String rePw = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

		member.setPassword(rePw);
		member.setUpdateDt(LocalDateTime.now());
		memberRepository.save(member);

		return MemberDto.from(member);
	}

	private Member validateMember(String token) {

		String subToken = token.substring(TOKEN_PREFIX.length());

		String email = "";
		email = tokenProvider.getUserPk(subToken);

		return memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberError.MEMBER_NOT_FOUND));
	}

}