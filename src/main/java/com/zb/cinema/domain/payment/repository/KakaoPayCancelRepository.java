package com.zb.cinema.domain.payment.repository;

import com.zb.cinema.domain.payment.entity.KakaoPayCancel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KakaoPayCancelRepository extends JpaRepository<KakaoPayCancel, Long> {

}
