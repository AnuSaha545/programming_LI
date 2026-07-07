package com.anu.ast;

public interface StatementVisitor<T> {

    T visitExpressionStatement(ExpressionStatement statement);

    T visitVariableStatement(VariableStatement statement);

    T visitPrintStatement(PrintStatement statement);

    T visitBlockStatement(BlockStatement statement);
}