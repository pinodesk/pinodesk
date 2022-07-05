package pinus.desktop.constant;

public enum ProductStatus {
    ACTIVE,
    INACTIVE;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}
