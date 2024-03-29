package com.fdeight.tictactoe.view;

import com.fdeight.tictactoe.controller.TTTControllerV01;
import com.fdeight.tictactoe.model.info.TTTInfo;
import com.fdeight.tictactoe.model.info.TTTInfo02;
import com.fdeight.utils.Utils;

import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.Scanner;

public class TTTView01 {

    /**
     * Элемент поля.
     */
    private enum Item {
        EMPTY(" "),
        CROSS("X"),
        ZERO("0");

        private final String output;

        Item(final String output) {
            this.output = output;
        }

        public static Item convert(final int formatted) {
            Utils.checkArgument(formatted, 0, values().length - 1, "convert(), formatted");
            return values()[formatted];
        }
    }

    private enum Command {
        UNDEFINED,
        START_OF_GAME,
        EXIT,
    }

    private TTTControllerV01 controller;

    private final PrintStream ps;

    private final Scanner scanner;

    public TTTView01() {
        ps = System.out;
        scanner = new Scanner(System.in);
    }

    public void setController(final TTTControllerV01 controller) {
        this.controller = controller;
    }

    public String getVersion() {
        return "TTT 1.0";
    }

    public void run() {
        ps.println(controller.getVersion());
        while (true) {
            final Command mainCommand = readMainCommand();
            switch (mainCommand) {
                case START_OF_GAME:
                    startOfGame();
                    break;
                case EXIT:
                    if (exit()) {
                        return;
                    }
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    private void startOfGame() {
        ps.print("Enter size: ");
        final Integer size = nextInt();
        if (size != null) {
            controller.startOfGame(size);
        }
    }

    public void drawField(final TTTInfo info) {
        final int size = controller.getSize();
        final int[] field = info.parseField(size);
        int index = 0;
        for (int i = 0; i < size; i++) {
            ps.print("|");
            for (int j = 0; j < size; j++) {
                final Item item = Item.convert(field[index]);
                ps.print(item.output);
                if (j == size - 1) {
                    ps.print("|");
                } else {
                    ps.print(" ");
                }
                index++;
            }
            ps.println();
        }
    }

    public TTTInfo getAction(final int playerId) {
        final int size = controller.getSize();
        ps.println(String.format("Enter action of player %d: ", playerId));
        Integer row;
        Integer col;
        while (true) {
            ps.println(String.format("Enter row, from 1 to %d or another number to stop game: ", size));
            row = nextInt();
            if (row == null) {
                continue;
            }
            if (row < 1 || row > size) {
                col = 0;
                break;
            }
            ps.println(String.format("Enter col, from 1 to %d or another number to stop game: ", size));
            col = nextInt();
            if (col != null) {
                break;
            }
        }
        return new TTTInfo02(String.format("%d %d", row, col));
    }

    public void setAction(final int playerId, final TTTInfo action) {
        ps.println(String.format("Action of player %d: %s", playerId, action.getState()));
    }

    public void endOfGame(final String state) {
        ps.println(String.format("End of game: %s", state));
    }

    private boolean exit() {
        ps.println();
        ps.println("End of work");
        return true;
    }

    //------------------------------------------------------------------------------------------------------------------

    @SuppressWarnings("unused")
    private Long nextLong() {
        try {
            return scanner.nextLong();
        } catch (final InputMismatchException e) {
            scanner.nextLine();
            ps.println("Wrong input");
            return null;
        }
    }

    private Integer nextInt() {
        try {
            return scanner.nextInt();
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
        final Command[] commands = new Command[]{Command.START_OF_GAME, Command.EXIT};
        return readAndPrintCommand(commands);
    }
}
