package com.zb.cinema.admin.repository;

import com.zb.cinema.admin.entity.MovieInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieInfoRepository extends JpaRepository<MovieInfo, Long> {

}
