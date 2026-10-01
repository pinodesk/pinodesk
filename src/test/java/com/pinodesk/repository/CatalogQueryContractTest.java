package com.pinodesk.repository;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.pinodesk.entity.Unit;
import com.pinodesk.entity.DrugClassification;

@DatabaseSetup("CatalogQueryContractTest.xml")
class CatalogQueryContractTest extends RepositoryTestBase {
    @Autowired
    private UnitRepository units;
    @Autowired
    private DrugClassificationRepository classifications;

    @Test
    void unitSearchMatchesNameCodeAndLabelWithinLanguage() {
        for (String keyword : List.of("TABLET", "0001", "TBL")) {
            List<Unit> results = units.findByKeyword(keyword, "en");
            assertEquals(List.of(8101L), results.stream().map(Unit::getId).collect(Collectors.toList()), keyword);
        }
        assertTrue(units.findByKeyword("unknown", "en").isEmpty());
        assertTrue(units.findByKeyword("0003", "en").isEmpty());
    }

    @Test
    void unitsAreSortedAndDeletedRowsAreHidden() {
        assertEquals(
                List.of("Capsule", "Tablet"),
                units.findByKeyword("", "en").stream().map(Unit::getName).collect(Collectors.toList()));
        assertEquals(2, units.findByLanguageAndDeletedAtIsNullOrderByName("en").size());
        assertTrue(units.findByIdAndDeletedAtIsNull(8103L).isEmpty());
        assertFalse(units.existsByCodeAndDeletedAtIsNull("0003"));
        assertEquals(8101L, units.findByLanguageAndCodeAndDeletedAtIsNull("en", "0001").orElseThrow().getId());
        assertEquals(8104L, units.findByLanguageAndCodeAndDeletedAtIsNull("id", "0001").orElseThrow().getId());
        assertEquals("Tablet", units.findByLabelAndDeletedAtIsNull("tbl").orElseThrow().getName());
        assertEquals(3, units.findByDeletedAtIsNull().size());
    }

    @Test
    void classificationSearchIsCaseInsensitiveAndLanguageScoped() {
        assertEquals(
                List.of(8201L),
                classifications.findByKeyword("ANALGES", "en").stream().map(DrugClassification::getId)
                        .collect(Collectors.toList()));
        assertEquals(
                List.of(8201L),
                classifications.findByKeyword("0001", "en").stream().map(DrugClassification::getId)
                        .collect(Collectors.toList()));
        assertEquals(
                List.of("Analgesic", "Vitamin"),
                classifications.findByKeyword("", "en").stream().map(DrugClassification::getName)
                        .collect(Collectors.toList()));
        assertTrue(classifications.findByKeyword("deleted", "en").isEmpty());
        assertTrue(classifications.findByKeyword("missing", "en").isEmpty());
    }

    @Test
    void classificationLookupsExcludeDeletedRows() {
        assertTrue(classifications.findByIdAndDeletedAtIsNull(8203L).isEmpty());
        assertFalse(classifications.existsByCodeAndDeletedAtIsNull("0003"));
        assertTrue(classifications.existsByCodeAndDeletedAtIsNull("0001"));
        assertEquals(
                "Analgesic",
                classifications.findByLanguageAndCodeAndDeletedAtIsNull("en", "0001").orElseThrow().getName());
        assertEquals(
                "Analgesik",
                classifications.findByLanguageAndCodeAndDeletedAtIsNull("id", "0001").orElseThrow().getName());
    }
}
