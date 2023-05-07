package com.mshoes.mshoes.services.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.mshoes.mshoes.models.*;
import com.mshoes.mshoes.repositories.*;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mshoes.mshoes.exception.ResourceNotFoundException;
import com.mshoes.mshoes.libraries.Utilities;
import com.mshoes.mshoes.mapper.ColorMapper;
import com.mshoes.mshoes.mapper.ImageMapper;
import com.mshoes.mshoes.mapper.ProductMapper;
import com.mshoes.mshoes.mapper.SizeMapper;
import com.mshoes.mshoes.models.request.ColorRequest;
import com.mshoes.mshoes.models.request.ProductRequest;
import com.mshoes.mshoes.models.request.SizeRequest;
import com.mshoes.mshoes.models.response.ProductResponse;
import com.mshoes.mshoes.services.ProductService;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;


	private final ImageRepository imageRepository;

	private final CategoryRepository categoryRepository;

	private final ColorRepository colorRepository;


	private final SizeRepository sizeRepository;


	private final ProductMapper productMapper;

	private final ImageMapper imageMapper;

	private final ColorMapper colorMapper;

	private final SizeMapper sizeMapper;


	private final Utilities utilities;

	@Autowired
	public ProductServiceImpl(ProductRepository productRepository, ImageRepository imageRepository,
							  CategoryRepository categoryRepository, ColorRepository colorRepository, SizeRepository sizeRepository, ProductMapper productMapper,
							  ImageMapper imageMapper, ColorMapper colorMapper, SizeMapper sizeMapper, Utilities utilities) {
		this.productRepository = productRepository;
		this.imageRepository = imageRepository;
		this.categoryRepository = categoryRepository;
		this.colorRepository = colorRepository;
		this.sizeRepository = sizeRepository;
		this.productMapper = productMapper;
		this.imageMapper = imageMapper;
		this.colorMapper = colorMapper;
		this.sizeMapper = sizeMapper;
		this.utilities = utilities;
	}

	private void saveColorsAndSizes(List<ColorRequest> colorRequests, long productId) {
		if (colorRequests != null) {
			for (ColorRequest colorRequest : colorRequests) {
				colorRequest.setProductId(productId);
				Color color = colorMapper.mapRequestedToModel(colorRequest);
				color.setSizes(null);
				Color newColor = colorRepository.save(color);
				long newColorId = newColor.getId();
				if (newColorId == 0) {
					break;
				} else {
					List<SizeRequest> sizeRequests = colorRequest.getSizes().stream().toList();
					for (SizeRequest sizeRequest : sizeRequests) {
						sizeRequest.setColorId(newColorId);
						Size size = sizeMapper.mapRequestedToModel(sizeRequest);
						size.setSold(0);
						sizeRepository.save(size);
					}
				}

			}
		}

	}

	private void saveImages(MultipartFile[] images, long productId) throws IOException {
		// Lặp qua từng file trong danh sách images.
		for (MultipartFile file : images) {
			// Lấy tên file và extension.
			String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
			String extension = FilenameUtils.getExtension(filename);

			// Tạo đường dẫn tới file ảnh.
			String imagePath = "/assets/images/uploads/products/" + filename;

			// Tạo file mới với đường dẫn được chỉ định.
			
			File savedFile = new File("D:/DATN2023/src/main/resources/static/assets/images/uploads/products/"+ filename);

			// Lưu file vào đường dẫn.
			try (OutputStream outputStream = new FileOutputStream(savedFile)) {
				outputStream.write(file.getBytes());
			}
			// Tạo một đối tượng Image mới và thiết lập thuộc tính url.
			Image image = new Image();
			image.setUrl(imagePath);
			image.setProduct(productRepository.findById(productId).orElseThrow());

			// Lưu đối tượng Image vào cơ sở dữ liệu.
			imageRepository.save(image);
		}
	}

	@Transactional
	@Override
	public ProductResponse createProduct(ProductRequest productRequest, MultipartFile[] images) throws IOException {
		String str = productRequest.getName();
		String[] parts = str.split(" ");
		StringBuilder sb = new StringBuilder();
		int count = 0; // biến đếm
		for (String part : parts) {
			if (count >= 3) { // kiểm tra giới hạn 3 kí tự
				break;
			}
			sb.append(part.charAt(0)); // lấy kí tự đầu tiên của phần tử
			count++; // tăng biến đếm
		}

		String sku = sb.toString() +"U"+ String.valueOf(productRequest.getUserId()) +"P"+ String.valueOf(productRepository.findNewestId());
		sku = sku.toUpperCase(); // chuyển thành chữ hoa

		productRequest.setSku(sku);
		Product product = productMapper.mapRequestedToModel(productRequest);

		// set current date
		product.setCreatedDate(utilities.getCurrentDate());
		product.setModifiedDate(utilities.getCurrentDate());

		product.setVisited(-1);
		product.setStatus(1);

		product.setImages(null);
		// Save new product to database
		Product newProduct = productRepository.save(product);

		// Get id of product create recently
		long productId = newProduct.getId();

		List<ColorRequest> colorRequests = productRequest.getColors();
		this.saveColorsAndSizes(colorRequests, productId);

		// Save image into table image
		this.saveImages(images, productId);

		return this.getProductById(productId);
	}

	// Save color and size into database

	/**
	 * Lấy danh sách tất cả sản phẩm, phân trang theo
	 */
	@Transactional
	@Override
	public Page<ProductResponse> getAllProducts(int pageNumber, int pageSize, String sortBy) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
		Page<Product> products = productRepository.findAll(pageable);
		return products.map(productMapper::toProductResponse);
	}

	@Override
	public long countProduct() {
		return productRepository.count();
	}

	/**
	 * Lấy danh sách sản phẩm theo mã danh mục
	 */
	@Transactional
	@Override
	public Page<ProductResponse> getProductsByCategoryId(long categoryId, int pageNumber, int pageSize, String sortBy) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
		Page<Product> products = productRepository.findByCategoryId(categoryId, pageable);
		return products.map(productMapper::toProductResponse);
	}

	@Override
	public Optional<List<ProductResponse>> searchProducts(String search) {
		List<Product> products = productRepository.searchProducts(search);

		if (!products.isEmpty()) {
			List<ProductResponse> productResponses = productMapper.mapModelsToResponses(products);
			return Optional.of(productResponses);
		} else {
			return Optional.empty();
		}
	}

	/**
	 * Lấy thông tin một sản phẩm theo product_id
	 */
	@Transactional
	@Override
	public ProductResponse getProductById(long productId) {
		productRepository.incrementVisitedById(productId);
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
		return productMapper.toProductResponse(product);
	}

	@Override
	@Transactional
	public ProductResponse updateProduct(ProductRequest productRequest, long productId) {
		// get product by id from the database
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

		if(!Objects.equals(product.getCategory().getId(), productRequest.getCategoryId())){
			Category category = categoryRepository.findById(productRequest.getCategoryId()).orElseThrow();
			product.setCategory(category);
		}
		if (!Objects.equals(product.getDescription(), productRequest.getDescription())){
			product.setDescription(productRequest.getDescription());
		}
		if (!Objects.equals(product.getName(), productRequest.getName())){
			product.setName(productRequest.getName());
		}
		if (!Objects.equals(product.getPrice(), productRequest.getPrice())){
			product.setPrice(productRequest.getPrice());
		}
		if (!Objects.equals(product.getDiscountPrice(), productRequest.getDiscountPrice())){
			product.setDiscountPrice(productRequest.getDiscountPrice());
		}
		product.setModifiedDate(utilities.getCurrentDate());

		Product responseProduct = productRepository.save(product);

		return productMapper.toProductResponse(responseProduct);
	}

	@Override
	public void deleteProductById(long productId) {
		// get product by id from the database
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
		try {
			productRepository.delete(product);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print("Ex: " + e);
		}

	}

	@Override
	public void actionProduct(Long productId, int action) {
		Product product = productRepository.findById(productId).orElseThrow();
		try{
			if(action == 0){
				product.setStatus(1);
			}else if (action == 1){
				product.setStatus(0);
			}
			productRepository.save(product);
		}catch (Exception ex){
			System.out.print("Ex: " + ex);
		}
	}

}
