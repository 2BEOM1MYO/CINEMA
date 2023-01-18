package com.zb.cinema.movie.type;

import java.util.regex.PatternSyntaxException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INVALID_INPUT("입력값이 정확하지 않습니다.")
    ,MOVIE_NOT_FOUND("영화가 없습니다.")
    ,MANY_MOVIE_FOUND("여러개의 영화가 조회되었습니다.")
    ,MOVIE_ALREADY_SHOWING("이미 영화가 상영중입니다.")
    ,MOVIE_ALREADY_NOT_SHOWING("이미 영화가 상영중이지 않습니다.")
    ,MOVIE_ALREADY_WILL_SHOWING("이미 영화가 상영 예정입니다.")
    ,THEATER_NOT_FOUND("극장이 없습니다.")
    ,MOVIE_NOT_SHOWING("상영중인 영화가 아닙니다.")
    ,AUDITORIUM_ALREADY_EXIST("해당 상영관에 이미 일정이 있습니다.")
    ,INVALID_ACCESS_MEMBER("일반 회원은 해당 권한이 없습니다.")

    ;

    private final String description;
}
