package com.gitlab.muhammadkholidb.bianglala.service;

import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.constant.ConfigurationConstants;
import com.gitlab.muhammadkholidb.bianglala.constant.DomainError;
import com.gitlab.muhammadkholidb.bianglala.domain.Drug;
import com.gitlab.muhammadkholidb.bianglala.domain.Product;
import com.gitlab.muhammadkholidb.bianglala.domain.Wholesale;
import com.gitlab.muhammadkholidb.bianglala.exception.DomainException;
import com.gitlab.muhammadkholidb.bianglala.repository.DrugRepository;
import com.gitlab.muhammadkholidb.bianglala.repository.ProductRepository;
import com.gitlab.muhammadkholidb.bianglala.repository.WholesaleRepository;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.DrugVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductEditVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductFilterVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.WholesaleVM;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.model.DataModel;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.sql.Where;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Cacheable("productsByFilter")
    public List<ProductVM> searchProduct(ProductFilterVM param) {
        String languageId = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_ID);
        return productRepository.filter(param, Long.valueOf(languageId));
    }

    @CacheEvict(value = "productsByFilter", allEntries = true)
    @Transactional
    public boolean updateProduct(ProductEditVM productEdit) {

        if (productRepository.existsByCode(productEdit.getCode())) {
            throw new DomainException(DomainError.PRODUCT_EXISTS_BY_CODE);
        }

        String barcode = productEdit.getBarcode();

        if (StringUtils.isNotBlank(barcode) && productRepository.existsByBarcode(barcode)) {
            throw new DomainException(DomainError.PRODUCT_EXISTS_BY_BARCODE);
        }

        Integer countUpdated = productRepository.updateProduct(productEdit);

        DrugVM drug = productEdit.getDrug();
        if (drug != null) {
            drugRepository.delete(new Where().equals(Drug.C_PRODUCT_ID, productEdit.getId()), true);
            drugRepository.create(convertObject(drug, Drug.class));
        }

        List<WholesaleVM> wholesales = productEdit.getWholesales();
        if (ObjectUtils.isNotEmpty(wholesales)) {
            wholesaleRepository.delete(new Where().equals(Wholesale.C_PRODUCT_ID, productEdit.getId()), true);
            wholesales.forEach(wholesale -> wholesaleRepository.create(convertObject(wholesale, Wholesale.class)));
        }

        return countUpdated > 0;
    }

    @CacheEvict(value = "productsByFilter", allEntries = true)
    @Transactional
    public void removeProducts(List<Long> ids) {
        productRepository.delete(new Where().in(DataModel.C_ID, ids)); 
    }

}
