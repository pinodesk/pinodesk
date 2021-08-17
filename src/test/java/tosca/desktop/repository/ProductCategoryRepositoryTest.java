package tosca.desktop.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;

import java.util.List;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import tosca.desktop.domain.ProductCategory;

@DatabaseSetup("ProductCategoryRepositoryTest.xml")
class ProductCategoryRepositoryTest extends RepositoryTestBase {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Test
    void testFilter_shouldReturnFilteredProductCategories() {
        String keyword = "supplies";
        String languageCode = "en";
        List<ProductCategory> productCategories = productCategoryRepository.filter(keyword, languageCode);
        assertThat(productCategories, hasSize(3));
        for (ProductCategory pc : productCategories) {
            Long id = pc.getId();
            if (id == 1000000001) {
                assertThat(pc.getParentCategoryId(), is(nullValue()));
                assertThat(pc.getLanguageCode(), is(languageCode));
                assertThat(pc.getCode(), is("000000001"));
                assertThat(pc.getName(), is("Animals & Pet Supplies"));
                break;
            }
            if (id == 1000000002) {
                assertThat(pc.getParentCategoryId(), is(1000000001));
                assertThat(pc.getLanguageCode(), is(languageCode));
                assertThat(pc.getCode(), is("000000002"));
                assertThat(pc.getName(), is("Pet Supplies"));
                break;
            }
            if (id == 1000000003) {
                assertThat(pc.getParentCategoryId(), is(1000000002));
                assertThat(pc.getLanguageCode(), is(languageCode));
                assertThat(pc.getCode(), is("000000003"));
                assertThat(pc.getName(), is("Bird Supplies"));
                break;
            }
        }
    }

}
