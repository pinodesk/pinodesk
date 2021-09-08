package toscabox.desktop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import toscabox.desktop.constant.CacheNameConstants;
import toscabox.desktop.constant.ConfigurationConstants;
import toscabox.desktop.constant.DomainError;
import toscabox.desktop.domain.ProductCategory;
import toscabox.desktop.exception.DomainException;
import toscabox.desktop.repository.ProductCategoryRepository;
import toscabox.desktop.viewmodel.ProductCategoryVM;

@Service
public class ProductCategoryService extends BaseService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ConfigurationService configurationService;

    public ProductCategoryVM getProductCategoryById(Long id) {
        return objectConverter.convertOptionalOrThrow(
                productCategoryRepository.readOne(id),
                ProductCategoryVM.class,
                new DomainException(DomainError.PRODUCT_CATEGORY_NOT_FOUND_BY_ID));
    }

    @Cacheable(CacheNameConstants.PRODUCT_CATEGORIES_BY_KEYWORD)
    public List<ProductCategoryVM> searchProductCategoryByKeyword(String keyword) {
        String languageCode = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_CODE);
        List<ProductCategory> categories = productCategoryRepository.filter(keyword, languageCode);
        return objectConverter.convertList(categories, ProductCategoryVM.class);
    }

}
