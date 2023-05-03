package com.mshoes.mshoes.repositories;

import com.mshoes.mshoes.models.response.OrderDetailResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mshoes.mshoes.models.OrderDetail;
import com.mshoes.mshoes.models.User;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
	// Lấy thông tin giỏ hàng chưa thanh toán ( type = 0)
	OrderDetail findByUserAndType(User user, int type);

	List<OrderDetail> findAllByUserAndType(User user, int type);

	OrderDetail findByUserAndTypeAndStatus(User user, int type, int status);

	List<OrderDetail> findAllByUserAndTypeAndStatus(User user, int type, int status);

	OrderDetail findByUserAndId(User user, Long orderDetailId);
}
