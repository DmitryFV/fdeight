package com.fdeight.sport.khl.process;

import com.fdeight.sport.khl.data.KHLMatchInfo;
import com.fdeight.sport.khl.data.KHLMetric;
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
import java.util.*;

public class KHLProcessor {
    private static final double MIN_WEIGHT = 0;
    private static final double MAX_WEIGHT = 1;
    private static final double STEP_WEIGHT = 0.5;
    private static final int COUNT_LEVELS_05 = 10;

    private static class Result05 {
        private double[] weights;
        private double result;
    }

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
        final double[] weights05Full = computeWeights05(khlStorage, startYear05, endYear05);
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

            final KHLSolver05 solver05w = new KHLSolver05(subStorage, queryStorage, weights05Full);
            solver05w.solve();
            final List<KHLMatchInfo> resultList05w = solver05w.getResultList();
            System.out.println(String.format("Result 05w: %s", resultList05w));
            System.out.println(String.format("Test: %s", testStorage));
            System.out.println(String.format("Metric 05w: %s", testStorage.compare(resultList05w)));
        }
        System.out.println(String.format("Done %s", KHLProcessor.class.getSimpleName()));
    }

    private static double[] computeWeights05(final KHLStorage khlStorage, final int startYear, final int endYear) {
        final long startTime = System.currentTimeMillis();
        System.out.println("Start solve weights 05");
        // Если до конца считать долго, то используем значение меньше длины.
        final double[] weights05 = new double[COUNT_LEVELS_05];
        final Result05 best = new Result05();
        compare05(khlStorage, startYear, endYear, weights05, 0, best);
        System.out.println(String.format(Locale.US, "Done solve 05: %s, %5.3f",
                Arrays.toString(best.weights), best.result));
        System.out.println(String.format("Time solve weights 05: %d ms", System.currentTimeMillis() - startTime));
        return createWeights05Full(best.weights);
    }

    private static void compare05(final KHLStorage khlStorage, final int startYear, final int endYear,
                                  final double[] weights05, final int level, final Result05 best) {
        if (level < 4) {
            // Оптимизация расчетов, первые несколько весов всегда в максимуме.
            weights05[level] = MAX_WEIGHT;
            compare05(khlStorage, startYear, endYear, weights05, level + 1, best);
            return;
        }
        if (level < weights05.length) {
            double weight = MIN_WEIGHT;
            while (weight <= MAX_WEIGHT + 1e-9) {
                weights05[level] = weight;
                compare05(khlStorage, startYear, endYear, weights05, level + 1, best);
                weight += STEP_WEIGHT;
            }
            return;
        }
        double result = 0;
        for (int year = startYear; year <= endYear; year++) {
            final Date min = new GregorianCalendar(year, Calendar.AUGUST, 1).getTime();
            final Date max = new GregorianCalendar(year, Calendar.NOVEMBER, 30).getTime();
            final KHLStorage subStorage = khlStorage.getSubStorageFiltredByDate(min, max);
            final Date queryMin = new GregorianCalendar(year, Calendar.DECEMBER, 1).getTime();
            final Date queryMax = new GregorianCalendar(year, Calendar.DECEMBER, 10).getTime();
            final KHLStorage queryStorage = khlStorage.getQueryStorageFiltredByDate(queryMin, queryMax);

            final KHLStorage testStorage = khlStorage.getSubStorageFiltredByStorage(queryStorage);

            final KHLSolver05 solver05 = new KHLSolver05(subStorage, queryStorage, createWeights05Full(weights05));
            solver05.solve();
            final List<KHLMatchInfo> resultList05 = solver05.getResultList();
            final KHLMetric metric = testStorage.compare(resultList05);
            result += metric.getResult();
        }
        result /= (endYear - startYear + 1);
        if (best.result < result) {
            best.result = result;
            best.weights = weights05.clone();
        }
    }

    private static double[] createWeights05Full(final double[] weights05) {
        if (weights05.length == COUNT_LEVELS_05) {
            return weights05;
        }
        final double[] weights05Full = new double[COUNT_LEVELS_05];
        Arrays.fill(weights05Full, 0);
        System.arraycopy(weights05, 0, weights05Full, 0, weights05.length);
        return weights05Full;
    }
}
