package com.zb.cinema.admin.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriumSchedule {

    private long theater_id;
    private long auditorium_id;
    private String theater_nm;
    private long movie_id;
    private String title;
    private LocalDateTime startDt;
    private LocalDateTime endDt;
}