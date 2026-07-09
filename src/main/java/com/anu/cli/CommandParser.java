package com.anu.cli;

public class CommandParser {

    public Command parse(String[] args) {

        if (args.length == 0) {
            return new Command(CommandType.RUN, "program.anu");
        }

        switch (args[0]) {

            case "run":

                if (args.length < 2) {
                    throw new RuntimeException("Missing file name.");
                }

                return new Command(CommandType.RUN, args[1]);

            case "version":
                return new Command(CommandType.VERSION, null);

            case "help":
                return new Command(CommandType.HELP, null);

            case "repl":
                return new Command(CommandType.REPL, null);

            default:
                throw new RuntimeException(
                        "Unknown command: " + args[0]);
        }
    }
}