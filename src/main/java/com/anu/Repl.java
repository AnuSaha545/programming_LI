package com.anu;

import java.util.Scanner;

public class Repl {

    public static void start() {

        Scanner scanner = new Scanner(System.in);

        System.out.println("AnuLang v0.1");
        System.out.println("Type 'exit' to quit.");

        while (true) {

            System.out.print("> ");

            String line = scanner.nextLine();

            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            System.out.println("You entered: " + line);
        }

        System.out.println("Goodbye!");
    }
}