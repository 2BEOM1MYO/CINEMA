package com.zb.cinema.movie.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMessage {

    private ResponseMessageHeader header;
    private Object body;

    public static ResponseMessage fail(String message) {
        return fail(message, null);
    }

    public static ResponseMessage fail(String message, Object data) {
        return ResponseMessage.builder()
            .header(ResponseMessageHeader.builder()
                .result(false)
                .resultCode("")
                .message(message)
                .status(HttpStatus.BAD_REQUEST.value())
                .build())
            .body(data)
            .build();
    }

    public static ResponseMessage success(Object data) {
        return ResponseMessage.builder()
            .header(ResponseMessageHeader.builder()
                .result(true)
                .resultCode("")
                .message("")
                .status(HttpStatus.OK.value())
                .build())
            .body(data)
            .build();
    }

    public static ResponseMessage success() {
        return success(null);
    }
}
