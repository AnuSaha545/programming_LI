package com.anu;

import com.anu.lexer.Lexer;
import com.anu.token.Token;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        String source = "\"Hello World\"";

        Lexer lexer = new Lexer(source);

        List<Token> tokens = lexer.scanTokens();

        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}