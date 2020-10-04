package com.gitlab.muhammadkholidb.bianglala.repository;

import com.gitlab.muhammadkholidb.bianglala.entity.LanguageEntity;
import com.gitlab.muhammadkholidb.bianglala.entity.ProductEntity;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.lang3.StringUtils;

public class ProductRepositoryImpl implements ProductRepositoryCustom {
    
    @PersistenceContext
    private EntityManager em;

    @SuppressWarnings("unchecked")
    @Override
    public List<ProductEntity> filter(ProductFilter param, LanguageEntity language) {
        StringBuilder sb = new StringBuilder("SELECT p FROM ProductEntity p WHERE p.deletedAt IS NULL ");
        Map<String, Object> parameters = new HashMap<>();
        if (StringUtils.isNotBlank(param.getCode())) {
            sb.append(" AND LOWER(p.code) LIKE :productCode ");
            parameters.put("productCode", "%" + param.getCode() + "%");
        }
        if (StringUtils.isNotBlank(param.getName())) {
            sb.append(" AND LOWER(p.name) LIKE :productName ");
            parameters.put("productName", "%" + param.getName() + "%");
        }
        if (StringUtils.isNotBlank(param.getCategoryCode())) {
            sb.append(" AND p.category.code = :categoryCode "
                    + " AND p.category.language = :language "
                    + " AND p.category.deletedAt IS NULL");
            parameters.put("categoryCode", param.getCategoryCode());
            parameters.put("language", language);
        }
        Query query = em.createQuery(sb.toString());
        parameters.entrySet().forEach(entry -> {
            query.setParameter(entry.getKey(), entry.getValue());
        });
        return query.getResultList();
    }

}
