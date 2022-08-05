package pinus.desktop.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import pinus.desktop.annotation.ForActivity;
import pinus.desktop.constant.Activity;
import pinus.desktop.constant.CacheNameConstants;
import pinus.desktop.constant.ConfigurationConstants;
import pinus.desktop.constant.DomainError;
import pinus.desktop.domain.ProductCategory;
import pinus.desktop.exception.DomainException;
import pinus.desktop.repository.ProductCategoryRepository;
import pinus.desktop.viewmodel.ProductCategoryVM;

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

}
