package com.zb.cinema.notice.model;

import com.zb.cinema.notice.entity.Notice;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class NoticeDto {

	private Long movieCode;
	private String movieTitle;
	private String email;
	private int starRating;
	private String contents;
	private LocalDateTime regDt;
	private LocalDateTime updateDt;

	public static NoticeDto from(Notice notice) {
		return NoticeDto.builder().contents(notice.getContents()).starRating(notice.getStarRating())
			.movieCode(notice.getNoticeMovie().getCode())
			.movieTitle(notice.getNoticeMovie().getTitle())
			.email(notice.getNoticeMember().getEmail()).regDt(notice.getRegDt())
			.updateDt(notice.getUpdateDt()).build();
	}

}
