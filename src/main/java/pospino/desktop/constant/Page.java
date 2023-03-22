package pospino.desktop.constant;

import com.gitlab.muhammadkholidb.pandora.utility.IPage;

public enum Page implements IPage {

    MAIN("main"),
    SPLASH("splash"),
    LOADING("loading"),
    LOGIN("login"),
    INITIAL_SETUP("initial-setup"),

    CATALOG_PRODUCT_MAIN("catalog/product/main"),
    CATALOG_PRODUCT_ADD("catalog/product/add"),
    CATALOG_PRODUCT_EDIT("catalog/product/edit"),
    CATALOG_PRODUCT_FILTER("catalog/product/filter"),
    CATALOG_PRODUCT_CHOOSE("catalog/product/choose"),
    CATALOG_PRODUCT_CHOOSE_CATEGORY("catalog/product/choose-category"),
    CATALOG_PRODUCT_CHOOSE_UNIT("catalog/product/choose-unit"),
    CATALOG_PRODUCT_CHOOSE_DRUG_CATEGORY("catalog/product/choose-drug-category"),
    CATALOG_PRODUCT_IMPORT("catalog/product/import"),
    CATALOG_PRODUCT_CHOOSE_DRUG_CLASSIFICATION("catalog/product/choose-drug-classification"),
    CATALOG_PRODUCT_ADD_PACKAGE("catalog/product/add-package"),
    CATALOG_PRODUCT_EDIT_PACKAGE("catalog/product/edit-package"),

    CATALOG_CUSTOMER_MAIN("catalog/customer/main"),
    CATALOG_CUSTOMER_FILTER("catalog/customer/filter"),
    CATALOG_CUSTOMER_ADD("catalog/customer/add"),
    CATALOG_CUSTOMER_EDIT("catalog/customer/edit"),
    CATALOG_CUSTOMER_CHOOSE("catalog/customer/choose"),

    CATALOG_SUPPLIER_MAIN("catalog/supplier/main"),
    CATALOG_SUPPLIER_FILTER("catalog/supplier/filter"),
    CATALOG_SUPPLIER_ADD("catalog/supplier/add"),
    CATALOG_SUPPLIER_EDIT("catalog/supplier/edit"),
    CATALOG_SUPPLIER_CHOOSE("catalog/supplier/choose"),

    CATALOG_SUPPLIER_CONTACT_ADD("catalog/supplier/add-contact"),

    SETTINGS_CONFIGURATION_MAIN("settings/configuration/main"),

    SETTINGS_USER_GROUP_MAIN("settings/user-group/main"),
    SETTINGS_USER_GROUP_FILTER("settings/user-group/filter"),
    SETTINGS_USER_GROUP_ADD("settings/user-group/add"),
    SETTINGS_USER_GROUP_EDIT("settings/user-group/edit"),
    SETTINGS_USER_GROUP_CHOOSE("settings/user-group/choose"),

    SETTINGS_USER_MAIN("settings/user/main"),
    SETTINGS_USER_ADD("settings/user/add"),
    SETTINGS_USER_EDIT("settings/user/edit"),
    SETTINGS_USER_FILTER("settings/user/filter"),

    TRANSACTION_PURCHASE_MAIN("transaction/purchase/main"),
    TRANSACTION_PURCHASE_FILTER("transaction/purchase/filter"),
    TRANSACTION_PURCHASE_ADD("transaction/purchase/add"),
    TRANSACTION_PURCHASE_EDIT("transaction/purchase/edit"),

    TRANSACTION_SALE_MAIN("transaction/sale/main"),
    TRANSACTION_SALE_FILTER("transaction/sale/filter"),
    TRANSACTION_SALE_ADD("transaction/sale/add"),
    TRANSACTION_SALE_EDIT("transaction/sale/edit"),
    TRANSACTION_SALE_CASHIER_MAIN("transaction/sale/cashier/main"),
    TRANSACTION_SALE_CASHIER_PAY("transaction/sale/cashier/pay"),
    TRANSACTION_SALE_CASHIER_SALE_COMPLETE("transaction/sale/cashier/sale-complete"),
    TRANSACTION_SALE_CASHIER_CONFIRM_PRODUCT("transaction/sale/cashier/confirm-product"),

    CATALOG_DOCTOR_MAIN("catalog/doctor/main"),
    CATALOG_DOCTOR_FILTER("catalog/doctor/filter"),
    CATALOG_DOCTOR_ADD("catalog/doctor/add"),
    CATALOG_DOCTOR_EDIT("catalog/doctor/edit"),
    CATALOG_DOCTOR_CHOOSE("catalog/doctor/choose"),
    CATALOG_DOCTOR_CHOOSE_CATEGORY("catalog/doctor/choose-category"),

    TRANSACTION_PAYABLE_MAIN("transaction/payable/main"),
    TRANSACTION_PAYABLE_FILTER("transaction/payable/filter"),
    TRANSACTION_PAYABLE_EDIT("transaction/payable/edit"),

    TRANSACTION_RECEIVABLE_MAIN("transaction/receivable/main"),
    TRANSACTION_RECEIVABLE_FILTER("transaction/receivable/filter"),
    TRANSACTION_RECEIVABLE_EDIT("transaction/receivable/edit");

    private final String templateName;

    private Page(String templateName) {
        this.templateName = templateName;
    }

    public String templateName() {
        return this.templateName;
    }

}
