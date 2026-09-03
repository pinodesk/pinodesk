package com.pinodesk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pinodesk.repository.PayableRepository;
import com.pinodesk.repository.ProductRepository;
import com.pinodesk.repository.PurchaseRepository;
import com.pinodesk.repository.ReceivableRepository;
import com.pinodesk.repository.SaleRepository;
import com.pinodesk.viewmodel.BestSellingProductCategoryVM;
import com.pinodesk.viewmodel.BestSellingProductVM;
import com.pinodesk.viewmodel.LowestSellingProductVM;
import com.pinodesk.viewmodel.MonthlyPurchaseTransactionVM;
import com.pinodesk.viewmodel.MonthlySaleTransactionVM;
import com.pinodesk.viewmodel.PayableClosestDueDateVM;
import com.pinodesk.viewmodel.ProductClosestExpiryVM;
import com.pinodesk.viewmodel.ProductOutOfStockVM;
import com.pinodesk.viewmodel.ReceivableClosestDueDateVM;
import com.pinodesk.viewmodel.TotalPurchaseTransactionVM;
import com.pinodesk.viewmodel.TotalSaleTransactionVM;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DashboardService extends BaseService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PayableRepository payableRepository;

    @Autowired
    private ReceivableRepository receivableRepository;

    public TotalSaleTransactionVM getTotalSaleTransaction(LocalDate start, LocalDate end) {
        return saleRepository.findTotalSaleTransaction(start, end);
    }

    public BigDecimal getAverageMonthlyRevenue(LocalDate start, LocalDate end) {
        return saleRepository.findAverageMonthlyRevenue(start, end);
    }

    public BigDecimal getCurrentMonthRevenue(LocalDate start, LocalDate end) {
        return saleRepository.findCurrentMonthRevenue(start, end).orElse(BigDecimal.ZERO);
    }

    public TotalPurchaseTransactionVM getTotalPurchaseTransaction(LocalDate start, LocalDate end) {
        return purchaseRepository.findTotalPurchaseTransaction(start, end);
    }

    public BigDecimal getAverageMonthlyExpense(LocalDate start, LocalDate end) {
        return purchaseRepository.findAverageMonthlyExpense(start, end);
    }

    public BigDecimal getCurrentMonthExpense(LocalDate start, LocalDate end) {
        return purchaseRepository.findCurrentMonthExpense(start, end).orElse(BigDecimal.ZERO);
    }

    public List<BestSellingProductCategoryVM> getBestSellingProductCategories(
            LocalDate start,
            LocalDate end,
            String language) {
        return saleRepository.findBestSellingProductCategories(start, end, language);
    }

    public List<MonthlyPurchaseTransactionVM> getMonthlyPurchaseTransactions(LocalDate start, LocalDate end) {
        return purchaseRepository.findMonthlyPurchaseTransactions(start, end);
    }

    public List<MonthlySaleTransactionVM> getMonthlySaleTransactions(LocalDate start, LocalDate end) {
        return saleRepository.findMonthlySaleTransactions(start, end);
    }

    public List<BestSellingProductVM> getBestSellingProducts(LocalDate start, LocalDate end, String language) {
        return saleRepository.findBestSellingProducts(start, end, language);
    }

    public List<LowestSellingProductVM> getLowestSellingProducts(LocalDate start, LocalDate end, String language) {
        return saleRepository.findLowestSellingProducts(start, end, language);
    }

    public List<Integer> getYears() {
        Integer yearMax = LocalDate.now().getYear();
        Integer yearMin = yearMax;
        List<Integer> years = new ArrayList<>();
        Optional<Integer> minCreatedYear = productRepository.findMinCreatedYear();
        if (minCreatedYear.isPresent()) {
            yearMin = minCreatedYear.get();
        }
        int diff = yearMax - yearMin;
        if (diff < 0) {
            diff = 0;
        }
        years.add(yearMax);
        for (int i = 1; i < diff; i++) {
            years.add(yearMax - i);
        }
        years.add(yearMin);
        return years;
    }

    public List<ProductClosestExpiryVM> getProductClosestExpiries(String language) {
        LocalDate now = LocalDate.now();
        LocalDate next3month = now.plusMonths(3);
        return productRepository.findByExpiredDateBefore(next3month, language);
    }

    public List<ProductOutOfStockVM> getProductsOutOfStock(String language) {
        return productRepository.findByQuantityLowerThan(10, language);
    }

    public List<PayableClosestDueDateVM> getPayableClosestDueDates() {
        LocalDate now = LocalDate.now();
        LocalDate next2month = now.plusMonths(2);
        return payableRepository.findByDueDateBefore(next2month);
    }

    public List<ReceivableClosestDueDateVM> getReceivableClosestDueDates() {
        LocalDate now = LocalDate.now();
        LocalDate next2month = now.plusMonths(2);
        return receivableRepository.findByDueDateBefore(next2month);
    }

}
