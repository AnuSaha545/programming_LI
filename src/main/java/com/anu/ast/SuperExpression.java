package com.anu.ast;

import com.anu.token.Token;

public class SuperExpression extends Expression {

    private final Token keyword;
    private final Token method;

    public SuperExpression(Token keyword, Token method) {
        this.keyword = keyword;
        this.method = method;
    }

    public Token getKeyword() {
        return keyword;
    }

    public Token getMethod() {
        return method;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitSuperExpression(this);
    }
}