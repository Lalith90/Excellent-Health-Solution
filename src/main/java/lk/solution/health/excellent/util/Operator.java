package lk.solution.health.excellent.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Operator {

    public static BigDecimal mul(BigDecimal operand1, BigDecimal operand2) {
        return operand1.multiply(operand2).setScale(2, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal div(BigDecimal dividend, BigDecimal divisor) {
        return dividend.divide(divisor, 2, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal add(BigDecimal operand1, BigDecimal operand2) {
        return operand1.add(operand2).setScale(2, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal sub(BigDecimal operand1, BigDecimal operand2) {
        return operand1.subtract(operand2).setScale(2, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal pow(BigDecimal operand1, int operand2) {
        return operand1.pow(operand2).setScale(2, RoundingMode.HALF_EVEN);
    }
}