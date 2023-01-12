package com.zb.cinema.movie.type;

import lombok.Getter;

@Getter
public enum MovieStatus {

    STATUS_SHOWING("상영중"),
    STATUS_WILL("상영예정"),
    STATUS_OVER("상영종료");

    MovieStatus(String value) {
        this.value = value;
    }

    private String value;
}