package com.anu.runtime;

import java.util.List;

public class ArrayInstance implements AnuInstance {

    private final List<Object> values;

    public ArrayInstance(List<Object> values) {
        this.values = values;
    }

    @Override
    public Object get(String name) {
        switch (name) {
            case "size":
            case "add":
            case "remove":
                return new ArrayMethod(this, name);

            default:
                throw new RuntimeException("Unknown property: " + name);
        }
    }

    public List<Object> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return values.toString();
    }
}