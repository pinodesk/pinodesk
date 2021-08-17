package tosca.desktop.service;

import java.util.List;

import tosca.desktop.constant.CacheName;
import tosca.desktop.constant.ConfigurationConstants;
import tosca.desktop.constant.DomainError;
import tosca.desktop.domain.ProductCategory;
import tosca.desktop.exception.DomainException;
import tosca.desktop.repository.ProductCategoryRepository;
import tosca.desktop.viewmodel.ProductCategoryVM;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ProductCategoryService extends BaseService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ConfigurationService configurationService;

    public ProductCategoryVM getProductCategoryById(Long id) {
        return objectConverter.convertOptionalOrThrow(productCategoryRepository.readOne(id), ProductCategoryVM.class,
                new DomainException(DomainError.PRODUCT_CATEGORY_NOT_FOUND_BY_ID));
    }

    @Cacheable(CacheName.Keys.PRODUCT_CATEGORIES_BY_KEYWORD)
    public List<ProductCategoryVM> searchProductCategoryByKeyword(String keyword) {
        String languageCode = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_CODE);
        List<ProductCategory> categories = productCategoryRepository.filter(keyword, languageCode);
        return objectConverter.convertList(categories, ProductCategoryVM.class);
    }

}
