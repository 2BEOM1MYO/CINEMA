package com.zb.cinema.admin.model.response;

import java.time.LocalDateTime;
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
public class AuditoriumSchedule {

    private long theaterId;
    private String auditoriumNm;
    private String theaterNm;
    private long movieId;
    private String title;
    private LocalDateTime startDt;
    private LocalDateTime endDt;
}