package stoready.desktop.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import stoready.desktop.annotation.ForActivity;
import stoready.desktop.constant.Activity;
import stoready.desktop.constant.CacheNameConstants;
import stoready.desktop.constant.ConfigurationConstants;
import stoready.desktop.constant.DomainError;
import stoready.desktop.domain.ProductCategory;
import stoready.desktop.exception.DomainException;
import stoready.desktop.repository.ProductCategoryRepository;
import stoready.desktop.viewmodel.ProductCategoryVM;

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
