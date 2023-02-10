package com.zb.cinema.domain.movie.model.request.kobis.movieInfo;

import java.util.List;
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
public class MovieInfo {

    private String movieCd;
    private String movieNm;
    private String movieNmEn;
    private String movieNmOg;
    private String showTm;
    private String prdtYear;
    private String openDt;
    private String prdtStatNm;
    private String typeNm;
    private List<Nations> nations;
    private List<Genres> genres;
    private List<Directors> directors;
    private List<Actors> actors;
    private List<ShowTypes> showTypes;
    private List<Companys> companys;
    private List<Audits> audits;
    private List<Staffs> staffs;
}
