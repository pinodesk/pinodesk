package com.pinodesk.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;
import com.pinodesk.repository.ProductRepository;

class DashboardCalendarTest {
    @Test
    void yearsUseLocalApplicationYearAcrossUtcNewYearBoundary() {
        Clock clock = Clock.fixed(Instant.parse("2026-12-31T18:00:00Z"), ZoneId.of("Asia/Jakarta"));
        ProductRepository products = mock(ProductRepository.class);
        DashboardService service = new DashboardService();
        ReflectionTestUtils.setField(service, "productRepository", products);
        when(products.findMinCreatedYear()).thenReturn(Optional.of(2025));
        try (MockedStatic<Clock> clocks = mockStatic(Clock.class)) {
            clocks.when(Clock::systemDefaultZone).thenReturn(clock);
            assertEquals(List.of(2027, 2026, 2025), service.getYears());
        }
    }
}
