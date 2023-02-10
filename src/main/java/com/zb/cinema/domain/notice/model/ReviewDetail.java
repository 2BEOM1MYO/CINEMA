package com.zb.cinema.domain.notice.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDetail {

	private String movieTitle;
	private String email;
	private int starRating;
	private String contents;
	private LocalDateTime regDt;
	private LocalDateTime updateDt;

	public static ReviewDetail from(NoticeDto noticeDto) {

		return ReviewDetail.builder().contents(noticeDto.getContents())
			.starRating(noticeDto.getStarRating()).movieTitle(noticeDto.getMovieTitle())
			.email(noticeDto.getEmail()).regDt(noticeDto.getRegDt())
			.updateDt(noticeDto.getUpdateDt()).build();
	}
}
