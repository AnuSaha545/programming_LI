package com.anu.parser;

import com.anu.ast.*;
import com.anu.token.Token;
import com.anu.token.TokenType;
import com.anu.ast.AssignmentExpression;
import com.anu.ast.BlockStatement;
import com.anu.ast.IfStatement;
import com.anu.ast.WhileStatement;
import com.anu.ast.FunctionStatement;
import com.anu.ast.CallExpression;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int current;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Program parse() {

        List<Statement> statements = new ArrayList<>();

        while (!isAtEnd()) {
            statements.add(statement());
        }

        return new Program(statements);
    }

    private Statement statement() {

        if (match(TokenType.FUN)) {
            return functionDeclaration();
        }

        if (match(TokenType.RETURN)) {
            return returnStatement();
        }
        if (match(TokenType.FOR)) {
            return forStatement();
        }

        if (match(TokenType.WHILE)) {
            return whileStatement();
        }

        if (match(TokenType.IF)) {
            return ifStatement();
        }

        if (match(TokenType.LEFT_BRACE)) {
            return block();
        }

        if (match(TokenType.LET)) {
            return variableDeclaration();
        }

        if (match(TokenType.PRINT)) {
            return printStatement();
        }
        if (match(TokenType.BREAK)) {
            return breakStatement();
        }
        return expressionStatement();
    }
    private Statement returnStatement() {

        Expression value = null;

        if (!check(TokenType.SEMICOLON)) {
            value = expression();
        }

        consume(
                TokenType.SEMICOLON,
                "Expected ';' after return value.");

        return new ReturnStatement(value);
    }
    private FunctionStatement functionDeclaration() {

        Token name = consume(
                TokenType.IDENTIFIER,
                "Expected function name.");

        consume(
                TokenType.LEFT_PAREN,
                "Expected '(' after function name.");

        List<Token> parameters = new ArrayList<>();

        if (!check(TokenType.RIGHT_PAREN)) {

            do {
                parameters.add(
                        consume(
                                TokenType.IDENTIFIER,
                                "Expected parameter name."));
            } while (match(TokenType.COMMA));
        }

        consume(
                TokenType.RIGHT_PAREN,
                "Expected ')' after parameters.");

        consume(
                TokenType.LEFT_BRACE,
                "Expected '{' before function body.");

        List<Statement> body = new ArrayList<>();

        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            body.add(statement());
        }

        consume(
                TokenType.RIGHT_BRACE,
                "Expected '}' after function body.");

        return new FunctionStatement(name, parameters, body);
    }
    private Statement whileStatement() {

        consume(TokenType.LEFT_PAREN,
                "Expected '(' after 'while'.");

        Expression condition = expression();

        consume(TokenType.RIGHT_PAREN,
                "Expected ')' after condition.");

        Statement body = statement();

        return new WhileStatement(condition, body);
    }
    private Statement ifStatement() {

        consume(TokenType.LEFT_PAREN,
                "Expected '(' after 'if'.");

        Expression condition = expression();

        consume(TokenType.RIGHT_PAREN,
                "Expected ')' after condition.");

        Statement thenBranch = statement();

        Statement elseBranch = null;

        if (match(TokenType.ELSE)) {
            elseBranch = statement();
        }

        return new IfStatement(condition, thenBranch, elseBranch);
    }

    private Statement block() {

        List<Statement> statements = new ArrayList<>();

        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(statement());
        }

        consume(TokenType.RIGHT_BRACE,
                "Expected '}' after block.");

        return new BlockStatement(statements);
    }
    private Statement variableDeclaration() {

        Token name = consume(
                TokenType.IDENTIFIER,
                "Expected variable name.");

        consume(
                TokenType.ASSIGN,
                "Expected '=' after variable name.");

        Expression initializer = expression();

        consume(
                TokenType.SEMICOLON,
                "Expected ';' after variable declaration.");

        return new VariableStatement(name, initializer);
    }

    private Statement printStatement() {

        Expression value = expression();

        consume(TokenType.SEMICOLON,
                "Expected ';' after value.");

        return new PrintStatement(value);
    }

    private Statement expressionStatement() {

        Expression value = expression();

        consume(TokenType.SEMICOLON,
                "Expected ';' after expression.");

        return new ExpressionStatement(value);
    }

    private Expression call() {

        Expression expression = primary();

        while (true) {

            if (match(TokenType.LEFT_PAREN)) {

                expression = finishCall(expression);

            }
            else if (match(TokenType.DOT)) {

                Token name = consume(
                        TokenType.IDENTIFIER,
                        "Expected property name after '.'.");

                expression = new GetExpression(expression, name);
            }else if (match(TokenType.LEFT_BRACKET)) {

                Expression index = expression();

                consume(
                        TokenType.RIGHT_BRACKET,
                        "Expected ']' after index.");

                expression = new IndexExpression(expression, index);

            } else {

                break;
            }
        }

        return expression;
    }
    private Expression finishCall(Expression callee) {

        List<Expression> arguments = new ArrayList<>();

        if (!check(TokenType.RIGHT_PAREN)) {

            do {
                arguments.add(expression());
            } while (match(TokenType.COMMA));
        }

        consume(
                TokenType.RIGHT_PAREN,
                "Expected ')' after arguments.");

        return new CallExpression(callee, arguments);
    }
    private Expression logic() {

        Expression expression = equality();

        while (match(TokenType.AND, TokenType.OR)) {

            Token operator = previous();
            Expression right = equality();

            expression = new BinaryExpression(expression, operator, right);
        }

        return expression;
    }
    private Expression assignment() {

        Expression expression = logic();

        if (match(TokenType.ASSIGN)) {

            Expression value = assignment();

            if (expression instanceof VariableExpression) {

                Token name = ((VariableExpression) expression).getName();

                return new AssignmentExpression(name, value);
            }

            if (expression instanceof IndexExpression) {

                IndexExpression indexExpression = (IndexExpression) expression;

                return new SetIndexExpression(
                        indexExpression.getArray(),
                        indexExpression.getIndex(),
                        value
                );
            }

            throw new RuntimeException("Invalid assignment target.");
        }

        return expression;
    }    private Expression expression() {
        return assignment();
    }

    private Expression equality() {

        Expression expression = comparison();

        while (match(TokenType.EQUAL_EQUAL, TokenType.NOT_EQUAL)) {

            Token operator = previous();
            Expression right = comparison();

            expression = new BinaryExpression(expression, operator, right);
        }

        return expression;
    }

    private Expression comparison() {

        Expression expression = term();

        while (match(
                TokenType.GREATER,
                TokenType.GREATER_EQUAL,
                TokenType.LESS,
                TokenType.LESS_EQUAL)) {

            Token operator = previous();
            Expression right = term();

            expression = new BinaryExpression(expression, operator, right);
        }

        return expression;
    }

    private Expression term() {

        Expression expression = factor();

        while (match(TokenType.PLUS, TokenType.MINUS)) {

            Token operator = previous();
            Expression right = factor();

            expression = new BinaryExpression(expression, operator, right);
        }

        return expression;
    }

    private Expression factor() {

        Expression expression = unary();

        while (match(TokenType.STAR, TokenType.SLASH, TokenType.MODULO)) {

            Token operator = previous();
            Expression right = unary();

            expression = new BinaryExpression(expression, operator, right);
        }

        return expression;
    }

    private Expression unary() {

        if (match(TokenType.MINUS, TokenType.NOT)) {

            Token operator = previous();
            Expression right = unary();

            return new UnaryExpression(operator, right);
        }

        return call();
    }
    private Expression primary() {

        if (match(TokenType.FALSE)) return new LiteralExpression(false);

        if (match(TokenType.TRUE)) return new LiteralExpression(true);

        if (match(TokenType.NULL)) return new LiteralExpression(null);

        if (match(TokenType.NUMBER)) {
            return new LiteralExpression(previous().getLiteral());
        }

        if (match(TokenType.STRING)) {
            return new LiteralExpression(previous().getLiteral());
        }

        if (match(TokenType.IDENTIFIER)) {
            return new VariableExpression(previous());
        }

        if (match(TokenType.LEFT_BRACKET)) {

            List<Expression> elements = new ArrayList<>();

            if (!check(TokenType.RIGHT_BRACKET)) {

                do {
                    elements.add(expression());
                } while (match(TokenType.COMMA));
            }

            consume(TokenType.RIGHT_BRACKET,
                    "Expected ']' after array.");

            return new ArrayExpression(elements);
        }

        throw new RuntimeException("Expected expression.");
    }
    private boolean match(TokenType... types) {

        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {

        if (isAtEnd()) return false;

        return peek().getType() == type;
    }

    private Token advance() {

        if (!isAtEnd()) current++;

        return previous();
    }

    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token consume(TokenType type, String message) {

        if (check(type)) return advance();

        throw new RuntimeException(message);
    }
    private Statement forStatement() {

        consume(TokenType.LEFT_PAREN,
                "Expected '(' after 'for'.");

        Statement initializer;

        if (match(TokenType.SEMICOLON)) {
            initializer = null;
        } else if (match(TokenType.LET)) {
            initializer = variableDeclaration();
        } else {
            initializer = expressionStatement();
        }

        Expression condition = null;

        if (!check(TokenType.SEMICOLON)) {
            condition = expression();
        }

        consume(TokenType.SEMICOLON,
                "Expected ';' after loop condition.");

        Expression increment = null;

        if (!check(TokenType.RIGHT_PAREN)) {
            increment = expression();
        }

        consume(TokenType.RIGHT_PAREN,
                "Expected ')' after for clauses.");

        Statement body = statement();

        if (increment != null) {

            List<Statement> statements = new ArrayList<>();
            statements.add(body);
            statements.add(new ExpressionStatement(increment));

            body = new BlockStatement(statements);
        }

        if (condition == null) {
            condition = new LiteralExpression(true);
        }

        body = new WhileStatement(condition, body);

        if (initializer != null) {

            List<Statement> statements = new ArrayList<>();
            statements.add(initializer);
            statements.add(body);

            body = new BlockStatement(statements);
        }
        return body;
    }
    private Statement breakStatement() {

        consume(TokenType.SEMICOLON,
                "Expected ';' after break.");

        return new BreakStatement();
    }
    private Statement classDeclaration() {

        Token name = consume(TokenType.IDENTIFIER,
                "Expected class name.");

        consume(TokenType.LEFT_BRACE,
                "Expected '{' before class body.");

        List<FunctionStatement> methods = new ArrayList<>();

        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            methods.add(functionDeclaration());
        }

        consume(TokenType.RIGHT_BRACE,
                "Expected '}' after class body.");

        return new ClassStatement(name, methods);
    }
}