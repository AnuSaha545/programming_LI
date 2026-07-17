package com.anu.token;

public enum TokenType {

    // ===== Single Character Tokens =====
    LEFT_PAREN,
    RIGHT_PAREN,

    LEFT_BRACE,
    RIGHT_BRACE,

    LEFT_BRACKET,
    RIGHT_BRACKET,

    COMMA,
    DOT,

    SEMICOLON,

    PLUS,
    MINUS,
    STAR,
    SLASH,
    MODULO,

    // ===== One or Two Character Tokens =====
    ASSIGN,
    EQUAL_EQUAL,

    AND,
    OR,

    NOT,
    NOT_EQUAL,

    LESS,
    LESS_EQUAL,

    GREATER,
    GREATER_EQUAL,

    // ===== Literals =====
    IDENTIFIER,
    STRING,
    NUMBER,

    // ===== Keywords =====
    LET,
    FUN,
    RETURN,

    FOR,
    BREAK,

    IF,
    ELSE,

    WHILE,

    TRUE,
    FALSE,

    NULL,

    PRINT,
    CLASS,

    // ===== End of File =====
    EOF

}