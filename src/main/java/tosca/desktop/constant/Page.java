package tosca.desktop.constant;

import com.gitlab.muhammadkholidb.pandora.utility.IPage;

public enum Page implements IPage {
    
    MAIN("main"),

    MASTER_PRODUCT_MAIN("master/product/main"),
    MASTER_PRODUCT_ADD("master/product/add"),
    MASTER_PRODUCT_EDIT("master/product/edit"),
    MASTER_PRODUCT_FILTER("master/product/filter"),

    MASTER_CUSTOMER_MAIN("master/customer/main"), 
    MASTER_CUSTOMER_FILTER("master/customer/filter"), 
    MASTER_CUSTOMER_ADD("master/customer/add"), 
    MASTER_CUSTOMER_EDIT("master/customer/edit"),
    
    MASTER_SUPPLIER_MAIN("master/supplier/main"), 
    MASTER_SUPPLIER_FILTER("master/supplier/filter"), 
    MASTER_SUPPLIER_ADD("master/supplier/add"), 
    MASTER_SUPPLIER_EDIT("master/supplier/edit"),

    MASTER_SUPPLIER_CONTACT_ADD("master/supplier/add-contact"),
    
    SETTINGS_CONFIGURATION_MAIN("settings/configuration/main"),
    
    TRANSACTION_PURCHASE_MAIN("transaction/purchase/main"), 
    TRANSACTION_PURCHASE_FILTER("transaction/purchase/filter"), 
    TRANSACTION_PURCHASE_ADD("transaction/purchase/add"), 
    TRANSACTION_PURCHASE_EDIT("transaction/purchase/edit"),
    ;
    
    private final String templateName;
    
    private Page(String templateName) {
        this.templateName = templateName;
    }
    
    public String templateName() {
        return this.templateName;
    }
    
}
