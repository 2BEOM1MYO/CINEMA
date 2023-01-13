package com.zb.cinema.admin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InputAuditorium {

    private long theater_id;
    private long movieCode;
    private long price;
    private long seatNum;
    private String startDt;
}
