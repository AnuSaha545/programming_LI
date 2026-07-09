package com.anu.runtime;

import com.anu.interpreter.Interpreter;

import java.util.List;

public class ClockFunction implements AnuCallable {

    @Override
    public int arity() {
        return 0;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        return System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "<native fn clock>";
    }
}