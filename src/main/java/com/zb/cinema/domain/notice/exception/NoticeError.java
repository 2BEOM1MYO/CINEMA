package com.zb.cinema.domain.notice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NoticeError {

	MOVIE_TITLE_NOT_FOUND("해당 영화 제목을 찾을 수 없습니다."), MOVIE_REVIEW_NO_DUPLICATION(
		"이미 리뷰 등록한 영화는 추가 등록할 수 없습니다."), MOVIE_REVIEW_ID_NOT_FOUND(
		"해당 영화 리뷰를 찾을 수 없습니다."), MOVIE_REVIEW_NOT_FOUND(
		"등록된 리뷰가 없습니다."), MOVIE_REVIEW_USER_UN_MATCH("본인이 등록한 후기가 아닙니다."), MOVIE_STATUS_WILL(
		"상영 예정 영화는 리뷰 등록이 불가능 합니다.");

	private final String description;
}
