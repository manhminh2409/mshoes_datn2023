package com.mshoes.mshoes.services;

import java.util.Date;

public interface StatisticsService {
//    Lấy đơn hàng theo ngày, tháng, năm
    long getOrderByDate(Date date);

    long getOrderByMonth(Date date);

    long getOrderByYear(Date date);


//  Doanh thu theo ngày, tháng, năm

    long getRevenueByDate(Date date);

    long getRevenueByMonth(Date date);

    long getRevenueByYear(Date date);

//    Lấy đơn hàng chưa duyệt
    long getOrderByStatus(int status, Date date);
}
