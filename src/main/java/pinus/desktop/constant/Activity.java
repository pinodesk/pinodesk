package pinus.desktop.constant;

public enum Activity {
    ADD_PRODUCT,
    EDIT_PRODUCT,
    DELETE_PRODUCT,
    IMPORT_PRODUCT,
    ADD_PURCHASE,
    EDIT_PURCHASE,
    DELETE_PURCHASE,
    ADD_SALE,
    EDIT_SALE,
    DELETE_SALE,
    ADD_CUSTOMER,
    EDIT_CUSTOMER,
    DELETE_CUSTOMER,
    ADD_SUPPLIER,
    EDIT_SUPPLIER,
    DELETE_SUPPLIER;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}
