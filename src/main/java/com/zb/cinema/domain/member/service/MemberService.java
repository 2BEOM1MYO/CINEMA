package com.zb.cinema.domain.member.service;

import static com.zb.cinema.global.jwt.JwtAuthenticationFilter.TOKEN_PREFIX;

import com.zb.cinema.global.jwt.TokenProvider;
import com.zb.cinema.domain.member.entity.Member;
import com.zb.cinema.domain.member.model.LoginMember;
import com.zb.cinema.domain.member.model.MemberDto;
import com.zb.cinema.domain.member.model.ModifyMember;
import com.zb.cinema.domain.member.model.RegisterMember.Request;
import com.zb.cinema.domain.member.model.WithDrawMember;
import com.zb.cinema.domain.member.repository.MemberRepository;
import com.zb.cinema.domain.member.type.MemberType;
import com.zb.cinema.domain.member.exception.MemberError;
import com.zb.cinema.domain.member.exception.MemberException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;

	public MemberDto register(Request parameter) {

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

	public MemberDto getMemberInfo(Long memberId, String token) {

		Member member = validateMember(token);

		if (!Objects.equals(member.getId(), memberId)) {
			throw new MemberException((MemberError.INFO_MEMBER_UN_MATCH));
		}

		return MemberDto.from(member);
	}

	@Transactional
	public MemberDto modifyMember(Long memberId, String token, ModifyMember.Request request) {

		Member member = validateMember(token);

		if (!Objects.equals(member.getId(), memberId)) {
			throw new MemberException((MemberError.MODIFY_MEMBER_UN_MATCH));
		}
		String rePw = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

		member.setPassword(rePw);
		member.setPhone(request.getPhone());
		member.setEmail(request.getEmail());
		member.setUpdateDt(LocalDateTime.now());
		memberRepository.save(member);

		return MemberDto.from(member);
	}

	public void withDrawMember(Long memberId, String token, WithDrawMember request) {
		Member member = validateMember(token);

		if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
			throw new MemberException(MemberError.MEMBER_PASSWORD_NOT_SAME);
		}

		if (!Objects.equals(member.getId(), memberId)) {
			throw new MemberException((MemberError.MEMBER_WRONG_APPROACH));
		}

		member.setType(MemberType.ROLE_UN_ACCESSIBLE);
		memberRepository.save(member);
	}

	private Member validateMember(String token) {

		String subToken = token.substring(TOKEN_PREFIX.length());

		String email = "";
		email = tokenProvider.getUserPk(subToken);

		return memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberError.MEMBER_NOT_FOUND));
	}
}