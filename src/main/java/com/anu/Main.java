package com.anu;

import com.anu.ast.Program;
import com.anu.cli.Command;
import com.anu.cli.CommandParser;
import com.anu.interpreter.Interpreter;
import com.anu.io.SourceReader;
import com.anu.lexer.Lexer;
import com.anu.parser.Parser;
import com.anu.resolver.Resolver;
import com.anu.token.Token;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        try {

            Command command = new CommandParser().parse(args);

            switch (command.getType()) {

                case RUN:
                    run(command.getArgument());
                    break;

                case VERSION:
                    printVersion();
                    break;

                case HELP:
                    printHelp();
                    break;

                case REPL:
                    System.out.println("REPL coming soon...");
                    break;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void run(String file) throws IOException {

        try {

            String source = SourceReader.read(file);

            Lexer lexer = new Lexer(source);
            List<Token> tokens = lexer.scanTokens();

            // Uncomment for debugging
            /*
            System.out.println("===== TOKENS =====");
            for (Token token : tokens) {
                System.out.println(token);
            }
            System.out.println("==================");
            */

            Parser parser = new Parser(tokens);
            Program program = parser.parse();

            Interpreter interpreter = new Interpreter();

            Resolver resolver = new Resolver(interpreter);
            resolver.resolve(program);

            interpreter.execute(program);

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void printVersion() {
        System.out.println(AnuLang.NAME + " v" + AnuLang.VERSION);
    }

    private static void printHelp() {

        System.out.println("AnuLang Command Line");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  run <file>      Execute an .anu file");
        System.out.println("  repl            Start interactive REPL");
        System.out.println("  version         Show language version");
        System.out.println("  help            Show this help");
    }
}