package pospino.desktop.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import pospino.desktop.annotation.ForActivity;
import pospino.desktop.constant.Activity;
import pospino.desktop.constant.CacheNameConstants;
import pospino.desktop.constant.ConfigurationConstants;
import pospino.desktop.constant.DomainError;
import pospino.desktop.domain.ProductCategory;
import pospino.desktop.exception.DomainException;
import pospino.desktop.repository.ProductCategoryRepository;
import pospino.desktop.viewmodel.ProductCategoryVM;

@Service
public class ProductCategoryService extends BaseService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ConfigurationService configurationService;

    @ForActivity(Activity.GET_PRODUCT_CATEGORY_BY_ID)
    public ProductCategoryVM getProductCategoryById(Long id) {
        return objectConverter.convertOptionalOrThrow(
                productCategoryRepository.findByIdAndDeletedAtIsNull(id),
                ProductCategoryVM.class,
                new DomainException(DomainError.PRODUCT_CATEGORY_NOT_FOUND_BY_ID));
    }

    @ForActivity(Activity.SEARCH_PRODUCT_CATEGORIES_BY_KEYWORD)
    @Cacheable(CacheNameConstants.PRODUCT_CATEGORIES_BY_KEYWORD)
    public List<ProductCategoryVM> searchProductCategoryByKeyword(String keyword) {
        String language = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE);
        List<ProductCategory> categories;
        if (StringUtils.isBlank(keyword)) {
            categories = productCategoryRepository.findByLanguageAndDeletedAtIsNullOrderByName(language);
        } else {
            categories = productCategoryRepository.findByKeyword(keyword, language);
        }
        return objectConverter.convertList(categories, ProductCategoryVM.class);
    }

    public ProductCategoryVM getProductCategoryByCode(String code, String language) {
        return objectConverter.convertOptionalOrThrow(
                productCategoryRepository.findByCodeAndLanguageAndDeletedAtIsNull(code, language),
                ProductCategoryVM.class,
                new DomainException(DomainError.PRODUCT_CATEGORY_NOT_FOUND_BY_CODE));
    }

}
