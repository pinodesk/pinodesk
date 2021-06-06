package com.getkembang.kembangdesktop.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import com.getkembang.kembangdesktop.constant.CommonConstants;
import com.getkembang.kembangdesktop.viewmodel.ProductFilterVM;
import com.getkembang.kembangdesktop.viewmodel.ProductVM;
import com.github.database.rider.core.api.dataset.DataSet;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DataSet("t_product.yml")
class ProductRepositoryTest extends BaseRepositoryTest {

    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testFilter_shouldReturnFilteredProducts() throws ParseException {
        ProductFilterVM vm = new ProductFilterVM();
        vm.setCode("P00003");
        vm.setBarcode("111100001");
        vm.setName("potato");
        vm.setQuantityMax(100);
        vm.setQuantityMin(1);
        vm.setUnitId(1l);
        vm.setCategoryCode("000000002");
        vm.setPurchasePriceMin(new BigDecimal(1000));
        vm.setPurchasePriceMax(new BigDecimal(10000));
        vm.setSellingPriceMin(new BigDecimal(1000));
        vm.setSellingPriceMax(new BigDecimal(20000));
        vm.setIncludesVat(CommonConstants.NO);
        vm.setRackId(2l);
        vm.setExpiredDateMin(DateUtils.parseDate("2021-01-01", DATE_PATTERN));
        vm.setExpiredDateMax(DateUtils.parseDate("2022-12-31", DATE_PATTERN));
        long languageId = 2;
        List<ProductVM> products = productRepository.filter(vm, languageId);
        assertThat(products, hasSize(1));
        assertThat(products.get(0), allOf(
            hasProperty("code", is("P00003")),
            hasProperty("barcode", is("111100001")),
            hasProperty("name", is("Potato BBQ")),
            hasProperty("quantity", is(100)),
            hasProperty("unitId", is(1l)),
            hasProperty("categoryCode", is("000000002")),
            hasProperty("purchasePrice", is(NumberUtils.toScaledBigDecimal(8000.00))),
            hasProperty("sellingPrice", is(NumberUtils.toScaledBigDecimal(12000.00))),
            hasProperty("vatIncluded", is(CommonConstants.NO)),
            hasProperty("rackId", is(2l)),
            hasProperty("expiredDate", allOf(
                hasProperty("year", is(2022-1900)),
                hasProperty("month", is(3)),
                hasProperty("date", is(1))))));
    }

}
