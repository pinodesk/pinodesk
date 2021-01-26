package com.getkembang.kembangdesktop.constant;

public enum Page {
    
    MAIN("Main"),
    MASTER_PRODUCT_MAIN("master/product/ProductMain"),
    MASTER_PRODUCT_ADD("master/product/ProductAdd"),
    MASTER_PRODUCT_EDIT("master/product/ProductEdit"),
    MASTER_PRODUCT_FILTER("master/product/ProductFilter"),
    MASTER_SUPPLIER_MAIN("master/Suppliers"),
    MASTER_CUSTOMER_MAIN("master/Customers");
    
    private final String templateName;
    
    private Page(String templateName) {
        this.templateName = templateName;
    }
    
    public String templateName() {
        return this.templateName;
    }
    
}
