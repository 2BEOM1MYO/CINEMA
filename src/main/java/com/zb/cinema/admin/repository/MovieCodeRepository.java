package com.zb.cinema.admin.repository;

import com.zb.cinema.admin.entity.MovieCode;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieCodeRepository extends JpaRepository<MovieCode, Long> {

    List<MovieCode> findByTitleContaining(String movieNm);
}
