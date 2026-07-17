package com.anu.ast;

public interface StatementVisitor<T> {

    T visitExpressionStatement(ExpressionStatement statement);

    T visitVariableStatement(VariableStatement statement);

    T visitPrintStatement(PrintStatement statement);

    T visitBlockStatement(BlockStatement statement);

    T visitIfStatement(IfStatement statement);

    T visitWhileStatement(WhileStatement statement);

    T visitFunctionStatement(FunctionStatement statement);

    T visitReturnStatement(ReturnStatement statement);

    T visitBreakStatement(BreakStatement statement);

    T visitClassStatement(ClassStatement statement);
}