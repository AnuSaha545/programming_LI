package com.anu.runtime;

public class StringInstance implements AnuInstance {

    private final String value;

    public StringInstance(String value) {
        this.value = value;
    }

    @Override
    public Object get(String name) {
        return switch (name) {
            case "length" -> value.length();
            default -> throw new RuntimeException("Unknown property: " + name);
        };
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}