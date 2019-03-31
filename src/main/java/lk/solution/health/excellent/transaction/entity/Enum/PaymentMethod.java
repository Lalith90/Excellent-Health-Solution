package lk.solution.health.excellent.transaction.entity.Enum;


public enum PaymentMethod {
    CASH("Cash"),
    CREDICARD("Card Payment or Cheque");

private final String paymentMethod;

    PaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}
