package com.anu.runtime;

import com.anu.interpreter.Interpreter;

import java.util.List;

public class ArrayMethod implements AnuCallable {

    private final ArrayInstance array;
    private final String name;

    public ArrayMethod(ArrayInstance array, String name) {
        this.array = array;
        this.name = name;
    }

    @Override
    public int arity() {
        return switch (name) {
            case "size" -> 0;
            case "add" -> 1;
            case "remove" -> 1;
            default -> throw new RuntimeException("Unknown method: " + name);
        };
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {

        List<Object> values = array.getValues();

        switch (name) {

            case "size":
                return values.size();

            case "add":
                values.add(arguments.get(0));
                return null;

            case "remove": {
                Object index = arguments.get(0);

                if (!(index instanceof Integer))
                    throw new RuntimeException("Index must be an integer.");

                int i = (Integer) index;

                if (i < 0 || i >= values.size())
                    throw new RuntimeException("Index out of bounds.");

                return values.remove(i);
            }

            default:
                throw new RuntimeException("Unknown method: " + name);
        }
    }
}