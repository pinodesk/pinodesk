package pinodesk.constant;

public enum PaymentStatus {
    PAID,
    UNPAID;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}
