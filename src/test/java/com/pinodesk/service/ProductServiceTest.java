package com.pinodesk.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import com.pinodesk.constant.*;
import com.pinodesk.entity.*;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.*;
import com.pinodesk.viewmodel.*;

class ProductServiceTest extends BaseServiceTest {
    @Mock
    private ProductRepository products;
    @Mock
    private ConfigurationService configuration;
    @Mock
    private DrugRepository drugs;
    @Mock
    private ProductPriceRepository prices;
    @Mock
    private ProductStockRepository stocks;
    @Mock
    private ProductExpiryRepository expiries;
    @Mock
    private ProductCategoryRepository categories;
    @Mock
    private UnitRepository units;
    @Mock
    private DrugClassificationRepository classifications;
    @Mock
    private PackageDetailRepository packages;
    @Mock
    private SessionService sessions;
    @Mock
    private PurchaseDetailRepository purchases;
    @InjectMocks
    private ProductService service;
    private ValidatorFactory validators;
    private final LocalDate expiryDate = LocalDate.of(2027, 3, 31);

    @BeforeEach
    void configureValidationAndSession() {
        validators = Validation.buildDefaultValidatorFactory();
        ReflectionTestUtils.setField(service, "validator", validators.getValidator());
        CurrentSessionVM session = new CurrentSessionVM();
        UserVM user = new UserVM();
        user.setId(8L);
        session.setUser(user);
        when(sessions.getCurrentSession()).thenReturn(session);
    }

    @AfterEach
    void closeValidation() {
        validators.close();
    }

    private ProductAddVM addition() {
        ProductAddVM add = new ProductAddVM();
        add.setCode("P01");
        add.setName("Product");
        add.setBarcode("BAR01");
        add.setStatus(ProductStatus.ACTIVE);
        ProductCategoryVM category = new ProductCategoryVM();
        category.setCode("GENERAL");
        add.setProductCategory(category);
        UnitVM unit = new UnitVM();
        unit.setCode("0001");
        add.setUnit(unit);
        return add;
    }

    private void saveWithId() {
        when(products.save(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p.setId(7L);
            return p;
        });
    }

    private Product existing() {
        Product p = new Product();
        p.setId(7L);
        p.setCode("P01");
        p.setBarcode("BAR01");
        p.setUnitCode("0001");
        p.setName("Product");
        p.setQuantity(10);
        p.setGeneralSellingPrice(new BigDecimal("10"));
        when(products.findByIdAndDeletedAtIsNull(7L)).thenReturn(Optional.of(p));
        return p;
    }

    @Test
    void minimalCreationDoesNotWriteOptionalHistories() {
        saveWithId();
        ProductAddVM add = addition();
        add.setBarcode(null);
        service.createProduct(add);
        ArgumentCaptor<Product> product = ArgumentCaptor.forClass(Product.class);
        verify(products).save(product.capture());
        assertEquals("P01", product.getValue().getCode());
        assertEquals("0001", product.getValue().getUnitCode());
        assertEquals("GENERAL", product.getValue().getCategoryCode());
        assertEquals("active", product.getValue().getStatus());
        verifyNoInteractions(drugs, prices, stocks, expiries);
    }

    @Test
    void drugCreationRecordsPriceStockAndExpiryWithActorAndProductId() {
        saveWithId();
        ProductAddVM add = addition();
        add.getProductCategory().setCode(CommonConstants.PRODUCT_CATEGORY_CODE_DRUGS);
        DrugClassificationVM classification = new DrugClassificationVM();
        classification.setCode("RX");
        add.setDrugClassification(classification);
        add.setIndication("Indication");
        add.setContraindication("Contraindication");
        add.setGeneralSellingPrice(new BigDecimal("12.3456"));
        add.setPrescriptionSellingPrice(new BigDecimal("11"));
        add.setStockQuantity(9);
        add.setExpiredDate(expiryDate);
        add.setExpiryQuantity(4);
        add.setBatchNumber("LOT-A");
        Product result = service.createProduct(add, Activity.ADD_PRODUCT);
        assertEquals(7L, result.getId());
        assertEquals(9, result.getQuantity());
        ArgumentCaptor<Drug> drug = ArgumentCaptor.forClass(Drug.class);
        verify(drugs).save(drug.capture());
        assertEquals(7L, drug.getValue().getProductId());
        assertEquals("RX", drug.getValue().getClassificationCode());
        assertEquals("Indication", drug.getValue().getIndication());
        ArgumentCaptor<ProductPrice> price = ArgumentCaptor.forClass(ProductPrice.class);
        verify(prices).save(price.capture());
        assertEquals(new BigDecimal("12.3456"), price.getValue().getGeneralSellingPrice());
        assertEquals(8L, price.getValue().getUserId());
        assertEquals(Activity.ADD_PRODUCT.toString(), price.getValue().getActivity());
        ArgumentCaptor<ProductStock> stock = ArgumentCaptor.forClass(ProductStock.class);
        verify(stocks).save(stock.capture());
        assertEquals(9, stock.getValue().getFinalQuantity());
        assertEquals(7L, stock.getValue().getProductId());
        ArgumentCaptor<ProductExpiry> expiry = ArgumentCaptor.forClass(ProductExpiry.class);
        verify(expiries).save(expiry.capture());
        assertEquals(4, expiry.getValue().getQuantityIn());
        assertEquals(4, expiry.getValue().getFinalQuantity());
        assertEquals(4, expiry.getValue().getFinalQuantityExpiredDate());
        assertEquals("LOT-A", expiry.getValue().getBatchNumber());
    }

    @Test
    void invalidProductIsRejectedBeforeDatabaseWork() {
        ProductAddVM add = addition();
        add.setStockQuantity(-1);
        assertThrows(ConstraintViolationException.class, () -> service.createProduct(add));
        verifyNoInteractions(products, prices, stocks, expiries);
    }

    @Test
    void duplicateCodeBarcodeAndNameUnitAreRejectedWithoutWrites() {
        when(products.existsByCodeAndDeletedAtIsNull("P01")).thenReturn(true);
        assertEquals(
                DomainError.PRODUCT_EXISTS_BY_CODE,
                assertThrows(DomainException.class, () -> service.createProduct(addition())).getError());
        when(products.existsByCodeAndDeletedAtIsNull("P01")).thenReturn(false);
        when(products.existsByBarcodeAndDeletedAtIsNull("BAR01")).thenReturn(true);
        assertEquals(
                DomainError.PRODUCT_EXISTS_BY_BARCODE,
                assertThrows(DomainException.class, () -> service.createProduct(addition())).getError());
        when(products.existsByBarcodeAndDeletedAtIsNull("BAR01")).thenReturn(false);
        when(products.existsByNameIgnoreCaseAndUnitCodeAndDeletedAtIsNull("Product", "0001")).thenReturn(true);
        assertEquals(
                DomainError.PRODUCT_EXISTS_BY_NAME_AND_UNIT,
                assertThrows(DomainException.class, () -> service.createProduct(addition())).getError());
        verify(products, never()).save(any());
        verifyNoInteractions(drugs, prices, stocks, expiries);
    }

    @Test
    void updateNonDrugPreservesUnspecifiedStockAndPriceAndClearsMissingExpiry() {
        Product original = existing();
        original.setClosestExpiredDate(expiryDate);
        ProductEditVM edit = objectMapper.convertValue(addition(), ProductEditVM.class);
        service.updateProduct(edit, 7L);
        assertEquals(10, original.getQuantity());
        assertEquals(new BigDecimal("10"), original.getGeneralSellingPrice());
        assertNull(original.getClosestExpiredDate());
        verify(drugs).deleteByProductId(7L);
        verify(products).save(original);
        verifyNoInteractions(prices, stocks);
    }

    @Test
    void updateDrugReconcilesExpiryQuantitiesAndWritesAuditHistory() {
        Product original = existing();
        ProductEditVM edit = objectMapper.convertValue(addition(), ProductEditVM.class);
        edit.getProductCategory().setCode(CommonConstants.PRODUCT_CATEGORY_CODE_DRUGS);
        edit.setGeneralSellingPrice(new BigDecimal("20"));
        edit.setPrescriptionSellingPrice(new BigDecimal("18"));
        edit.setStockQuantity(30);
        edit.setExpiredDate(expiryDate);
        edit.setExpiryQuantity(6);
        ProductExpiry previous = new ProductExpiry();
        previous.setFinalQuantity(10);
        previous.setFinalQuantityExpiredDate(4);
        when(expiries.findFirstByProductIdOrderByIdDesc(7L)).thenReturn(Optional.of(previous));
        when(expiries.findFirstByProductIdAndExpiredDateOrderByIdDesc(7L, expiryDate))
                .thenReturn(Optional.of(previous));
        when(expiries.findClosestExpiredDateAvailableByProductId(7L)).thenReturn(Optional.of(expiryDate));
        service.updateProduct(edit, 7L);
        assertEquals(30, original.getQuantity());
        assertEquals(new BigDecimal("20"), original.getGeneralSellingPrice());
        assertEquals(new BigDecimal("18"), original.getPrescriptionSellingPrice());
        assertEquals(expiryDate, original.getClosestExpiredDate());
        ArgumentCaptor<ProductExpiry> cap = ArgumentCaptor.forClass(ProductExpiry.class);
        verify(expiries).save(cap.capture());
        assertEquals(12, cap.getValue().getFinalQuantity());
        assertEquals(6, cap.getValue().getFinalQuantityExpiredDate());
        assertEquals(8L, cap.getValue().getUserId());
        verify(prices).save(any(ProductPrice.class));
        verify(stocks).save(any(ProductStock.class));
        ArgumentCaptor<Drug> drug = ArgumentCaptor.forClass(Drug.class);
        verify(drugs).save(drug.capture());
        assertEquals(7L, drug.getValue().getProductId());
        assertNull(drug.getValue().getClassificationCode());
    }

    @Test
    void existingDrugIsUpdatedWithoutReplacingIdentity() {
        existing();
        ProductEditVM edit = objectMapper.convertValue(addition(), ProductEditVM.class);
        edit.getProductCategory().setCode(CommonConstants.PRODUCT_CATEGORY_CODE_DRUGS);
        DrugClassificationVM classification = new DrugClassificationVM();
        classification.setCode("RX");
        edit.setDrugClassification(classification);
        Drug drug = new Drug();
        drug.setId(9L);
        when(drugs.findByProductIdAndDeletedAtIsNull(7L)).thenReturn(Optional.of(drug));
        service.updateProduct(edit, 7L);
        verify(drugs).save(drug);
        assertEquals(9L, drug.getId());
        assertEquals("RX", drug.getClassificationCode());
    }

    @Test
    void missingProductAndConflictingChangesFailBeforeWrites() {
        ProductEditVM edit = objectMapper.convertValue(addition(), ProductEditVM.class);
        assertEquals(
                DomainError.PRODUCT_NOT_FOUND_BY_ID,
                assertThrows(DomainException.class, () -> service.updateProduct(edit, 7L)).getError());
        existing();
        edit.setCode("P02");
        when(products.existsByCodeAndDeletedAtIsNull("P02")).thenReturn(true);
        assertEquals(
                DomainError.PRODUCT_OTHER_EXISTS_BY_CODE,
                assertThrows(DomainException.class, () -> service.updateProduct(edit, 7L)).getError());
        edit.setCode("P01");
        edit.setBarcode("NEW");
        when(products.existsByBarcodeAndDeletedAtIsNull("NEW")).thenReturn(true);
        assertEquals(
                DomainError.PRODUCT_OTHER_EXISTS_BY_BARCODE,
                assertThrows(DomainException.class, () -> service.updateProduct(edit, 7L)).getError());
        edit.setBarcode("BAR01");
        edit.getUnit().setCode("0002");
        when(products.existsByNameIgnoreCaseAndUnitCodeAndDeletedAtIsNull("Product", "0002")).thenReturn(true);
        assertEquals(
                DomainError.PRODUCT_OTHER_EXISTS_BY_NAME_AND_UNIT,
                assertThrows(DomainException.class, () -> service.updateProduct(edit, 7L)).getError());
        verify(products, never()).save(any());
        verifyNoInteractions(drugs, prices, stocks, expiries);
    }

    @Test
    void expiryAdjustmentStartsAtZeroAndClearsExhaustedClosestDate() {
        ProductExpiryAddVM add = new ProductExpiryAddVM();
        add.setProductId(7L);
        add.setExpiredDate(expiryDate);
        add.setQuantity(5);
        service.addProductExpiry(add, Activity.ADD_PRODUCT);
        ArgumentCaptor<ProductExpiry> cap = ArgumentCaptor.forClass(ProductExpiry.class);
        verify(expiries).save(cap.capture());
        assertEquals(5, cap.getValue().getFinalQuantity());
        assertEquals(5, cap.getValue().getFinalQuantityExpiredDate());
        verify(products).updateClosestExpiredDateById(7L, null);
    }

    @Test
    void localizedSearchAndDeletionUseRequestedArguments() {
        when(configuration.getConfiguration(ConfigurationConstants.LANGUAGE)).thenReturn("id");
        ProductFilterVM filter = new ProductFilterVM();
        List<ProductVM> result = List.of(new ProductVM());
        when(products.findByFilter(filter, "id")).thenReturn(result);
        when(products.findByKeyword("pain", "id")).thenReturn(result);
        when(products.findByCode("P01", "id")).thenReturn(Optional.of(result.get(0)));
        assertSame(result, service.searchProductsByFilter(filter));
        assertSame(result, service.searchProductsByKeyword("pain"));
        assertSame(result.get(0), service.searchProductByCode("P01").orElseThrow());
        service.removeProducts(List.of(7L));
        verify(products).deleteUpdateByIdIn(List.of(7L));
    }
}
