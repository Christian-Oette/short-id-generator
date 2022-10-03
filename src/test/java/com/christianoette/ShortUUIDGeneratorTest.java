package com.christianoette;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShortUUIDGeneratorTest {

    @Test
    public void testGenerate() {
        // given
        var generator = new ShortUUIDGenerator(
                ShortUUIDGenerator.DEFAULT_OFFSET_DATE_TIME,
                0,
                ShortUUIDGenerator.ExpectedIdsPerSecond.LOW,
                ShortUUIDGenerator.Base.BASE_62);
        var seconds = 100_000_000;
        var testDateTime = ShortUUIDGenerator.DEFAULT_OFFSET_DATE_TIME
                .plusSeconds(seconds);
        var expectedPrefix = "" + ShortUUIDGenerator.Base.BASE_62.format(seconds);

        // when
        var result = generator.generate( testDateTime);

        // the
        assertEquals(expectedPrefix + "01", result);
    }

    @Test
    public void test100yearsFutureDate() {
        // given
        var generator = new ShortUUIDGenerator(
                ShortUUIDGenerator.DEFAULT_OFFSET_DATE_TIME,
                0,
                ShortUUIDGenerator.ExpectedIdsPerSecond.HIGH,
                ShortUUIDGenerator.Base.BASE_62);
        var testDateTime = ShortUUIDGenerator.DEFAULT_OFFSET_DATE_TIME
                .plusYears(100);

        // when
        var result = generator.generate( testDateTime);

        // the
        assertEquals("3RYsrI001", result);
    }


    @Test
    public void testDateToShortUUIDWithPrefix() {
        // given
        var date = LocalDate.of(2022, Month.MAY, 15);
        var time = LocalTime.of(10, 45);
        var dateTime = LocalDateTime.of(date, time);
        var environment = "P";
        var podId = "10POD";
        var shortUUIDGenerator = new ShortUUIDGenerator();

        // when
        String result = shortUUIDGenerator.generate( environment + podId, dateTime);
        System.out.println(result);

        // then
        assertEquals("P10PODmjvg001", result);
    }

    @Test
    public void testToBase36() {
        assertEquals("B", ShortUUIDGenerator.Base.BASE_36.format(11));
        assertEquals("5", ShortUUIDGenerator.Base.BASE_36.format(5));
        assertEquals("Z", ShortUUIDGenerator.Base.BASE_36.format(35));
        assertEquals("10", ShortUUIDGenerator.Base.BASE_36.format(36));
        assertEquals("ZZ", ShortUUIDGenerator.Base.BASE_36.format(36 * 36 - 1));
    }

    @Test
    public void testToBase62() {
        assertEquals("B", ShortUUIDGenerator.Base.BASE_62.format(11));
        assertEquals("5", ShortUUIDGenerator.Base.BASE_62.format(5));
        assertEquals("Z", ShortUUIDGenerator.Base.BASE_62.format(35));
        assertEquals("a", ShortUUIDGenerator.Base.BASE_62.format(36));
        assertEquals("z", ShortUUIDGenerator.Base.BASE_62.format(61));
        assertEquals("10", ShortUUIDGenerator.Base.BASE_62.format(62));
        assertEquals("zz", ShortUUIDGenerator.Base.BASE_62.format(62 * 62 - 1));
    }

    @Test
    public void testPadding() {
        // given
        ShortUUIDGenerator shortUUIDGenerator = new ShortUUIDGenerator();

        // when / then
        assertEquals("0x", shortUUIDGenerator.padWithLeadingZeros("x", 2));
        assertEquals("xx", shortUUIDGenerator.padWithLeadingZeros("xx", 2));
    }

    @Test
    public void testMinId() {
        // given
        var generator = new ShortUUIDGenerator(
                ShortUUIDGenerator.DEFAULT_OFFSET_DATE_TIME,
                0,
                ShortUUIDGenerator.ExpectedIdsPerSecond.LOW,
                ShortUUIDGenerator.Base.BASE_62);
        var testDateTime = ShortUUIDGenerator.DEFAULT_OFFSET_DATE_TIME;

        // when
        var result = generator.generate(testDateTime);

        // then
        assertEquals("001", result);
    }


    @Test
    public void testMaxSuffix() {
        // given
        int startValueBeforeMaxOffset = 62 * 62 - 2;
        var shortUUIDGenerator = new ShortUUIDGenerator(
                ShortUUIDGenerator.DEFAULT_OFFSET_DATE_TIME,
                startValueBeforeMaxOffset,
                ShortUUIDGenerator.ExpectedIdsPerSecond.LOW,
                ShortUUIDGenerator.Base.BASE_62);

        // when
        String result = shortUUIDGenerator.generate(ShortUUIDGenerator.DEFAULT_OFFSET_DATE_TIME);

        assertEquals("0zz", result);
    }

    @Test
    public void testMaxSuffix3Digits() {
        // given
        int startValueBeforeMaxOffset = 62 * 62 * 62 - 2;
        var shortUUIDGenerator = new ShortUUIDGenerator(
                ShortUUIDGenerator.DEFAULT_OFFSET_DATE_TIME,
                startValueBeforeMaxOffset,
                ShortUUIDGenerator.ExpectedIdsPerSecond.HIGH,
                ShortUUIDGenerator.Base.BASE_62);

        // when
        String result = shortUUIDGenerator.generate(ShortUUIDGenerator.DEFAULT_OFFSET_DATE_TIME);

        assertEquals("0zzz", result);
    }

    @Test
    public void testMaxIdsPerSecond() {
        var shortUUIDGenerator = new ShortUUIDGenerator(
                ShortUUIDGenerator.DEFAULT_OFFSET_DATE_TIME,
                0,
                ShortUUIDGenerator.ExpectedIdsPerSecond.HIGH,
                ShortUUIDGenerator.Base.BASE_62);

        assertEquals(238328L, shortUUIDGenerator.getMaxIdsPerSecond());
    }

}