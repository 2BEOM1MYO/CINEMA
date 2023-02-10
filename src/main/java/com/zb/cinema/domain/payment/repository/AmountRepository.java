package com.zb.cinema.domain.payment.repository;

import com.zb.cinema.domain.payment.entity.Amount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmountRepository extends JpaRepository<Amount, Long> {

}
