package com.anu.runtime;

import com.anu.interpreter.Interpreter;

import java.util.List;
import java.util.Scanner;

public class InputFunction implements AnuCallable {

    private static final Scanner scanner = new Scanner(System.in);

    @Override
    public int arity() {
        return 1;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {

        System.out.print(arguments.get(0));

        return scanner.nextLine();
    }

    @Override
    public String toString() {
        return "<native fn input>";
    }
}