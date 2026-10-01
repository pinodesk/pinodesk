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
    void payableHorizonHandlesLeapYearEndOfMonthInApplicationZone() {
        Clock clock = Clock.fixed(Instant.parse("2027-12-30T18:00:00Z"), ZoneId.of("Asia/Jakarta"));
        com.pinodesk.repository.PayableRepository payables = mock(com.pinodesk.repository.PayableRepository.class);
        DashboardService service = new DashboardService();
        ReflectionTestUtils.setField(service, "payableRepository", payables);
        java.time.LocalDate cutoff = java.time.LocalDate.of(2028, 2, 29);
        List<com.pinodesk.viewmodel.PayableClosestDueDateVM> rows = List
                .of(new com.pinodesk.viewmodel.PayableClosestDueDateVM());
        when(payables.findByDueDateBefore(cutoff)).thenReturn(rows);
        try (MockedStatic<Clock> clocks = mockStatic(Clock.class)) {
            clocks.when(Clock::systemDefaultZone).thenReturn(clock);
            assertSame(rows, service.getPayableClosestDueDates());
        }
        verify(payables).findByDueDateBefore(cutoff);
    }

    @Test
    void expiryHorizonUsesApplicationDateAndClampsToMonthEnd() {
        Clock clock = Clock.fixed(Instant.parse("2026-11-29T18:00:00Z"), ZoneId.of("Asia/Jakarta"));
        ProductRepository products = mock(ProductRepository.class);
        DashboardService service = new DashboardService();
        ReflectionTestUtils.setField(service, "productRepository", products);
        java.time.LocalDate cutoff = java.time.LocalDate.of(2027, 2, 28);
        List<com.pinodesk.viewmodel.ProductClosestExpiryVM> rows = List
                .of(new com.pinodesk.viewmodel.ProductClosestExpiryVM());
        when(products.findByExpiredDateBefore(cutoff, "id")).thenReturn(rows);
        try (MockedStatic<Clock> clocks = mockStatic(Clock.class)) {
            clocks.when(Clock::systemDefaultZone).thenReturn(clock);
            assertSame(rows, service.getProductClosestExpiries("id"));
        }
        verify(products).findByExpiredDateBefore(cutoff, "id");
    }

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
