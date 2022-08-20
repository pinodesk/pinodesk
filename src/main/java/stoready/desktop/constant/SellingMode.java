package stoready.desktop.constant;

public enum SellingMode {
    PRESCRIPTION,
    GENERAL;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}
