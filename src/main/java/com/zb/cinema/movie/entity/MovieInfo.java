package com.zb.cinema.movie.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MovieInfo {

    @Id
    private long code;

    @Column
    private String title;

    @Column
    private String actors;

    @Column
    private String directors;

    @Column
    private String genre;

    @Column
    private String nation;

    @Column
    private long runTime;

    @Column
    private LocalDateTime openDt;

}
