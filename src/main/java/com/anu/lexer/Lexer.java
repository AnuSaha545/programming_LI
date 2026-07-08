package com.anu.lexer;

import com.anu.token.Keywords;
import com.anu.token.Token;
import com.anu.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start;
    private int current;
    private int line;

    public Lexer(String source) {
        this.source = source;
        this.start = 0;
        this.current = 0;
        this.line = 1;
    }

    public List<Token> scanTokens() {

        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        addToken(TokenType.EOF);

        return tokens;
    }

    private void scanToken() {

        char c = advance();

        switch (c) {

            case '&':

                if (match('&')) {
                    addToken(TokenType.AND);
                } else {
                    throw new RuntimeException(
                            "Unexpected character '&' at line " + line);
                }

                break;

            case '|':

                if (match('|')) {
                    addToken(TokenType.OR);
                } else {
                    throw new RuntimeException(
                            "Unexpected character '|' at line " + line);
                }

                break;

            case '(':
                addToken(TokenType.LEFT_PAREN);
                break;

            case ')':
                addToken(TokenType.RIGHT_PAREN);
                break;

            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;

            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;

            case '+':
                addToken(TokenType.PLUS);
                break;

            case '-':
                addToken(TokenType.MINUS);
                break;

            case '*':
                addToken(TokenType.STAR);
                break;

            case '/':

                if (match('/')) {

                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }

                } else if (match('*')) {

                    while (!isAtEnd()) {

                        if (peek() == '\n') {
                            line++;
                        }

                        if (peek() == '*' && peekNext() == '/') {
                            advance(); // *
                            advance(); // /
                            break;
                        }

                        advance();
                    }

                    if (isAtEnd()) {
                        throw new RuntimeException(
                                "Unterminated multi-line comment at line " + line);
                    }

                } else {

                    addToken(TokenType.SLASH);
                }

                break;

            case '%':
                addToken(TokenType.MODULO);
                break;

            case ';':
                addToken(TokenType.SEMICOLON);
                break;

            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.ASSIGN);
                break;

            case '!':

                addToken(match('=') ? TokenType.NOT_EQUAL : TokenType.NOT);

                break;

            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;

            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;

            case ' ':
            case '\r':
            case '\t':
                break;

            case '\n':
                line++;
                break;

            case '"':
                string();
                break;

            default:
                if (Character.isLetter(c) || c == '_') {
                    identifier();
                } else if (Character.isDigit(c)) {
                    number();
                }
                break;
        }
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean match(char expected) {

        if (isAtEnd()) return false;

        if (source.charAt(current) != expected) return false;

        current++;

        return true;
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {

        String text = source.substring(start, current);

        tokens.add(new Token(type, text, literal, line));
    }

    private void identifier() {

        while (Character.isLetterOrDigit(peek()) || peek() == '_') {
            advance();
        }

        String text = source.substring(start, current);

        TokenType type = Keywords.KEYWORDS.get(text);

        if (type == null) {
            type = TokenType.IDENTIFIER;
        }

        addToken(type);
    }

    private void number() {

        while (Character.isDigit(peek())) {
            advance();
        }

        String value = source.substring(start, current);

        addToken(TokenType.NUMBER, Integer.parseInt(value));
    }

    private void string() {

        while (peek() != '"' && !isAtEnd()) {

            if (peek() == '\n') {
                line++;
            }

            advance();
        }

        if (isAtEnd()) {
            System.out.println("Unterminated string at line " + line);
            return;
        }

        advance();

        String value = source.substring(start + 1, current - 1);

        addToken(TokenType.STRING, value);
    }
}