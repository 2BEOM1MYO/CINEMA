package com.zb.cinema.notice.model;

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
public class ViewMovieInfo {

	private String movieTitle;
	private String actors;
	private String directors;
	private String genre;
	private String nation;
	private double ratingAvg;

}
