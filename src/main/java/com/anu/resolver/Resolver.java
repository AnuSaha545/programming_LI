package com.anu.resolver;

import com.anu.ast.*;
import com.anu.interpreter.Interpreter;
import com.anu.token.Token;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Resolver implements
        ExpressionVisitor<Void>,
        StatementVisitor<Void> {
    private enum FunctionType {
        NONE,
        FUNCTION,
        METHOD,
        INITIALIZER
    }
    private enum ClassType {
        NONE,
        CLASS,
        SUBCLASS
    }

    private final Interpreter interpreter;

    private final Stack<Map<String, Boolean>> scopes =
            new Stack<>();

    private FunctionType currentFunction = FunctionType.NONE;
    private ClassType currentClass = ClassType.NONE;
    private int loopDepth = 0;

    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public void resolve(Program program) {
        for (Statement statement : program.getStatements()) {
            resolve(statement);
        }
    }

    private void resolve(Statement statement) {
        statement.accept(this);
    }

    private void resolve(Expression expression) {
        expression.accept(this);
    }

    private void beginScope() {
        scopes.push(new HashMap<>());
    }

    private void endScope() {
        scopes.pop();
    }

    private void declare(String name) {

        if (scopes.isEmpty()) return;

        scopes.peek().put(name, false);
    }

    private void define(String name) {

        if (scopes.isEmpty()) return;

        scopes.peek().put(name, true);
    }
    private void resolveLocal(Expression expression, String name) {

        for (int i = scopes.size() - 1; i >= 0; i--) {

            if (scopes.get(i).containsKey(name)) {

                interpreter.resolve(
                        expression,
                        scopes.size() - 1 - i
                );

                return;
            }
        }
    }

    // 👇 ADD THIS HERE
    private void resolveFunction(FunctionStatement function,
                                 FunctionType type) {

        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;

        beginScope();

        for (Token parameter : function.getParameters()) {
            declare(parameter.getLexeme());
            define(parameter.getLexeme());
        }

        for (Statement statement : function.getBody()) {
            resolve(statement);
        }

        endScope();

        currentFunction = enclosingFunction;
    }


    // ==========================
    // Statement Visitors
    // ==========================

    @Override
    public Void visitBlockStatement(BlockStatement statement) {

        beginScope();

        for (Statement stmt : statement.getStatements()) {
            resolve(stmt);
        }

        endScope();

        return null;
    }

    @Override
    public Void visitVariableStatement(VariableStatement statement) {

        declare(statement.getName().getLexeme());

        if (statement.getInitializer() != null) {
            resolve(statement.getInitializer());
        }

        define(statement.getName().getLexeme());

        return null;
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatement statement) {
        resolve(statement.getExpression());
        return null;
    }

    @Override
    public Void visitPrintStatement(PrintStatement statement) {
        resolve(statement.getExpression());
        return null;
    }

    @Override
    public Void visitIfStatement(IfStatement statement) {

        resolve(statement.getCondition());
        resolve(statement.getThenBranch());

        if (statement.getElseBranch() != null) {
            resolve(statement.getElseBranch());
        }

        return null;
    }

    @Override
    public Void visitWhileStatement(WhileStatement statement) {

        resolve(statement.getCondition());

        loopDepth++;

        resolve(statement.getBody());

        loopDepth--;

        return null;
    }

    @Override
    public Void visitBreakStatement(BreakStatement statement) {

        if (loopDepth == 0) {
            throw new RuntimeException(
                    "Cannot use 'break' outside a loop."
            );
        }

        return null;
    }

    @Override
    public Void visitFunctionStatement(FunctionStatement statement) {

        declare(statement.getName().getLexeme());
        define(statement.getName().getLexeme());

        resolveFunction(statement, FunctionType.FUNCTION);

        return null;
    }
    @Override
    public Void visitReturnStatement(ReturnStatement statement) {

        if (currentFunction == FunctionType.NONE) {
            throw new RuntimeException(
                    "Cannot return from top-level code."
            );
        }

        if (statement.getValue() != null) {
            resolve(statement.getValue());
        }

        return null;
    }

    @Override
    public Void visitClassStatement(ClassStatement statement) {

        ClassType enclosingClass = currentClass;
        currentClass = ClassType.CLASS;

        declare(statement.getName().getLexeme());
        define(statement.getName().getLexeme());

        if (statement.getSuperclass() != null) {
            currentClass = ClassType.SUBCLASS;
            resolve(statement.getSuperclass());
        }

        beginScope();
        scopes.peek().put("this", true);

        for (FunctionStatement method : statement.getMethods()) {

            FunctionType declaration = FunctionType.METHOD;

            if (method.getName().getLexeme().equals("init")) {
                declaration = FunctionType.INITIALIZER;
            }

            resolveFunction(method, declaration);
        }

        endScope();

        currentClass = enclosingClass;

        return null;
    }

    // ==========================
    // Expression Visitors
    // ==========================

    @Override
    public Void visitLiteralExpression(LiteralExpression expression) {
        return null;
    }

    @Override
    public Void visitVariableExpression(VariableExpression expression) {

        if (!scopes.isEmpty()) {

            Boolean defined =
                    scopes.peek().get(expression.getName().getLexeme());

            if (defined != null && !defined) {
                throw new RuntimeException(
                        "Cannot read local variable in its own initializer."
                );
            }
        }

        resolveLocal(
                expression,
                expression.getName().getLexeme()
        );

        return null;
    }

    @Override
    public Void visitAssignmentExpression(AssignmentExpression expression) {

        resolve(expression.getValue());

        resolveLocal(
                expression,
                expression.getName().getLexeme()
        );

        return null;
    }

    @Override
    public Void visitUnaryExpression(UnaryExpression expression) {
        resolve(expression.getRight());
        return null;
    }

    @Override
    public Void visitBinaryExpression(BinaryExpression expression) {
        resolve(expression.getLeft());
        resolve(expression.getRight());
        return null;
    }

    @Override
    public Void visitCallExpression(CallExpression expression) {

        resolve(expression.getCallee());

        for (Expression argument : expression.getArguments()) {
            resolve(argument);
        }

        return null;
    }

    @Override
    public Void visitArrayExpression(ArrayExpression expression) {

        for (Expression element : expression.getElements()) {
            resolve(element);
        }

        return null;
    }

    @Override
    public Void visitIndexExpression(IndexExpression expression) {
        resolve(expression.getArray());
        resolve(expression.getIndex());
        return null;
    }

    @Override
    public Void visitSetIndexExpression(SetIndexExpression expression) {
        resolve(expression.getArray());
        resolve(expression.getIndex());
        resolve(expression.getValue());
        return null;
    }

    @Override
    public Void visitGetExpression(GetExpression expression) {
        resolve(expression.getObject());
        return null;
    }

    @Override
    public Void visitSetExpression(SetExpression expression) {
        resolve(expression.getObject());
        resolve(expression.getValue());
        return null;
    }

    @Override
    public Void visitSuperExpression(SuperExpression expression) {
        return null;
    }
    @Override
    public Void visitThisExpression(ThisExpression expression) {

        if (currentClass == ClassType.NONE) {
            throw new RuntimeException(
                    "Cannot use 'this' outside of a class."
            );
        }

        resolveLocal(
                expression,
                expression.getKeyword().getLexeme()
        );

        return null;
    }

}