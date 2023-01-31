package com.zb.cinema.payment.repository;

import com.zb.cinema.payment.entity.KakaoPayApproval;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<KakaoPayApproval, Long> {

	Optional<KakaoPayApproval> findByTid(String parameter);
}
