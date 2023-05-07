package com.mshoes.mshoes.services;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.mshoes.mshoes.models.request.ProductRequest;
import com.mshoes.mshoes.models.response.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
	/**
	 * Method get all product is enable(product_status=1) in database <br>
	 * <u><i>Update: 02/03/2023</i></u>
	 *
	 * @return ProductDTO
	 */
	Page<ProductResponse> getAllProducts(int pageNumber, int pageSize, String sortBy);

	long countProduct();
	/**
	 * Method get all product is enabled (product_status=1) by category in database
	 * <br>
	 * <u><i>Update: 06/03/2023</i></u>
	 *
	 * @return ProductDTO
	 */
	Page<ProductResponse> getProductsByCategoryId(long categoryId, int pageNumber, int pageSize, String sortBy);

	/**
	 * Method get products with String "search" <br>
	 * <u><i>Update: 16/04/2023</i></u>
	 *
	 */
	Optional<List<ProductResponse>> searchProducts(String search);

	/**
	 * Method get a product by product_id in database <br>
	 * <u><i>Update: 02/03/2023</i></u>
	 *
	 */
	ProductResponse getProductById(long productID);

	/**
	 * Method create new product<br>
	 * <u><i>Update: 02/03/2023</i></u>
	 *
	 */
	ProductResponse createProduct(ProductRequest productRequest, MultipartFile[] images) throws IOException;

	/**
	 * Method update detail a product with new information and product_id<br>
	 * <u><i>Update: 02/03/2023</i></u>
	 *
	 */
	ProductResponse updateProduct(ProductRequest productRequest, long productId);

	/**
	 * Method delete (change product_status to value 0) change enable product to
	 * disable product<br>
	 * <u><i>Update: 02/03/2023</i></u>
	 *
	 */
	void deleteProductById(long productId);

	void actionProduct(Long productId,int action);
}
