package com.zb.cinema.movie.entity;

import com.zb.cinema.movie.type.MovieStatus;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Movie {

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

    @Column
    @Enumerated(EnumType.STRING)
    private MovieStatus status;

}
