package com.zb.cinema.notice.respository;

import com.zb.cinema.member.entity.Member;
import com.zb.cinema.movie.entity.MovieCode;
import com.zb.cinema.notice.entity.Notice;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

	Optional<Notice> findByNoticeMovieAndNoticeMember(MovieCode noticeMovie, Member noticeMember);

	Page<Notice> findAllByOrderByRegDt(Pageable pageable);

	Optional<Notice> findById(Long noticeId);

	Page<Notice> findByNoticeMovieCode(Long noticeMovie, Pageable pageable);

	@Query("select avg(m.starRating)from Notice m "
		+ "where m.noticeMovie.code in (:movieCode) group by m.noticeMovie.code")
	Double getByNoticeMovieCode(Long movieCode);

	void deleteAllById(Long noticeId);
}
