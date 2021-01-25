package com.gitlab.muhammadkholidb.bianglala.constant;

/**
 * Errors related with domain at repository or service level. The code for this
 * error should follow this format: "DE" + domain category number (01-99) +
 * error number (001-999).
 */
public enum DomainError {

    // Product (01)
    PRODUCT_NOT_FOUND_BY_ID("DE01001", MessageCode.ERROR_PRODUCT_NOT_FOUND_BY_ID),
    PRODUCT_EXISTS_BY_CODE("DE01002", MessageCode.ERROR_PRODUCT_EXISTS_BY_CODE),
    PRODUCT_EXISTS_BY_BARCODE("DE01003", MessageCode.ERROR_PRODUCT_EXISTS_BY_BARCODE),
    PRODUCT_OTHER_EXISTS_BY_CODE("DE01004", MessageCode.ERROR_PRODUCT_EXISTS_BY_CODE),
    PRODUCT_OTHER_EXISTS_BY_BARCODE("DE01005", MessageCode.ERROR_PRODUCT_EXISTS_BY_BARCODE),

    // Product category (02)
    PRODUCT_CATEGORY_NOT_FOUND_BY_ID("DE02001", MessageCode.ERROR_PRODUCT_CATEGORY_NOT_FOUND_BY_ID),

    // Unit (03)
    UNIT_NOT_FOUND_BY_ID("DE03001", MessageCode.ERROR_UNIT_NOT_FOUND_BY_ID),

    // Rack (04)
    RACK_NOT_FOUND_BY_ID("DE04001", MessageCode.ERROR_RACK_NOT_FOUND_BY_ID),

    // Drug category (05)
    DRUG_CATEGORY_NOT_FOUND_BY_ID("DE05001", MessageCode.ERROR_DRUG_CATEGORY_NOT_FOUND_BY_ID);

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
