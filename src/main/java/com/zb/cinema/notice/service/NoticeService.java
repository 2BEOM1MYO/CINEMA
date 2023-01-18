package com.zb.cinema.notice.service;

import com.zb.cinema.member.entity.Member;
import com.zb.cinema.member.exception.MemberError;
import com.zb.cinema.member.exception.MemberException;
import com.zb.cinema.member.repository.MemberRepository;
import com.zb.cinema.movie.entity.MovieCode;
import com.zb.cinema.movie.repository.MovieCodeRepository;
import com.zb.cinema.notice.entity.Notice;
import com.zb.cinema.notice.exception.NoticeError;
import com.zb.cinema.notice.exception.NoticeException;
import com.zb.cinema.notice.model.DeleteReview;
import com.zb.cinema.notice.model.ModifyReview;
import com.zb.cinema.notice.model.NoticeDto;
import com.zb.cinema.notice.model.WriteReview;
import com.zb.cinema.notice.respository.NoticeRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

	private final NoticeRepository noticeRepository;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final MovieCodeRepository movieCodeRepository;

	/*
	 	리뷰 등록
	 */
	public NoticeDto writeReview(WriteReview.Request parameter) {

		Member reviewMember = memberRepository.findByEmail(parameter.getEmail())
			.orElseThrow(() -> new MemberException(MemberError.MEMBER_NOT_FOUND));

		MovieCode movieTitle = movieCodeRepository.findByTitle(parameter.getTitle())
			.orElseThrow(() -> new NoticeException(NoticeError.MOVIE_TITLE_NOT_FOUND));

		Optional<Notice> noticeOptional = noticeRepository.findByNoticeMovieAndNoticeMember(
			movieTitle, reviewMember);

		if (noticeOptional.isPresent()) {
			throw new NoticeException(NoticeError.MOVIE_REVIEW_NO_DUPLICATION);
		}

		return NoticeDto.fromEntity(noticeRepository.save(
			Notice.builder().contents(parameter.getContents()).noticeMember(reviewMember)
				.noticeMovie(movieTitle).starRating(parameter.getStarRating())
				.regDt(LocalDateTime.now()).build()));
	}

	/*
	 	전체 리스트 불러오기
	 */
	public List<NoticeDto> getNoticeList() {

		Pageable limit = PageRequest.of(0, 10);
		Page<Notice> noticeList = noticeRepository.findAllByOrderByRegDt(limit);

		return noticeList.stream().map(NoticeDto::fromEntity).collect(Collectors.toList());

	}

	/*
	 	후기 상세보기
	 */
	public NoticeDto getReviewDetail(Long noticeId) {

		Optional<Notice> noticeOptional = noticeRepository.findById(noticeId);

		if (noticeOptional.isEmpty()) {
			throw new NoticeException(NoticeError.MOVIE_REVIEW_ID_NOT_FOUND);

		} else {
			return NoticeDto.fromEntity(noticeOptional.get());
		}
	}

	/*
	 	영화 별 후기 리스트 보기
	 */
	public List<NoticeDto> getReviewByMovie(Long movieCode) {

		Pageable limit = PageRequest.of(0, 10);
		Page<Notice> notice = noticeRepository.findByNoticeMovieCode(movieCode, limit);

		if (notice.isEmpty()) {
			throw new NoticeException(NoticeError.MOVIE_REVIEW_NOT_FOUND);
		}

		return notice.stream().map(NoticeDto::fromEntity).collect(Collectors.toList());
	}

	/*
	 	후기 수정 (내용, 별점 수정 가능)
	 */
	public NoticeDto modifyReview(Long noticeId, ModifyReview.Request parameter) {

		Member reviewMember = validateMember(parameter.getEmail(), parameter.getPassword());
		Notice notice = validateWriter(noticeId, reviewMember);

		notice.setContents(parameter.getContents());
		notice.setStarRating(parameter.getStarRating());
		notice.setUpdateDt(LocalDateTime.now());
		noticeRepository.save(notice);

		return NoticeDto.fromEntity(notice);
	}

	/*
		후기 삭제하기
	 */
	@Transactional
	public void deleteReview(Long noticeId, DeleteReview parameter) {

		Member reviewMember = validateMember(parameter.getEmail(), parameter.getPassword());
		validateWriter(noticeId, reviewMember);

		noticeRepository.deleteAllById(noticeId);
	}

	private Member validateMember(String email, String password) {

		Member reviewMember = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberError.MEMBER_NOT_FOUND));

		if (!passwordEncoder.matches(password, reviewMember.getPassword())) {
			throw new MemberException(MemberError.MEMBER_PASSWORD_NOT_SAME);
		}

		return reviewMember;
	}

	private Notice validateWriter(Long noticeId, Member member) {

		Optional<Notice> optionalNotice = noticeRepository.findById(noticeId);

		if (optionalNotice.isEmpty()) {
			throw new NoticeException(NoticeError.MOVIE_REVIEW_ID_NOT_FOUND);
		}

		Notice notice = optionalNotice.get();

		if (!Objects.equals(notice.getNoticeMember().getEmail(), member.getEmail())) {
			throw new NoticeException(NoticeError.MOVIE_REVIEW_USER_UN_MATCH);
		}

		return notice;
	}

}
