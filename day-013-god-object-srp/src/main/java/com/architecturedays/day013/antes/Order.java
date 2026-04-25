package com.architecturedays.day013.antes;

import java.util.List;

public record Order(Long id, Long userId, List<Long> productIds, double total) {
}
