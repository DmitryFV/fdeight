package com.fdeight.tictactoe.model;

import com.fdeight.tictactoe.model.controller.TTTControllerM02;
import com.fdeight.tictactoe.model.info.TTTInfo;
import com.fdeight.tictactoe.model.info.TTTInfo02;

import java.util.Arrays;

public class TTTModel02 {

    private TTTControllerM02 controller;

    //------------------------------------------------------------------------------------------------------------------

    private enum PlayerType {
        HUMAN,
        AI,
        RANDOM
    }

    /**
     * Элемент поля.
     */
    private enum Item {
        EMPTY,
        CROSS,
        ZERO;

        private Item otherItem() {
            if (this == CROSS) {
                return ZERO;
            }
            if (this == ZERO) {
                return CROSS;
            }
            throw new IllegalStateException(String.format("No other item for: %s", this));
        }

        public String format() {
            return String.valueOf(ordinal());
        }
    }

    private class State {
        /**
         * Поле.
         */
        private final Item[][] field;

        private State() {
            field = new Item[size][size];
            for (final Item[] items : field) {
                Arrays.fill(items, Item.EMPTY);
            }
        }

        private State(final State state) {
            field = new Item[size][size];
            for (int i = 0; i < size; i++) {
                System.arraycopy(state.field[i], 0, field[i], 0, size);
            }
        }

        private State getCopy() {
            return new State(this);
        }
    }

    /**
     * Размер квадратного поля.
     */
    private int size;
    /**
     * Состояние игры.
     */
    private State state;
    /**
     * Типы игроков.
     */
    private final PlayerType[] playerTypes = {PlayerType.AI, PlayerType.HUMAN};
    /**
     * Величина задержки в милисекундах.
     * 0, если не требуется.
     */
    private int delay;

    //------------------------------------------------------------------------------------------------------------------

    public TTTModel02() {
        controller = null;
        size = 0;
        state = null;
        delay = 100;
    }

    public void setController(final TTTControllerM02 controller) {
        this.controller = controller;
    }

    public void setDelay(final int delay) {
        this.delay = delay;
    }

    public static String getVersion() {
        return "TTT 2.0";
    }

    //------------------------------------------------------------------------------------------------------------------

    public int getSize() {
        return size;
    }

    public void startOfGame(final int size) {
        if (!initGame(size)) {
            return;
        }
        play();
    }

    /**
     * Инициализировать игру.
     *
     * @param size размер поля.
     * @return {@code true}, если игра инициализирована.
     */
    boolean initGame(final int size) {
        if (size < 1) {
            endOfGame(String.format("Wrong size (%d)", size));
            return false;
        }
        this.size = size;
        state = new State();
        return true;
    }

    //------------------------------------------------------------------------------------------------------------------

    private void play() {
        controller.drawField(new TTTInfo02(writeField()));
        int playerId = 1;
        while (true) {
            if (checkEndOfGame()) {
                return;
            }
            final Item item = playerId == 1 ? Item.CROSS : Item.ZERO;
            final TTTInfo action;
            switch (playerTypes[playerId - 1]) {
                case HUMAN:
                    action = controller.getAction(playerId);
                    break;
                case AI:
                    action = getActionAI(item);
                    delay();
                    controller.setAction(playerId, action);
                    break;
                case RANDOM:
                    action = getActionRandom();
                    delay();
                    controller.setAction(playerId, action);
                    break;
                default:
                    throw new IllegalStateException("play(): unsupported playerType: " + playerTypes[playerId - 1]);
            }
            final int[] place = action.parseAction();
            if (!checkPlace(place)) {
                endOfGame("Stop game");
                return;
            }
            if (!checkPlaceItem(place)) {
                endOfGame("Wrong move");
                return;
            }
            state.field[place[0]][place[1]] = item;
            controller.drawField(new TTTInfo02(writeField()));
            playerId = playerId == 1 ? 2 : 1;
        }
    }

    private void delay() {
        if (delay <= 0) {
            return;
        }
        try {
            Thread.sleep(delay);
        } catch (final InterruptedException ignored) {
        }
    }

    /**
     * Выдать ход AI.
     *
     * @param item за кого следует ходить.
     * @return ход AI.
     */
    private TTTInfo getActionAI(final Item item) {
        final Item otherItem = item.otherItem();
        int[] placeWin = null;
        int[] placeNoLose = null;
        int[] placeOtherLinesWithOneEmpty = null;
        int[] placeEmpty = null;
        int countLines = -1;
        int countOtherLines = -1;
        int countOtherLinesWithOneEmpty = -1;
        endOfSearch:
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (state.field[i][j] != Item.EMPTY) {
                    continue;
                }
                final State nextState = state.getCopy();
                nextState.field[i][j] = item;
                if (isWin(nextState, item)) {
                    placeWin = createPlaceIfNeedAndSet(placeWin, i, j);
                    break endOfSearch;
                }
                nextState.field[i][j] = otherItem;
                if (isWin(nextState, otherItem)) {
                    placeNoLose = createPlaceIfNeedAndSet(placeNoLose, i, j);
                    continue;
                }
                final int lCountOtherLinesWithOneEmpty = computeCountLinesWithOneEmpty(nextState, i, j, otherItem);
                if (lCountOtherLinesWithOneEmpty >= 2) {
                    if (countOtherLinesWithOneEmpty < lCountOtherLinesWithOneEmpty) {
                        if (size == 3 && item == Item.ZERO && lCountOtherLinesWithOneEmpty == 2
                                && state.field[1][1] == Item.ZERO
                                && (state.field[0][0] == Item.CROSS && state.field[2][2] == Item.CROSS
                                || state.field[0][2] == Item.CROSS && state.field[2][0] == Item.CROSS)) {
                            // Заглушка на частный случай, после которого следует проигрыш для zero.
                            continue;
                        }
                        placeOtherLinesWithOneEmpty = createPlaceIfNeedAndSet(placeOtherLinesWithOneEmpty, i, j);
                        countOtherLinesWithOneEmpty = lCountOtherLinesWithOneEmpty;
                        continue;
                    }
                }
                final int lCountLines = computeCountLines(state, i, j, otherItem);
                if (countLines < lCountLines) {
                    placeEmpty = createPlaceIfNeedAndSet(placeEmpty, i, j);
                    countLines = lCountLines;
                    countOtherLines = computeCountLines(state, i, j, item);
                } else if (countLines == lCountLines) {
                    final int lCountOtherLines = computeCountLines(state, i, j, item);
                    if (countOtherLines < lCountOtherLines) {
                        placeEmpty = createPlaceIfNeedAndSet(placeEmpty, i, j);
                        countOtherLines = lCountOtherLines;
                    }
                }
            }
        }
        return processPlaces(placeWin, placeNoLose, placeOtherLinesWithOneEmpty, placeEmpty);
    }

    private TTTInfo processPlaces(final int[] placeWin, final int[] placeNoLose,
                                  final int[] placeOtherLinesWithOneEmpty, final int[] placeEmpty) {
        final int[] placeResult;
        if (placeWin != null) {
            placeResult = placeWin;
        } else if (placeNoLose != null) {
            placeResult = placeNoLose;
        } else if (placeOtherLinesWithOneEmpty != null) {
            placeResult = placeOtherLinesWithOneEmpty;
        } else if (placeEmpty != null) {
            placeResult = placeEmpty;
        } else {
            throw new IllegalArgumentException("processPlaces(): no empty items");
        }
        return new TTTInfo02(String.format("%d %d", placeResult[0] + 1, placeResult[1] + 1));
    }

    private int computeCountLines(final State state, final int row, final int col, final Item otherItem) {
        boolean hasRow = true;
        boolean hasCol = true;
        boolean hasDiagonal = row == col;
        boolean hasBackDiagonal = row == size - col - 1;
        int result = 0;
        for (int k = 0; k < size; k++) {
            if (hasRow && state.field[row][k] == otherItem) {
                hasRow = false;
            }
            if (hasCol && state.field[k][col] == otherItem) {
                hasCol = false;
            }
            if (hasDiagonal && state.field[k][k] == otherItem) {
                hasDiagonal = false;
            }
            if (hasBackDiagonal && state.field[k][size - k - 1] == otherItem) {
                hasBackDiagonal = false;
            }
            result = computeCountLines(hasRow, hasCol, hasDiagonal, hasBackDiagonal);
            if (result == 0) {
                return 0;
            }
        }
        return result;
    }

    private int computeCountLines(final boolean hasRow, final boolean hasCol,
                                  final boolean hasDiagonal, final boolean hasBackDiagonal) {
        int result = 0;
        if (hasRow) {
            result++;
        }
        if (hasCol) {
            result++;
        }
        if (hasDiagonal) {
            result++;
        }
        if (hasBackDiagonal) {
            result++;
        }
        return result;
    }

    private int computeCountLinesWithOneEmpty(final State state, final int row, final int col, final Item item) {
        int rowBadCellsCount = 0;
        int colBadCellsCount = 0;
        int diagonalBadCellsCount = row == col ? 0 : -1;
        int backDiagonalBadCellsCount = row == size - col - 1 ? 0 : -1;
        int result = 0;
        for (int k = 0; k < size; k++) {
            if (rowBadCellsCount != -1 && state.field[row][k] != item) {
                if (state.field[row][k] != Item.EMPTY) {
                    rowBadCellsCount = -1;
                } else {
                    rowBadCellsCount++;
                }
            }
            if (colBadCellsCount != -1 && state.field[k][col] != item) {
                if (state.field[k][col] != Item.EMPTY) {
                    colBadCellsCount = -1;
                } else {
                    colBadCellsCount++;
                }
            }
            if (diagonalBadCellsCount != -1 && state.field[k][k] != item) {
                if (state.field[k][k] != Item.EMPTY) {
                    diagonalBadCellsCount = -1;
                } else {
                    diagonalBadCellsCount++;
                }
            }
            if (backDiagonalBadCellsCount != -1 && state.field[k][size - k - 1] != item) {
                if (state.field[k][size - k - 1] != Item.EMPTY) {
                    backDiagonalBadCellsCount = -1;
                } else {
                    backDiagonalBadCellsCount++;
                }
            }
            result = computeCountLinesWithOneEmpty(rowBadCellsCount, colBadCellsCount,
                    diagonalBadCellsCount, backDiagonalBadCellsCount);
            if (result == 0) {
                return 0;
            }
        }
        return result;
    }

    private int computeCountLinesWithOneEmpty(final int rowBadCellsCount, final int colBadCellsCount,
                                              final int diagonalBadCellsCount, final int backDiagonalBadCellsCount) {
        int result = 0;
        if (rowBadCellsCount != -1 && rowBadCellsCount < 2) {
            result++;
        }
        if (colBadCellsCount != -1 && colBadCellsCount < 2) {
            result++;
        }
        if (diagonalBadCellsCount != -1 && diagonalBadCellsCount < 2) {
            result++;
        }
        if (backDiagonalBadCellsCount != -1 && backDiagonalBadCellsCount < 2) {
            result++;
        }
        return result;
    }

    /**
     * Выдать ход случайно.
     *
     * @return случайный ход.
     */
    private TTTInfo getActionRandom() {
        for (int i = 0; i < size * size * 10; i++) {
            final int row = (int) (Math.random() * size);
            final int col = (int) (Math.random() * size);
            if (state.field[row][col] == Item.EMPTY) {
                return new TTTInfo02(String.format("%d %d", row + 1, col + 1));
            }
        }
        throw new RuntimeException("getActionRandom(): empty item not found");
    }

    private int[] createPlaceIfNeedAndSet(final int[] place, final int row, final int col) {
        final int[] lPlace = place == null ? new int[2] : place;
        lPlace[0] = row;
        lPlace[1] = col;
        return lPlace;
    }

    private String writeField() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(state.field[i][j].format());
            }
        }
        return sb.toString();
    }

    private boolean checkEndOfGame() {
        if (isWin(state, Item.CROSS)) {
            endOfGame("Cross wins");
            return true;
        }
        if (isWin(state, Item.ZERO)) {
            endOfGame("Zero wins");
            return true;
        }
        if (drawWillBe()) {
            endOfGame("Draw will be");
            return true;
        }
        if (!hasEmpty()) {
            endOfGame("Draw has happened");
            return true;
        }
        return false;
    }

    private boolean isWin(final State state, final Item item) {
        boolean isWinDiagonal = true;
        boolean isWinBackDiagonal = true;
        for (int i = 0; i < size; i++) {
            if (state.field[i][i] != item) {
                isWinDiagonal = false;
            }
            if (state.field[i][size - i - 1] != item) {
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
                if (state.field[i][j] != item) {
                    isWinRow = false;
                }
                if (state.field[j][i] != item) {
                    isWinColumn = false;
                }
            }
            if (isWinRow || isWinColumn) {
                return true;
            }
        }
        return false;
    }

    private boolean drawWillBe() {
        int countCrossLines = -1;
        int countZeroLines = -1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (state.field[i][j] == Item.EMPTY) {
                    final int lCountCrossLines = computeCountLines(state, i, j, Item.ZERO);
                    if (countCrossLines < lCountCrossLines) {
                        countCrossLines = lCountCrossLines;
                    }
                    final int lCountZeroLines = computeCountLines(state, i, j, Item.CROSS);
                    if (countZeroLines < lCountZeroLines) {
                        countZeroLines = lCountZeroLines;
                    }
                }
            }
        } 
        return countCrossLines == 0 && countZeroLines == 0;
    }

    private boolean hasEmpty() {
        for (final Item[] items : state.field) {
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
        return state.field[place[0]][place[1]] == Item.EMPTY;
    }

    private void endOfGame(final String reason) {
        controller.endOfGame(reason);
        clear();
    }

    private void clear() {
        size = 0;
        state = null;
    }
}
