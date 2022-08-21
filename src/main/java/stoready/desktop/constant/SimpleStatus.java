package stoready.desktop.constant;

public enum SimpleStatus {
    YES,
    NO;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}
