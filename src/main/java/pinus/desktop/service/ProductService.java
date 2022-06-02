package pinus.desktop.service;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pinus.desktop.constant.Activity;
import pinus.desktop.constant.CacheNameConstants;
import pinus.desktop.constant.CommonConstants;
import pinus.desktop.constant.ConfigurationConstants;
import pinus.desktop.constant.DomainError;
import pinus.desktop.domain.Drug;
import pinus.desktop.domain.Product;
import pinus.desktop.domain.ProductExpiry;
import pinus.desktop.domain.ProductPrice;
import pinus.desktop.domain.ProductStock;
import pinus.desktop.exception.DomainException;
import pinus.desktop.repository.DrugRepository;
import pinus.desktop.repository.ProductExpiryRepository;
import pinus.desktop.repository.ProductPriceRepository;
import pinus.desktop.repository.ProductRepository;
import pinus.desktop.repository.ProductStockRepository;
import pinus.desktop.viewmodel.DrugCategoryVM;
import pinus.desktop.viewmodel.ProductAddVM;
import pinus.desktop.viewmodel.ProductEditVM;
import pinus.desktop.viewmodel.ProductExpiryAddVM;
import pinus.desktop.viewmodel.ProductExpiryVM;
import pinus.desktop.viewmodel.ProductFilterVM;
import pinus.desktop.viewmodel.ProductPriceVM;
import pinus.desktop.viewmodel.ProductStockVM;
import pinus.desktop.viewmodel.ProductVM;

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

    @Cacheable(CacheNameConstants.PRODUCTS_BY_FILTER)
    public List<ProductVM> searchProductsByFilter(ProductFilterVM filter) {
        String languageCode = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_CODE);
        return productRepository.findByFilter(filter, languageCode);
    }

    @Cacheable(CacheNameConstants.PRODUCTS_BY_KEYWORD)
    public List<ProductVM> searchProductsByKeyword(String keyword) {
        String languageCode = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_CODE);
        return productRepository.findByKeyword(keyword, languageCode);
    }

    @CacheEvict(value = { CacheNameConstants.PRODUCTS_BY_FILTER, CacheNameConstants.PRODUCTS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public void updateProduct(ProductEditVM productEdit, Long productId) {

        validateConstraints(productEdit);

        String activityName = Activity.EDIT_PRODUCT.name();
        String name = productEdit.getName();
        String code = productEdit.getCode();
        String barcode = productEdit.getBarcode();
        String categoryCode = productEdit.getProductCategory().getCode();
        Long unitId = productEdit.getUnit().getId();

        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new DomainException(DomainError.PRODUCT_NOT_FOUND_BY_ID));

        if (!code.equals(product.getCode()) && productRepository.existsByCodeAndDeletedAtIsNull(code)) {
            throw new DomainException(DomainError.PRODUCT_OTHER_EXISTS_BY_CODE);
        }

        if (StringUtils.isNotBlank(barcode) && !barcode.equals(product.getBarcode())
                && productRepository.existsByBarcodeAndDeletedAtIsNull(barcode)) {
            throw new DomainException(DomainError.PRODUCT_OTHER_EXISTS_BY_BARCODE);
        }

        if (ObjectUtils.notEqual(product.getUnitId(), unitId)
                && productRepository.existsByNameIgnoreCaseAndUnitIdAndDeletedAtIsNull(name, unitId)) {
            throw new DomainException(DomainError.PRODUCT_OTHER_EXISTS_BY_NAME_AND_UNIT);
        }

        if (CommonConstants.PRODUCT_CATEGORY_CODE_DRUGS.equals(categoryCode)) {
            DrugCategoryVM drugCategory = productEdit.getDrugCategory();
            drugRepository.findByProductIdAndDeletedAtIsNull(productId).ifPresentOrElse(drug -> {
                drug.setDrugCategoryId(drugCategory.getId());
                drug.setIndication(productEdit.getIndication());
                drug.setContraindication(productEdit.getContraindication());
                drugRepository.save(drug);
            }, () -> {
                Drug drug = new Drug();
                drug.setProductId(productId);
                drug.setDrugCategoryId(drugCategory.getId());
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
            pp.setUserId(1l);
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
            ps.setUserId(1l);
            productStockRepository.save(ps);
        }

        if (productEdit.getExpiredDate() != null) {
            productStockRepository.findFirstByProductIdAndDeletedAtIsNullOrderByIdDesc(productId)
                    .ifPresentOrElse(ps -> {
                        Integer expiryFinalQuantity = productExpiryRepository
                                .findFirstByProductIdAndDeletedAtIsNullOrderByIdDesc(productId)
                                .map(ProductExpiry::getFinalQuantity).orElse(0);
                        Integer totalExpiryQuantity = expiryFinalQuantity + productEdit.getExpiryQuantity();
                        if (totalExpiryQuantity > ps.getFinalQuantity()) {
                            throw new DomainException(DomainError.PRODUCT_EXPIRY_QUANTITY_EXCEEDS_PRODUCT_STOCK);
                        }
                        ProductExpiry px = new ProductExpiry();
                        px.setProductId(productId);
                        px.setExpiredDate(productEdit.getExpiredDate());
                        px.setBatchNumber(productEdit.getBatchNumber());
                        px.setQuantityIn(productEdit.getExpiryQuantity());
                        px.setFinalQuantity(totalExpiryQuantity);
                        px.setUserId(1l);
                        px.setActivity(activityName);
                        px.setRemarks(productEdit.getExpiryRemarks());
                        productExpiryRepository.save(px);
                    }, () -> {
                        throw new DomainException(DomainError.PRODUCT_EXPIRY_QUANTITY_EXCEEDS_PRODUCT_STOCK);
                    });
        }

        productExpiryRepository.findFirstByProductIdAndDeletedAtIsNullOrderByExpiredDate(productId)
                .ifPresent(px -> product.setClosestExpiredDate(px.getExpiredDate()));

        product.setCode(code);
        product.setBarcode(barcode);
        product.setName(name);
        product.setDescription(productEdit.getDescription());
        product.setUnitId(unitId);
        product.setUnitLabel(productEdit.getUnit().getLabel());
        product.setCategoryCode(categoryCode);
        product.setStatus(productEdit.getStatus().name());
        productRepository.save(product);
    }

    @CacheEvict(value = { CacheNameConstants.PRODUCTS_BY_FILTER, CacheNameConstants.PRODUCTS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public void removeProducts(List<Long> ids) {
        productRepository.deleteUpdateByIdIn(ids);
    }

    @CacheEvict(value = { CacheNameConstants.PRODUCTS_BY_FILTER, CacheNameConstants.PRODUCTS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public void createProduct(ProductAddVM productAdd) {

        validateConstraints(productAdd);

        String activityName = Activity.ADD_PRODUCT.name();
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
        product.setStatus(productAdd.getStatus().name());

        Product created = productRepository.save(product);
        Long productId = created.getId();

        if (CommonConstants.PRODUCT_CATEGORY_CODE_DRUGS.equals(categoryCode)) {
            DrugCategoryVM drugCategory = productAdd.getDrugCategory();
            Drug drug = new Drug();
            drug.setProductId(productId);
            drug.setDrugCategoryId(drugCategory.getId());
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
            pp.setUserId(1l);
            productPriceRepository.save(pp);
        }

        if (productAdd.getStockQuantity() != null) {
            ProductStock ps = new ProductStock();
            ps.setProductId(productId);
            ps.setFinalQuantity(productAdd.getStockQuantity());
            ps.setRemarks(productAdd.getStockRemarks());
            ps.setActivity(activityName);
            ps.setUserId(1l);
            productStockRepository.save(ps);
        }

        if (productAdd.getExpiredDate() != null) {
            ProductExpiry px = new ProductExpiry();
            px.setProductId(productId);
            px.setExpiredDate(productAdd.getExpiredDate());
            px.setBatchNumber(productAdd.getBatchNumber());
            px.setQuantityIn(productAdd.getExpiryQuantity());
            px.setFinalQuantity(productAdd.getExpiryQuantity());
            px.setUserId(1l);
            px.setActivity(activityName);
            px.setRemarks(productAdd.getExpiryRemarks());
            productExpiryRepository.save(px);
        }
    }

    public List<ProductPriceVM> getProductPriceByProductId(Long productId) {
        return objectConverter.convertList(
                productPriceRepository.findByProductIdAndDeletedAtIsNullOrderByIdDesc(productId),
                ProductPriceVM.class);
    }

    public List<ProductExpiryVM> getProductExpiryByProductId(Long productId) {
        return objectConverter.convertList(
                productExpiryRepository.findByProductIdAndDeletedAtIsNullOrderByIdDesc(productId),
                ProductExpiryVM.class);
    }

    public List<ProductStockVM> getProductStockByProductId(Long productId) {
        return objectConverter.convertList(
                productStockRepository.findByProductIdAndDeletedAtIsNullOrderByIdDesc(productId),
                ProductStockVM.class);
    }

    @CacheEvict(value = { CacheNameConstants.PRODUCTS_BY_FILTER, CacheNameConstants.PRODUCTS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public void addProductExpiry(ProductExpiryAddVM productExpiryAddVM, Activity activity) {
        Long productId = productExpiryAddVM.getProductId();
        productStockRepository.findFirstByProductIdAndDeletedAtIsNullOrderByIdDesc(productId).ifPresentOrElse(ps -> {
            Integer expiryFinalQuantity = productExpiryRepository
                    .findFirstByProductIdAndDeletedAtIsNullOrderByIdDesc(productId).map(ProductExpiry::getFinalQuantity)
                    .orElse(0);
            Integer totalExpiryQuantity = expiryFinalQuantity + productExpiryAddVM.getQuantity();
            if (totalExpiryQuantity > ps.getFinalQuantity()) {
                throw new DomainException(DomainError.PRODUCT_EXPIRY_QUANTITY_EXCEEDS_PRODUCT_STOCK);
            }
            ProductExpiry px = new ProductExpiry();
            px.setProductId(productId);
            px.setExpiredDate(productExpiryAddVM.getExpiredDate());
            px.setBatchNumber(productExpiryAddVM.getBatchNumber());
            px.setQuantityIn(productExpiryAddVM.getQuantity());
            px.setFinalQuantity(totalExpiryQuantity);
            px.setActivity(activity.name());
            px.setUserId(1l);
            productExpiryRepository.save(px);
            productExpiryRepository.findFirstByProductIdAndDeletedAtIsNullOrderByExpiredDate(productId)
                    .ifPresent(productExpiry -> {
                        productRepository.updateClosestExpiredDateById(productId, productExpiry.getExpiredDate());
                    });
        }, () -> {
            throw new DomainException(DomainError.PRODUCT_EXPIRY_QUANTITY_EXCEEDS_PRODUCT_STOCK);
        });
    }

}
