package pospino.desktop.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import lombok.Setter;
import pospino.desktop.annotation.ForActivity;
import pospino.desktop.constant.Activity;
import pospino.desktop.constant.CacheNameConstants;
import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.ConfigurationConstants;
import pospino.desktop.constant.DomainError;
import pospino.desktop.domain.Drug;
import pospino.desktop.domain.PackageDetail;
import pospino.desktop.domain.Product;
import pospino.desktop.domain.ProductExpiry;
import pospino.desktop.domain.ProductPrice;
import pospino.desktop.domain.ProductStock;
import pospino.desktop.domain.Unit;
import pospino.desktop.exception.DomainException;
import pospino.desktop.repository.DrugClassificationRepository;
import pospino.desktop.repository.DrugRepository;
import pospino.desktop.repository.PackageDetailRepository;
import pospino.desktop.repository.ProductCategoryRepository;
import pospino.desktop.repository.ProductExpiryRepository;
import pospino.desktop.repository.ProductPriceRepository;
import pospino.desktop.repository.ProductRepository;
import pospino.desktop.repository.ProductStockRepository;
import pospino.desktop.repository.UnitRepository;
import pospino.desktop.util.ProductUtils;
import pospino.desktop.viewmodel.DrugClassificationVM;
import pospino.desktop.viewmodel.GroupedProductExpiryVM;
import pospino.desktop.viewmodel.PackageProductVM;
import pospino.desktop.viewmodel.ProductAddVM;
import pospino.desktop.viewmodel.ProductEditVM;
import pospino.desktop.viewmodel.ProductExpiryAddVM;
import pospino.desktop.viewmodel.ProductExpiryVM;
import pospino.desktop.viewmodel.ProductFilterVM;
import pospino.desktop.viewmodel.ProductImportVM;
import pospino.desktop.viewmodel.ProductPriceVM;
import pospino.desktop.viewmodel.ProductStockVM;
import pospino.desktop.viewmodel.ProductVM;

@Service
public class ProductService extends BaseService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private ProductPriceRepository productPriceRepository;

    @Autowired
    private ProductStockRepository productStockRepository;

    @Autowired
    private ProductExpiryRepository productExpiryRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private DrugClassificationRepository drugClassificationRepository;

    @Autowired
    private PackageDetailRepository packageDetailRepository;

    @Autowired
    private SessionService sessionService;

    @ForActivity(Activity.SEARCH_PRODUCTS_BY_FILTER)
    @Cacheable(CacheNameConstants.PRODUCTS_BY_FILTER)
    public List<ProductVM> searchProductsByFilter(ProductFilterVM filter) {
        String language = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE);
        return productRepository.findByFilter(filter, language);
    }

    @ForActivity(Activity.SEARCH_PRODUCTS_BY_KEYWORD)
    @Cacheable(CacheNameConstants.PRODUCTS_BY_KEYWORD)
    public List<ProductVM> searchProductsByKeyword(String keyword) {
        String language = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE);
        return productRepository.findByKeyword(keyword, language);
    }

    @ForActivity(Activity.SEARCH_PRODUCT_BY_CODE)
    @Cacheable(CacheNameConstants.PRODUCT_BY_CODE)
    public Optional<ProductVM> searchProductByCode(String code) {
        String language = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE);
        return productRepository.findByCode(code, language);
    }

    @ForActivity(Activity.EDIT_PRODUCT)
    @CacheEvict(value = {
            CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD,
            CacheNameConstants.PRODUCT_BY_CODE },
        allEntries = true)
    @Transactional
    public void updateProduct(ProductEditVM productEdit, Long productId) {
        updateProduct(productEdit, productId, Activity.EDIT_PRODUCT);
    }

    public void updateProduct(ProductEditVM productEdit, Long productId, Activity activity) {
        validateConstraints(productEdit);
        Long currentUserId = sessionService.getCurrentSession().getUser().getId();
        String activityName = activity.toString();
        String name = productEdit.getName();
        String code = productEdit.getCode();
        String barcode = productEdit.getBarcode();
        String categoryCode = productEdit.getProductCategory().getCode();
        Long unitId = productEdit.getUnit().getId();

        Product product = validateProductId(productId);
        validateProductCode(code, product);
        validateProductBarcode(barcode, product);
        validateProductNameAndUnit(name, unitId, product);

        if (CommonConstants.PRODUCT_CATEGORY_CODE_DRUGS.equals(categoryCode)) {
            DrugClassificationVM drugClassification = productEdit.getDrugClassification();
            String classificationCode = drugClassification == null ? null : drugClassification.getCode();
            drugRepository.findByProductIdAndDeletedAtIsNull(productId).ifPresentOrElse(drug -> {
                drug.setClassificationCode(classificationCode);
                drug.setIndication(productEdit.getIndication());
                drug.setContraindication(productEdit.getContraindication());
                drugRepository.save(drug);
            }, () -> {
                Drug drug = new Drug();
                drug.setProductId(productId);
                drug.setClassificationCode(classificationCode);
                drug.setIndication(productEdit.getIndication());
                drug.setContraindication(productEdit.getContraindication());
                drugRepository.save(drug);
            });
        } else {
            drugRepository.deleteByProductId(productId);
        }

        if (productEdit.getGeneralSellingPrice() != null) {
            product.setGeneralSellingPrice(productEdit.getGeneralSellingPrice());
            ProductPrice pp = new ProductPrice();
            pp.setProductId(productId);
            pp.setGeneralSellingPrice(productEdit.getGeneralSellingPrice());
            pp.setPrescriptionSellingPrice(productEdit.getPrescriptionSellingPrice());
            pp.setRemarks(productEdit.getPriceRemarks());
            pp.setActivity(activityName);
            pp.setUserId(currentUserId);
            productPriceRepository.save(pp);
        }

        if (productEdit.getPrescriptionSellingPrice() != null) {
            product.setPrescriptionSellingPrice(productEdit.getPrescriptionSellingPrice());
        }

        if (productEdit.getStockQuantity() != null) {
            product.setQuantity(productEdit.getStockQuantity());
            ProductStock ps = new ProductStock();
            ps.setProductId(productId);
            ps.setFinalQuantity(productEdit.getStockQuantity());
            ps.setRemarks(productEdit.getStockRemarks());
            ps.setActivity(activityName);
            ps.setUserId(currentUserId);
            productStockRepository.save(ps);
        }

        if (productEdit.getExpiredDate() != null) {
            productStockRepository.findFirstByProductIdAndDeletedAtIsNullOrderByIdDesc(productId)
                    .ifPresentOrElse(ps -> {
                        Integer finalQuantity = productExpiryRepository.findFirstByProductIdOrderByIdDesc(productId)
                                .map(ProductExpiry::getFinalQuantity).orElse(0);
                        Integer finalQuantityExpiredDate = productExpiryRepository
                                .findFirstByProductIdAndExpiredDateOrderByIdDesc(
                                        productId,
                                        productEdit.getExpiredDate())
                                .map(ProductExpiry::getFinalQuantityExpiredDate).orElse(0);
                        Integer expiryQuantity = productEdit.getExpiryQuantity();
                        Integer totalExpiryQuantity = finalQuantity + expiryQuantity;
                        Integer totalExpiryQuantityExpiredDate = finalQuantityExpiredDate + expiryQuantity;
                        if (totalExpiryQuantity > ps.getFinalQuantity()) {
                            throw new DomainException(DomainError.PRODUCT_EXPIRY_QUANTITY_EXCEEDS_PRODUCT_STOCK);
                        }
                        ProductExpiry px = new ProductExpiry();
                        px.setProductId(productId);
                        px.setExpiredDate(productEdit.getExpiredDate());
                        px.setBatchNumber(productEdit.getBatchNumber());
                        px.setQuantityIn(expiryQuantity);
                        px.setFinalQuantity(totalExpiryQuantity);
                        px.setFinalQuantityExpiredDate(totalExpiryQuantityExpiredDate);
                        px.setUserId(currentUserId);
                        px.setActivity(activityName);
                        px.setRemarks(productEdit.getExpiryRemarks());
                        productExpiryRepository.save(px);
                    }, () -> {
                        throw new DomainException(DomainError.PRODUCT_EXPIRY_QUANTITY_EXCEEDS_PRODUCT_STOCK);
                    });
        }

        productExpiryRepository.findClosestExpiredDateAvailableByProductId(productId)
                .ifPresentOrElse(product::setClosestExpiredDate, () -> product.setClosestExpiredDate(null));

        product.setCode(code);
        product.setBarcode(barcode);
        product.setName(name);
        product.setDescription(productEdit.getDescription());
        product.setUnitId(unitId);
        product.setUnitLabel(productEdit.getUnit().getLabel());
        product.setCategoryCode(categoryCode);
        product.setStatus(productEdit.getStatus().toString());
        productRepository.save(product);
    }

    private Product validateProductId(Long productId) {
        return productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new DomainException(DomainError.PRODUCT_NOT_FOUND_BY_ID));
    }

    private void validateProductNameAndUnit(String name, Long unitId, Product product) {
        if (ObjectUtils.notEqual(product.getUnitId(), unitId)
                && productRepository.existsByNameIgnoreCaseAndUnitIdAndDeletedAtIsNull(name, unitId)) {
            throw new DomainException(DomainError.PRODUCT_OTHER_EXISTS_BY_NAME_AND_UNIT);
        }
    }

    private void validateProductBarcode(String barcode, Product product) {
        if (StringUtils.isNotBlank(barcode) && !barcode.equals(product.getBarcode())
                && productRepository.existsByBarcodeAndDeletedAtIsNull(barcode)) {
            throw new DomainException(DomainError.PRODUCT_OTHER_EXISTS_BY_BARCODE);
        }
    }

    private void validateProductCode(String code, Product product) {
        if (!code.equals(product.getCode()) && productRepository.existsByCodeAndDeletedAtIsNull(code)) {
            throw new DomainException(DomainError.PRODUCT_OTHER_EXISTS_BY_CODE);
        }
    }

    @ForActivity(Activity.REMOVE_PRODUCTS)
    @CacheEvict(value = {
            CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD,
            CacheNameConstants.PRODUCT_BY_CODE },
        allEntries = true)
    @Transactional
    public void removeProducts(List<Long> ids) {
        productRepository.deleteUpdateByIdIn(ids);
    }

    @ForActivity(Activity.ADD_PRODUCT)
    @CacheEvict(value = { CacheNameConstants.PRODUCTS_BY_FILTER, CacheNameConstants.PRODUCTS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public void createProduct(ProductAddVM productAdd) {
        createProduct(productAdd, Activity.ADD_PRODUCT);
    }

    public Product createProduct(ProductAddVM productAdd, Activity activity) {
        validateConstraints(productAdd);
        Long currentUserId = sessionService.getCurrentSession().getUser().getId();
        String activityName = activity.toString();
        String code = productAdd.getCode();
        String barcode = productAdd.getBarcode();
        String categoryCode = productAdd.getProductCategory().getCode();

        if (productRepository.existsByCodeAndDeletedAtIsNull(code)) {
            throw new DomainException(DomainError.PRODUCT_EXISTS_BY_CODE);
        }

        if (StringUtils.isNotBlank(barcode) && productRepository.existsByBarcodeAndDeletedAtIsNull(barcode)) {
            throw new DomainException(DomainError.PRODUCT_EXISTS_BY_BARCODE);
        }

        if (productRepository.existsByNameIgnoreCaseAndUnitIdAndDeletedAtIsNull(
                productAdd.getName(),
                productAdd.getUnit().getId())) {
            throw new DomainException(DomainError.PRODUCT_EXISTS_BY_NAME_AND_UNIT);
        }

        Product product = new Product();
        product.setCode(code);
        product.setBarcode(barcode);
        product.setName(productAdd.getName());
        product.setDescription(productAdd.getDescription());
        product.setQuantity(productAdd.getStockQuantity());
        product.setUnitId(productAdd.getUnit().getId());
        product.setUnitLabel(productAdd.getUnit().getLabel());
        product.setCategoryCode(categoryCode);
        product.setGeneralSellingPrice(productAdd.getGeneralSellingPrice());
        product.setPrescriptionSellingPrice(productAdd.getPrescriptionSellingPrice());
        product.setClosestExpiredDate(productAdd.getExpiredDate());
        product.setStatus(productAdd.getStatus().toString());

        Product created = productRepository.save(product);
        Long productId = created.getId();

        if (CommonConstants.PRODUCT_CATEGORY_CODE_DRUGS.equals(categoryCode)) {
            DrugClassificationVM drugClassification = productAdd.getDrugClassification();
            Drug drug = new Drug();
            drug.setProductId(productId);
            drug.setClassificationCode(StringUtils.trimToNull(drugClassification.getCode()));
            drug.setIndication(productAdd.getIndication());
            drug.setContraindication(productAdd.getContraindication());
            drugRepository.save(drug);
        }

        if (productAdd.getGeneralSellingPrice() != null) {
            ProductPrice pp = new ProductPrice();
            pp.setProductId(productId);
            pp.setGeneralSellingPrice(productAdd.getGeneralSellingPrice());
            pp.setPrescriptionSellingPrice(productAdd.getPrescriptionSellingPrice());
            pp.setRemarks(productAdd.getPriceRemarks());
            pp.setActivity(activityName);
            pp.setUserId(currentUserId);
            productPriceRepository.save(pp);
        }

        if (productAdd.getStockQuantity() != null) {
            ProductStock ps = new ProductStock();
            ps.setProductId(productId);
            ps.setFinalQuantity(productAdd.getStockQuantity());
            ps.setRemarks(productAdd.getStockRemarks());
            ps.setActivity(activityName);
            ps.setUserId(currentUserId);
            productStockRepository.save(ps);
        }

        if (productAdd.getExpiredDate() != null) {
            ProductExpiry px = new ProductExpiry();
            px.setProductId(productId);
            px.setExpiredDate(productAdd.getExpiredDate());
            px.setBatchNumber(productAdd.getBatchNumber());
            px.setQuantityIn(productAdd.getExpiryQuantity());
            px.setFinalQuantity(productAdd.getExpiryQuantity());
            px.setFinalQuantityExpiredDate(productAdd.getExpiryQuantity());
            px.setUserId(currentUserId);
            px.setActivity(activityName);
            px.setRemarks(productAdd.getExpiryRemarks());
            productExpiryRepository.save(px);
        }
        return created;
    }

    @ForActivity(Activity.GET_PRODUCT_PRICES_BY_PRODUCT_ID)
    public List<ProductPriceVM> getProductPriceByProductId(Long productId) {
        return productPriceRepository.findByProductIdOrderByIdDesc(productId);
    }

    @ForActivity(Activity.GET_PRODUCT_EXPIRIES_BY_PRODUCT_ID)
    public List<ProductExpiryVM> getProductExpiryByProductId(Long productId) {
        return productExpiryRepository.findByProductIdOrderByIdDesc(productId);
    }

    @ForActivity(Activity.GET_PRODUCT_STOCKS_BY_PRODUCT_ID)
    public List<ProductStockVM> getProductStockByProductId(Long productId) {
        return productStockRepository.findByProductIdOrderByIdDesc(productId);
    }

    @ForActivity(Activity.ADD_PRODUCT_EXPIRY)
    @CacheEvict(value = { CacheNameConstants.PRODUCTS_BY_FILTER, CacheNameConstants.PRODUCTS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public void addProductExpiry(ProductExpiryAddVM productExpiryAddVM, Activity activity) {
        Long productId = productExpiryAddVM.getProductId();
        productStockRepository.findFirstByProductIdAndDeletedAtIsNullOrderByIdDesc(productId).ifPresentOrElse(ps -> {
            Integer expiryFinalQuantity = productExpiryRepository.findFirstByProductIdOrderByIdDesc(productId)
                    .map(ProductExpiry::getFinalQuantity).orElse(0);
            Integer finalQuantityExpiredDate = productExpiryRepository
                    .findFirstByProductIdAndExpiredDateOrderByIdDesc(productId, productExpiryAddVM.getExpiredDate())
                    .map(ProductExpiry::getFinalQuantityExpiredDate).orElse(0);
            Integer totalExpiryQuantity = expiryFinalQuantity + productExpiryAddVM.getQuantity();
            Integer totalExpiryQuantityExpiredDate = finalQuantityExpiredDate + productExpiryAddVM.getQuantity();
            if (totalExpiryQuantity > ps.getFinalQuantity()) {
                throw new DomainException(DomainError.PRODUCT_EXPIRY_QUANTITY_EXCEEDS_PRODUCT_STOCK);
            }
            ProductExpiry px = new ProductExpiry();
            px.setProductId(productId);
            px.setExpiredDate(productExpiryAddVM.getExpiredDate());
            px.setBatchNumber(productExpiryAddVM.getBatchNumber());
            px.setQuantityIn(productExpiryAddVM.getQuantity());
            px.setActivity(activity.toString());
            px.setUserId(sessionService.getCurrentSession().getUser().getId());
            px.setFinalQuantity(totalExpiryQuantity);
            px.setFinalQuantityExpiredDate(totalExpiryQuantityExpiredDate);
            productExpiryRepository.save(px);
            productExpiryRepository.findClosestExpiredDateAvailableByProductId(productId)
                    .ifPresent(expiredDate -> productRepository.updateClosestExpiredDateById(productId, expiredDate));
        }, () -> {
            throw new DomainException(DomainError.PRODUCT_EXPIRY_QUANTITY_EXCEEDS_PRODUCT_STOCK);
        });
    }

    @ForActivity(Activity.GET_REMAINING_PRODUCT_EXPIRIES)
    public List<GroupedProductExpiryVM> getRemainingProductExpiry(Long productId) {
        return productExpiryRepository.findGroupedByProductId(productId);
    }

    private boolean containsProductNameAndUnitId(List<ProductImportMapping> mappings, String name, Long unitId) {
        return mappings.stream().anyMatch(mapping -> {
            Product p = mapping.getProduct();
            return p.getName().equalsIgnoreCase(name) && p.getUnitId().equals(unitId);
        });
    }

    @ForActivity(Activity.IMPORT_PRODUCTS)
    @CacheEvict(value = {
            CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD,
            CacheNameConstants.PRODUCT_BY_CODE },
        allEntries = true)
    @Transactional
    public void importProducts(List<ProductImportVM> productImports) {
        Long currentUserId = sessionService.getCurrentSession().getUser().getId();
        String activityName = Activity.IMPORT_PRODUCTS.toString();
        List<ProductImportMapping> mappings = new ArrayList<>();
        Set<String> checkedCategoryCodes = new HashSet<>();
        Set<Unit> checkedUnits = new HashSet<>();
        Set<String> checkedDrugCategoryCodes = new HashSet<>();
        productImports.forEach(pi -> {

            String productName = pi.getName();
            String productCategoryCode = pi.getProductCategoryCode();
            Long unitId = pi.getUnitId();
            BigDecimal generalSellingPrice = pi.getGeneralSellingPrice();
            BigDecimal prescriptionSellingPrice = pi.getPrescriptionSellingPrice();
            Integer quantity = pi.getQuantity();
            LocalDate expiredDate = pi.getExpiredDate();

            if (containsProductNameAndUnitId(mappings, productName, unitId)) {
                return;
            }

            validateConstraints(pi);
            validateProductCategoryCode(checkedCategoryCodes, productCategoryCode);
            Unit unit = validateUnitId(checkedUnits, unitId);

            ProductImportMapping mapping = new ProductImportMapping();

            if (ProductUtils.isProductCategoryDrugs(productCategoryCode)) {
                String drugClassificationCode = pi.getDrugClassificationCode();
                validateDrugClassificationCode(checkedDrugCategoryCodes, drugClassificationCode);
                Drug drug = new Drug();
                drug.setContraindication(pi.getContraindication());
                drug.setIndication(pi.getIndication());
                drug.setClassificationCode(StringUtils.trimToNull(drugClassificationCode));
                mapping.setDrug(drug);
            }
            Product product = new Product();
            product.setBarcode(pi.getBarcode());
            product.setCategoryCode(productCategoryCode);
            product.setCode(pi.getCode());
            product.setDescription(pi.getDescription());
            product.setGeneralSellingPrice(
                    getGeneralSellingPriceOrDefault(generalSellingPrice, prescriptionSellingPrice));
            product.setName(productName);
            product.setPrescriptionSellingPrice(prescriptionSellingPrice);
            product.setQuantity(quantity);
            product.setStatus(pi.getStatus().toString());
            product.setUnitId(unit.getId());
            product.setUnitLabel(unit.getLabel());
            mapping.setProduct(product);
            if (prescriptionSellingPrice != null || generalSellingPrice != null) {
                ProductPrice pp = new ProductPrice();
                pp.setActivity(activityName);
                pp.setGeneralSellingPrice(
                        getGeneralSellingPriceOrDefault(generalSellingPrice, prescriptionSellingPrice));
                pp.setPrescriptionSellingPrice(prescriptionSellingPrice);
                pp.setUserId(currentUserId);
                mapping.setProductPrice(pp);
            }
            if (quantity != null) {
                ProductStock ps = new ProductStock();
                ps.setActivity(activityName);
                ps.setFinalQuantity(quantity);
                ps.setUserId(currentUserId);
                mapping.setProductStock(ps);

                if (expiredDate != null) {
                    product.setClosestExpiredDate(expiredDate);
                    ProductExpiry px = new ProductExpiry();
                    px.setActivity(activityName);
                    px.setExpiredDate(expiredDate);
                    px.setFinalQuantity(quantity);
                    px.setFinalQuantityExpiredDate(quantity);
                    px.setUserId(currentUserId);
                    mapping.setProductExpiry(px);
                }
            }
            mappings.add(mapping);
        });
        processProductImportMappings(mappings);
    }

    @ForActivity(Activity.ADD_PACKAGE)
    @CacheEvict(value = { CacheNameConstants.PRODUCTS_BY_FILTER, CacheNameConstants.PRODUCTS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public void createPackage(ProductAddVM productAdd, List<PackageProductVM> packageProducts) {
        Product created = createProduct(productAdd, Activity.ADD_PACKAGE);
        List<PackageDetail> packageDetails = packageProducts.stream().map(pp -> {
            PackageDetail packageDetail = new PackageDetail();
            packageDetail.setProductId(created.getId());
            packageDetail.setPackageProductId(pp.getId());
            packageDetail.setQuantity(pp.getQuantityInPackage());
            return packageDetail;
        }).toList();
        packageDetailRepository.saveAll(packageDetails);
    }

    @ForActivity(Activity.GET_PACKAGE_PRODUCTS_BY_PRODUCT_ID)
    public List<PackageProductVM> getPackageProductsByProductId(Long productId) {
        return packageDetailRepository.findByProductId(productId);
    }

    @ForActivity(Activity.EDIT_PACKAGE)
    @CacheEvict(value = { CacheNameConstants.PRODUCTS_BY_FILTER, CacheNameConstants.PRODUCTS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public void updatePackage(ProductEditVM productEdit, Long productId, List<PackageProductVM> packageProducts) {
        updateProduct(productEdit, productId, Activity.EDIT_PACKAGE);
        packageDetailRepository.deleteByProductId(productId);
        List<PackageDetail> packageDetails = packageProducts.stream().map(pp -> {
            PackageDetail packageDetail = new PackageDetail();
            packageDetail.setProductId(productId);
            packageDetail.setPackageProductId(pp.getId());
            packageDetail.setQuantity(pp.getQuantityInPackage());
            return packageDetail;
        }).toList();
        packageDetailRepository.saveAll(packageDetails);
    }

    /**
     * Returns the package product with the lowest quantity from all products in
     * this package.
     */
    @ForActivity(Activity.GET_PACKAGE_QUANTITY)
    public PackageProductVM getLowestQuantityPackageProduct(Long productId) {
        List<PackageProductVM> products = packageDetailRepository.findByProductId(productId);
        return products.stream().min((p1, p2) -> {
            int q1 = p1.getQuantity() == null ? 0 : p1.getQuantity().intValue();
            int q2 = p2.getQuantity() == null ? 0 : p2.getQuantity().intValue();
            return Integer.compare(q1, q2);
        }).orElseThrow();
    }

    private BigDecimal getGeneralSellingPriceOrDefault(BigDecimal generalSellingPrice, BigDecimal defaultPrice) {
        return generalSellingPrice == null ? defaultPrice : generalSellingPrice;
    }

    private void validateDrugClassificationCode(Set<String> checkedDrugCategoryCodes, String drugClassificationCode) {
        if (StringUtils.isNotBlank(drugClassificationCode) && !checkedDrugCategoryCodes.contains(drugClassificationCode)
                && !drugClassificationRepository.existsByCodeAndDeletedAtIsNull(drugClassificationCode)) {
            throw new DomainException(DomainError.DRUG_CATEGORY_NOT_FOUND_BY_ID);
        }
        checkedDrugCategoryCodes.add(drugClassificationCode);
    }

    private Unit validateUnitId(Set<Unit> checkedUnits, Long unitId) {
        Unit unit = checkedUnits.stream().filter(u -> u.getId().equals(unitId)).findAny()
                .or(() -> unitRepository.findByIdAndDeletedAtIsNull(unitId))
                .orElseThrow(() -> new DomainException(DomainError.UNIT_NOT_FOUND_BY_ID));
        checkedUnits.add(unit);
        return unit;
    }

    private void validateProductCategoryCode(Set<String> checkedCategoryCodes, String productCategoryCode) {
        if (!checkedCategoryCodes.contains(productCategoryCode)
                && !productCategoryRepository.existsByCodeAndDeletedAtIsNull(productCategoryCode)) {
            throw new DomainException(DomainError.PRODUCT_CATEGORY_NOT_FOUND_BY_CODE, productCategoryCode);
        }
        checkedCategoryCodes.add(productCategoryCode);
    }

    private void processProductImportMappings(List<ProductImportMapping> mappings) {
        mappings.forEach(mapping -> {
            Product product = productRepository.save(mapping.getProduct());
            Long productId = product.getId();
            Drug drug = mapping.getDrug();
            if (drug != null) {
                drug.setProductId(productId);
                drugRepository.save(drug);
            }
            ProductPrice pp = mapping.getProductPrice();
            if (pp != null) {
                pp.setProductId(productId);
                productPriceRepository.save(pp);
            }
            ProductStock ps = mapping.getProductStock();
            if (ps != null) {
                ps.setProductId(productId);
                productStockRepository.save(ps);
            }
            ProductExpiry px = mapping.getProductExpiry();
            if (px != null) {
                px.setProductId(productId);
                productExpiryRepository.save(px);
            }
        });
    }

    @Getter
    @Setter
    private class ProductImportMapping {
        private Product product;
        private Drug drug;
        private ProductPrice productPrice;
        private ProductStock productStock;
        private ProductExpiry productExpiry;
    }

}
