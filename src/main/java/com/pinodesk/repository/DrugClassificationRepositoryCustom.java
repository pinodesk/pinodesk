package com.pinodesk.repository;

import java.util.List;

import com.pinodesk.entity.DrugClassification;

public interface DrugClassificationRepositoryCustom {

    List<DrugClassification> findByKeyword(String keyword, String language);

}
