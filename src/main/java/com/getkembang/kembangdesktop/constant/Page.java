package com.getkembang.kembangdesktop.constant;

public enum Page {
    
    MAIN("main"),
    MASTER_PRODUCT_MAIN("master/product/main"),
    MASTER_PRODUCT_ADD("master/product/add"),
    MASTER_PRODUCT_EDIT("master/product/edit"),
    MASTER_PRODUCT_FILTER("master/product/filter"),
    MASTER_CONTACT_MAIN("master/contact/main"), 
    MASTER_CONTACT_FILTER("master/contact/filter"), 
    MASTER_CONTACT_ADD("master/contact/add"), 
    MASTER_CONTACT_EDIT("master/contact/edit");
    
    private final String templateName;
    
    private Page(String templateName) {
        this.templateName = templateName;
    }
    
    public String templateName() {
        return this.templateName;
    }
    
}
