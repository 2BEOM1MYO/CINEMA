package com.zb.cinema.domain.movie.model.request.kobis.movieInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieInfoResult {

    private MovieInfo movieInfo;
    private String source;
}
