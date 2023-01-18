package com.zb.cinema.notice.model;

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
public class ReviewAllList {

	private String movieTitle;
	private String email;
	private String contents;
	private LocalDateTime regDt;

}
