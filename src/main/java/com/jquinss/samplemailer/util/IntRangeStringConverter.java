package com.jquinss.samplemailer.util;

import javafx.util.StringConverter;

public class IntRangeStringConverter extends StringConverter<Integer> {

    private final int min;
    private final int max;

    private final String outputFormat;

    public IntRangeStringConverter(int min, int max) {
        this(min, max, "%d");
    }

    public IntRangeStringConverter(int min, int max, String outputFormat) {
        this.min = min;
        this.max = max;
        this.outputFormat = outputFormat;
    }

    @Override
    public Integer fromString(String string) {
        int integer = Integer.parseInt(string);
        if (integer > max)
            return max;
        if (integer < min)
            return min;
        return integer;
    }

    @Override
    public String toString(Integer integer) {
        return String.format(outputFormat, integer);
    }

}