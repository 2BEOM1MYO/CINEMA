package com.zb.cinema.ticketing.repository;

import com.zb.cinema.payment.model.KakaoPayApprovalVO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<KakaoPayApprovalVO, Long> {

}
