package com.anu.token;

import java.util.HashMap;
import java.util.Map;

public class Keywords {

    public static final Map<String, TokenType> KEYWORDS = new HashMap<>();

    static {
        KEYWORDS.put("let", TokenType.LET);
        KEYWORDS.put("fun", TokenType.FUN);
        KEYWORDS.put("return", TokenType.RETURN);
        KEYWORDS.put("if", TokenType.IF);
        KEYWORDS.put("else", TokenType.ELSE);
        KEYWORDS.put("while", TokenType.WHILE);
        KEYWORDS.put("true", TokenType.TRUE);
        KEYWORDS.put("false", TokenType.FALSE);
        KEYWORDS.put("null", TokenType.NULL);
        KEYWORDS.put("print", TokenType.PRINT);
        KEYWORDS.put("for", TokenType.FOR);
        KEYWORDS.put("break", TokenType.BREAK);
    }

    private Keywords() {
    }
}