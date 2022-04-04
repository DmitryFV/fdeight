package com.fdeight.sport.khl.process;

import com.fdeight.sport.khl.parsers.KHLRBParser;
import com.fdeight.sport.khl.data.KHLStorage;
import com.fdeight.sport.parsers.TxtParser;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class KHLProcessor {
    public static void main(final String[] args) throws IOException, ParseException {
        System.out.println(String.format("Start %s", KHLProcessor.class.getSimpleName()));
        final KHLStorage storage = new KHLStorage();
        final TxtParser parser = new KHLRBParser(storage);
        final File file = new File("txt_rb_v03");
        parser.parse(file);
        System.out.println(String.format("Storage size = %d", storage.storage.size()));
        System.out.println(String.format("Done %s", KHLProcessor.class.getSimpleName()));
    }
}
