package com.mshoes.mshoes.controllers.admin;

import com.mshoes.mshoes.models.dtos.CategoryDTO;
import com.mshoes.mshoes.models.request.CategoryRequest;
import com.mshoes.mshoes.models.response.UserResponse;
import com.mshoes.mshoes.services.CategoryService;
import com.mshoes.mshoes.utils.GetUserFromToken;
import com.mshoes.mshoes.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin")
public class ACategoryController {

    private final CategoryService categoryService;

    private final JwtUtils jwtUtils;

    private final GetUserFromToken getUserFromToken;

    @Autowired
    public ACategoryController(CategoryService categoryService, JwtUtils jwtUtils, GetUserFromToken getUserFromToken) {
        this.categoryService = categoryService;
        this.jwtUtils = jwtUtils;
        this.getUserFromToken = getUserFromToken;
    }
    @ModelAttribute("userLogined")
    public UserResponse getUserLogined(HttpServletRequest request){
        return getUserFromToken.getUserFromToken(request);
    }

    @GetMapping("/category")
    public String viewCategories(ModelMap model,
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                 @RequestParam(value = "sortBy",defaultValue = "id") String sortBy){
        try{
            model.addAttribute("countCategory",categoryService.countCategory());
            model.addAttribute("currentPage", pageNumber);
            model.addAttribute("pageSize", pageSize);
            //Lấy danh sách danh mục
            model.addAttribute("categories",categoryService.getCategories(pageNumber,pageSize,sortBy));
            return "admin/categories";
        }catch (Exception e){
            return "redirect:/admin?false";
        }
    }

    @GetMapping("/category/create")
    public String viewCreate(){
        try {
            return "admin/newCategory";
        }catch (Exception e){
            return "redirect:/admin/category?err=false-to-view-update";
        }
    }

    @PostMapping("/category/create")
    public String createCategory(@ModelAttribute("categoryRequest")CategoryRequest categoryRequest){
        try {
            CategoryDTO categoryDTO = categoryService.createCategory(categoryRequest);
            return "redirect:/admin/category";
        }catch (Exception e){
            return "redirect:/category/create?err=false";
        }
    }

    @GetMapping("/category/detail/{id}")
    public String viewUpdate(ModelMap model, @PathVariable("id") Long categoryId){
        try {
            CategoryDTO categoryDTO = categoryService.getCategoryById(categoryId);
            model.addAttribute("categoryDetail", categoryDTO);
            return "admin/categoryDetail";
        }catch (Exception e){
            return "redirect:/admin/category?err=false-to-load-detail";
        }
    }

    @PutMapping("/category/update/{id}")
    public String updateCategory(@PathVariable("id") long categoryId, @ModelAttribute("categoryRequest") CategoryRequest categoryRequest){
        try {
            if (categoryRequest.getDescription() == null || categoryRequest.getDescription().equals("")){
                categoryRequest.setDescription("Không");
            }
            CategoryDTO categoryDTO = categoryService.updateCategory(categoryRequest,categoryId);
            return "redirect:/admin/category?message=update-success";
        }catch (Exception e){
            return "redirect:/admin/category/detail/" + categoryId +"?err=false";
        }
    }

    @PutMapping("/category/action/{id}")
    public String actionCategory(@PathVariable("id") long categoryId,@RequestParam("ac") int action){
        try {
            categoryService.actionCategory(categoryId,action);
            return "redirect:/admin/category?message=action-success";
        }catch (Exception e){
            return "redirect:/admin/category/?err=action";
        }
    }
}
