package com.zb.cinema.admin.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InputAuditorium {

    private long theaterId;
    private long movieCode;
    private long price;
    private long seatNum;
    private String startDt;
}
