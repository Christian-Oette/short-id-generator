package com.christianoette;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Create an id which is unique within instance if you create a limited amount of ids per second.
 * <p>
 * Example: 3RYsrI001
 * UUID in comparison be810fec-5060-43f1-864a-8a87376c03ef
 */

public class ShortUUIDGenerator {

    private static final LocalDate DEFAULT_OFFSET_DATE = LocalDate.of(2022, Month.JANUARY, 1);
    public static final LocalDateTime DEFAULT_OFFSET_DATE_TIME = LocalDateTime.of(DEFAULT_OFFSET_DATE, LocalTime.MIDNIGHT);

    private final LocalDateTime offsetDateTime;
    private final AtomicInteger suffixOffset;
    private final ExpectedIdsPerSecond idsPerSecond;
    private final Base base;

    public ShortUUIDGenerator(LocalDateTime offsetDateTime, int offsetStartValue, ExpectedIdsPerSecond idsPerSecond, Base base) {
        this.suffixOffset = new AtomicInteger(offsetStartValue);
        this.idsPerSecond = idsPerSecond;
        this.base = base;
        this.offsetDateTime  =offsetDateTime;
    }

    public ShortUUIDGenerator() {
        this.suffixOffset = new AtomicInteger(0);
        this.idsPerSecond = ExpectedIdsPerSecond.HIGH;
        this.base = Base.BASE_62;
        this.offsetDateTime = DEFAULT_OFFSET_DATE_TIME;
    }


    public String generate(LocalDateTime dateTime) {
        return generate("", dateTime);
    }

    public String generate(String prefix, LocalDateTime dateTime) {

        if (dateTime.isBefore(offsetDateTime)) {
            var message = String.format("Given dateTime %s must not be before offset date time %s", dateTime, offsetDateTime);
            throw new IllegalArgumentException(message);
        }
        String suffix = base.format(suffixOffset.incrementAndGet() % base.getMaxValue(idsPerSecond.digits));
        String suffixWithPadding = padWithLeadingZeros(suffix, idsPerSecond.digits);

        long seconds = offsetDateTime.until(dateTime, ChronoUnit.SECONDS);
        return prefix + base.format(seconds) + suffixWithPadding;
    }

    public String padWithLeadingZeros(String input, int length) {
        return String.format("%" + length + "s", input)
                .replace(' ', '0');
    }

    public long getMaxIdsPerSecond() {
        return this.base.getMaxValue(this.idsPerSecond.digits);
    }

    public enum ExpectedIdsPerSecond {
        LOW( 2),
        HIGH( 3);

        private final int digits;

        ExpectedIdsPerSecond(int digits) {
            this.digits = digits;
        }
    }

    public enum Base {
        BASE_62(62) {
            @Override
            public String format(Long input) {
                BigInteger number = BigInteger.valueOf(input);
                StringBuilder result = new StringBuilder();
                while (number.compareTo(BigInteger.ZERO) > 0) {
                    BigInteger[] divmod = number.divideAndRemainder(BigInteger.valueOf(62));
                    number = divmod[0];
                    int digit = divmod[1].intValue();
                    result.insert(0, BASE62_DIGITS.charAt(digit));
                }
                return (result.length() == 0) ? BASE62_DIGITS.substring(0, 1) : result.toString();
            }
        },
        BASE_36(36) {
            @Override
            public String format(Long number) {
                return Long.toString(number, 36).toUpperCase();
            }
        };

        private static final String BASE62_DIGITS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        private final int baseNumber;

        Base(int baseNumber) {
            this.baseNumber = baseNumber;
        }

        public String format(Integer number) {
            return format(Long.valueOf(number));
        }

        Long getMaxValue(int digits) {
            return BigInteger
                    .valueOf(this.baseNumber)
                    .pow(digits)
                    .longValue();
        }

        public abstract String format(Long number);
    }
}
