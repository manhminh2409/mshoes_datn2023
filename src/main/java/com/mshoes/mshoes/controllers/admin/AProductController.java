package com.mshoes.mshoes.controllers.admin;

import com.mshoes.mshoes.models.Category;
import com.mshoes.mshoes.models.Image;
import com.mshoes.mshoes.models.Size;
import com.mshoes.mshoes.models.request.ColorRequest;
import com.mshoes.mshoes.models.request.ProductRequest;
import com.mshoes.mshoes.models.request.SizeRequest;
import com.mshoes.mshoes.models.response.ColorResponse;
import com.mshoes.mshoes.models.response.ProductResponse;
import com.mshoes.mshoes.models.response.SizeResponse;
import com.mshoes.mshoes.models.response.UserResponse;
import com.mshoes.mshoes.repositories.CategoryRepository;
import com.mshoes.mshoes.repositories.ImageRepository;
import com.mshoes.mshoes.repositories.ProductRepository;
import com.mshoes.mshoes.repositories.SizeRepository;
import com.mshoes.mshoes.services.CategoryService;
import com.mshoes.mshoes.utils.GetUserFromToken;
import com.mshoes.mshoes.services.ProductService;
import com.mshoes.mshoes.utils.JwtUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AProductController {

    private final ProductService productService;

    private final CategoryRepository categoryRepository;

    private final ImageRepository imageRepository;

    private final SizeRepository sizeRepository;

    private final ProductRepository productRepository;

    private final CategoryService categoryService;

    private final GetUserFromToken getUserFromToken;

    private final JwtUtils jwtUtils;
    @Autowired
    public AProductController(ProductService productService, CategoryRepository categoryRepository, ImageRepository imageRepository, SizeRepository sizeRepository, ProductRepository productRepository, CategoryService categoryService, GetUserFromToken getUserFromToken, JwtUtils jwtUtils) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
        this.imageRepository = imageRepository;
        this.sizeRepository = sizeRepository;
        this.productRepository = productRepository;
        this.categoryService = categoryService;
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
                                @RequestParam(value = "pageNumber",defaultValue = "0") int pageNumber,
                                @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                @RequestParam(value = "sortBy",defaultValue = "id") String sortBy,
                                @RequestParam(value = "message",defaultValue = "")String message){
        model.addAttribute("message",message);
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
            productResponse.setId(p.getId());
            productResponse.setStatus(p.getStatus());
            return productResponse;
        }).collect(Collectors.toList());
        model.addAttribute("products",productResponses);
        return "admin/products";
    }

    @ModelAttribute("categoryList")
    public List<Category> getCategories(){
        return categoryRepository.findAll();
    }
    @GetMapping("/product/create")
    public String createProduct(ModelMap model){
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

    @GetMapping("/product/detail/{id}")
    public String productDetail(ModelMap model, HttpServletRequest request,
                                HttpServletResponse response, RedirectAttributes redirectAttributes,
                                @PathVariable("id") Long productId,
                                @RequestParam(value = "message", defaultValue = "null") String message){
        if(message != null && !message.equals("")){
            if(message.equals("success")){
                model.addAttribute("message", message);
            }else if (message.equals("false")){
                model.addAttribute("message",message);
            }
        }
        try {

            //Lấy thông tin sản phẩm xem chi tiết
            ProductResponse productResponse = productService.getProductById(productId);
            model.addAttribute("productDetail", productResponse);

            model.addAttribute("category", categoryService.getCategoryById(productResponse.getCategoryId()));
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
                                String key = color.getValue() + " / " + size.getValue();
                                return Map.entry(key, size.getTotal() + "-" + size.getId());
                            }))
                    .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (v1, v2) -> v1,
                            LinkedHashMap::new
                    ));

            // Gửi dữ liệu đi bằng model map
            model.addAttribute("listColors",listColors);
            model.addAttribute("listSizes",listSizes);
            model.addAttribute("sold", sold);
            model.addAttribute("totalByColorAndSize", totalByColorAndSize);
            return "admin/productDetail";
        }catch (Exception e){
            return "redirect:/admin/product?message=false";
        }
    }
    @PutMapping("/product/action/{id}")
    @Transactional
    public String actionProduct(@PathVariable("id") Long productId,
                                @RequestParam("ac") int action){
        try {
            productService.actionProduct(productId, action);
            return "redirect:/admin/product?message=success";
        }catch (Exception e){
            return "redirect:/admin/product?message=false";
        }
    }
    @PutMapping("/product/update/{id}")
    @Transactional
    public String updateProduct(@PathVariable("id") long productId,
                                @RequestParam("name") String name,
                                @RequestParam("description") String description,
                                @RequestParam("price") int price,
                                @RequestParam("discountPrice") int discountPrice,
                                @RequestParam("category") long categoryId,
                                @RequestParam("images") MultipartFile[] images,
                                @RequestParam("sizes") String sizes){
        try {
            ProductRequest productRequest = new ProductRequest();
            productRequest.setName(name);
            productRequest.setDescription(description);
            productRequest.setPrice(price);
            productRequest.setDiscountPrice(discountPrice);
            productRequest.setCategoryId(categoryId);

            //Xử lý size (id-số lượng)
            String[] listSize = sizes.split(",");

            for (String size : listSize) {
                String[] sizeInfo = size.split("-");
                long sizeId = Long.parseLong(sizeInfo[0]) ;
                int total = Integer.parseInt(sizeInfo[1]);
                // Thực hiện cập nhật giá trị total mới
                Size size1 = sizeRepository.findById(sizeId).orElseThrow();
                if (size1.getTotal() != total){
                    size1.setTotal(total);
                }
                sizeRepository.save(size1);
            }

            //Xử lý thêm ảnh
            if(images != null){
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
            ProductResponse productResponse = productService.updateProduct(productRequest,productId);
            return "redirect:/admin/product/detail/"+productId+"?message=success";
        }catch (Exception e){
            return "redirect:/admin/product/detail/"+productId+"?message=false";
        }
    }
    @DeleteMapping("/product/detail/deleteImage/{id}")
    @Transactional
    public String deleteImage(@PathVariable("id") Long imageId,
                              @RequestParam("product") Long productId){
        try{
            Image image = imageRepository.findById(imageId).orElseThrow();
            imageRepository.delete(image);
            return "redirect:/admin/product/detail/"+productId+"?message=success";
        }catch (Exception e){
            return "redirect:/admin/product/detail/"+productId+"?message=false";
        }
    }
}
