package com.anu.ast;

import com.anu.token.Token;

public class VariableStatement extends Statement {

    private final Token name;
    private final Expression initializer;

    public VariableStatement(Token name, Expression initializer) {
        this.name = name;
        this.initializer = initializer;
    }

    public Token getName() {
        return name;
    }

    public Expression getInitializer() {
        return initializer;
    }

    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visitVariableStatement(this);
    }
}