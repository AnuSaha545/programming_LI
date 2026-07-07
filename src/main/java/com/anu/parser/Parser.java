package com.anu.parser;

import com.anu.ast.*;
import com.anu.token.Token;
import com.anu.token.TokenType;

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

        if (match(TokenType.LET)) {
            return variableDeclaration();
        }

        if (match(TokenType.PRINT)) {
            return printStatement();
        }

        return expressionStatement();
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

    private Expression expression() {
        return equality();
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

        if (match(TokenType.MINUS)) {

            Token operator = previous();
            Expression right = unary();

            return new UnaryExpression(operator, right);
        }

        return primary();
    }

    private Expression primary() {

        if (match(TokenType.FALSE)) return new LiteralExpression(false);
        if (match(TokenType.TRUE)) return new LiteralExpression(true);
        if (match(TokenType.NULL)) return new LiteralExpression(null);

        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new LiteralExpression(previous().getLiteral());
        }

        if (match(TokenType.IDENTIFIER)) {
            return new VariableExpression(previous());
        }

        if (match(TokenType.LEFT_PAREN)) {

            Expression expression = expression();

            consume(TokenType.RIGHT_PAREN,
                    "Expected ')' after expression.");

            return expression;
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
}