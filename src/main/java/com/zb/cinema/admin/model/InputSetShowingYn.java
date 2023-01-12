package com.zb.cinema.admin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InputSetShowingYn {

    private Long movieCode;

    private boolean showingYn;
}
