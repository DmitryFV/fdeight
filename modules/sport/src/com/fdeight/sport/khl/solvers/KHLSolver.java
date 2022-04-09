package com.fdeight.sport.khl.solvers;

import com.fdeight.sport.khl.data.KHLMatchInfo;
import com.fdeight.sport.khl.data.KHLStorage;
import com.fdeight.sport.utils.Utils;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class KHLSolver {
    private static class SolverScore {
        public int count;
        public int first;
        public int second;

        public void add(final KHLMatchInfo.Score score) {
            count++;
            first += score.first;
            second += score.second;
        }
    }

    private static class Stat {
        public int count;
        public int value;

        public void add(final int value) {
            count++;
            this.value += value;
        }
    }

    private static class Stats {
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
        /**
         * Победы на своей площадке.
         */
        private final Stat hostWins;
        /**
         * Победы в гостях.
         */
        private final Stat guestWins;
        /**
         * Ничьи на своей площадке.
         */
        private final Stat hostDraws;
        /**
         * Ничьи в гостях.
         */
        private final Stat guestDraws;

        public Stats() {
            this.hostScore = new SolverScore();
            this.guestScore = new SolverScore();
            hostScorePeriods = createScorePeriods();
            guestScorePeriods = createScorePeriods();
            hostWins = new Stat();
            guestWins = new Stat();
            hostDraws = new Stat();
            guestDraws = new Stat();
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
        checkHonest();
    }

    /**
     * Проверить, что запрос честный, т.е. вся информация о начальных даннных находится
     * в прошлом относительно каждой записи из запроса.
     */
    private void checkHonest() {
        final List<KHLMatchInfo> queryList = queryStorage.getUnmodifiableList();
        final List<KHLMatchInfo> initList = initStorage.getUnmodifiableList();
        for (final KHLMatchInfo queryInfo : queryList) {
            for (final KHLMatchInfo initInfo : initList) {
                if (initInfo.date.getTime() >= queryInfo.date.getTime()) {
                    throw new IllegalStateException(String.format("Init date (%s) >= query date (%s)",
                            initInfo.date, queryInfo.date));
                }
            }
        }
    }

    public void solve() {
        final List<KHLMatchInfo> initList = initStorage.getUnmodifiableList();
        for (final KHLMatchInfo info : initList) {
            final Stats hostStats = teamsStats.computeIfAbsent(info.firstTeam, key -> new Stats());
            hostStats.hostScore.add(info.score);
            for (int i = 0; i < info.scorePeriods.size(); i++) {
                hostStats.hostScorePeriods[i].add(info.scorePeriods.get(i));
            }
            final Stats guestStats = teamsStats.computeIfAbsent(info.secondTeam, key -> new Stats());
            guestStats.guestScore.add(info.score);
            for (int i = 0; i < info.scorePeriods.size(); i++) {
                guestStats.guestScorePeriods[i].add(info.scorePeriods.get(i));
            }
            if (info.score.first > info.score.second) {
                hostStats.hostWins.add(1);
                guestStats.guestWins.add(0);
            } else if (info.score.first < info.score.second) {
                hostStats.hostWins.add(0);
                guestStats.guestWins.add(1);
            } else {
                Utils.impossibleIllegalState();
            }
            if (info.scorePeriods.size() > KHLMatchInfo.PLAIN_PERIODS_COUNT) {
                hostStats.hostDraws.add(1);
                guestStats.guestDraws.add(1);
            } else if (info.scorePeriods.size() == KHLMatchInfo.PLAIN_PERIODS_COUNT) {
                hostStats.hostDraws.add(0);
                guestStats.guestDraws.add(0);
            } else {
                Utils.impossibleIllegalState();
            }
        }
    }
}
