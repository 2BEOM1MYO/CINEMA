package com.zb.cinema.movie.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INVALID_INPUT("입력값이 정확하지 않습니다."),
    MOVIE_NOT_FOUND("영화가 없습니다."),
    MANY_MOVIE_FOUND("여러개의 영화가 조회되었습니다.");

    private final String description;
}
