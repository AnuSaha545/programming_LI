package com.anu.runtime;

import java.util.List;
import java.util.Map;
import com.anu.interpreter.Interpreter;

public class AnuClass implements AnuCallable {

    private final String name;
    private final AnuClass superclass;
    private final Map<String, AnuFunction> methods;

    public AnuClass(
            String name,
            AnuClass superclass,
            Map<String, AnuFunction> methods) {

        this.name = name;
        this.superclass = superclass;
        this.methods = methods;
    }

    public String getName() {
        return name;
    }
    public AnuClass getSuperclass() {
        return superclass;
    }

    public AnuFunction findMethod(String name) {

        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        if (superclass != null) {
            return superclass.findMethod(name);
        }

        return null;
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
            initializer.bind(instance, superclass)
                    .call(interpreter, arguments);
        }

        return instance;
    }

    @Override
    public String toString() {
        return "<class " + name + ">";
    }
}