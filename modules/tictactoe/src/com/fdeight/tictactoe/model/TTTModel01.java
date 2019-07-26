package com.fdeight.tictactoe.model;

import com.fdeight.tictactoe.model.controller.TTTControllerM01;
import com.fdeight.tictactoe.model.info.TTTInfo;
import com.fdeight.tictactoe.model.info.TTTInfo02;

public class TTTModel01 {

    private TTTControllerM01 controller;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Элемент поля.
     */
    private enum Item {
        EMPTY,
        CROSS,
        ZERO
    }

    /**
     * Размер квадратного поля.
     */
    private int size;
    /**
     * Поле.
     */
    private Item[][] field;

    //------------------------------------------------------------------------------------------------------------------

    public TTTModel01() {
        controller = null;
        size = 0;
        field = null;
    }

    public void setController(final TTTControllerM01 controller) {
        this.controller = controller;
    }

    public String getVersion() {
        return "TTT 1.0";
    }

    //------------------------------------------------------------------------------------------------------------------

    public int getSize() {
        return size;
    }

    public void startOfGame(final int size) {
        if (size < 1) {
            endOfGame(String.format("Wrong size (%d)", size));
            return;
        }
        this.size = size;
        field = new Item[size][size];
        for (final Item[] items : field) {
            for (int j = 0; j < items.length; j++) {
                items[j] = Item.EMPTY;
            }
        }
        play();
    }

    //------------------------------------------------------------------------------------------------------------------

    private void play() {
        int playerId = 1;
        while (true) {
            if (isWin(Item.CROSS)) {
                endOfGame("Cross wins");
                return;
            }
            if (isWin(Item.ZERO)) {
                endOfGame("Zero wins");
                return;
            }
            if (!hasEmpty()) {
                endOfGame("Draw");
                return;
            }
            final TTTInfo action = controller.getAction(playerId);
            final int[] place = action.parseAction();
            if (!checkPlace(place)) {
                endOfGame("Stop game");
                return;
            }
            if (!checkPlaceItem(place)) {
                endOfGame("Wrong move");
                return;
            }
            field[place[0]][place[1]] = playerId == 1 ? Item.CROSS : Item.ZERO;
            controller.drawField(new TTTInfo02(writeField()));
            playerId = playerId == 1 ? 2 : 1;
        }
    }

    private String writeField() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(field[i][j].ordinal());
            }
        }
        return sb.toString();
    }

    private boolean isWin(final Item item) {
        boolean isWinDiagonal = true;
        boolean isWinBackDiagonal = true;
        for (int i = 0; i < size; i++) {
            if (field[i][i] != item) {
                isWinDiagonal = false;
            }
            if (field[i][size - i - 1] != item) {
                isWinBackDiagonal = false;
            }
        }
        if (isWinDiagonal || isWinBackDiagonal) {
            return true;
        }
        for (int i = 0; i < size; i++) {
            boolean isWinRow = true;
            boolean isWinColumn = true;
            for (int j = 0; j < size; j++) {
                if (field[i][j] != item) {
                    isWinRow = false;
                }
                if (field[j][i] != item) {
                    isWinColumn = false;
                }
            }
            if (isWinRow || isWinColumn) {
                return true;
            }
        }
        return false;
    }

    private boolean hasEmpty() {
        for (final Item[] items : field) {
            for (final Item item : items) {
                if (item == Item.EMPTY) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkPlace(final int[] place) {
        return (place[0] >= 0 && place[1] >= 0 && place[0] <= size - 1 && place[1] <= size - 1);
    }

    private boolean checkPlaceItem(final int[] place) {
        return field[place[0]][place[1]] == Item.EMPTY;
    }

    private void endOfGame(final String reason) {
        clear();
        controller.endOfGame(reason);
    }

    private void clear() {
        size = 0;
        field = null;
    }
}
