package com.anu.ast;

public interface ExpressionVisitor<T> {

    T visitLiteralExpression(LiteralExpression expression);

    T visitBinaryExpression(BinaryExpression expression);

    T visitUnaryExpression(UnaryExpression expression);

    T visitVariableExpression(VariableExpression expression);

}