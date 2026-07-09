package com.anu.runtime;

import com.anu.ast.FunctionStatement;
import com.anu.interpreter.Interpreter;

import java.util.List;
import com.anu.runtime.Environment;
import com.anu.runtime.Return;

public class AnuFunction implements AnuCallable {

    private final FunctionStatement declaration;

    public AnuFunction(FunctionStatement declaration) {
        this.declaration = declaration;
    }

    @Override
    public int arity() {
        return declaration.getParameters().size();
    }
    @Override
    public String toString() {
        return "<fn " + declaration.getName().getLexeme() + ">";
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {

        Environment previous = interpreter.getEnvironment();
        Environment local = new Environment(previous);

        for (int i = 0; i < declaration.getParameters().size(); i++) {
            local.define(
                    declaration.getParameters().get(i).getLexeme(),
                    arguments.get(i));
        }

        interpreter.setEnvironment(local);

        try {
            interpreter.executeStatements(declaration.getBody());
        } catch (Return returnValue) {
            interpreter.setEnvironment(previous);
            return returnValue.getValue();
        }

        interpreter.setEnvironment(previous);

        return null;
    }
}