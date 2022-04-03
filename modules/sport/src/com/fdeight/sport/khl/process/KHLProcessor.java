package com.fdeight.sport.khl.process;

import com.fdeight.sport.khl.parsers.KHLRBParser;
import com.fdeight.sport.parsers.TxtParser;

import java.io.File;
import java.io.IOException;

public class KHLProcessor {
    public static void main(final String[] args) throws IOException {
        System.out.println(String.format("Start %s", KHLProcessor.class.getSimpleName()));
        File file;
        final TxtParser parser = new KHLRBParser();
        file = new File("txt_rb_v02/Результаты хоккея 2014-2015.txt");
        parser.parseFile(file);
        file = new File("txt_rb_v02/Результаты хоккея 2015-2016.txt");
        parser.parseFile(file);
        System.out.println(String.format("Done %s", KHLProcessor.class.getSimpleName()));
    }
}
