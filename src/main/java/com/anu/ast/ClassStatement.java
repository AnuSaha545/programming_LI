package com.anu.ast;

import com.anu.token.Token;
import java.util.List;

public class ClassStatement extends Statement {

    private final Token name;
    private final VariableExpression superclass;
    private final List<FunctionStatement> methods;

    public ClassStatement(
            Token name,
            VariableExpression superclass,
            List<FunctionStatement> methods) {

        this.name = name;
        this.superclass = superclass;
        this.methods = methods;
    }

    public Token getName() {
        return name;
    }

    public VariableExpression getSuperclass() {
        return superclass;
    }

    public List<FunctionStatement> getMethods() {
        return methods;
    }

    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visitClassStatement(this);
    }
}