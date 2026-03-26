package com.architecturedays.day001.service;

import com.architecturedays.day001.model.Product;
import java.util.List;

public interface ProductService {
    List<Product> findAll();
    default void initializeAsync() {}
}
