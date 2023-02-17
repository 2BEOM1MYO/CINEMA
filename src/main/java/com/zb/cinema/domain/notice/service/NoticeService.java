package com.zb.cinema.domain.notice.service;

import static com.zb.cinema.global.jwt.JwtAuthenticationFilter.TOKEN_PREFIX;

import com.zb.cinema.domain.notice.model.ReviewAllList;
import com.zb.cinema.domain.notice.model.ReviewByMovie;
import com.zb.cinema.domain.notice.model.ReviewDetail;
import com.zb.cinema.domain.ticketing.entity.Ticket;
import com.zb.cinema.domain.ticketing.repository.TicketRepository;
import com.zb.cinema.global.jwt.TokenProvider;
import com.zb.cinema.domain.member.entity.Member;
import com.zb.cinema.domain.member.exception.MemberError;
import com.zb.cinema.domain.member.exception.MemberException;
import com.zb.cinema.domain.member.repository.MemberRepository;
import com.zb.cinema.domain.movie.entity.Movie;
import com.zb.cinema.domain.movie.entity.MovieCode;
import com.zb.cinema.domain.movie.repository.MovieCodeRepository;
import com.zb.cinema.domain.movie.repository.MovieRepository;
import com.zb.cinema.domain.movie.type.MovieStatus;
import com.zb.cinema.domain.notice.entity.Notice;
import com.zb.cinema.domain.notice.exception.NoticeError;
import com.zb.cinema.domain.notice.exception.NoticeException;
import com.zb.cinema.domain.notice.model.DeleteReview;
import com.zb.cinema.domain.notice.model.ModifyReview;
import com.zb.cinema.domain.notice.model.ViewMovieInfo;
import com.zb.cinema.domain.notice.model.NoticeDto;
import com.zb.cinema.domain.notice.model.WriteReview;
import com.zb.cinema.domain.notice.respository.NoticeRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
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
	private final MovieRepository movieRepository;
	private final TicketRepository ticketRepository;
	private final TokenProvider tokenProvider;

	/*
	 	리뷰 등록
	 */
	@Transactional
	public WriteReview.Response writeReview(String token, WriteReview.Request parameter) {

		Member reviewMember = validateMember(token);

		MovieCode movieTitle = movieCodeRepository.findByTitle(parameter.getTitle())
			.orElseThrow(() -> new NoticeException(NoticeError.MOVIE_TITLE_NOT_FOUND));

		Movie movieStatus = movieRepository.findByTitle(movieTitle.getTitle());

		validMovieStatus(reviewMember, movieTitle, movieStatus);

		Optional<Notice> noticeOptional = noticeRepository.findByNoticeMovieAndNoticeMember(
			movieTitle, reviewMember);

		if (noticeOptional.isPresent()) {
			throw new NoticeException(NoticeError.MOVIE_REVIEW_NO_DUPLICATION);
		}

		Notice notice = noticeRepository.save(
			Notice.builder().contents(parameter.getContents()).noticeMember(reviewMember)
				.noticeMovie(movieTitle).starRating(parameter.getStarRating())
				.regDt(LocalDateTime.now()).build());

		return WriteReview.Response.from(NoticeDto.from(notice));
	}

	/*
	 	전체 리스트 불러오기
	 */
	public List<ReviewAllList> getNoticeList() {

		Pageable limit = PageRequest.of(0, 10);
		Page<Notice> noticeList = noticeRepository.findAllByOrderByRegDt(limit);

		return noticeList.stream().map(NoticeDto::from).map(
			noticeDto -> ReviewAllList.builder().email(noticeDto.getEmail())
				.movieTitle(noticeDto.getMovieTitle()).contents(noticeDto.getContents())
				.regDt(noticeDto.getRegDt()).build()).collect(Collectors.toList());

	}

	/*
	 	후기 상세보기
	 */
	public ReviewDetail getReviewDetail(Long noticeId) {

		Notice notice = noticeRepository.findById(noticeId)
			.orElseThrow(() -> new NoticeException(NoticeError.MOVIE_REVIEW_ID_NOT_FOUND));

		return ReviewDetail.from(NoticeDto.from(notice));
	}

	public ViewMovieInfo getInfoByMovie(Long movieCode) {

		Optional<Movie> movieOptional = movieRepository.findByCode(movieCode);
		Movie movie = movieOptional.get();

		Double starAvg = noticeRepository.getByNoticeMovieCode(movieCode);

		return ViewMovieInfo.builder().movieTitle(movie.getTitle()).actors(movie.getActors())
			.directors(movie.getDirectors()).genre(movie.getGenre()).nation(movie.getNation())
			.ratingAvg(starAvg).build();
	}

	/*
	 	영화 별 후기 리스트 보기
	 */
	public List<ReviewByMovie> getReviewByMovie(Long movieCode) {

		Pageable limit = PageRequest.of(0, 10);
		Page<Notice> notice = noticeRepository.findByNoticeMovieCode(movieCode, limit);

		if (notice.isEmpty()) {
			throw new NoticeException(NoticeError.MOVIE_REVIEW_NOT_FOUND);
		}
		return notice.stream().map(NoticeDto::from).map(
				noticeDto -> ReviewByMovie.builder().email(noticeDto.getEmail())
					.movieTitle(noticeDto.getMovieTitle()).contents(noticeDto.getContents())
					.starRating(noticeDto.getStarRating()).regDt(noticeDto.getRegDt()).build())
			.collect(Collectors.toList());
	}

	/*
	 	후기 수정 (내용, 별점 수정 가능)
	 */
	@Transactional
	public ModifyReview.Response modifyReview(Long noticeId, String token,
		ModifyReview.Request parameter) {

		Member reviewMember = validateMember(token);
		Notice notice = validateWriter(noticeId, reviewMember);

		notice.setContents(parameter.getContents());
		notice.setStarRating(parameter.getStarRating());
		notice.setUpdateDt(LocalDateTime.now());
		noticeRepository.save(notice);

		return ModifyReview.Response.from(NoticeDto.from(notice));
	}

	/*
		후기 삭제하기
	 */
	@Transactional
	public void deleteReview(Long noticeId, String token, DeleteReview parameter) {

		Member reviewMember = validateMember(token);

		if (!passwordEncoder.matches(parameter.getPassword(), reviewMember.getPassword())) {
			throw new MemberException(MemberError.MEMBER_PASSWORD_NOT_SAME);
		}

		validateWriter(noticeId, reviewMember);

		noticeRepository.deleteAllById(noticeId);
	}

	private Member validateMember(String token) {

		String subToken = token.substring(TOKEN_PREFIX.length());

		String email = "";
		email = tokenProvider.getUserPk(subToken);

		return memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberError.MEMBER_NOT_FOUND));
	}

	private Notice validateWriter(Long noticeId, Member member) {

		Notice notice = noticeRepository.findById(noticeId)
			.orElseThrow(() -> new NoticeException(NoticeError.MOVIE_REVIEW_ID_NOT_FOUND));

		if (!Objects.equals(notice.getNoticeMember().getEmail(), member.getEmail())) {
			throw new NoticeException(NoticeError.MOVIE_REVIEW_USER_UN_MATCH);
		}

		return notice;
	}

	private void validMovieStatus(Member reviewMember, MovieCode movieTitle, Movie movieStatus) {
		if (MovieStatus.STATUS_WILL.equals(movieStatus.getStatus())) {
			throw new NoticeException(NoticeError.MOVIE_STATUS_WILL);
		}

		if (MovieStatus.STATUS_SHOWING.equals(movieStatus.getStatus())) {
			Optional<Ticket> ticketCheck = ticketRepository.findTicketByMemberIdAndMovieCode(
				reviewMember.getId(), movieTitle.getCode());

			if (ticketCheck.isEmpty()) {
				throw new NoticeException(NoticeError.MOVIE_STATUS_SHOWING);
			}
		}
	}
}
