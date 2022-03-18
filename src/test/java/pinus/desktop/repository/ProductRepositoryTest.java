package pinus.desktop.repository;

import java.text.ParseException;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DatabaseSetup("ProductRepositoryTest.xml")
class ProductRepositoryTest extends RepositoryTestBase {

    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testFilter_shouldReturnFilteredProducts() throws ParseException {
        // ProductFilterVM filter = new ProductFilterVM();
        // filter.setCode("P00003");
        // filter.setBarcode("111100001");
        // filter.setName("potato");
        // filter.setQuantityMax(100);
        // filter.setQuantityMin(1);
        // filter.setUnitId(1l);
        // filter.setCategoryCode("000000002");
        // filter.setPurchasePriceMin(new BigDecimal(1000));
        // filter.setPurchasePriceMax(new BigDecimal(10000));
        // filter.setSellingPriceMin(new BigDecimal(1000));
        // filter.setSellingPriceMax(new BigDecimal(20000));
        // filter.setIncludesVat(SimpleStatus.NO.name());
        // filter.setRackId(2l);
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        // filter.setExpiredDateMin(LocalDate.parse("2021-01-01", formatter));
        // filter.setExpiredDateMax(LocalDate.parse("2022-12-31", formatter));
        // List<ProductVM> products = productRepository.filter(filter, "id");
        // assertThat(products, hasSize(1));
        // assertThat(
        // products.get(0),
        // allOf(
        // hasProperty("code", is("P00003")),
        // hasProperty("barcode", is("111100001")),
        // hasProperty("name", is("Potato BBQ")),
        // hasProperty("quantity", is(100)),
        // hasProperty("unitId", is(1l)),
        // hasProperty("categoryCode", is("000000002")),
        // hasProperty("purchasePrice", is(NumberUtils.toScaledBigDecimal(8000.00))),
        // hasProperty("sellingPrice", is(NumberUtils.toScaledBigDecimal(12000.00))),
        // hasProperty("vatIncluded", is(SimpleStatus.NO.name())),
        // hasProperty("rackId", is(2l)),
        // hasProperty(
        // "expiredDate",
        // allOf(
        // hasProperty("year", is(2022)),
        // hasProperty("monthValue", is(4)),
        // hasProperty("dayOfMonth", is(1))))));

        // filter = new ProductFilterVM();
        // products = productRepository.filter(filter, "en");
        // assertThat(products, hasSize(3));
    }

}
