package com.anu.interpreter;

import com.anu.ast.*;
import com.anu.runtime.*;
import com.anu.ast.AssignmentExpression;
import com.anu.ast.IfStatement;
import com.anu.ast.WhileStatement;
import com.anu.ast.FunctionStatement;
import com.anu.ast.CallExpression;
import com.anu.ast.ReturnStatement;
import com.anu.runtime.LenFunction;
import com.anu.runtime.InputFunction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.anu.ast.ClassStatement;


public class Interpreter implements ExpressionVisitor<Object>, StatementVisitor<Void> {

    private Environment environment = new Environment();

    public Interpreter() {
        environment.define("clock", new ClockFunction());
        environment.define("len", new LenFunction());
        environment.define("input", new InputFunction());
    }

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

        Object value = expression.getValue();
        if (value instanceof String s) {
            return new StringInstance(s);
        }
        return value;
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

                // Number + Number
                if (left instanceof Double && right instanceof Double) {
                    return (Double) left + (Double) right;
                }
                // String concatenation
                if (left instanceof StringInstance || right instanceof StringInstance) {
                    return new StringInstance(
                            stringify(left) + stringify(right)
                    );
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

        System.out.println(stringify(value));

        return null;
    }

    private String stringify(Object object) {

        if (object == null) {
            return "null";
        }

        if (object instanceof StringInstance stringInstance) {
            return stringInstance.getValue();
        }

        if (object instanceof Double number) {
            if (number == number.longValue()) {
                return String.valueOf(number.longValue());
            }
            return number.toString();
        }

        return object.toString();
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

            try {
                execute(statement.getBody());
            } catch (Break ignored) {
                break;
            }
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

    @Override
    public Void visitBreakStatement(BreakStatement statement) {
        throw new Break();
    }

    @Override
    public Object visitArrayExpression(ArrayExpression expression) {
        List<Object> values = new ArrayList<>();

        for (Expression element : expression.getElements()) {
            values.add(evaluate(element));
        }

        return new ArrayInstance(values);
    }

    @Override
    public Object visitIndexExpression(IndexExpression expression) {

        Object array = evaluate(expression.getArray());
        Object index = evaluate(expression.getIndex());

        if (!(array instanceof ArrayInstance instance))
            throw new RuntimeException("Can only index arrays.");

        if (!(index instanceof Integer))
            throw new RuntimeException("Array index must be an integer.");

        List<Object> values = instance.getValues();
        int i = (Integer) index;

        if (i < 0 || i >= values.size())
            throw new RuntimeException("Array index out of bounds.");

        return values.get(i);
    }

    @Override
    public Object visitSetIndexExpression(SetIndexExpression expression) {

        Object array = evaluate(expression.getArray());
        Object index = evaluate(expression.getIndex());
        Object value = evaluate(expression.getValue());

        if (!(array instanceof ArrayInstance instance))
            throw new RuntimeException("Can only assign into arrays.");

        if (!(index instanceof Integer))
            throw new RuntimeException("Array index must be an integer.");

        List<Object> values = instance.getValues();
        int i = (Integer) index;

        if (i < 0 || i >= values.size())
            throw new RuntimeException("Array index out of bounds.");

        values.set(i, value);

        return value;
    }

    @Override
    public Object visitGetExpression(GetExpression expression) {

        Object object = evaluate(expression.getObject());

        if (object instanceof AnuInstance instance)
            return instance.get(expression.getName().getLexeme());

        throw new RuntimeException("Object has no properties.");
    }
    @Override
    public Void visitClassStatement(ClassStatement statement) {

        AnuClass superclass = null;

        if (statement.getSuperclass() != null) {

            Object superObject = evaluate(statement.getSuperclass());

            if (!(superObject instanceof AnuClass)) {
                throw new RuntimeException(
                        "Superclass must be a class."
                );
            }

            superclass = (AnuClass) superObject;
        }

        HashMap<String, AnuFunction> methods = new HashMap<>();

        for (FunctionStatement method : statement.getMethods()) {

            methods.put(
                    method.getName().getLexeme(),
                    new AnuFunction(method)
            );
        }

        AnuClass anuClass = new AnuClass(
                statement.getName().getLexeme(),
                superclass,
                methods
        );

        environment.define(
                statement.getName().getLexeme(),
                anuClass
        );

        return null;
    }
    @Override
    public Object visitSetExpression(SetExpression expression) {

        Object object = evaluate(expression.getObject());

        if (!(object instanceof AnuObject instance)) {
            throw new RuntimeException("Only objects have fields.");
        }

        Object value = evaluate(expression.getValue());

        instance.set(
                expression.getName().getLexeme(),
                value
        );

        return value;
    }

    @Override
    public Object visitSuperExpression(SuperExpression expression) {

        Object superObject = environment.get("super");

        if (!(superObject instanceof AnuClass superclass)) {
            throw new RuntimeException("'super' is not a class.");
        }

        Object thisObject = environment.get("this");

        if (!(thisObject instanceof AnuObject instance)) {
            throw new RuntimeException("'this' is not an object.");
        }

        AnuFunction method = superclass.findMethod(
                expression.getMethod().getLexeme()
        );

        if (method == null) {
            throw new RuntimeException(
                    "Undefined superclass method '" +
                            expression.getMethod().getLexeme() + "'."
            );
        }

        return method.bind(instance, superclass.getSuperclass());
    }

}
