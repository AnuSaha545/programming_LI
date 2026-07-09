package com.anu.runtime;

import com.anu.interpreter.Interpreter;

import java.util.List;

public class LenFunction implements AnuCallable {

    @Override
    public int arity() {
        return 1;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {

        Object value = arguments.get(0);

        if (value instanceof String) {
            return ((String) value).length();
        }

        throw new RuntimeException("len() expects a string.");
    }

    @Override
    public String toString() {
        return "<native fn len>";
    }
}