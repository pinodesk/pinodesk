package tosca.desktop.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.List;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import tosca.desktop.domain.DrugCategory;

@DatabaseSetup("DrugCategoryRepositoryTest.xml")
class DrugCategoryRepositoryTest extends RepositoryTestBase {

    @Autowired
    private DrugCategoryRepository drugCategoryRepository;

    @Test
    void testFilter_shouldReturnFilteredDrugCategories() {
        String keyword = "bebas";
        long drugCategoryBaseId = 1;
        List<DrugCategory> drugCategories = drugCategoryRepository.filter(keyword, drugCategoryBaseId);
        assertThat(drugCategories, hasSize(2));
        assertThat(drugCategories, hasItems(
                hasProperty("code", is("PERMENKESRI01")),
                hasProperty("name", is("Obat Bebas Terbatas")), 
                hasProperty("code", is("PERMENKESRI02")),
                hasProperty("name", is("Obat Bebas"))));
    }

}
