package com.pinodesk.entity;

import java.math.BigDecimal;

import com.pinodesk.sequel.model.DataModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProductPrice extends DataModel {

    public static final String C_PRODUCT_ID = "product_id";
    public static final String C_GENERAL_SELLING_PRICE = "general_selling_price";
    public static final String C_PRESCRIPTION_SELLING_PRICE = "prescription_selling_price";
    public static final String C_PURCHASE_ID = "purchase_id";
    public static final String C_PURCHASE_INVOICE_NUMBER = "purchase_invoice_number";
    public static final String C_SALE_ID = "sale_id";
    public static final String C_SALE_INVOICE_NUMBER = "sale_invoice_number";
    public static final String C_CONSIGNMENT_ID = "consignment_id";
    public static final String C_CONSIGNMENT_INVOICE_NUMBER = "consignment_invoice_number";
    public static final String C_USER_ID = "user_id";
    public static final String C_ACTIVITY = "activity";
    public static final String C_REMARKS = "remarks";

    private Long productId;
    private BigDecimal generalSellingPrice;
    private BigDecimal prescriptionSellingPrice;
    private Long purchaseId;
    private String purchaseInvoiceNumber;
    private Long saleId;
    private String saleInvoiceNumber;
    private Long consignmentId;
    private String consignmentInvoiceNumber;
    private Long userId;
    private String activity;
    private String remarks;
}
