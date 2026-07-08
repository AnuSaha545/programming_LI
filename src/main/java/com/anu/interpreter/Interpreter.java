package com.anu.interpreter;

import com.anu.ast.*;
import com.anu.runtime.Environment;
import com.anu.ast.AssignmentExpression;
import com.anu.ast.IfStatement;
import com.anu.ast.WhileStatement;
import com.anu.ast.FunctionStatement;
import com.anu.runtime.AnuFunction;
import com.anu.ast.CallExpression;
import com.anu.ast.ReturnStatement;
import com.anu.runtime.Return;
import com.anu.runtime.AnuCallable;

import java.util.List;

public class Interpreter implements ExpressionVisitor<Object>, StatementVisitor<Void> {

    private Environment environment = new Environment();
    public void executeBlock(BlockStatement block, Environment environment) {

        executeBlock(block.getStatements(), environment);
    }

    public void executeBlock(List<Statement> statements, Environment environment) {

        Environment previous = this.environment;

        try {
            this.environment = environment;

            for (Statement statement : statements) {
                execute(statement);
            }

        } finally {
            this.environment = previous;
        }
    }
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

            case NOT:
                return !((Boolean) right);

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
            case AND:
                return (Boolean) left && (Boolean) right;

            case OR:
                return (Boolean) left || (Boolean) right;

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
    public Object visitAssignmentExpression(AssignmentExpression expression) {

        Object value = evaluate(expression.getValue());

        environment.assign(
                expression.getName().getLexeme(),
                value);

        return value;
    }

    @Override
    public Void visitBlockStatement(BlockStatement statement) {

        executeBlock(
                statement,
                new Environment(environment));

        return null;
    }
    @Override
    public Void visitIfStatement(IfStatement statement) {

        Object condition = evaluate(statement.getCondition());

        if ((Boolean) condition) {
            execute(statement.getThenBranch());
        } else if (statement.getElseBranch() != null) {
            execute(statement.getElseBranch());
        }

        return null;
    }
    @Override
    public Void visitWhileStatement(WhileStatement statement) {

        while ((Boolean) evaluate(statement.getCondition())) {
            execute(statement.getBody());
        }

        return null;
    }
    @Override
    public Void visitFunctionStatement(FunctionStatement statement) {

        AnuFunction function = new AnuFunction(statement);

        environment.define(
                statement.getName().getLexeme(),
                function);

        return null;
    }
    @Override
    public Object visitCallExpression(CallExpression expression) {

        Object callee = evaluate(expression.getCallee());

        if (!(callee instanceof AnuCallable)) {
            throw new RuntimeException("Can only call functions.");
        }

        AnuCallable function = (AnuCallable) callee;

        List<Object> arguments = new java.util.ArrayList<>();

        for (Expression argument : expression.getArguments()) {
            arguments.add(evaluate(argument));
        }

        if (arguments.size() != function.arity()) {
            throw new RuntimeException(
                    "Expected " + function.arity()
                            + " arguments but got "
                            + arguments.size() + ".");
        }

        return function.call(this, arguments);
    }

    @Override
    public Void visitReturnStatement(ReturnStatement statement) {

        Object value = null;

        if (statement.getValue() != null) {
            value = evaluate(statement.getValue());
        }

        throw new com.anu.runtime.Return(value);
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void executeStatements(List<Statement> statements) {
        for (Statement statement : statements) {
            execute(statement);
        }
    }
}
