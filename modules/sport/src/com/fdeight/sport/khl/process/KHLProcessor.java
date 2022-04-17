package com.fdeight.sport.khl.process;

import com.fdeight.sport.khl.data.KHLMatchInfo;
import com.fdeight.sport.khl.parsers.KHLRBParser;
import com.fdeight.sport.khl.data.KHLStorage;
import com.fdeight.sport.khl.solvers.KHLSolver02;
import com.fdeight.sport.khl.solvers.KHLSolver03;
import com.fdeight.sport.khl.solvers.KHLSolver04;
import com.fdeight.sport.khl.solvers.KHLSolver05;
import com.fdeight.sport.parsers.TxtParser;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class KHLProcessor {
    public static void main(final String[] args) throws IOException, ParseException {
        System.out.println(String.format("Start %s", KHLProcessor.class.getSimpleName()));
        final KHLStorage khlStorage = new KHLStorage();
        final TxtParser parser = new KHLRBParser(khlStorage);
        final File file = new File("txt_rb_v03");
        //final File file = new File("2016-2017_txt_rb_v03");
        parser.parse(file);
        khlStorage.sort();
        System.out.println(String.format("Storage size = %d", khlStorage.size()));
        for (int year = 2014; year < 2022; year++) {
            System.out.println(String.format("Year: %d", year));
            final Date min = new GregorianCalendar(year, Calendar.AUGUST, 1).getTime();
            final Date max = new GregorianCalendar(year, Calendar.NOVEMBER, 30).getTime();
            final KHLStorage subStorage = khlStorage.getSubStorageFiltredByDate(min, max);
            System.out.println(String.format("Sub storage size = %d", subStorage.size()));
            final Date queryMin = new GregorianCalendar(year, Calendar.DECEMBER, 1).getTime();
            final Date queryMax = new GregorianCalendar(year, Calendar.DECEMBER, 10).getTime();
            final KHLStorage queryStorage = khlStorage.getQueryStorageFiltredByDate(queryMin, queryMax);
            System.out.println(String.format("Query storage size = %d", queryStorage.size()));

            final KHLStorage testStorage = khlStorage.getSubStorageFiltredByStorage(queryStorage);

            final KHLSolver02 solver02 = new KHLSolver02(subStorage, queryStorage);
            solver02.solve();
            final List<KHLMatchInfo> resultList02 = solver02.getResultList();
            System.out.println(String.format("Result 02: %s", resultList02));
            System.out.println(String.format("Test: %s", testStorage));
            System.out.println(String.format("Metric 02: %s", testStorage.compare(resultList02)));

            final KHLSolver03 solver03 = new KHLSolver03(108, queryStorage);
            solver03.solve();
            final List<KHLMatchInfo> resultList03 = solver03.getResultList();
            System.out.println(String.format("Result 03: %s", resultList03));
            System.out.println(String.format("Test: %s", testStorage));
            System.out.println(String.format("Metric 03: %s", testStorage.compare(resultList03)));

            final KHLSolver04 solver04 = new KHLSolver04(100, subStorage, queryStorage);
            solver04.solve();
            final List<KHLMatchInfo> resultList04 = solver04.getResultList();
            System.out.println(String.format("Result 04: %s", resultList04));
            System.out.println(String.format("Test: %s", testStorage));
            System.out.println(String.format("Metric 04: %s", testStorage.compare(resultList04)));

            final KHLSolver05 solver05 = new KHLSolver05(subStorage, queryStorage,
                    new double[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
            solver05.solve();
            final List<KHLMatchInfo> resultList05 = solver05.getResultList();
            System.out.println(String.format("Result 05: %s", resultList05));
            System.out.println(String.format("Test: %s", testStorage));
            System.out.println(String.format("Metric 05: %s", testStorage.compare(resultList05)));
        }

        System.out.println(String.format("Done %s", KHLProcessor.class.getSimpleName()));
    }
}
