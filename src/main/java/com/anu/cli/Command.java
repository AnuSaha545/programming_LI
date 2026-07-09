package com.anu.cli;

public class Command {

    private final CommandType type;
    private final String argument;

    public Command(CommandType type, String argument) {
        this.type = type;
        this.argument = argument;
    }

    public CommandType getType() {
        return type;
    }

    public String getArgument() {
        return argument;
    }
}