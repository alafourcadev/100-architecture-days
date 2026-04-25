package com.architecturedays.day014.despues;

import java.time.Instant;

public record UserReportData(Long id, String name, String role, Instant createdAt, int totalOrders) {
}
