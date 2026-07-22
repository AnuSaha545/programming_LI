package com.anu.ast;

import com.anu.token.Token;

public class ThisExpression extends Expression {

    private final Token keyword;

    public ThisExpression(Token keyword) {
        this.keyword = keyword;
    }

    public Token getKeyword() {
        return keyword;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitThisExpression(this);
    }
}