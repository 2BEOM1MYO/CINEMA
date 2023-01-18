package com.zb.cinema.admin.repository;

import com.zb.cinema.admin.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, Long> {

    long countByAreaAndCityAndName(String area, String city, String name);
}
