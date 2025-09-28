package com.sb_ecom.backend_ecom.repo;

import com.sb_ecom.backend_ecom.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
