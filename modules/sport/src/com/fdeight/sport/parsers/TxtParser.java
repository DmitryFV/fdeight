package com.fdeight.sport.parsers;

import java.io.*;
import java.text.ParseException;

public abstract class TxtParser {
    public void parseFile(final File file) throws IOException, ParseException {
        fileStarted(file);
        try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line);
            }
        }
        fileEnded(file);
    }

    private void fileStarted(final File file) {
        System.out.println(String.format("Started [%s]", file.getName()));
    }

    private void fileEnded(final File file) {
        System.out.println(String.format("Ended [%s]", file.getName()));
    }

    protected abstract void processLine(final String line) throws ParseException;
}
