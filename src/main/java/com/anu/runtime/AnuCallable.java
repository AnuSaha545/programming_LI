package com.anu.runtime;

import com.anu.interpreter.Interpreter;
import java.util.List;

public interface AnuCallable {

    int arity();

    Object call(Interpreter interpreter, List<Object> arguments);
}