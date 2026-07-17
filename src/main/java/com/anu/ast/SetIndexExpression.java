package com.anu.ast;

public class SetIndexExpression extends Expression {

    private final Expression array;
    private final Expression index;
    private final Expression value;

    public SetIndexExpression(
            Expression array,
            Expression index,
            Expression value) {

        this.array = array;
        this.index = index;
        this.value = value;
    }

    public Expression getArray() {
        return array;
    }

    public Expression getIndex() {
        return index;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitSetIndexExpression(this);
    }
}