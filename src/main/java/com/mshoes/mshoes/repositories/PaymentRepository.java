package com.mshoes.mshoes.repositories;

import com.mshoes.mshoes.models.OrderDetail;
import com.mshoes.mshoes.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByOrderDetail(OrderDetail orderDetail);
}
