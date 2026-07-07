package com.anu.runtime;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    private final Map<String, Object> variables = new HashMap<>();

    public void define(String name, Object value) {
        variables.put(name, value);
    }

    public Object get(String name) {

        if (!variables.containsKey(name)) {
            throw new RuntimeException("Undefined variable: " + name);
        }

        return variables.get(name);
    }

    public void assign(String name, Object value) {

        if (!variables.containsKey(name)) {
            throw new RuntimeException("Undefined variable: " + name);
        }

        variables.put(name, value);
    }
}