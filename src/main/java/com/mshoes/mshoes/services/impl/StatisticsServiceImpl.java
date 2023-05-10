package com.mshoes.mshoes.services.impl;

import com.mshoes.mshoes.repositories.OrderDetailRepository;
import com.mshoes.mshoes.services.StatisticsService;
import com.mshoes.mshoes.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    private static final int TYPE_ORDER = 1;
    private final OrderDetailRepository orderDetailRepository;

    private final DateUtils dateUtils;

    @Autowired
    public StatisticsServiceImpl(OrderDetailRepository orderDetailRepository, DateUtils dateUtils) {
        this.orderDetailRepository = orderDetailRepository;
        this.dateUtils = dateUtils;
    }

    @Override
    public long getOrderByDate(Date date) {
        try {
            String strDate = dateUtils.formatToStrDate(date);
            return orderDetailRepository.countByCreatedDateAndType(strDate, TYPE_ORDER);
        }catch (Exception e){
            return 0;
        }
    }

    @Override
    public long getOrderByMonth(Date date) {
        try {
            String strDate = dateUtils.formatToStrMonth(date);
            return orderDetailRepository.countByCreatedDateAndType(strDate, TYPE_ORDER);
            }catch (Exception e){
                return 0;
        }
    }

    @Override
    public long getOrderByYear(Date date) {
        try{
            String strDate = dateUtils.formatToStrYear(date);
            return orderDetailRepository.countByCreatedDateAndType(strDate, TYPE_ORDER);
        }catch (Exception e){
            return 0;
        }
    }

    @Override
    public long getRevenueByDate(Date date) {
        try{
            String strDate = dateUtils.formatToStrDate(date);
            return orderDetailRepository.revenueByCreatedDateAndType(strDate, TYPE_ORDER);
        }catch (Exception e){
            return 0;
        }

    }

    @Override
    public long getRevenueByMonth(Date date) {
        try {
            String strDate = dateUtils.formatToStrMonth(date);
            return orderDetailRepository.revenueByCreatedDateAndType(strDate, TYPE_ORDER);
        }catch (Exception e){
            return 0;
        }
    }

    @Override
    public long getRevenueByYear(Date date) {
        try {
            String strDate = dateUtils.formatToStrYear(date);
            return orderDetailRepository.revenueByCreatedDateAndType(strDate, TYPE_ORDER);
        }catch (Exception e){
            return 0;
        }
    }

    @Override
    public long getOrderByStatus(int status, Date date) {
        try {
            String strDate = dateUtils.formatToStrDate(date);
            return orderDetailRepository.countByCreatedDateAndStatusAndType(strDate, status, TYPE_ORDER);
        }catch (Exception e){
            return 0;
        }
    }
}
