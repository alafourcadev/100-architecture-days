package com.architecturedays.day003.service;

import com.architecturedays.day003.model.Order;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderService {
    List<Order> findOrdersByStatusAndDateRange(String status, LocalDateTime start, LocalDateTime end);
    Map<String, Object> getQueryStats();
}
