package com.zb.cinema.domain.notice.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeException extends RuntimeException {

	private NoticeError noticeError;
	private String error;

	public NoticeException(NoticeError noticeError) {
		super(noticeError.getDescription());
		this.noticeError = noticeError;
	}
}
