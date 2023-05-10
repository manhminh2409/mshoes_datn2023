package com.mshoes.mshoes.controllers.admin;

import com.mshoes.mshoes.models.response.StatisticsResponse;
import com.mshoes.mshoes.models.response.UserResponse;
import com.mshoes.mshoes.services.StatisticsService;
import com.mshoes.mshoes.utils.DateUtils;
import com.mshoes.mshoes.utils.GetUserFromToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final GetUserFromToken getUserFromToken;

    private final StatisticsService statisticsService;

    private final DateUtils dateUtils;

    public AdminController(GetUserFromToken getUserFromToken, StatisticsService statisticsService, DateUtils dateUtils) {
        this.getUserFromToken = getUserFromToken;
        this.statisticsService = statisticsService;
        this.dateUtils = dateUtils;
    }

    //Lấy thông tin người dùng
    @ModelAttribute("userLogined")
    public UserResponse getUserLogined(ModelMap model, HttpServletRequest request) {
        return getUserFromToken.getUserFromToken(request);
    }

    @GetMapping
    public String home(ModelMap  model, @RequestParam(value = "count",defaultValue = "day") String count,
                       @RequestParam(value = "revenue", defaultValue = "month") String revenue
                       ){
        model.addAttribute("count_time", count);
        model.addAttribute("revenue_time", revenue);
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        // Lấy ngày hôm qua
        calendar.add(Calendar.DATE, -1);
        Date yesterday = calendar.getTime();

        // Lấy tháng trước
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1);
        Date lastMonth = calendar.getTime();

        // Lấy năm trước
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -1);
        Date lastYear = calendar.getTime();

        long orderByDate = statisticsService.getOrderByDate(date);
        long orderByYesterday = statisticsService.getOrderByDate(yesterday);

        long orderByMonth = statisticsService.getOrderByMonth(date);
        long orderByLastMonth = statisticsService.getOrderByMonth(lastMonth);

        long orderByYear = statisticsService.getOrderByYear(date);
        long orderByLastYear = statisticsService.getOrderByYear(lastYear);

        switch (count) {
            case "day" -> model.addAttribute("order", new StatisticsResponse(orderByDate, orderByYesterday));
            case "month" -> model.addAttribute("order", new StatisticsResponse(orderByMonth, orderByLastMonth));
            case "year" -> model.addAttribute("order", new StatisticsResponse(orderByYear, orderByLastYear));
        }

//        Tính toán doanh thu
        long revenueByDate = statisticsService.getRevenueByDate(date);
        long revenueByYesterday = statisticsService.getRevenueByMonth(yesterday);

        long revenueByMonth = statisticsService.getRevenueByMonth(date);
        long revenueByLastMonth = statisticsService.getRevenueByMonth(lastMonth);

        long revenueByYear = statisticsService.getRevenueByYear(date);
        long revenueByLastYear = statisticsService.getRevenueByYear(lastYear);

        switch (revenue) {
            case "day" -> model.addAttribute("revenue", new StatisticsResponse(revenueByDate, revenueByYesterday));
            case "month" -> model.addAttribute("revenue", new StatisticsResponse(revenueByMonth, revenueByLastMonth));
            case "year" -> model.addAttribute("revenue", new StatisticsResponse(revenueByYear, revenueByLastYear));
        }

//         Tính toá đơn hàng chưa duyệt
        model.addAttribute("orderStatus0",statisticsService.getOrderByStatus(0, date));

//        Lấy dữ liệu biểu đồ (báo cáo 7 ngày gần nhất)
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<Date> datesOfChart = dateUtils.getRecentDaysList(7);
        List<Long> revenuesOfChart = new ArrayList<>();
        for (Date dateOfChart : datesOfChart) {
            revenuesOfChart.add(statisticsService.getRevenueByDate(dateOfChart));
        }
        model.addAttribute("datesOfChart", datesOfChart.stream().map(dateFormat::format).toList());
        model.addAttribute("revenuesOfChart", revenuesOfChart);
        return "admin/index";
    }
}
