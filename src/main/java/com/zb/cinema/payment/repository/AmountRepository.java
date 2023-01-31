package com.zb.cinema.payment.repository;

import com.zb.cinema.payment.entity.Amount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmountRepository extends JpaRepository<Amount, Long> {

}
