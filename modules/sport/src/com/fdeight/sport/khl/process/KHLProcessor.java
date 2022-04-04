package com.fdeight.sport.khl.process;

import com.fdeight.sport.khl.parsers.KHLRBParser;
import com.fdeight.sport.khl.data.KHLStorage;
import com.fdeight.sport.parsers.TxtParser;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class KHLProcessor {
    public static void main(final String[] args) throws IOException, ParseException {
        System.out.println(String.format("Start %s", KHLProcessor.class.getSimpleName()));
        final KHLStorage khlStorage = new KHLStorage();
        final TxtParser parser = new KHLRBParser(khlStorage);
        final File file = new File("txt_rb_v03");
        parser.parse(file);
        khlStorage.sort();
        System.out.println(String.format("Storage size = %d", khlStorage.size()));
        final Date min = new GregorianCalendar(2016, Calendar.OCTOBER, 1).getTime();
        final Date max = new GregorianCalendar(2016, Calendar.OCTOBER, 5).getTime();
        final KHLStorage subStorage = khlStorage.getFiltredByDateSubStorage(min, max);
        System.out.println(String.format("Sub storage size = %d", subStorage.size()));
        System.out.println(String.format("Done %s", KHLProcessor.class.getSimpleName()));
    }
}
