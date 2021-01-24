package com.gitlab.muhammadkholidb.bianglala.constant;

public enum DomainError {
    
    // Product
    PRODUCT_NOT_FOUND_BY_ID("E10001", MessageCode.ERROR_PRODUCT_NOT_FOUND_BY_ID),
    PRODUCT_EXISTS_BY_CODE("E10002", MessageCode.ERROR_PRODUCT_EXISTS_BY_CODE),
    PRODUCT_EXISTS_BY_BARCODE("E10003", MessageCode.ERROR_PRODUCT_EXISTS_BY_CODE),

    // Product category
    PRODUCT_CATEGORY_NOT_FOUND_BY_ID("E20001", MessageCode.ERROR_PRODUCT_CATEGORY_NOT_FOUND_BY_ID),
    
    // Unit
    UNIT_NOT_FOUND_BY_ID("E30001", MessageCode.ERROR_UNIT_NOT_FOUND_BY_ID),
    
    // Rack
    RACK_NOT_FOUND_BY_ID("E40001", MessageCode.ERROR_RACK_NOT_FOUND_BY_ID),
    
    // Drug category
    DRUG_CATEGORY_NOT_FOUND_BY_ID("E50001", MessageCode.ERROR_DRUG_CATEGORY_NOT_FOUND_BY_ID);

    private final String code;
    private final MessageCode messageCode;

    private DomainError(String code, MessageCode messageCode) {
        this.code = code;
        this.messageCode = messageCode;
    }

    public String code() {
        return this.code;
    }

    public MessageCode messageCode() {
        return this.messageCode;
    }

}
