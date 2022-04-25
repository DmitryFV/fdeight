package com.fdeight.sport.khl.process;

import com.fdeight.sport.khl.data.KHLMatchInfo;
import com.fdeight.sport.khl.parsers.KHLRBParser;
import com.fdeight.sport.khl.data.KHLStorage;
import com.fdeight.sport.khl.solvers.*;
import com.fdeight.sport.parsers.TxtParser;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

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
        final int startYear05 = 2014;
        final int endYear05 = 2016;
        final double[] weights05Full = KHLSolver05.computeWeights(khlStorage, startYear05, endYear05);
        System.out.println(String.format(Locale.US, "Weights 05 full: %s",
                Arrays.toString(weights05Full)));
        for (int year = endYear05 + 1; year < 2022; year++) {
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
            System.out.println(String.format("Test: %s", testStorage));

            final KHLSolver02 solver02 = new KHLSolver02(subStorage, queryStorage);
            solver02.solve();
            final List<KHLMatchInfo> resultList02 = solver02.getResultList();
            //System.out.println(String.format("Result 02: %s", resultList02));
            System.out.println(String.format("Metric 02: %s", testStorage.compare(resultList02)));

            final KHLSolver03 solver03 = new KHLSolver03(108, queryStorage);
            solver03.solve();
            final List<KHLMatchInfo> resultList03 = solver03.getResultList();
            //System.out.println(String.format("Result 03: %s", resultList03));
            System.out.println(String.format("Metric 03: %s", testStorage.compare(resultList03)));

            final KHLSolver04 solver04 = new KHLSolver04(100, subStorage, queryStorage);
            solver04.solve();
            final List<KHLMatchInfo> resultList04 = solver04.getResultList();
            //System.out.println(String.format("Result 04: %s", resultList04));
            System.out.println(String.format("Metric 04: %s", testStorage.compare(resultList04)));

            final KHLSolver05 solver05 = new KHLSolver05(subStorage, queryStorage, weights05Full);
            solver05.solve();
            final List<KHLMatchInfo> resultList05w = solver05.getResultList();
            //System.out.println(String.format("Result 05: %s", resultList05w));
            System.out.println(String.format("Metric 05: %s", testStorage.compare(resultList05w)));

            final KHLSolver06 solver06 = new KHLSolver06(subStorage, queryStorage);
            solver06.solve();
            final List<KHLMatchInfo> resultList06 = solver06.getResultList();
            //System.out.println(String.format("Result 06: %s", resultList06));
            System.out.println(String.format("Metric 06: %s", testStorage.compare(resultList06)));

            final KHLSolver08 solver08 = new KHLSolver08(subStorage, queryStorage);
            solver08.solve();
            final List<KHLMatchInfo> resultList08 = solver08.getResultList();
            //System.out.println(String.format("Result 08: %s", resultList08));
            System.out.println(String.format("Metric 08: %s", testStorage.compare(resultList08)));
            
            final List<KHLSolver> khlSolvers = new ArrayList<>();
            khlSolvers.add(solver05);
            khlSolvers.add(solver06);
            final KHLSolver07 solver07s2 = new KHLSolver07(queryStorage, khlSolvers);
            solver07s2.solve();
            final List<KHLMatchInfo> resultList07s2 = solver07s2.getResultList();
            //System.out.println(String.format("Result 07s2(05,06): %s", resultList07s2));
            System.out.println(String.format("Metric 07s2(05,06): %s", testStorage.compare(resultList07s2, false)));
            khlSolvers.add(solver02);
            final KHLSolver07 solver07s3 = new KHLSolver07(queryStorage, khlSolvers);
            solver07s3.solve();
            final List<KHLMatchInfo> resultList07s3 = solver07s3.getResultList();
            //System.out.println(String.format("Result 07s3(05,06,02): %s", resultList07s3));
            System.out.println(String.format("Metric 07s3(05,06,02): %s", testStorage.compare(resultList07s3, false)));

            final List<KHLSolver> khlSolvers0508 = new ArrayList<>();
            khlSolvers0508.add(solver05);
            khlSolvers0508.add(solver08);
            final KHLSolver07 solver07s0508 = new KHLSolver07(queryStorage, khlSolvers0508);
            solver07s0508.solve();
            final List<KHLMatchInfo> resultList07s0508 = solver07s0508.getResultList();
            //System.out.println(String.format("Result 07s(05,08): %s", resultList07s0508));
            System.out.println(String.format("Metric 07s(05,08): %s", testStorage.compare(resultList07s0508, false)));
        }
        System.out.println(String.format("Done %s", KHLProcessor.class.getSimpleName()));
    }
}
