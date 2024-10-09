package com.flash.order.application.service;

import com.flash.order.application.dtos.response.ProductResponseDto;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

public interface FeignClientService {
    ProductResponseDto getProduct(@PathVariable UUID productId);
}
