package com.anu;

import com.anu.ast.Program;
import com.anu.interpreter.Interpreter;
import com.anu.io.SourceReader;
import com.anu.lexer.Lexer;
import com.anu.parser.Parser;
import com.anu.token.Token;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        String source = SourceReader.read("program.anu");

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();

        Parser parser = new Parser(tokens);
        Program program = parser.parse();

        Interpreter interpreter = new Interpreter();

        // We will implement this method in the next step.
        interpreter.execute(program);
    }
}