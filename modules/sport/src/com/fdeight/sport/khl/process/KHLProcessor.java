package com.fdeight.sport.khl.process;

import com.fdeight.sport.khl.parsers.KHLRBParser;
import com.fdeight.sport.khl.data.KHLStorage;
import com.fdeight.sport.khl.solvers.KHLSolver;
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
        //final File file = new File("txt_rb_v03");
        final File file = new File("2016-2017_txt_rb_v03");
        parser.parse(file);
        khlStorage.sort();
        System.out.println(String.format("Storage size = %d", khlStorage.size()));
        final Date min = new GregorianCalendar(2016, Calendar.AUGUST, 1).getTime();
        final Date max = new GregorianCalendar(2016, Calendar.OCTOBER, 31).getTime();
//        final Date min = new GregorianCalendar(2016, Calendar.OCTOBER, 1).getTime();
//        final Date max = new GregorianCalendar(2016, Calendar.OCTOBER, 1).getTime();
        final KHLStorage subStorage = khlStorage.getSubStorageFiltredByDate(min, max);
        System.out.println(String.format("Sub storage size = %d", subStorage.size()));
        final Date queryMin = new GregorianCalendar(2016, Calendar.NOVEMBER, 1).getTime();
        final Date queryMax = new GregorianCalendar(2016, Calendar.NOVEMBER, 5).getTime();
        final KHLStorage queryStorage = khlStorage.getQueryStorageFiltredByDate(queryMin, queryMax);
        System.out.println(String.format("Query storage size = %d", queryStorage.size()));
        final KHLSolver solver = new KHLSolver(subStorage, queryStorage);
        solver.solve();
        System.out.println(String.format("Result list size = %d", solver.getResultList().size()));
        System.out.println(String.format("Done %s", KHLProcessor.class.getSimpleName()));
    }
}
