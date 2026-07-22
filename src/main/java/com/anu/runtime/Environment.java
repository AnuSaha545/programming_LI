package com.anu.runtime;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    private final Environment enclosing;
    private final Map<String, Object> variables = new HashMap<>();

    public Environment() {
        this.enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public void define(String name, Object value) {
        variables.put(name, value);
    }

    public Object get(String name) {

        if (variables.containsKey(name)) {
            return variables.get(name);
        }

        if (enclosing != null) {
            return enclosing.get(name);
        }

        throw new RuntimeException(
                "\nRuntime Error\n" +
                        "-------------------------\n" +
                        "Undefined variable: " + name
        );
    }

    public void assign(String name, Object value) {

        if (variables.containsKey(name)) {
            variables.put(name, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeException(
                "\nRuntime Error\n" +
                        "-------------------------\n" +
                        "Undefined variable: " + name
        );
    }

    public Environment getEnclosing() {
        return enclosing;
    }

    private Environment ancestor(int distance) {

        Environment environment = this;

        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }

        return environment;
    }

    public Object getAt(int distance, String name) {
        return ancestor(distance).variables.get(name);
    }

    public void assignAt(int distance, String name, Object value) {
        ancestor(distance).variables.put(name, value);
    }
}