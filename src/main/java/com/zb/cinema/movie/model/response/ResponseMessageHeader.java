package com.zb.cinema.movie.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMessageHeader {

    private boolean result;
    private String resultCode;
    private String message;
    private int status;
}
