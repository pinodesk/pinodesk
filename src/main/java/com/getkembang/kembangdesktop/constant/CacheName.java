package com.getkembang.kembangdesktop.constant;

import java.util.Arrays;

public enum CacheName {

    // @formatter:off
    CONFIGURATION_BY_CODE(Keys.CONFIGURATION_BY_CODE),
    PRODUCTS_BY_FILTER(Keys.PRODUCTS_BY_FILTER), 
    PRODUCT_CATEGORIES_BY_KEYWORD(Keys.PRODUCT_CATEGORIES_BY_KEYWORD), 
    RACKS_ALL(Keys.RACKS_ALL),
    RACKS_BY_KEYWORD(Keys.RACKS_BY_KEYWORD), 
    UNITS_ALL(Keys.UNITS_ALL),
    UNITS_BY_KEYWORD(Keys.UNITS_BY_KEYWORD),
    DRUG_CATEGORIES_BY_KEYWORD(Keys.DRUG_CATEGORIES_BY_KEYWORD),
    CUSTOMERS_BY_FILTER(Keys.CUSTOMERS_BY_FILTER);
    // @formatter:on

    private String key;

    private CacheName(String key) {
        this.key = key;
    }

    public String key() {
        return this.key;
    }

    public static String[] keys() {
        return Arrays.stream(values()).map(CacheName::key).toArray(String[]::new);
    }

    public static interface Keys {
        String CONFIGURATION_BY_CODE = "configurationByCode";
        String PRODUCTS_BY_FILTER = "productsByFilter";
        String PRODUCT_CATEGORIES_BY_KEYWORD = "productCategoriesByKeyword";
        String RACKS_ALL = "racksAll";
        String RACKS_BY_KEYWORD = "racksByKeyword";
        String UNITS_ALL = "unitsAll";
        String UNITS_BY_KEYWORD = "unitsByKeyword";
        String DRUG_CATEGORIES_BY_KEYWORD = "drugCategoriesByKeyword";
        String CUSTOMERS_BY_FILTER = "customersByFilter";
    }

}
