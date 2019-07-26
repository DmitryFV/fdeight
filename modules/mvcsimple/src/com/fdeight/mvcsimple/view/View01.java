package com.fdeight.mvcsimple.view;

import com.fdeight.mvcsimple.controller.ControllerV01;

import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.Scanner;

public class View01 {

    private enum Command {
        UNDEFINED,
        GET_DATA,
        START_OF_SOLVING,
        EXIT,
    }

    private ControllerV01 controller;

    private final PrintStream ps;

    private final Scanner scanner;

    public View01() {
        ps = System.out;
        scanner = new Scanner(System.in);
    }

    public void setController(final ControllerV01 controller) {
        this.controller = controller;
    }

    public String getVersion() {
        return "1.0";
    }

    public void run() {
        ps.println(controller.getVersion());
        while (true) {
            final Command mainCommand = readMainCommand();
            switch (mainCommand) {
                case GET_DATA:
                    getData();
                    break;
                case START_OF_SOLVING:
                    startOfSolving();
                    break;
                case EXIT:
                    if (exit()) {
                        return;
                    }
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    private void getData() {
        ps.println(controller.getData());
    }

    private void startOfSolving() {
        ps.print("Enter number: ");
        final Long l = nextLong();
        if (l != null) {
            controller.startOfSolving(l);
        }
    }

    public void endOfSolving(final String output) {
        ps.println(String.format("endOfSolving(): %s", output));
    }

    private boolean exit() {
        ps.println();
        ps.println("End of work");
        return true;
    }

    //------------------------------------------------------------------------------------------------------------------

    private Long nextLong() {
        try {
            return scanner.nextLong();
        } catch (final InputMismatchException e) {
            scanner.nextLine();
            ps.println("Wrong input");
            return null;
        }
    }

    private void printRequest(final Command[] commands) {
        ps.println();
        ps.println("Enter command");
        for (int i = 0; i < commands.length; i++) {
            ps.println(String.format("%s: %d", commands[i].toString(), i));
        }
    }

    private Command readCommand(final Command[] commands) {
        printRequest(commands);
        final int commandId;
        try {
            commandId = scanner.nextInt();
        } catch (final InputMismatchException e) {
            scanner.nextLine();
            return Command.UNDEFINED;
        }
        if (commandId >= 0 && commandId < commands.length) {
            return commands[commandId];
        } else {
            return Command.UNDEFINED;
        }
    }

    private Command readAndPrintCommand(final Command[] commands) {
        final Command command = readCommand(commands);
        ps.println(command.toString());
        return command;
    }

    private Command readMainCommand() {
        final Command[] commands = new Command[]{Command.GET_DATA, Command.START_OF_SOLVING, Command.EXIT};
        return readAndPrintCommand(commands);
    }
}
