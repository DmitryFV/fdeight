package com.fdeight.tictactoe.model.info;

public class TTTInfo02 implements TTTInfo {

    private final String state;

    public TTTInfo02(final String state) {
        this.state = state;
    }

    private TTTInfo02(final TTTInfo02 source) {
        state = source.state;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public TTTInfo getCopy() {
        return new TTTInfo02(this);
    }

    @Override
    public int[] parseField(final int size) {
        final String lState = state.trim();
        if (lState.isEmpty()) {
            throw new IllegalStateException("Trimmed state is empty");
        }
        final String[] strings = state.split(" ");
        if (strings.length != size * size) {
            throw new IllegalArgumentException(String.format(
                    "Wrong size (%d): %d (state length) != %d (size * size), %s",
                    size, strings.length, size * size, state));
        }
        final int[] field = new int[size * size];
        for (int i = 0; i < strings.length; i++) {
            field[i] = Integer.parseInt(strings[i]);
        }
        return field;
    }

    @Override
    public int[] parseAction() {
        final String[] strings = state.split(" ");
        if (strings.length != 2) {
            throw new IllegalStateException(String.format("Wrong state: %d (state length) != 2, %s",
                    strings.length, state));
        }
        final int[] place = new int[2];
        for (int k = 0; k < place.length; k++) {
            place[k] = Integer.parseInt(strings[k]) - 1;
        }
        return place;
    }
}
