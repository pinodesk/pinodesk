package toska.desktop.constant;

import java.util.Optional;

public enum ContactType {
    CUSTOMER, SUPPLIER;

    public static Optional<ContactType> of(String name) {
        for (ContactType ct : values()) {
            if (ct.name().equals(name)) {
                return Optional.of(ct);
            }
        }
        return Optional.empty();
    }

}
