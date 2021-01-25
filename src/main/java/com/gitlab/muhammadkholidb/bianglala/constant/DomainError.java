package com.gitlab.muhammadkholidb.bianglala.constant;

public enum DomainError {
    
    // Product
    PRODUCT_NOT_FOUND_BY_ID("DE1-0001", MessageCode.ERROR_PRODUCT_NOT_FOUND_BY_ID),
    PRODUCT_EXISTS_BY_CODE("DE1-0002", MessageCode.ERROR_PRODUCT_EXISTS_BY_CODE),
    PRODUCT_EXISTS_BY_BARCODE("DE1-0003", MessageCode.ERROR_PRODUCT_EXISTS_BY_BARCODE),
    PRODUCT_OTHER_EXISTS_BY_CODE("DE1-0004", MessageCode.ERROR_PRODUCT_EXISTS_BY_CODE),
    PRODUCT_OTHER_EXISTS_BY_BARCODE("DE1-0005", MessageCode.ERROR_PRODUCT_EXISTS_BY_BARCODE),

    // Product category
    PRODUCT_CATEGORY_NOT_FOUND_BY_ID("DE2-0001", MessageCode.ERROR_PRODUCT_CATEGORY_NOT_FOUND_BY_ID),
    
    // Unit
    UNIT_NOT_FOUND_BY_ID("DE3-0001", MessageCode.ERROR_UNIT_NOT_FOUND_BY_ID),
    
    // Rack
    RACK_NOT_FOUND_BY_ID("DE4-0001", MessageCode.ERROR_RACK_NOT_FOUND_BY_ID),
    
    // Drug category
    DRUG_CATEGORY_NOT_FOUND_BY_ID("DE5-0001", MessageCode.ERROR_DRUG_CATEGORY_NOT_FOUND_BY_ID);

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
