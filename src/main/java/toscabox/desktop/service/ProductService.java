package toscabox.desktop.service;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.model.DataModel;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import toscabox.desktop.constant.CacheNameConstants;
import toscabox.desktop.constant.ConfigurationConstants;
import toscabox.desktop.constant.DomainError;
import toscabox.desktop.domain.Drug;
import toscabox.desktop.domain.Product;
import toscabox.desktop.domain.Wholesale;
import toscabox.desktop.exception.DomainException;
import toscabox.desktop.repository.DrugRepository;
import toscabox.desktop.repository.ProductRepository;
import toscabox.desktop.repository.WholesaleRepository;
import toscabox.desktop.viewmodel.DrugVM;
import toscabox.desktop.viewmodel.ProductAddVM;
import toscabox.desktop.viewmodel.ProductEditVM;
import toscabox.desktop.viewmodel.ProductFilterVM;
import toscabox.desktop.viewmodel.ProductVM;
import toscabox.desktop.viewmodel.WholesaleVM;

@Service
public class ProductService extends BaseService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private WholesaleRepository wholesaleRepository;

    @Cacheable(CacheNameConstants.PRODUCTS_BY_FILTER)
    public List<ProductVM> searchProduct(ProductFilterVM param) {
        String languageCode = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_CODE);
        return productRepository.filter(param, languageCode);
    }

    @Cacheable(CacheNameConstants.PRODUCTS_BY_KEYWORD)
    public List<ProductVM> searchProductsByKeyword(String keyword) {
        String languageCode = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_CODE);
        return productRepository.findByKeyword(keyword, languageCode);
    }

    @CacheEvict(value = { CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD }, allEntries = true)
    @Transactional
    public boolean updateProduct(ProductEditVM productEdit) {

        Long productId = productEdit.getId();
        String code = productEdit.getCode();
        String barcode = productEdit.getBarcode();

        if (!productRepository.exists(productId)) {
            throw new DomainException(DomainError.PRODUCT_NOT_FOUND_BY_ID);
        }

        if (productRepository.existsByCode(code, productId)) {
            throw new DomainException(DomainError.PRODUCT_OTHER_EXISTS_BY_CODE);
        }

        if (StringUtils.isNotBlank(barcode) && productRepository.existsByBarcode(barcode, productId)) {
            throw new DomainException(DomainError.PRODUCT_OTHER_EXISTS_BY_BARCODE);
        }

        if (productRepository.existsByNameAndUnit(productEdit.getName(), productEdit.getUnit().getId(), productId)) {
            throw new DomainException(DomainError.PRODUCT_OTHER_EXISTS_BY_NAME_AND_UNIT);
        }

        Integer countUpdated = productRepository.updateProduct(productEdit);

        DrugVM drug = productEdit.getDrug();
        if (drug != null) {
            drugRepository.delete(new Where().equals(Drug.C_PRODUCT_ID, productEdit.getId()), true);
            drugRepository.create(objectConverter.convertObject(drug, Drug.class));
        }

        List<WholesaleVM> wholesales = productEdit.getWholesales();
        if (ObjectUtils.isNotEmpty(wholesales)) {
            wholesaleRepository.delete(new Where().equals(Wholesale.C_PRODUCT_ID, productEdit.getId()), true);
            wholesales.forEach(
                    wholesale -> wholesaleRepository.create(objectConverter.convertObject(wholesale, Wholesale.class)));
        }

        return countUpdated > 0;
    }

    @CacheEvict(value = { CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD }, allEntries = true)
    @Transactional
    public void removeProducts(List<Long> ids) {
        productRepository.delete(new Where().in(DataModel.C_ID, ids));
    }

    @CacheEvict(value = { CacheNameConstants.PRODUCTS_BY_FILTER,
            CacheNameConstants.PRODUCTS_BY_KEYWORD }, allEntries = true)
    @Transactional
    public Long createProduct(ProductAddVM productAdd) {

        String code = productAdd.getCode();
        String barcode = productAdd.getBarcode();

        if (productRepository.existsByCode(code)) {
            throw new DomainException(DomainError.PRODUCT_EXISTS_BY_CODE);
        }

        if (StringUtils.isNotBlank(barcode) && productRepository.existsByBarcode(barcode)) {
            throw new DomainException(DomainError.PRODUCT_EXISTS_BY_BARCODE);
        }

        if (productRepository.existsByNameAndUnit(productAdd.getName(), productAdd.getUnit().getId())) {
            throw new DomainException(DomainError.PRODUCT_EXISTS_BY_NAME_AND_UNIT);
        }

        Long productId = productRepository.createProduct(productAdd);

        DrugVM drug = productAdd.getDrug();
        if (drug != null) {
            drug.setProductId(productId);
            drugRepository.create(objectConverter.convertObject(drug, Drug.class));
        }

        List<WholesaleVM> wholesales = productAdd.getWholesales();
        if (ObjectUtils.isNotEmpty(wholesales)) {
            wholesales.forEach(wholesale -> {
                wholesale.setProductId(productId);
                wholesaleRepository.create(objectConverter.convertObject(wholesale, Wholesale.class));
            });
        }

        return productId;
    }

}
