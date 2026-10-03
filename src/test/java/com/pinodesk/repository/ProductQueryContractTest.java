package com.pinodesk.repository;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.pinodesk.constant.ProductStatus;
import com.pinodesk.viewmodel.ProductFilterVM;
import com.pinodesk.viewmodel.ProductVM;
import com.pinodesk.viewmodel.ProductCategoryVM;
import com.pinodesk.viewmodel.UnitVM;

@DatabaseSetup("ProductQueryContractTest.xml")
class ProductQueryContractTest extends RepositoryTestBase {
    @Autowired
    private ProductRepository products;
    @Autowired
    private DataSource dataSource;

    @AfterEach
    void removeFixtureProductsBeforeOtherRepositoryFixtures() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        jdbc.update("delete from product_expiry where id between 9401 and 9403");
        jdbc.update("delete from product where id between 9301 and 9303");
    }

    private List<Long> ids(List<ProductVM> results) {
        return results.stream().map(ProductVM::getId).sorted().collect(Collectors.toList());
    }

    private void matches(Consumer<ProductFilterVM> configure, Long... expected) {
        ProductFilterVM filter = new ProductFilterVM();
        configure.accept(filter);
        assertEquals(List.of(expected), ids(products.findByFilter(filter, "en")));
    }

    @Test
    void emptyFilterReturnsOnlyLiveProductsAndMapsLocalizedJoins() {
        assertEquals(List.of(9301L, 9302L), ids(products.findByFilter(new ProductFilterVM(), "en")));
        List<ProductVM> localized = products.findByFilter(new ProductFilterVM(), "id");
        assertEquals(List.of(9301L), ids(localized));
        ProductVM product = localized.get(0);
        assertEquals("Obat", product.getCategoryName());
        assertEquals(9202L, product.getCategoryId());
        assertEquals("tablet-id", product.getUnitLabel());
        assertEquals(9102L, product.getUnitId());
        assertEquals(10, product.getQuantity());
        assertEquals(new BigDecimal("10.0000"), product.getGeneralSellingPrice());
    }

    @Test
    void textFiltersMatchIndependentlyAndCombineWithAnd() {
        matches(f -> f.setName("ASPIR"), 9301L);
        matches(f -> f.setDescription("PAIN"), 9301L);
        matches(f -> f.setCode("ASP"), 9301L);
        matches(f -> f.setBarcode("BAR-ASP"), 9301L);
        matches(f -> {
            f.setName("Aspirin");
            f.setDescription("Supplement");
        });
        matches(f -> {
            f.setName(" ");
            f.setDescription("");
            f.setCode(" ");
            f.setBarcode(" ");
        }, 9301L, 9302L);
    }

    @Test
    void categoryUnitAndStatusAreExactFilters() {
        matches(f -> {
            ProductCategoryVM c = new ProductCategoryVM();
            c.setCode("MED");
            f.setCategory(c);
        }, 9301L);
        matches(f -> {
            UnitVM u = new UnitVM();
            u.setCode("0002");
            f.setUnit(u);
        }, 9302L);
        matches(f -> f.setStatus(ProductStatus.ACTIVE), 9301L);
        matches(f -> f.setStatus(ProductStatus.INACTIVE), 9302L);
    }

    @Test
    void stockBoundsAreInclusiveAndRejectOutsideValues() {
        matches(f -> f.setStockQuantityMin(10), 9301L, 9302L);
        matches(f -> f.setStockQuantityMin(11), 9302L);
        matches(f -> f.setStockQuantityMax(10), 9301L);
        matches(f -> f.setStockQuantityMax(9));
        matches(f -> {
            f.setStockQuantityMin(10);
            f.setStockQuantityMax(10);
        }, 9301L);
    }

    @Test
    void generalPriceBoundsAreInclusiveWithDecimalPrecision() {
        matches(f -> f.setGeneralSellingPriceMin(new BigDecimal("10.0000")), 9301L, 9302L);
        matches(f -> f.setGeneralSellingPriceMin(new BigDecimal("10.0001")), 9302L);
        matches(f -> f.setGeneralSellingPriceMax(new BigDecimal("10.0000")), 9301L);
        matches(f -> f.setGeneralSellingPriceMax(new BigDecimal("9.9999")));
    }

    @Test
    void prescriptionPriceBoundsAreInclusiveWithDecimalPrecision() {
        matches(f -> f.setPrescriptionSellingPriceMin(new BigDecimal("8.0000")), 9301L, 9302L);
        matches(f -> f.setPrescriptionSellingPriceMin(new BigDecimal("8.0001")), 9302L);
        matches(f -> f.setPrescriptionSellingPriceMax(new BigDecimal("8.0000")), 9301L);
        matches(f -> f.setPrescriptionSellingPriceMax(new BigDecimal("7.9999")));
    }

    @Test
    void expiryDateAndBatchConditionsMustMatchSameExpiryRow() {
        LocalDate october = LocalDate.of(2026, 10, 31);
        matches(f -> f.setExpiredDateMax(october), 9301L);
        matches(f -> f.setExpiredDateMax(october.minusDays(1)));
        matches(f -> f.setExpiredDateMin(october), 9301L, 9302L);
        matches(f -> {
            f.setExpiredDateMin(october);
            f.setExpiredDateMax(october);
        }, 9301L);
        matches(f -> f.setBatchNumber("batch-A"), 9301L);
        matches(f -> {
            f.setExpiredDateMax(october);
            f.setBatchNumber("batch-B");
        });
        matches(f -> f.setBatchNumber(" "), 9301L, 9302L);
    }

    @Test
    void allConditionsCanBeCombinedWithoutParameterOrderErrors() {
        matches(f -> {
            f.setName("aspirin");
            f.setDescription("pain");
            f.setCode("asp");
            f.setBarcode("bar");
            ProductCategoryVM c = new ProductCategoryVM();
            c.setCode("MED");
            f.setCategory(c);
            UnitVM u = new UnitVM();
            u.setCode("0001");
            f.setUnit(u);
            f.setStatus(ProductStatus.ACTIVE);
            f.setStockQuantityMin(10);
            f.setStockQuantityMax(10);
            f.setGeneralSellingPriceMin(new BigDecimal("10"));
            f.setGeneralSellingPriceMax(new BigDecimal("10"));
            f.setPrescriptionSellingPriceMin(new BigDecimal("8"));
            f.setPrescriptionSellingPriceMax(new BigDecimal("8"));
            f.setExpiredDateMin(LocalDate.of(2026, 10, 31));
            f.setExpiredDateMax(LocalDate.of(2026, 10, 31));
            f.setBatchNumber("batch-A");
        }, 9301L);
    }

    @Test
    void keywordMatchesAllSearchColumnsAndTrimsWhitespace() {
        for (String keyword : List.of(" ASPIRIN ", "asp01", "bar-asp", "med", "MEDICINE")) {
            assertEquals(List.of(9301L), ids(products.findByKeyword(keyword, "en")), keyword);
        }
        assertEquals(List.of(9301L), ids(products.findByKeyword("OBAT", "id")));
        assertTrue(products.findByKeyword("OBAT", "en").isEmpty());
        assertTrue(products.findByKeyword("does-not-exist", "en").isEmpty());
        for (String blank : new String[] { null, "", " " }) {
            assertEquals(List.of(9301L, 9302L), ids(products.findByKeyword(blank, "en")));
        }
    }
}
