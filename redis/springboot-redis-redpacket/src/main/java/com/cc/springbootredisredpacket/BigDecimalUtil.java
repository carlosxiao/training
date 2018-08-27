package com.cc.springbootredisredpacket;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Created by CarlosXiao on 2018/7/3.
 */
public class BigDecimalUtil {

    static BigDecimal add(BigDecimal a, BigDecimal b) {
        return a.add(b, MathContext.DECIMAL128);
    }

    static BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return a.subtract(b, MathContext.DECIMAL128);
    }

    static BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return a.multiply(b, MathContext.DECIMAL128);
    }

    static BigDecimal divide(BigDecimal a, BigDecimal b) {
        return a.divide(b, MathContext.DECIMAL128);
    }
}
