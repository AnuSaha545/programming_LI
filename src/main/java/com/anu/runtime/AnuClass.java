package com.anu.runtime;

import java.util.List;
import java.util.Map;
import com.anu.interpreter.Interpreter;

public class AnuClass implements AnuCallable {

    private final String name;
    private final Map<String, AnuFunction> methods;

    public AnuClass(String name,
                    Map<String, AnuFunction> methods) {
        this.name = name;
        this.methods = methods;
    }

    public String getName() {
        return name;
    }

    public AnuFunction findMethod(String name) {
        return methods.get(name);
    }

    @Override
    public int arity() {

        AnuFunction initializer = findMethod("init");

        if (initializer == null) {
            return 0;
        }

        return initializer.arity();
    }

    @Override
    public Object call(Interpreter interpreter,
                       List<Object> arguments) {

        AnuObject instance = new AnuObject(this);

        AnuFunction initializer = findMethod("init");

        if (initializer != null) {
            initializer.bind(instance)
                    .call(interpreter, arguments);
        }

        return instance;
    }

    @Override
    public String toString() {
        return "<class " + name + ">";
    }
}