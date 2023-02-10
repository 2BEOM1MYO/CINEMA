package com.zb.cinema.domain.payment.repository;

import com.zb.cinema.domain.payment.entity.KakaoPayApproval;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<KakaoPayApproval, Long> {

	Optional<KakaoPayApproval> findByTid(String parameter);
}
