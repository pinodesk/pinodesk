package pinus.desktop.constant;

import com.gitlab.muhammadkholidb.pandora.utility.IPage;

public enum Page implements IPage {

    MAIN("main"),

    MASTER_PRODUCT_MAIN("master/product/main"),
    MASTER_PRODUCT_ADD("master/product/add"),
    MASTER_PRODUCT_EDIT("master/product/edit"),
    MASTER_PRODUCT_FILTER("master/product/filter"),
    MASTER_PRODUCT_CHOOSE("master/product/choose"),
    MASTER_PRODUCT_CHOOSE_CATEGORY("master/product/choose-category"),
    MASTER_PRODUCT_CHOOSE_UNIT("master/product/choose-unit"),
    MASTER_PRODUCT_CHOOSE_DRUG_CATEGORY("master/product/choose-drug-category"),
    MASTER_PRODUCT_IMPORT("master/product/import"),

    MASTER_CUSTOMER_MAIN("master/customer/main"),
    MASTER_CUSTOMER_FILTER("master/customer/filter"),
    MASTER_CUSTOMER_ADD("master/customer/add"),
    MASTER_CUSTOMER_EDIT("master/customer/edit"),
    MASTER_CUSTOMER_CHOOSE("master/customer/choose"),

    MASTER_SUPPLIER_MAIN("master/supplier/main"),
    MASTER_SUPPLIER_FILTER("master/supplier/filter"),
    MASTER_SUPPLIER_ADD("master/supplier/add"),
    MASTER_SUPPLIER_EDIT("master/supplier/edit"),
    MASTER_SUPPLIER_CHOOSE("master/supplier/choose"),

    MASTER_SUPPLIER_CONTACT_ADD("master/supplier/add-contact"),

    SETTINGS_CONFIGURATION_MAIN("settings/configuration/main"),

    TRANSACTION_PURCHASE_MAIN("transaction/purchase/main"),
    TRANSACTION_PURCHASE_FILTER("transaction/purchase/filter"),
    TRANSACTION_PURCHASE_ADD("transaction/purchase/add"),
    TRANSACTION_PURCHASE_EDIT("transaction/purchase/edit"),

    TRANSACTION_SALE_MAIN("transaction/sale/main"),
    TRANSACTION_SALE_FILTER("transaction/sale/filter"),
    TRANSACTION_SALE_ADD("transaction/sale/add"),
    TRANSACTION_SALE_EDIT("transaction/sale/edit"),

    MASTER_DOCTOR_ADD("master/doctor/add"),
    MASTER_DOCTOR_CHOOSE("master/doctor/choose"),
    MASTER_DOCTOR_CHOOSE_CATEGORY("master/doctor/choose-category"),

    TRANSACTION_PAYABLE_MAIN("transaction/payable/main"),

    TRANSACTION_RECEIVABLE_MAIN("transaction/receivable/main");

    private final String templateName;

    private Page(String templateName) {
        this.templateName = templateName;
    }

    public String templateName() {
        return this.templateName;
    }

}
