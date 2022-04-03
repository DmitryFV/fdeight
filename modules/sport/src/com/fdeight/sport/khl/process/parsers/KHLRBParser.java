package com.fdeight.sport.khl.process.parsers;

import com.fdeight.sport.parsers.TxtParser;

public class KHLRBParser extends TxtParser {
    @Override
    protected void processLine(final String line) {
        System.out.println(line);
    }
}
