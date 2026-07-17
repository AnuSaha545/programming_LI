package com.anu.runtime;

import com.anu.interpreter.Interpreter;

import java.util.List;
import java.util.Map;

public class AnuClass implements AnuCallable {

    private final String name;
    private final Map<String, AnuFunction> methods;

    public AnuClass(String name,
                    Map<String, AnuFunction> methods) {

        this.name = name;
        this.methods = methods;
    }

    public AnuFunction findMethod(String name) {
        return methods.get(name);
    }

    @Override
    public int arity() {
        return 0;
    }

    @Override
    public Object call(Interpreter interpreter,
                       List<Object> arguments) {

        return new AnuObject(this);
    }

    @Override
    public String toString() {
        return name;
    }
}