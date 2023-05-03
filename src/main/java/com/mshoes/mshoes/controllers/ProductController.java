package com.mshoes.mshoes.controllers;

import com.mshoes.mshoes.models.dtos.CategoryDTO;
import com.mshoes.mshoes.models.response.ColorResponse;
import com.mshoes.mshoes.models.response.ProductResponse;
import com.mshoes.mshoes.models.response.SizeResponse;
import com.mshoes.mshoes.models.response.UserResponse;
import com.mshoes.mshoes.services.CategoryService;
import com.mshoes.mshoes.services.OrderDetailService;
import com.mshoes.mshoes.services.ProductService;
import com.mshoes.mshoes.utils.GetUserFromToken;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/home/product")
public class ProductController {

    private final ProductService productService;

    private final CategoryService categoryService;

    private final GetUserFromToken getUserFromToken;
    private final OrderDetailService orderDetailService;

    public ProductController(ProductService productService, CategoryService categoryService, GetUserFromToken getUserFromToken, OrderDetailService orderDetailService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.getUserFromToken = getUserFromToken;
        this.orderDetailService = orderDetailService;
    }

    @ModelAttribute("userLogined")
    public UserResponse getUserLogined(ModelMap model, HttpServletRequest request) {
        return getUserFromToken.getUserFromToken(request);
    }
    @ModelAttribute("cartItem")
    public int countCartItem(HttpServletRequest request){
        UserResponse user = getUserFromToken.getUserFromToken(request);
        if (user != null){
            return orderDetailService.countItemFromCart(user.getId());
        }else {
            return 0;
        }
    }
    @GetMapping
    public String viewProducts(ModelMap model, HttpServletRequest request,
                               @RequestParam(defaultValue = "0") int pageNumber,
                               @RequestParam(defaultValue = "20") int pageSize,
                               @RequestParam(defaultValue = "name") String sortBy){
        model.addAttribute("countProduct",productService.countProduct());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("pageSize", pageSize);
        List<ProductResponse> productResponses = productService.getAllProducts(pageNumber,pageSize, sortBy).stream().map(p -> {
            ProductResponse productResponse = new ProductResponse();
            productResponse.setId(p.getId());
            productResponse.setName(p.getName());
            productResponse.setSku(p.getSku());
            productResponse.setPrice(p.getPrice());
            productResponse.setDiscountPrice(p.getDiscountPrice());
            productResponse.setColorResponses(p.getColorResponses());
            productResponse.setImageResponses(p.getImageResponses());
            productResponse.setDescription(p.getDescription());
            productResponse.setVisited(p.getVisited());
            return productResponse;
        }).collect(Collectors.toList());
        model.addAttribute("products",productResponses);
        return "web/product";
    }

    @GetMapping("/detail/{id}")
    public String viewDetailProduct(ModelMap model, HttpServletRequest request, HttpServletResponse response, @PathVariable(name = "id") Long id){
        //Lấy thông tin sản phẩm xem chi tiết
        ProductResponse productResponse = productService.getProductById(id);
        model.addAttribute("productDetail", productResponse);

        // Lấy thông tin danh mục của sản phẩm xem chi tiết
        CategoryDTO categoryDTO = categoryService.getCategoryById(productResponse.getCategoryId());
        model.addAttribute("categoryTitle", categoryDTO.getTitle());

        // Lấy ra mảng Color và mảng Size
        List<ColorResponse> colorResponses = productResponse.getColorResponses();

        // Lấy dánh sách màu sắc trong sản phẩm
        List<String> listColors = colorResponses.stream()
                .map(ColorResponse::getValue)
                .distinct()
                .toList();

        // Lấy danh sách size
        List<String> listSizes = colorResponses.stream()
                .flatMap(color -> color.getSizeResponses().stream())
                .map(SizeResponse::getValue)
                .distinct()
                .sorted(Comparator.comparingInt(Integer::parseInt))
                .toList();

        // Lấy thông tin số lượng đã bán của sản phẩm
        int sold = colorResponses.stream()
                .flatMap(color -> color.getSizeResponses().stream())
                .mapToInt(SizeResponse::getSold)
                .sum();

        //Truyền về số lượng của từ sản phẩm theo cặp MÀU-SIZE
        Map<String, String> totalByColorAndSize = colorResponses.stream()
                .flatMap(color -> color.getSizeResponses().stream()
                        .map(size -> {
                            String key = color.getValue().replaceAll("\\s+", "") + "-" + size.getValue();
                            String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8);
                            return Map.entry(encodedKey, size.getTotal() +"-"+ size.getId());
                        }))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));

        // Gửi dữ liệu đi bằng model map
        model.addAttribute("listColors",listColors);
        model.addAttribute("listSizes",listSizes);
        model.addAttribute("sold", sold);
        model.addAttribute("totalByColorAndSize", totalByColorAndSize);

        //Lấy sản phẩm liên quan (Sản phẩm có cùng danh mục)
        List<ProductResponse> productResponsesByCate = productService.getProductsByCategoryId(categoryDTO.getId(), 0, 5, "visited").stream().map(p -> {
            ProductResponse product = new ProductResponse();
            product.setId(p.getId());
            product.setName(p.getName());
            product.setSku(p.getSku());
            product.setPrice(p.getPrice());
            product.setDiscountPrice(p.getDiscountPrice());
            product.setColorResponses(p.getColorResponses());
            product.setImageResponses(p.getImageResponses());
            product.setDescription(p.getDescription());
            product.setVisited(p.getVisited());
            return product;
        }).collect(Collectors.toList());
        // Lưu request của trang hiện tại vào session
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        requestCache.saveRequest(request, response);

        model.addAttribute("productsByCate", productResponsesByCate);
        return"web/productDetail";
    }

}
