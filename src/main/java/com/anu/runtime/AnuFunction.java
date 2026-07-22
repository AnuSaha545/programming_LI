package com.anu.runtime;

import com.anu.ast.FunctionStatement;
import com.anu.interpreter.Interpreter;

import java.util.List;


public class AnuFunction implements AnuCallable {

    private final FunctionStatement declaration;
    private final AnuObject instance;
    private final AnuClass superClass;

    public AnuFunction(FunctionStatement declaration) {
        this(declaration, null, null);
    }

    public AnuFunction(
            FunctionStatement declaration,
            AnuObject instance,
            AnuClass superClass) {

        this.declaration = declaration;
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

        // Environment that stores "this" and "super"
        Environment methodEnvironment = new Environment(previous);

        if (instance != null) {
            methodEnvironment.define("this", instance);
        }

        if (superClass != null) {
            methodEnvironment.define("super", superClass);
        }

        // Environment for parameters
        Environment local = new Environment(methodEnvironment);

        for (int i = 0; i < declaration.getParameters().size(); i++) {
            local.define(
                    declaration.getParameters().get(i).getLexeme(),
                    arguments.get(i)
            );
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
    public AnuFunction bind(AnuObject instance, AnuClass superClass) {
        return new AnuFunction(
                declaration,
                instance,
                superClass
        );
    }
}