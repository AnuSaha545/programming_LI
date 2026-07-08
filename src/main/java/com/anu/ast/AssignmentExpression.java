package com.anu.ast;

import com.anu.token.Token;

public class AssignmentExpression extends Expression {

    private final Token name;
    private final Expression value;

    public AssignmentExpression(Token name, Expression value) {
        this.name = name;
        this.value = value;
    }

    public Token getName() {
        return name;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitAssignmentExpression(this);
    }
}