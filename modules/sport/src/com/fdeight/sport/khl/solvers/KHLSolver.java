package com.fdeight.sport.khl.solvers;

import com.fdeight.sport.khl.data.KHLMatchInfo;
import com.fdeight.sport.khl.data.KHLStorage;
import com.fdeight.sport.utils.Utils;

import java.util.*;

public class KHLSolver {
    private static final String TOTAL = "Total";

    private static class StatScore {
        public int count;
        public int first;
        public int second;

        public void add(final KHLMatchInfo.Score score) {
            count++;
            first += score.first;
            second += score.second;
        }

        public Average computeAverage() {
            Utils.checkNotEquals(count, 0, () -> String.format("%s.computeAverage(), count",
                    StatScore.class.getSimpleName()));
            return new Average(first / (double) count, second / (double) count);
        }
    }

    private static class Average {
        public final double first;
        public final double second;

        public Average(final double first, final double second) {
            this.first = first;
            this.second = second;
        }
    }

    private static class Stat {
        public int count;
        public int value;

        public void add(final int value) {
            count++;
            this.value += value;
        }

        public double computeAverage() {
            Utils.checkNotEquals(count, 0, () -> String.format("%s.computeAverage(), count",
                    Stat.class.getSimpleName()));
            return value / (double) count;
        }
    }

    private static class Stats {
        /**
         * {@link StatScore#first} здесь - набраный счет (голы) hero team, она хозяин площадки, ее голы первые.
         */
        public final StatScore hostScore;
        /**
         * {@link StatScore#second} здесь - набраные счет (голы) hero team, она играет в гостях, ее голы вторые.
         */
        public final StatScore guestScore;
        /**
         * Счет по периодам, аналогично {@link #hostScore}.
         * Длина массива равна 5: 3 периода, овертайм (все овертаймы вместе), буллиты.
         */
        private final StatScore[] hostScorePeriods;
        /**
         * Счет по периодам, аналогично {@link #guestScore}.
         * Длина массива равна 5 ({@link KHLMatchInfo#WITH_SHOOTOUTS_PERIODS_COUNT}):
         * 3 периода, овертайм (все овертаймы вместе), буллиты.
         */
        private final StatScore[] guestScorePeriods;
        /**
         * Победы на своей площадке.
         */
        private final Stat hostWins;
        /**
         * Победы в гостях.
         */
        private final Stat guestWins;
        /**
         * Ничьи в основное время на своей площадке.
         */
        private final Stat hostDraws;
        /**
         * Ничьи в основное время в гостях.
         */
        private final Stat guestDraws;

        public Stats() {
            this.hostScore = new StatScore();
            this.guestScore = new StatScore();
            hostScorePeriods = createScorePeriods();
            guestScorePeriods = createScorePeriods();
            hostWins = new Stat();
            guestWins = new Stat();
            hostDraws = new Stat();
            guestDraws = new Stat();
        }

        private StatScore[] createScorePeriods() {
            final StatScore[] scorePeriods;
            scorePeriods = new StatScore[KHLMatchInfo.WITH_SHOOTOUTS_PERIODS_COUNT];
            for (int i = 0; i < scorePeriods.length; i++) {
                scorePeriods[i] = new StatScore();
            }
            return scorePeriods;
        }
    }

    private static class Settings {
        public final double deltaHostScore = 0.1;
        public final double deltaGuestScore = 0.1;
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

    private final Map<String, Stats> teamStats;

    private final Settings settings;

    public KHLSolver(final KHLStorage initStorage, final KHLStorage queryStorage) {
        this.initStorage = initStorage;
        this.queryStorage = queryStorage;
        resultStorage = new KHLStorage();
        teamStats = new TreeMap<>();
        settings = new Settings();
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
        computeTeamStats();
        computeResult();
    }

    private void computeTeamStats() {
        checkBeforeComputeStats();
        final Stats totalStats = teamStats.computeIfAbsent(TOTAL, key -> new Stats());
        final List<KHLMatchInfo> initList = initStorage.getUnmodifiableList();
        for (final KHLMatchInfo info : initList) {
            final Stats hostStats = teamStats.computeIfAbsent(info.firstTeam, key -> new Stats());
            final Stats guestStats = teamStats.computeIfAbsent(info.secondTeam, key -> new Stats());
            addScore(info.score, hostStats, guestStats, totalStats);
            addScorePeriods(info.scorePeriods, hostStats, guestStats, totalStats);
            addWins(info.score, hostStats, guestStats, totalStats);
            addDraws(info, hostStats, guestStats, totalStats);
        }
    }

    private void checkBeforeComputeStats() {
        Utils.checkNotEquals(initStorage.size(), 0, () -> "initStorage.size()");
        Utils.checkEquals(teamStats.size(), 0, () -> "teamStats.size()");
    }

    private void addScore(final KHLMatchInfo.Score score, final Stats hostStats, final Stats guestStats,
                          final Stats totalStats) {
        hostStats.hostScore.add(score);
        totalStats.hostScore.add(score);
        guestStats.guestScore.add(score);
        totalStats.guestScore.add(score);
    }

    private void addScorePeriods(final List<KHLMatchInfo.Score> scorePeriods,
                                 final Stats hostStats, final Stats guestStats,
                                 final Stats totalStats) {
        for (int i = 0; i < scorePeriods.size(); i++) {
            hostStats.hostScorePeriods[i].add(scorePeriods.get(i));
            totalStats.hostScorePeriods[i].add(scorePeriods.get(i));
            guestStats.guestScorePeriods[i].add(scorePeriods.get(i));
            totalStats.guestScorePeriods[i].add(scorePeriods.get(i));
        }
    }

    private void addWins(final KHLMatchInfo.Score score, final Stats hostStats, final Stats guestStats,
                         final Stats totalStats) {
        if (score.first > score.second) {
            hostStats.hostWins.add(1);
            totalStats.hostWins.add(1);
            guestStats.guestWins.add(0);
            totalStats.guestWins.add(0);
        } else if (score.first < score.second) {
            hostStats.hostWins.add(0);
            totalStats.hostWins.add(0);
            guestStats.guestWins.add(1);
            totalStats.guestWins.add(1);
        } else {
            Utils.impossibleIllegalState();
        }
    }

    private void addDraws(final KHLMatchInfo info, final Stats hostStats, final Stats guestStats,
                          final Stats totalStats) {
        if (info.scorePeriods.size() > KHLMatchInfo.PLAIN_PERIODS_COUNT) {
            hostStats.hostDraws.add(1);
            totalStats.hostDraws.add(1);
            guestStats.guestDraws.add(1);
            totalStats.guestDraws.add(1);
        } else if (info.scorePeriods.size() == KHLMatchInfo.PLAIN_PERIODS_COUNT) {
            hostStats.hostDraws.add(0);
            totalStats.hostDraws.add(0);
            guestStats.guestDraws.add(0);
            totalStats.guestDraws.add(0);
        } else {
            Utils.impossibleIllegalState();
        }
    }

    @SuppressWarnings("DuplicateExpressions")
    private void computeResult() {
        checkBeforeComputeResult();
        final Stats totalStats = teamStats.get(TOTAL);
        final List<KHLMatchInfo> queryList = queryStorage.getUnmodifiableList();
        for (final KHLMatchInfo info : queryList) {
            final Stats hostStats = Objects.requireNonNull(teamStats.get(info.firstTeam),
                    "hostStats undefined");
            final Stats guestStats = Objects.requireNonNull(teamStats.get(info.secondTeam),
                    "guestStats undefined");
            final Average hostScoreAverage = hostStats.hostScore.computeAverage();
            final Average guestScoreAverage = hostStats.guestScore.computeAverage();
            final double hostWinsAverage = hostStats.hostWins.computeAverage();
            final double guestWinsAverage = guestStats.guestWins.computeAverage();
            final double hostDrawsAverage = hostStats.hostDraws.computeAverage();
            final double guestDrawsAverage = guestStats.guestDraws.computeAverage();
            if (hostScoreAverage.first > hostScoreAverage.second + settings.deltaHostScore) {
                // Хозяева обычно забивают больше гостей.
                if (guestScoreAverage.second > guestScoreAverage.first + settings.deltaGuestScore) {
                    // Гости обычно забивают больше хозяев.
                    // Сложный случай по счету.
                } else if (guestScoreAverage.second + settings.deltaGuestScore < guestScoreAverage.first) {
                    // Гости обычно забивают меньше хозяев.
                    // Простой случай по счету.
                } else {
                    // Гости обычно забивают примерно столько же, что и хозяева.
                    // Средний по сложности случай по счету.
                }
            } else if (hostScoreAverage.first + settings.deltaHostScore < hostScoreAverage.second) {
                // Хозяева обычно забивают меньше гостей.
                if (guestScoreAverage.second > guestScoreAverage.first + settings.deltaGuestScore) {
                    // Гости обычно забивают больше хозяев.
                    // Простой случай по счету.
                } else if (guestScoreAverage.second + settings.deltaGuestScore < guestScoreAverage.first) {
                    // Гости обычно забивают меньше хозяев.
                    // Сложный случай по счету.
                } else {
                    // Гости обычно забивают примерно столько же, что и хозяева.
                    // Средний по сложности случай по счету.
                }
            } else {
                // Хозяева обычно забивают примерно столько же, что и гости.
                if (guestScoreAverage.second > guestScoreAverage.first + settings.deltaGuestScore) {
                    // Гости обычно забивают больше хозяев.
                    // Средний по сложности случай по счету.
                } else if (guestScoreAverage.second + settings.deltaGuestScore < guestScoreAverage.first) {
                    // Гости обычно забивают меньше хозяев.
                    // Средний по сложности случай по счету.
                } else {
                    // Гости обычно забивают примерно столько же, что и хозяева.
                    // Простой случай по счету.
                }
            }
            final KHLMatchInfo resultInfo = info;
            resultStorage.add(resultInfo);
        }
    }

    private void checkBeforeComputeResult() {
        checkTeamStats();
        Utils.checkEquals(resultStorage.size(), 0, () -> "resultStorage.size()");
    }

    private void checkTeamStats() {
        Utils.checkNotEquals(teamStats.size(), 0, () -> "teamStats.size()");

        final Stats totalStats = Objects.requireNonNull(teamStats.get(TOTAL), "Total undefined");

        Utils.checkEquals(totalStats.hostWins.count, initStorage.size(), () -> "Total hostWins.count");
        Utils.checkEquals(totalStats.guestWins.count, initStorage.size(), () -> "Total guestWins.count");
        Utils.checkEquals(totalStats.hostWins.value + totalStats.guestWins.value, initStorage.size(),
                () -> "Sum of wins.value");
        Utils.checkInterval(totalStats.hostWins.value, 0, initStorage.size(), () -> "Total hostWins.value");

        Utils.checkEquals(totalStats.hostDraws.count, initStorage.size(), () -> "Total hostDraws.count");
        Utils.checkEquals(totalStats.guestDraws.count, initStorage.size(), () -> "Total guestDraws.count");
        Utils.checkEquals(totalStats.hostDraws.value, totalStats.guestDraws.value, () -> "Total draws values");
        Utils.checkInterval(totalStats.hostDraws.value, 0, initStorage.size(), () -> "Total hostDraws.value");

        for (final String key : teamStats.keySet()) {
            final Stats stats = teamStats.get(key);
            Utils.checkInterval(stats.hostWins.value, 0, stats.hostWins.count,
                    () -> String.format("%s hostWins.value", key));
            Utils.checkInterval(stats.guestWins.value, 0, stats.guestWins.count,
                    () -> String.format("%s guestWins.value", key));
            Utils.checkInterval(stats.hostDraws.value, 0, stats.hostDraws.count,
                    () -> String.format("%s hostDraws.value", key));
            Utils.checkInterval(stats.guestDraws.value, 0, stats.guestDraws.count,
                    () -> String.format("%s guestDraws.value", key));
        }
    }

    public List<KHLMatchInfo> getResultList() {
        checkBeforeGetResult();
        return resultStorage.getUnmodifiableList();
    }

    private void checkBeforeGetResult() {
        Utils.checkNotEquals(resultStorage.size(), 0, () -> "resultStorage.size()");
    }
}
