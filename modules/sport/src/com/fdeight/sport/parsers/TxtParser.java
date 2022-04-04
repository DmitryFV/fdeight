package com.fdeight.sport.parsers;

import java.io.*;
import java.text.ParseException;
import java.util.Objects;

public abstract class TxtParser {
    public void parse(final File file) throws IOException, ParseException {
        if (!file.exists()) return;
        if (file.isFile()) {
            started(file);
            parseFile(file);
            ended(file);
            return;
        }
        if (file.isDirectory()) {
            started(file);
            for (final File lFile : Objects.requireNonNull(file.listFiles())) {
                parse(lFile);
            }
            ended(file);
            return;
        }
        throw new IllegalStateException(String.format("[%s] is not file and not directory", file.getName()));
    }

    private void parseFile(final File file) throws IOException, ParseException {
        try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                processLine(lineNumber++, line);
            }
        }
    }

    private void started(final File file) {
        System.out.println(String.format("Started [%s]", file.getName()));
    }

    private void ended(final File file) {
        System.out.println(String.format("Ended [%s]", file.getName()));
    }

    protected abstract void processLine(final int lineNumber, final String line) throws ParseException;
}
