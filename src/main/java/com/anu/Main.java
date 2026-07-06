package com.anu;

import com.anu.ast.Expression;
import com.anu.ast.AstPrinter;
import com.anu.lexer.Lexer;
import com.anu.parser.Parser;
import com.anu.token.Token;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        String source = "10 + 20 * 5";

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();

        Parser parser = new Parser(tokens);

        Expression expression = parser.parse();

        AstPrinter printer = new AstPrinter();

        System.out.println(printer.print(expression));
    }
}