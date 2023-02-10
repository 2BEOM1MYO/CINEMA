package com.zb.cinema.domain.admin.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SeatModel {

    private long id;
    private String seatNum;
    private long price;
    private boolean isUsing;
}
