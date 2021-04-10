package com.getkembang.kembangdesktop.constant;

import com.gitlab.muhammadkholidb.dior.utility.IPage;

public enum Page implements IPage {
    
    MAIN("main"),

    MASTER_PRODUCT_MAIN("master/product/main"),
    MASTER_PRODUCT_ADD("master/product/add"),
    MASTER_PRODUCT_EDIT("master/product/edit"),
    MASTER_PRODUCT_FILTER("master/product/filter"),

    MASTER_CUSTOMER_MAIN("master/customer/main"), 
    MASTER_CUSTOMER_FILTER("master/customer/filter"), 
    MASTER_CUSTOMER_ADD("master/customer/add"), 
    MASTER_CUSTOMER_EDIT("master/customer/edit");
    
    private final String templateName;
    
    private Page(String templateName) {
        this.templateName = templateName;
    }
    
    public String templateName() {
        return this.templateName;
    }
    
}
