package com.zb.cinema.member.service;

import com.zb.cinema.member.entity.Member;
import com.zb.cinema.member.exception.MemberError;
import com.zb.cinema.member.exception.MemberException;
import com.zb.cinema.member.model.LoginMember;
import com.zb.cinema.member.model.MemberDto;
import com.zb.cinema.member.repository.MemberRepository;
import com.zb.cinema.member.type.MemberType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public MemberDto register(String email, String password, String name, String phone) {

		Optional<Member> optionalMember = memberRepository.findByEmail(email);

		if (optionalMember.isPresent()) {
			throw new MemberException(MemberError.MEMBER_ALREADY_EMAIL);
		}

		String pw = BCrypt.hashpw(password, BCrypt.gensalt());

		return MemberDto.fromEntity(memberRepository.save(
			Member.builder().email(email).password(pw).name(name).phone(phone)
				.regDt(LocalDateTime.now()).type(MemberType.ROLE_READWRITE).build()));
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Member> memberOptional = this.memberRepository.findByEmail(username);
		if (memberOptional.isEmpty()) {
			throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
		}

		Member member = memberOptional.get();
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_READWRITE"));

		if (MemberType.ROLE_ADMIN.equals(member.getType())) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}

		return new org.springframework.security.core.userdetails.User(member.getEmail(),
			member.getPassword(), authorities);
	}

	public Member login(LoginMember parameter) {
		Member member = memberRepository.findByEmail(parameter.getEmail())
			.orElseThrow(() -> new MemberException(MemberError.MEMBER_NOT_FOUND));

		if (!passwordEncoder.matches(parameter.getPassword(), member.getPassword())) {
			throw new MemberException(MemberError.MEMBER_PASSWORD_NOT_SAME);
		}
		return member;
	}
}