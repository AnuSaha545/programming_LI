package com.anu.ast;

import com.anu.token.Token;

public class GetExpression extends Expression {

    private final Expression object;
    private final Token name;

    public GetExpression(Expression object, Token name) {
        this.object = object;
        this.name = name;
    }

    public Expression getObject() {
        return object;
    }

    public Token getName() {
        return name;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitGetExpression(this);
    }
}