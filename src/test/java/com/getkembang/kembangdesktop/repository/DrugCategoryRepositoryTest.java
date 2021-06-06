package com.getkembang.kembangdesktop.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.List;

import com.getkembang.kembangdesktop.domain.DrugCategory;
import com.github.database.rider.core.api.dataset.DataSet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DataSet("t_drug_category.yml")
class DrugCategoryRepositoryTest extends BaseRepositoryTest {

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
