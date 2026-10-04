package com.pinodesk.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import com.pinodesk.constant.*;
import com.pinodesk.entity.*;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.*;
import com.pinodesk.viewmodel.*;

class DoctorServiceTest extends BaseServiceTest {
    @Mock
    private DoctorRepository doctors;
    @Mock
    private DoctorCategoryRepository categories;
    @Mock
    private ConfigurationService configuration;
    @InjectMocks
    private DoctorService service;

    private DoctorAddVM addition() {
        DoctorAddVM a = new DoctorAddVM();
        a.setCode("DOC1");
        a.setName("Doctor");
        a.setCategoryCode("GP");
        a.setRegistrationNumber("REG1");
        a.setMedicalLicenseNumber("LIC1");
        a.setEmail("doc@example.com");
        a.setPhone("081");
        return a;
    }

    private DoctorEditVM edit() {
        DoctorEditVM e = objectMapper.convertValue(addition(), DoctorEditVM.class);
        DoctorCategoryVM c = new DoctorCategoryVM();
        c.setCode("GP");
        e.setCategory(c);
        return e;
    }

    private Doctor existing() {
        Doctor d = objectMapper.convertValue(addition(), Doctor.class);
        d.setId(7L);
        when(doctors.findByIdAndDeletedAtIsNull(7L)).thenReturn(Optional.of(d));
        return d;
    }

    private DomainError conflict(int field) {
        switch (field) {
            case 0:
                when(doctors.existsByCodeIgnoreCaseAndDeletedAtIsNull("DOC1")).thenReturn(true);
                return DomainError.DOCTOR_EXISTS_BY_CODE;
            case 1:
                when(doctors.existsByRegistrationNumberAndDeletedAtIsNull("REG1")).thenReturn(true);
                return DomainError.DOCTOR_EXISTS_BY_REGISTRATION_NUMBER;
            case 2:
                when(doctors.existsByMedicalLicenseNumberAndDeletedAtIsNull("LIC1")).thenReturn(true);
                return DomainError.DOCTOR_EXISTS_BY_MEDICAL_LICENSE_NUMBER;
            case 3:
                when(doctors.existsByEmailIgnoreCaseAndDeletedAtIsNull("doc@example.com")).thenReturn(true);
                return DomainError.DOCTOR_EXISTS_BY_EMAIL;
            default:
                when(doctors.existsByPhoneAndDeletedAtIsNull("081")).thenReturn(true);
                return DomainError.DOCTOR_EXISTS_BY_PHONE;
        }
    }

    @Test
    void createMapsAllDetailsAndReturnsPersistedDoctor() {
        when(doctors.save(any())).thenAnswer(inv -> {
            Doctor d = inv.getArgument(0);
            d.setId(7L);
            return d;
        });
        Doctor d = service.createDoctor(addition());
        assertEquals(7L, d.getId());
        assertEquals("GP", d.getCategoryCode());
        assertEquals("doc@example.com", d.getEmail());
        assertEquals("REG1", d.getRegistrationNumber());
        assertEquals("LIC1", d.getMedicalLicenseNumber());
        assertEquals("081", d.getPhone());
    }

    @Test
    void blankOptionalFieldsSkipUniquenessChecks() {
        DoctorAddVM a = addition();
        a.setRegistrationNumber(null);
        a.setMedicalLicenseNumber(" ");
        a.setEmail("");
        a.setPhone(null);
        service.createDoctor(a);
        verify(doctors).existsByCodeIgnoreCaseAndDeletedAtIsNull("DOC1");
        verify(doctors).save(any());
        verifyNoMoreInteractions(doctors);
    }

    @Test
    void duplicateFieldsRejectCreation() {
        for (int field = 0; field < 5; field++) {
            reset(doctors);
            DomainError error = conflict(field);
            assertEquals(error, assertThrows(DomainException.class, () -> service.createDoctor(addition())).getError());
            verify(doctors, never()).save(any());
        }
    }

    @Test
    void changedFieldsRejectDuplicates() {
        for (int field = 0; field < 5; field++) {
            reset(doctors);
            Doctor d = existing();
            d.setCode("OLD");
            d.setRegistrationNumber("OLD");
            d.setMedicalLicenseNumber("OLD");
            d.setEmail("old@example.com");
            d.setPhone("OLD");
            DomainError error = conflict(field);
            assertEquals(error, assertThrows(DomainException.class, () -> service.updateDoctor(edit(), 7L)).getError());
            verify(doctors, never()).save(any());
        }
    }

    @Test
    void unchangedFieldsDoNotConflictWithTheSameDoctor() {
        Doctor d = existing();
        DoctorEditVM e = edit();
        e.setName("Renamed");
        e.setAddress("New address");
        service.updateDoctor(e, 7L);
        assertEquals("Renamed", d.getName());
        assertEquals("New address", d.getAddress());
        verify(doctors).findByIdAndDeletedAtIsNull(7L);
        verify(doctors).save(d);
        verifyNoMoreInteractions(doctors);
    }

    @Test
    void changedUniqueFieldsAndClearingOptionalFieldsArePersisted() {
        Doctor d = existing();
        DoctorEditVM e = edit();
        e.setCode("DOC2");
        e.setRegistrationNumber("REG2");
        e.setMedicalLicenseNumber("LIC2");
        e.setEmail("two@example.com");
        e.setPhone("082");
        service.updateDoctor(e, 7L);
        assertEquals("DOC2", d.getCode());
        assertEquals("REG2", d.getRegistrationNumber());
        assertEquals("LIC2", d.getMedicalLicenseNumber());
        assertEquals("082", d.getPhone());
        e.setRegistrationNumber(null);
        e.setMedicalLicenseNumber(null);
        e.setEmail(null);
        e.setPhone(null);
        service.updateDoctor(e, 7L);
        assertNull(d.getRegistrationNumber());
        assertNull(d.getMedicalLicenseNumber());
        assertNull(d.getEmail());
        assertNull(d.getPhone());
    }

    @Test
    void missingDoctorFailsBeforeWrite() {
        assertEquals(
                DomainError.DOCTOR_NOT_FOUND_BY_ID,
                assertThrows(DomainException.class, () -> service.updateDoctor(edit(), 7L)).getError());
        verify(doctors, never()).save(any());
    }

    @Test
    void searchesUseLocaleAndTrimKeywordAndMapCategory() {
        when(configuration.getConfiguration(ConfigurationConstants.LANGUAGE)).thenReturn("id");
        DoctorFilterVM f = new DoctorFilterVM();
        List<DoctorVM> rows = List.of(new DoctorVM());
        when(doctors.findByFilter(f, "id")).thenReturn(rows);
        when(doctors.findByKeyword("doc", "id")).thenReturn(rows);
        assertSame(rows, service.searchDoctorsByFilter(f));
        assertSame(rows, service.searchDoctorsByKeyword(" doc "));
        DoctorCategory c = new DoctorCategory();
        c.setId(3L);
        c.setCode("GP");
        c.setName("General");
        when(categories.findByKeyword("gen", "id")).thenReturn(List.of(c));
        assertEquals("GP", service.searchDoctorCategoryByKeyword("gen").get(0).getCode());
        when(categories.findByIdAndDeletedAtIsNull(3L)).thenReturn(Optional.of(c));
        assertEquals("General", service.getDoctorCategoryById(3L).getName());
        assertEquals(
                DomainError.DOCTOR_CATEGORY_NOT_FOUND_BY_ID,
                assertThrows(DomainException.class, () -> service.getDoctorCategoryById(9L)).getError());
        service.removeDoctors(List.of(7L));
        verify(doctors).deleteUpdateByIdIn(List.of(7L));
    }

    @Test
    void generatedCodeStartsAtZeroAndIncrementsLastSequence() {
        when(doctors.findFirstByCodeStartingWithOrderByCodeDesc(anyString())).thenReturn(Optional.empty());
        String first = service.getNextDoctorCode();
        assertTrue(first.endsWith("0000"));
        when(doctors.findFirstByCodeStartingWithOrderByCodeDesc(anyString())).thenAnswer(inv -> {
            Doctor d = new Doctor();
            d.setCode(inv.getArgument(0, String.class) + "0008");
            return Optional.of(d);
        });
        assertTrue(service.getNextDoctorCode().endsWith("0009"));
    }
}
