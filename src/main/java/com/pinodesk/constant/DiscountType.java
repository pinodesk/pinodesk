package com.pinodesk.constant;

public enum DiscountType {
    PERCENTAGE,
    FIXED_AMOUNT;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}
