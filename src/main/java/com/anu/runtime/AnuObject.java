package com.anu.runtime;

import java.util.HashMap;
import java.util.Map;

public class AnuObject implements AnuInstance {

    private final AnuClass klass;

    private final Map<String, Object> fields =
            new HashMap<>();

    public AnuObject(AnuClass klass) {
        this.klass = klass;
    }

    @Override
    public Object get(String name) {

        if (fields.containsKey(name))
            return fields.get(name);

        AnuFunction method =
                klass.findMethod(name);

        if (method != null)
            return method;

        throw new RuntimeException(
                "Undefined property '" + name + "'.");
    }

    public void set(String name, Object value) {
        fields.put(name, value);
    }

    @Override
    public String toString() {
        return klass + " instance";
    }
}