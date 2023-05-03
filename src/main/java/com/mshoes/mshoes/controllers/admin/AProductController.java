package com.mshoes.mshoes.controllers.admin;

import com.mshoes.mshoes.models.request.ColorRequest;
import com.mshoes.mshoes.models.request.ProductRequest;
import com.mshoes.mshoes.models.request.SizeRequest;
import com.mshoes.mshoes.models.response.ProductResponse;
import com.mshoes.mshoes.models.response.UserResponse;
import com.mshoes.mshoes.repositories.CategoryRepository;
import com.mshoes.mshoes.utils.GetUserFromToken;
import com.mshoes.mshoes.services.ProductService;
import com.mshoes.mshoes.utils.JwtUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AProductController {

    private final ProductService productService;

    private final CategoryRepository categoryRepository;

    private final GetUserFromToken getUserFromToken;

    private final JwtUtils jwtUtils;
    @Autowired
    public AProductController(ProductService productService, CategoryRepository categoryRepository, GetUserFromToken getUserFromToken, JwtUtils jwtUtils) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
        this.getUserFromToken = getUserFromToken;
        this.jwtUtils = jwtUtils;
    }
    //Lấy thông tin người dùng
    @ModelAttribute("userLogined")
    public UserResponse getUserLogined(ModelMap model, HttpServletRequest request) {
        return getUserFromToken.getUserFromToken(request);
    }

    @GetMapping("/product")
    public String getAllProduct(Model model, HttpServletRequest request,
                                @RequestParam(defaultValue = "0") int pageNumber,
                                @RequestParam(defaultValue = "10") int pageSize,
                                @RequestParam(defaultValue = "id") String sortBy){
        model.addAttribute("countProduct",productService.countProduct());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("pageSize", pageSize);
        List<ProductResponse> productResponses = productService.getAllProducts(pageNumber,pageSize, sortBy).stream().map(p -> {
            ProductResponse productResponse = new ProductResponse();
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
        return "admin/products";
    }

    @GetMapping("/product/create")
    public String createProduct(ModelMap model){
        model.addAttribute("categoryList", categoryRepository.findAll());
        ProductRequest productRequest = new ProductRequest();
        model.addAttribute("productRequest", productRequest);
        return "admin/newProduct";
    }

    @PostMapping("/product/create")
    public String createProduct(ModelMap model, @ModelAttribute("productRequest") ProductRequest productRequest
                                , @RequestParam("images") MultipartFile[] images
                                , @RequestParam("jsonColors") String jsonColors
                                , HttpServletRequest request
                                )throws IOException {

        JSONObject jsonObj = new JSONObject(jsonColors);

        JSONArray colorsArr = jsonObj.getJSONArray("colors");
        List<ColorRequest> colorRequests = new ArrayList<>();

        for (int i = 0; i < colorsArr.length(); i++) {
            ColorRequest colorRequest = new ColorRequest();
            JSONObject colorObj = colorsArr.getJSONObject(i);
            String value = colorObj.getString("value");
            colorRequest.setValue(value);
            JSONArray sizesArr = colorObj.getJSONArray("sizes");
            List<SizeRequest> sizeRequests = new ArrayList<>();
            for (int j = 0; j < sizesArr.length(); j++) {
                JSONObject sizeObj = sizesArr.getJSONObject(j);
                SizeRequest sizeRequest =new SizeRequest();

                String sizeValue = sizeObj.getString("value");
                int total = sizeObj.getInt("total");
                sizeRequest.setValue(sizeValue);
                sizeRequest.setTotal(total);
                sizeRequests.add(sizeRequest);
            }
            colorRequest.setSizes(sizeRequests);
            colorRequests.add(colorRequest);
        }
        productRequest.setColors(colorRequests);
        Long userId = jwtUtils.getUserIdFromToken(jwtUtils.getTokenLoginFromCookie(request));
        productRequest.setUserId(userId);
        productService.createProduct(productRequest,images);

        return "redirect:/admin/product";
    }
}
