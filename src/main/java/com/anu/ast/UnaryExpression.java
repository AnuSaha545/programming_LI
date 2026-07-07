package com.anu.ast;

import com.anu.token.Token;

public class UnaryExpression extends Expression {

    private final Token operator;
    private final Expression right;

    public UnaryExpression(Token operator, Expression right) {
        this.operator = operator;
        this.right = right;
    }

    public Token getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitUnaryExpression(this);
    }
}