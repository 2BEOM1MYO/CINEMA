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
public class ReviewByMovie {

	private String movieTitle;
	private String email;
	private String contents;
	private int starRating;
	private LocalDateTime regDt;

	public static ReviewByMovie from(NoticeDto noticeDto) {

		return ReviewByMovie.builder().movieTitle(noticeDto.getMovieTitle())
			.email(noticeDto.getEmail()).contents(noticeDto.getContents())
			.starRating(noticeDto.getStarRating())
			.regDt(noticeDto.getRegDt()).build();
	}

}
