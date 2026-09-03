package com.pinodesk.constant;

public enum UserStatus {
    ACTIVE,
    INACTIVE;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}
