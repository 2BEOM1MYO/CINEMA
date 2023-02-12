package com.zb.cinema.domain.notice.controller;

import com.zb.cinema.domain.notice.model.ViewMovieInfo;
import com.zb.cinema.domain.notice.service.NoticeService;
import com.zb.cinema.domain.notice.model.DeleteReview;
import com.zb.cinema.domain.notice.model.ModifyReview;
import com.zb.cinema.domain.notice.model.ReviewAllList;
import com.zb.cinema.domain.notice.model.ReviewByMovie;
import com.zb.cinema.domain.notice.model.ReviewDetail;
import com.zb.cinema.domain.notice.model.WriteReview;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "Movie-Review-Notice-Api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

	private final NoticeService noticeService;

	@ApiOperation(value = "관람 후기 등록 시, 등록 내용이 DB에 저장됩니다.")
	@PostMapping()
	@PreAuthorize("hasRole('READWRITE')")
	public WriteReview.Response writeReview(@RequestHeader("Authorization") String token,
		@RequestBody @Valid WriteReview.Request request) {
		return WriteReview.Response.from(noticeService.writeReview(token, request));

	}

	/*
	 	전체 목록 보기 (Pagenation)
	 */
	@ApiOperation(value = "등록되어 있는 전체 영화 리뷰 목록을 볼 수 있습니다.")
	@GetMapping("/list")
	public List<ReviewAllList> getReviewALlList() {
		return noticeService.getNoticeList().stream().map(
			noticeDto -> ReviewAllList.builder().email(noticeDto.getEmail())
				.movieTitle(noticeDto.getMovieTitle()).contents(noticeDto.getContents())
				.regDt(noticeDto.getRegDt()).build()).collect(Collectors.toList());
	}

	/*
	 	후기 글 상세 보기
	 */
	@ApiOperation(value = "리뷰 상세 정보를 볼 수 있습니다.")
	@GetMapping("/detail/{noticeId}")
	public ReviewDetail getReviewDetail(@PathVariable Long noticeId) {
		return ReviewDetail.from(noticeService.getReviewDetail(noticeId));
	}

	/*
		영화 정보, 별점 평균 정보 보기
	 */
	@ApiOperation(value = "리뷰 등록 전, 해당 영화의 정보를 확인할 수 있습니다.", notes = "영화 정보, 등록된 리뷰 평균 별점")
	@GetMapping("/info/{movieCode}")
	public ViewMovieInfo getMovieByInfo(@PathVariable Long movieCode) {
		return noticeService.getInfoByMovie(movieCode);
	}

	/*
	 	영화 별 후기 리스트 보기
	 */
	@ApiOperation(value = "영화 별로 등록된 리뷰 목록을 확인할 수 있습니다.")
	@GetMapping("/info/list/{movieCode}")
	public List<ReviewByMovie> getReviewByMovie(@PathVariable Long movieCode) {

		return noticeService.getReviewByMovie(movieCode).stream().map(
				noticeDto -> ReviewByMovie.builder().email(noticeDto.getEmail())
					.movieTitle(noticeDto.getMovieTitle()).contents(noticeDto.getContents())
					.starRating(noticeDto.getStarRating()).regDt(noticeDto.getRegDt()).build())
			.collect(Collectors.toList());
	}

	@ApiOperation(value = "본인이 등록한 리뷰를 수정할 수 있습니다.")
	@PatchMapping("/detail/{noticeId}")
	@PreAuthorize("hasRole('READWRITE')")
	public ModifyReview.Response modifyReview(@PathVariable Long noticeId,
		@RequestHeader("Authorization") String token,
		@RequestBody @Valid ModifyReview.Request request) {

		log.info("token ::::::::::::::::::" + token);

		return ModifyReview.Response.from(noticeService.modifyReview(noticeId, token, request));
	}

	@ApiOperation(value = "본인이 등록한 리뷰를 삭제할 수 있습니다.")
	@DeleteMapping("/detail/{noticeId}")
	@PreAuthorize("hasRole('READWRITE')")
	public void deleteReview(@PathVariable Long noticeId,
		@RequestHeader("Authorization") String token, @RequestBody @Valid DeleteReview request) {
		noticeService.deleteReview(noticeId, token, request);
	}

}
