package com.mshoes.mshoes.services.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.mshoes.mshoes.mapper.OrderDetailMapper;
import com.mshoes.mshoes.mapper.UserMapper;
import com.mshoes.mshoes.models.*;
import com.mshoes.mshoes.models.request.OrderDetailRequest;
import com.mshoes.mshoes.models.request.UpdateOrderRequest;
import com.mshoes.mshoes.models.response.OrderDetailResponse;
import com.mshoes.mshoes.models.response.UserResponse;
import com.mshoes.mshoes.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.mshoes.mshoes.exception.ResourceNotFoundException;
import com.mshoes.mshoes.libraries.Utilities;
import com.mshoes.mshoes.mapper.ProductMapper;
import com.mshoes.mshoes.models.request.OrderItemRequest;
import com.mshoes.mshoes.models.response.OrderItemResponse;
import com.mshoes.mshoes.services.OrderDetailService;

import javax.transaction.Transactional;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

	private final OrderDetailRepository orderDetailRepository;

	private final UserRepository userRepository;

	private final SizeRepository sizeRepository;

	private final OrderItemRepository orderItemRepository;

	private final PaymentRepository paymentRepository;

	private final ProductMapper productMapper;

	private final OrderDetailMapper orderDetailMapper;

	private final UserMapper userMapper;

	private final Utilities utilities;

	@Autowired
	public OrderDetailServiceImpl(OrderDetailRepository orderDetailRepository, UserRepository userRepository,
								  SizeRepository sizeRepository, OrderItemRepository orderItemRepository, PaymentRepository paymentRepository, ProductMapper productMapper,
								  OrderDetailMapper orderDetailMapper, UserMapper userMapper, Utilities utilities) {
		this.orderDetailRepository = orderDetailRepository;
		this.userRepository = userRepository;
		this.sizeRepository = sizeRepository;
		this.orderItemRepository = orderItemRepository;
		this.paymentRepository = paymentRepository;
		this.productMapper = productMapper;
		this.orderDetailMapper = orderDetailMapper;
		this.userMapper = userMapper;
		this.utilities = utilities;
	}

	@Override
	@Transactional
	public List<OrderDetailResponse> getAllOrdersWithType1(Long userId, int type) {
		User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("USER", "ID", userId));
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByUserAndType(user, 1);
		if(orderDetails != null){
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return orderDetails.stream()
					.sorted(Comparator.comparing(o -> {
						try {
							return formatter.parse(((OrderDetail) o).getCreatedDate());
						} catch (ParseException e) {
							throw new IllegalArgumentException("Invalid date format: " + ((OrderDetail) o).getCreatedDate());
						}
					}).reversed())
					.map(this::mapToOrderDetailResponse)
					.toList();
		}else {
			return null;
		}
	}

	@Override
	@Transactional
	public Page<OrderDetailResponse> getAllOrdersByType1(int pageNumber, int pageSize, String sortBy) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
		Page<OrderDetail> orderDetailPage = orderDetailRepository.findAllByType(1, pageable);
		List<OrderDetail> orderDetails = orderDetailPage.getContent();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<OrderDetailResponse> orderDetailResponses = orderDetails.stream()
				.map(this::mapToOrderDetailResponse)
				.toList();
		return new PageImpl<>(orderDetailResponses, pageable, orderDetailPage.getTotalElements());
	}


	@Override
	@Transactional
	public Page<OrderDetailResponse> getAllOrdersByType1AndStatus(int status, int pageNumber, int pageSize, String sortBy) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
		Page<OrderDetail> orderDetailPage = orderDetailRepository.findAllByTypeAndStatus(1,status, pageable);
		List<OrderDetail> orderDetails = orderDetailPage.getContent();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<OrderDetailResponse> orderDetailResponses = orderDetails.stream()
				.map(this::mapToOrderDetailResponse)
				.toList();
		return new PageImpl<>(orderDetailResponses, pageable, orderDetailPage.getTotalElements());
	}

	@Override
	@Transactional
	public List<OrderDetailResponse> getAllOrdersWithType1ByStatus(Long userId, int type, int status) {
		User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("USER", "ID", userId));
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByUserAndTypeAndStatus(user, 1, status);
		if(orderDetails != null){
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return orderDetails.stream()
					.sorted(Comparator.comparing(o -> {
						try {
							return formatter.parse(((OrderDetail) o).getCreatedDate());
						} catch (ParseException e) {
							throw new IllegalArgumentException("Invalid date format: " + ((OrderDetail) o).getCreatedDate());
						}
					}).reversed())
					.map(this::mapToOrderDetailResponse)
					.toList();
		}else {
			return null;
		}
	}

	@Override
	@Transactional
	public OrderDetailResponse getOrderById(long orderDetailId) {
		OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId).orElseThrow();
		//Kiểm tra lại type
		if(orderDetail.getType() != 1){
			return null;
		}else {
			return this.mapToOrderDetailResponse(orderDetail);
		}

	}

	@Transactional
	@Override
	public void addToOrder(Long userId, OrderItemRequest orderItemRequest) {

		// mapper requestedOrderItem với OrderItem
		OrderItem orderItem = new OrderItem();

		// Lưu thông tin mới
		Size size = sizeRepository.findById(orderItemRequest.getSizeId())
				.orElseThrow(() -> new ResourceNotFoundException("Size", "id", orderItemRequest.getSizeId()));

		orderItem.setSize(size);
		orderItem.setQuantity(orderItemRequest.getQuantity());

		// Lấy thông tin người dùng đang đăng nhập
		User user = userRepository.findById(userId).orElseThrow();

		// Lấy thông tin của color
		Color color = size.getColor();

		// lấy thông tin product
		Product product = color.getProduct();
		// lưu và trả về kết quả

		// Lấy danh sách order của user trên và type = 0: chưa thanh toán
		OrderDetail checkOrderDetail = orderDetailRepository.findByUserAndType(user, 0);

		if (checkOrderDetail == null) {
			// trường hợp không còn giỏ hàng nào chưa thanh toán
			// Tạo 1 giỏ hàng mới với user sở hữu hiện tại, createdDate và type = 0
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setUser(user);
			orderDetail.setCreatedDate(utilities.getCurrentDate());
			orderDetail.setType(0);

			// Lưu giỏ hàng mới vào csdl
			OrderDetail newOrderDetail = orderDetailRepository.save(orderDetail);

			// Lưu thông tin OrderDetail cho OrderItem
			orderItem.setOrderDetail(newOrderDetail);
			OrderItem newOrderItem = orderItemRepository.save(orderItem);

			OrderItemResponse orderItemResponse = new OrderItemResponse();
			orderItemResponse.setId(newOrderItem.getId());
			orderItemResponse.setQuantity(newOrderItem.getQuantity());
			orderItemResponse.setProductItemResponse(productMapper.mapModelToProductItemResponse(product));
			orderItemResponse.setSize(size.getValue());
			orderItemResponse.setColor(color.getValue());

		} else {
			// Kiểm tra giỏ hàng đã tồn tại OrderItem này chưa
			Optional<OrderItem> checkOrderItem = Optional
					.ofNullable(orderItemRepository.findBySizeAndOrderDetail(size, checkOrderDetail));

			if (checkOrderItem.isEmpty()) {
				// Nếu chưa có Item đó trong giỏ hàng
				// Lưu thông tin OrderDetail cho OrderItem
				orderItem.setOrderDetail(checkOrderDetail);
				OrderItem newOrderItem = orderItemRepository.save(orderItem);

				OrderItemResponse orderItemResponse = new OrderItemResponse();
				orderItemResponse.setId(newOrderItem.getId());
				orderItemResponse.setQuantity(newOrderItem.getQuantity());
				orderItemResponse.setProductItemResponse(productMapper.mapModelToProductItemResponse(product));
				orderItemResponse.setSize(size.getValue());
				orderItemResponse.setColor(color.getValue());

			} else {
				OrderItem oldOrderItem = checkOrderItem
						.orElseThrow(() -> new ResourceNotFoundException("OrderItem", "id", -1L));
				// Nếu đã tồn tại Item trong giỏ hàng
				// Cập nhật lại số lượng
				int quantity = oldOrderItem.getQuantity() + orderItemRequest.getQuantity();

				oldOrderItem.setQuantity(quantity);

				OrderItem newOrderItem = orderItemRepository.save(oldOrderItem);

				OrderItemResponse orderItemResponse = new OrderItemResponse();
				orderItemResponse.setId(newOrderItem.getId());
				orderItemResponse.setQuantity(newOrderItem.getQuantity());
				orderItemResponse.setProductItemResponse(productMapper.mapModelToProductItemResponse(product));
				orderItemResponse.setSize(size.getValue());
				orderItemResponse.setColor(color.getValue());

			}
		}
	}


	private  OrderItemResponse toOrderItemResponse(OrderItem orderItem){
		OrderItemResponse orderItemResponse = new OrderItemResponse();

		//Lấy thông tin size
		Size size = orderItem.getSize();
		Color color = size.getColor();
		Product product = color.getProduct();

		orderItemResponse.setId(orderItem.getId());
		orderItemResponse.setQuantity(orderItem.getQuantity());
		orderItemResponse.setProductItemResponse(productMapper.mapModelToProductItemResponse(product));
		orderItemResponse.setSize(size.getValue());
		orderItemResponse.setColor(color.getValue());
		orderItemResponse.setSizeId(size.getId());

		return orderItemResponse;
	}

	@Transactional
	@Override
	public List<OrderItemResponse> getItemFromCart(Long userId) {
		//Lấy thông tin người dùng đang đăng nhập
		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User","id", userId));
		//Lấy thông tin giỏ hàng đang được set type = 0: định dạng chưa thanh toán
		OrderDetail orderDetail = orderDetailRepository.findByUserAndType(user, 0);

		//Kiểm tra kết quả trả về
		if(orderDetail != null){
			//Lấy danh sách orderItem có trong Giỏ hàng
			List<OrderItem> orderItems = orderItemRepository.findAllByOrderDetailId(orderDetail.getId());

			//Trả về kết quả
			return orderItems.stream().map(this::toOrderItemResponse).toList();
		}else {
			return null;
		}
	}

	@Transactional
	@Override
	public OrderDetailResponse getCartByUserAndType(Long userId, int type) {
		//Lấy thông tin người dùng
		User user = userRepository.findById(userId).orElseThrow();

		//Lấy thông tin người dùng và giỏ hàng hiện tại để thanh toán
		OrderDetail orderDetail = orderDetailRepository.findByUserAndType(user,type);

		if (orderDetail != null){
			//Chuyển đổi để trả về kết quả
			return this.mapToOrderDetailResponse(orderDetail);
		}else {
			return null;
		}

	}

	private OrderDetailResponse mapToOrderDetailResponse(OrderDetail orderDetail){
		//Chuyển đổi để trả về kết quả
		OrderDetailResponse orderDetailResponse = new OrderDetailResponse();

		orderDetailResponse.setId(orderDetail.getId());
		orderDetailResponse.setTotalAmount(orderDetail.getTotalAmount());
		orderDetailResponse.setTotalQuantity(orderDetail.getTotalQuantity());
		orderDetailResponse.setCreatedDate(orderDetail.getCreatedDate());
		orderDetailResponse.setType(orderDetail.getType());
		orderDetailResponse.setOrderCode(orderDetail.getOrderCode());
		orderDetailResponse.setStatus(orderDetail.getStatus());
		orderDetailResponse.setPhone(orderDetail.getPhone());
		orderDetailResponse.setAddress(orderDetail.getAddress());
		orderDetailResponse.setNotes(orderDetail.getNotes());
		if (orderDetail.getPayment() != null){
			orderDetailResponse.setPaymentType(orderDetail.getPayment().getType());
		}
		List<OrderItemResponse> orderItemResponses = orderDetail.getOrderItems().stream().map(this::toOrderItemResponse).toList();
		UserResponse userResponse = userMapper.mapModelToResponse(orderDetail.getUser());
		orderDetailResponse.setUserResponse(userResponse);
		orderDetailResponse.setOrderItemResponses(orderItemResponses);
		return orderDetailResponse;
	}

	@Override
	@Transactional
	public OrderDetailResponse checkOut(Long userId, OrderDetailRequest orderDetailRequest) {
		OrderDetail orderDetail = new OrderDetail();
		User user = userRepository.findById(userId).orElseThrow();

		orderDetail.setId(orderDetailRequest.getId());
		orderDetail.setTotalAmount(orderDetailRequest.getTotalAmount());
		orderDetail.setTotalQuantity(orderDetailRequest.getTotalQuantity());
		orderDetail.setCreatedDate(utilities.getCurrentDate());
		orderDetail.setOrderCode("HD" + orderDetailRequest.getId().toString() + "US" + userId);
		orderDetail.setUser(user);
		orderDetail.setAddress(orderDetailRequest.getAddress());
		orderDetail.setPhone(orderDetailRequest.getPhone());
		orderDetail.setStatus(0);
		orderDetail.setType(1);
		orderDetail.setNotes(orderDetailRequest.getNotes());
		orderDetail.setPayment(paymentRepository.findById(orderDetailRequest.getPaymentId()).orElseThrow());

		//Cập nhật lại số lượng và đã bán
		//Lấy danh sách orderItem có trong Giỏ hàng
		List<OrderItemResponse> orderItems = orderItemRepository.findAllByOrderDetailId(orderDetail.getId()).stream().map(this::toOrderItemResponse).toList();

		for (OrderItemResponse itemResponse : orderItems) {
			//lấy size
			Size size = sizeRepository.findById(itemResponse.getSizeId()).orElseThrow();
			size.setSold(size.getSold() + itemResponse.getQuantity());
			size.setTotal(size.getTotal() - itemResponse.getQuantity());

			sizeRepository.save(size);
		}

		orderDetailRepository.save(orderDetail);
		return this.mapToOrderDetailResponse(orderDetail);
	}


	//Phương thức tăng giảm số lượng Item trong giỏ hàng
	@Override
	@Transactional
	public void plusOrMinusQuantity(Long orderItemId, String operation) {

		// Kiểm tra xem yêu cầu là tăng hay giảm số lượng
		if (Objects.equals(operation, "p")) {
			// Lấy số lượng cũ
			OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow();

			orderItem.setQuantity(orderItem.getQuantity() + 1);

			this.toOrderItemResponse(orderItem);
		} else if (Objects.equals(operation, "m")) {
			// Lấy số lượng cũ
			OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow();
			if(orderItem.getQuantity() <= 1){
				this.toOrderItemResponse(orderItem);
			}else {
				orderItem.setQuantity(orderItem.getQuantity() - 1);
				this.toOrderItemResponse(orderItem);
			}
		}
	}

	@Override
	public int countItemFromCart(Long userId) {
		//Lấy giỏ hàng
		User user = userRepository.findById(userId).orElseThrow();
		OrderDetail orderDetail = orderDetailRepository.findByUserAndType(user,0);
		if(orderDetail == null){
			return 0;
		}else {
			//Lấy danh sách orderItem có trong Giỏ hàng
			List<OrderItem> orderItems = orderItemRepository.findAllByOrderDetailId(orderDetail.getId());
			return orderItems.size();
		}

	}

	@Override
	public long countOrderByType(int type) {
		return orderDetailRepository.countByType(type);
	}

	@Override
	@Transactional
	public OrderDetailResponse cancelOrder(Long userId, Long orderDetailId) {
		//Lấy giỏ hàng
		User user = userRepository.findById(userId).orElseThrow();
		OrderDetail orderDetail = orderDetailRepository.findByUserAndId(user,orderDetailId);
		if(orderDetail == null){
			return null;
		}else {
			orderDetail.setStatus(4);
			//Lấy danh sách orderItem có trong Giỏ hàng
			return this.mapToOrderDetailResponse(orderDetailRepository.save(orderDetail));
		}
	}

	@Override
	@Transactional
	public OrderDetailResponse updateOrder(Long orderDetailId, UpdateOrderRequest updateOrderRequest) {
		OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId).orElseThrow();
		if(updateOrderRequest.getStatus() == -1){
			updateOrderRequest.setStatus(orderDetail.getStatus());
		}
		orderDetailMapper.updateModel(orderDetail, updateOrderRequest);
		return this.mapToOrderDetailResponse(orderDetail);
	}
}
