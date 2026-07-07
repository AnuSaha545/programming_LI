package com.anu.ast;

import com.anu.token.Token;

public class VariableExpression extends Expression {

    private final Token name;

    public VariableExpression(Token name) {
        this.name = name;
    }

    public Token getName() {
        return name;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitVariableExpression(this);
    }
}