package com.anu.interpreter;

import com.anu.ast.*;
import com.anu.runtime.Environment;

public class Interpreter implements ExpressionVisitor<Object>, StatementVisitor<Void> {

    private final Environment environment = new Environment();

    /* ===========================
       Program Execution
       =========================== */

    public void execute(Program program) {

        for (Statement statement : program.getStatements()) {
            execute(statement);
        }
    }

    public void execute(Statement statement) {
        statement.accept(this);
    }

    public Object evaluate(Expression expression) {
        return expression.accept(this);
    }

    /* ===========================
       Expression Visitors
       =========================== */

    @Override
    public Object visitLiteralExpression(LiteralExpression expression) {
        return expression.getValue();
    }

    @Override
    public Object visitVariableExpression(VariableExpression expression) {
        return environment.get(expression.getName().getLexeme());
    }

    @Override
    public Object visitUnaryExpression(UnaryExpression expression) {

        Object right = evaluate(expression.getRight());

        switch (expression.getOperator().getType()) {

            case MINUS:
                return -((Integer) right);

            default:
                throw new RuntimeException(
                        "Unknown unary operator: "
                                + expression.getOperator().getLexeme());
        }
    }

    @Override
    public Object visitBinaryExpression(BinaryExpression expression) {

        Object left = evaluate(expression.getLeft());
        Object right = evaluate(expression.getRight());

        switch (expression.getOperator().getType()) {

            case PLUS:

                if (left instanceof Integer && right instanceof Integer) {
                    return (Integer) left + (Integer) right;
                }

                if (left instanceof String || right instanceof String) {
                    return String.valueOf(left) + String.valueOf(right);
                }

                throw new RuntimeException("Invalid operands for '+'");

            case MINUS:
                return (Integer) left - (Integer) right;

            case STAR:
                return (Integer) left * (Integer) right;

            case SLASH:

                if ((Integer) right == 0) {
                    throw new RuntimeException("Division by zero.");
                }

                return (Integer) left / (Integer) right;

            case MODULO:

                if ((Integer) right == 0) {
                    throw new RuntimeException("Division by zero.");
                }

                return (Integer) left % (Integer) right;

            case GREATER:
                return (Integer) left > (Integer) right;

            case GREATER_EQUAL:
                return (Integer) left >= (Integer) right;

            case LESS:
                return (Integer) left < (Integer) right;

            case LESS_EQUAL:
                return (Integer) left <= (Integer) right;

            case EQUAL_EQUAL:
                return left.equals(right);

            case NOT_EQUAL:
                return !left.equals(right);

            default:
                throw new RuntimeException(
                        "Unknown operator: "
                                + expression.getOperator().getLexeme());
        }
    }

    /* ===========================
       Statement Visitors
       =========================== */

    @Override
    public Void visitExpressionStatement(ExpressionStatement statement) {
        evaluate(statement.getExpression());
        return null;
    }

    @Override
    public Void visitPrintStatement(PrintStatement statement) {

        Object value = evaluate(statement.getExpression());

        System.out.println(value);

        return null;
    }

    @Override
    public Void visitVariableStatement(VariableStatement statement) {

        Object value = evaluate(statement.getInitializer());

        environment.define(statement.getName().getLexeme(), value);

        return null;
    }

    @Override
    public Void visitBlockStatement(BlockStatement statement) {

        for (Statement stmt : statement.getStatements()) {
            execute(stmt);
        }

        return null;
    }
}