package stoready.desktop.constant;

public enum UserGroupStatus {
    ACTIVE,
    INACTIVE;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}
