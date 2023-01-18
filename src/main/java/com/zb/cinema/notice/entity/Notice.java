package com.zb.cinema.notice.entity;

import com.zb.cinema.member.entity.Member;
import com.zb.cinema.movie.entity.MovieCode;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class Notice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Member noticeMember;

	@ManyToOne
	private MovieCode noticeMovie;

	private int starRating;
	private String contents;
	private LocalDateTime regDt;
	private LocalDateTime updateDt;

}
