package com.anu;

import com.anu.ast.Program;
import com.anu.cli.Command;
import com.anu.cli.CommandParser;
import com.anu.cli.CommandType;
import com.anu.interpreter.Interpreter;
import com.anu.io.SourceReader;
import com.anu.lexer.Lexer;
import com.anu.parser.Parser;
import com.anu.token.Token;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

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
    }

    private static void run(String file) throws IOException {

        System.out.println(
                AnuLang.NAME + " v" + AnuLang.VERSION
        );
        System.out.println("---------------------------");

        String source = SourceReader.read(file);

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();

        Parser parser = new Parser(tokens);
        Program program = parser.parse();

        Interpreter interpreter = new Interpreter();

        interpreter.execute(program);
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