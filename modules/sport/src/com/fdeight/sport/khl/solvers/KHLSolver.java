package com.fdeight.sport.khl.solvers;

import com.fdeight.sport.khl.data.KHLMatchInfo;
import com.fdeight.sport.khl.data.KHLStorage;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class KHLSolver {
    public static class SolverScore {
        public int count;
        public int first;
        public int second;

        public void add(final int toFirst, final int toSecond) {
            count++;
            first += toFirst;
            second += toSecond;
        }
    }

    public static class Stats {
        /**
         * {@link SolverScore#first} здесь - набраные очки hero team, она хозяин площадки, ее очки первые.
         */
        public final SolverScore hostScore;
        /**
         * {@link SolverScore#second} здесь - набраные очки hero team, она играет в гостях, ее очки вторые.
         */
        public final SolverScore guestScore;
        /**
         * Счет по периодам, аналогично {@link #hostScore}.
         * Длина массива равна 5: 3 периода, овертайм (все овертаймы вместе), буллиты.
         */
        private final SolverScore[] hostScorePeriods;
        /**
         * Счет по периодам, аналогично {@link #guestScore}.
         * Длина массива равна 5 ({@link KHLMatchInfo#WITH_SHOOTOUTS_PERIODS_COUNT}):
         * 3 периода, овертайм (все овертаймы вместе), буллиты.
         */
        private final SolverScore[] guestScorePeriods;

        public Stats() {
            this.hostScore = new SolverScore();
            this.guestScore = new SolverScore();
            hostScorePeriods = createScorePeriods();
            guestScorePeriods = createScorePeriods();
        }

        private SolverScore[] createScorePeriods() {
            final SolverScore[] scorePeriods;
            scorePeriods = new SolverScore[KHLMatchInfo.WITH_SHOOTOUTS_PERIODS_COUNT];
            for (int i = 0; i < scorePeriods.length; i++) {
                scorePeriods[i] = new SolverScore();
            }
            return scorePeriods;
        }
    }

    /**
     * Хранилище информации о матчах, которые являются начальными даннными, по которым собираем статистику, обучаемся.
     */
    private final KHLStorage initStorage;
    /**
     * Хранилище информации о матчах, по которым идет запрос на прогноз результатов.
     * В информации о матчах в этом хранилище отсутствуют данные о счете.
     */
    private final KHLStorage queryStorage;
    /**
     * Хранилище информации с прогнозом по матчам, по которым был запрос на прогноз результатов.
     */
    private final KHLStorage resultStorage;

    private final Map<String, Stats> teamsStats;

    public KHLSolver(final KHLStorage initStorage, final KHLStorage queryStorage) {
        this.initStorage = initStorage;
        this.queryStorage = queryStorage;
        resultStorage = new KHLStorage();
        teamsStats = new TreeMap<>();
    }

    public void solve() {
        final List<KHLMatchInfo> list = initStorage.getUnmodifiableList();
        for (final KHLMatchInfo info : list) {

        }
    }
}
