package com.pinodesk.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import com.pinodesk.repository.*;
import com.pinodesk.viewmodel.*;

class DashboardSummaryTest {
    private final SaleRepository sales = mock(SaleRepository.class);
    private final PurchaseRepository purchases = mock(PurchaseRepository.class);
    private final ProductRepository products = mock(ProductRepository.class);
    private final DashboardService service = new DashboardService(
            sales,
            purchases,
            products,
            mock(PayableRepository.class),
            mock(ReceivableRepository.class));
    private final LocalDate start = LocalDate.of(2026, 1, 1);
    private final LocalDate end = LocalDate.of(2026, 9, 30);

    @Test
    void missingMonthlyAmountsBecomeZero() {
        assertEquals(BigDecimal.ZERO, service.getCurrentMonthRevenue(start, end));
        assertEquals(BigDecimal.ZERO, service.getCurrentMonthExpense(start, end));
        verify(sales).findCurrentMonthRevenue(start, end);
        verify(purchases).findCurrentMonthExpense(start, end);
    }

    @Test
    void monetarySummariesKeepExactPrecisionAndDateRange() {
        BigDecimal amount = new BigDecimal("12345.6789");
        when(sales.findCurrentMonthRevenue(start, end)).thenReturn(Optional.of(amount));
        when(purchases.findCurrentMonthExpense(start, end)).thenReturn(Optional.of(amount.negate()));
        when(sales.findAverageMonthlyRevenue(start, end)).thenReturn(amount);
        when(purchases.findAverageMonthlyExpense(start, end)).thenReturn(amount);
        assertEquals(amount, service.getCurrentMonthRevenue(start, end));
        assertEquals(amount.negate(), service.getCurrentMonthExpense(start, end));
        assertEquals(amount, service.getAverageMonthlyRevenue(start, end));
        assertEquals(amount, service.getAverageMonthlyExpense(start, end));
    }

    @Test
    void transactionSummariesPreserveRepositoryResults() {
        TotalSaleTransactionVM sale = new TotalSaleTransactionVM();
        TotalPurchaseTransactionVM purchase = new TotalPurchaseTransactionVM();
        when(sales.findTotalSaleTransaction(start, end)).thenReturn(sale);
        when(purchases.findTotalPurchaseTransaction(start, end)).thenReturn(purchase);
        assertSame(sale, service.getTotalSaleTransaction(start, end));
        assertSame(purchase, service.getTotalPurchaseTransaction(start, end));
        List<MonthlySaleTransactionVM> monthlySales = List.of(new MonthlySaleTransactionVM());
        List<MonthlyPurchaseTransactionVM> monthlyPurchases = List.of(new MonthlyPurchaseTransactionVM());
        when(sales.findMonthlySaleTransactions(start, end)).thenReturn(monthlySales);
        when(purchases.findMonthlyPurchaseTransactions(start, end)).thenReturn(monthlyPurchases);
        assertSame(monthlySales, service.getMonthlySaleTransactions(start, end));
        assertSame(monthlyPurchases, service.getMonthlyPurchaseTransactions(start, end));
    }

    @Test
    void rankingsPreserveLanguageAndDateRange() {
        List<BestSellingProductCategoryVM> categories = List.of(new BestSellingProductCategoryVM());
        List<BestSellingProductVM> best = List.of(new BestSellingProductVM());
        List<LowestSellingProductVM> lowest = List.of(new LowestSellingProductVM());
        when(sales.findBestSellingProductCategories(start, end, "id")).thenReturn(categories);
        when(sales.findBestSellingProducts(start, end, "id")).thenReturn(best);
        when(sales.findLowestSellingProducts(start, end, "id")).thenReturn(lowest);
        assertSame(categories, service.getBestSellingProductCategories(start, end, "id"));
        assertSame(best, service.getBestSellingProducts(start, end, "id"));
        assertSame(lowest, service.getLowestSellingProducts(start, end, "id"));
    }

    @Test
    void lowStockUsesThresholdTenAndSelectedLanguage() {
        List<ProductOutOfStockVM> low = List.of(new ProductOutOfStockVM());
        when(products.findByQuantityLowerThan(10, "id")).thenReturn(low);
        assertSame(low, service.getProductsOutOfStock("id"));
        verify(products).findByQuantityLowerThan(10, "id");
    }

    @Test
    void databaseFailureIsNotReportedAsZeroRevenue() {
        IllegalStateException failure = new IllegalStateException("database unavailable");
        when(sales.findCurrentMonthRevenue(start, end)).thenThrow(failure);
        assertSame(
                failure,
                assertThrows(IllegalStateException.class, () -> service.getCurrentMonthRevenue(start, end)));
    }
}
