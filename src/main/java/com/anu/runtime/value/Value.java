package com.anu.runtime.value;

public class Value {

    private final ValueType type;
    private final Object value;

    private Value(ValueType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public static Value of(Integer value) {
        return new Value(ValueType.INTEGER, value);
    }

    public static Value of(Double value) {
        return new Value(ValueType.DOUBLE, value);
    }

    public static Value of(Boolean value) {
        return new Value(ValueType.BOOLEAN, value);
    }

    public static Value of(String value) {
        return new Value(ValueType.STRING, value);
    }

    public static Value nullValue() {
        return new Value(ValueType.NULL, null);
    }

    public ValueType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public int asInt() {
        return (Integer) value;
    }

    public double asDouble() {
        return (Double) value;
    }

    public boolean asBoolean() {
        return (Boolean) value;
    }

    public String asString() {
        return (String) value;
    }

    @Override
    public String toString() {
        return value == null ? "null" : value.toString();
    }
}