package com.zb.cinema.domain.movie.model.request.kobis.movieInfo;

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
public class Companys {

    private String companyCd;
    private String companyNm;
    private String companyNmEn;
    private String companyPartNm;
}
