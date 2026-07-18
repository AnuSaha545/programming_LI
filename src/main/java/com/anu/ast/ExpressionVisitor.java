package com.anu.ast;

public interface ExpressionVisitor<T> {

    T visitLiteralExpression(LiteralExpression expression);

    T visitBinaryExpression(BinaryExpression expression);

    T visitUnaryExpression(UnaryExpression expression);

    T visitVariableExpression(VariableExpression expression);

    T visitAssignmentExpression(AssignmentExpression expression);

    T visitCallExpression(CallExpression expression);

    T visitArrayExpression(ArrayExpression expression);

    T visitIndexExpression(IndexExpression expression);

    T visitSetIndexExpression(SetIndexExpression expression);

    T visitGetExpression(GetExpression expression);

    T visitSetExpression(SetExpression expression);
}