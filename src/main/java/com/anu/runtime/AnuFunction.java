package com.anu.runtime;

import com.anu.ast.FunctionStatement;
import com.anu.interpreter.Interpreter;

import java.util.List;

public class AnuFunction implements AnuCallable {

    private final FunctionStatement declaration;
    private final Environment closure;
    private final AnuObject instance;
    private final AnuClass superClass;

    public AnuFunction(FunctionStatement declaration, Environment closure) {
        this(declaration, closure, null, null);
    }

    public AnuFunction(
            FunctionStatement declaration,
            Environment closure,
            AnuObject instance,
            AnuClass superClass) {

        this.declaration = declaration;
        this.closure = closure;
        this.instance = instance;
        this.superClass = superClass;
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

        Environment environment;

        // Ordinary function
        if (instance == null && superClass == null) {
            environment = new Environment(closure);
        }
        // Bound method
        else {
            Environment methodEnvironment = new Environment(closure);

            if (instance != null) {
                methodEnvironment.define("this", instance);
            }

            if (superClass != null) {
                methodEnvironment.define("super", superClass);
            }

            environment = new Environment(methodEnvironment);
        }

        for (int i = 0; i < declaration.getParameters().size(); i++) {
            environment.define(
                    declaration.getParameters().get(i).getLexeme(),
                    arguments.get(i)
            );
        }

        interpreter.setEnvironment(environment);

        try {
            interpreter.executeStatements(declaration.getBody());
        } catch (Return returnValue) {
            interpreter.setEnvironment(previous);
            return returnValue.getValue();
        }

        interpreter.setEnvironment(previous);
        return null;
    }

    public AnuFunction bind(AnuObject instance, AnuClass superClass) {
        return new AnuFunction(
                declaration,
                closure,
                instance,
                superClass
        );
    }
}