package com.mshoes.mshoes.mapper;

import com.mshoes.mshoes.models.Payment;
import com.mshoes.mshoes.models.response.PaymentResponse;
import org.mapstruct.Mapper;

@Mapper
public interface PaymentMapper {
    PaymentResponse mapModelToResponse(Payment payment);
}
