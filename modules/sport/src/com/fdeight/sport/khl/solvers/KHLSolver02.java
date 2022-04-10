package com.fdeight.sport.khl.solvers;

import com.fdeight.sport.khl.data.KHLMatchInfo;
import com.fdeight.sport.khl.data.KHLStorage;
import com.fdeight.sport.utils.Utils;

import java.util.*;

public class KHLSolver02 {
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
        public final double deltaHostWins = 0.05;
        public final double deltaGuestWins = 0.05;
        public final double deltaHostDraws = 0.02;
        public final double deltaGuestDraws = 0.02;
        public final double deltaScore = 1;
        public final double deltaWins = 0.05;
    }

    private enum Variants {
        VARIANT0,
        VARIANT1,
        VARIANT2,
        //
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

    public KHLSolver02(final KHLStorage initStorage, final KHLStorage queryStorage) {
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
            Utils.impossibleIllegalState("addWins");
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
            Utils.impossibleIllegalState("addDraws");
        }
    }

    private void computeResult() {
        checkBeforeComputeResult();
        final Stats totalStats = teamStats.get(TOTAL);
        final double totalHostWinsAverage = totalStats.hostWins.computeAverage();
        final double totalGuestWinsAverage = totalStats.guestWins.computeAverage();
        final double totalHostDrawsAverage = totalStats.hostDraws.computeAverage();
        final double totalGuestDrawsAverage = totalStats.guestDraws.computeAverage();
        final List<KHLMatchInfo> queryList = queryStorage.getUnmodifiableList();
        for (final KHLMatchInfo queryInfo : queryList) {
            final Stats hostStats = Objects.requireNonNull(teamStats.get(queryInfo.firstTeam),
                    "hostStats undefined");
            final Stats guestStats = Objects.requireNonNull(teamStats.get(queryInfo.secondTeam),
                    "guestStats undefined");
            final Average hostScoreAverage = hostStats.hostScore.computeAverage();
            final Average guestScoreAverage = hostStats.guestScore.computeAverage();
            final double hostWinsAverage = hostStats.hostWins.computeAverage();
            final double guestWinsAverage = guestStats.guestWins.computeAverage();
            final double hostDrawsAverage = hostStats.hostDraws.computeAverage();
            final double guestDrawsAverage = guestStats.guestDraws.computeAverage();
            final int first = Utils.round(hostScoreAverage.first);
            final int second = Utils.round(guestScoreAverage.second);

            // Хозяева в среднем забивают против своих гостей.
            // VARIANT1 - хозяева больше, VARIANT1 - хозяева меньше, VARIANT2 - хозяева забивают столько же.
            final Variants hostScoreAverageFirstVSHostScoreAverageSecondLevel0;
            if (hostScoreAverage.first > hostScoreAverage.second + settings.deltaHostScore) {
                hostScoreAverageFirstVSHostScoreAverageSecondLevel0 = Variants.VARIANT0;
            } else if (hostScoreAverage.first + settings.deltaHostScore < hostScoreAverage.second) {
                hostScoreAverageFirstVSHostScoreAverageSecondLevel0 = Variants.VARIANT1;
            } else {
                hostScoreAverageFirstVSHostScoreAverageSecondLevel0 = Variants.VARIANT2;
            }

            // Гости в среднем забивают против своих хозяев.
            // VARIANT1 - гости больше, VARIANT1 - гости меньше, VARIANT2 - гости забивают столько же.
            final Variants guestScoreAverageSecondVSGuestScoreAverageFirstLevel1;
            if (guestScoreAverage.second > guestScoreAverage.first + settings.deltaGuestScore) {
                guestScoreAverageSecondVSGuestScoreAverageFirstLevel1 = Variants.VARIANT0;
            } else if (guestScoreAverage.second + settings.deltaGuestScore < guestScoreAverage.first) {
                guestScoreAverageSecondVSGuestScoreAverageFirstLevel1 = Variants.VARIANT1;
            } else {
                guestScoreAverageSecondVSGuestScoreAverageFirstLevel1 = Variants.VARIANT2;
            }

            // Хозяева в среднем выигрывают против total хозяев.
            // VARIANT1 - хозяева чаще total, VARIANT1 - хозяева реже total, VARIANT2 - столько же.
            final Variants hostWinsAverageVSTotalHostWinsAverageLevel2;
            if (hostWinsAverage > totalHostWinsAverage + settings.deltaHostWins) {
                hostWinsAverageVSTotalHostWinsAverageLevel2 = Variants.VARIANT0;
            } else if (hostWinsAverage + settings.deltaHostWins < totalHostWinsAverage) {
                hostWinsAverageVSTotalHostWinsAverageLevel2 = Variants.VARIANT1;
            } else {
                hostWinsAverageVSTotalHostWinsAverageLevel2 = Variants.VARIANT2;
            }

            // Гости в среднем выигрывают против total гостей.
            // VARIANT1 - гости чаще total, VARIANT1 - гости реже total, VARIANT2 - столько же.
            final Variants guestWinsAverageVSTotalGuestWinsAverageLevel3;
            if (guestWinsAverage > totalGuestWinsAverage + settings.deltaGuestWins) {
                guestWinsAverageVSTotalGuestWinsAverageLevel3 = Variants.VARIANT0;
            } else if (guestWinsAverage + settings.deltaGuestWins < totalGuestWinsAverage) {
                guestWinsAverageVSTotalGuestWinsAverageLevel3 = Variants.VARIANT1;
            } else {
                guestWinsAverageVSTotalGuestWinsAverageLevel3 = Variants.VARIANT2;
            }

            // Хозяева в среднем забивают против гости в среднем забивают.
            // VARIANT1 - хозяева больше, VARIANT1 - хозяева меньше, VARIANT2 - столько же.
            final Variants hostScoreAverageFirstVSGuestScoreAverageSecondLevel4;
            if (hostScoreAverage.first > guestScoreAverage.second + settings.deltaScore || first > second + 1) {
                hostScoreAverageFirstVSGuestScoreAverageSecondLevel4 = Variants.VARIANT0;
            } else if (hostScoreAverage.first + settings.deltaScore < guestScoreAverage.second || first + 1 < second) {
                hostScoreAverageFirstVSGuestScoreAverageSecondLevel4 = Variants.VARIANT1;
            } else {
                hostScoreAverageFirstVSGuestScoreAverageSecondLevel4 = Variants.VARIANT2;
            }

            // Хозяева в среднем играют вничью против total хозяев.
            // VARIANT1 - хозяева чаще total, VARIANT1 - хозяева реже total, VARIANT2 - столько же.
            final Variants hostDrawsAverageVSTotalHostDrawsAverageLevel5;
            if (hostDrawsAverage > totalHostDrawsAverage + settings.deltaHostDraws) {
                hostDrawsAverageVSTotalHostDrawsAverageLevel5 = Variants.VARIANT0;
            } else if (hostDrawsAverage + settings.deltaHostDraws < totalHostDrawsAverage) {
                hostDrawsAverageVSTotalHostDrawsAverageLevel5 = Variants.VARIANT1;
            } else {
                hostDrawsAverageVSTotalHostDrawsAverageLevel5 = Variants.VARIANT2;
            }

            // Гости в среднем играют вничью против total гостей.
            // VARIANT1 - гости чаще total, VARIANT1 - гости реже total, VARIANT2 - столько же.
            final Variants guestDrawsAverageVSTotalGuestDrawsAverageLevel6;
            if (guestDrawsAverage > totalGuestDrawsAverage + settings.deltaGuestDraws) {
                guestDrawsAverageVSTotalGuestDrawsAverageLevel6 = Variants.VARIANT0;
            } else if (guestDrawsAverage + settings.deltaGuestDraws < totalGuestDrawsAverage) {
                guestDrawsAverageVSTotalGuestDrawsAverageLevel6 = Variants.VARIANT1;
            } else {
                guestDrawsAverageVSTotalGuestDrawsAverageLevel6 = Variants.VARIANT2;
            }

            // Хозяева в среднем выигрывают против гости в среднем выигрывают.
            // VARIANT1 - хозяева чаще, VARIANT1 - хозяева реже, VARIANT2 - столько же.
            final Variants hostWinsAverageVSGuestWinsAverageLevel7;
            if (hostWinsAverage > guestWinsAverage + settings.deltaWins) {
                hostWinsAverageVSGuestWinsAverageLevel7 = Variants.VARIANT0;
            } else if (hostWinsAverage + settings.deltaWins < guestWinsAverage) {
                hostWinsAverageVSGuestWinsAverageLevel7 = Variants.VARIANT1;
            } else {
                hostWinsAverageVSGuestWinsAverageLevel7 = Variants.VARIANT2;
            }

            int hostWinInPeriods = 0;
            int hostWinInOvertimeOrShootouts = 0;
            int guestWinInPeriods = 0;
            int guestWinInOvertimeOrShootouts = 0;

            switch (hostScoreAverageFirstVSHostScoreAverageSecondLevel0) {
                case VARIANT0:
                    hostWinInPeriods++;
                    hostWinInOvertimeOrShootouts++;
                    break;
                case VARIANT1:
                    guestWinInPeriods++;
                    guestWinInOvertimeOrShootouts++;
                    break;
                case VARIANT2:
                    break;
                default:
                    Utils.impossibleIllegalState("hostScoreAverageFirstVSHostScoreAverageSecondLevel0");
            }

            switch (guestScoreAverageSecondVSGuestScoreAverageFirstLevel1) {
                case VARIANT0:
                    guestWinInPeriods++;
                    guestWinInOvertimeOrShootouts++;
                    break;
                case VARIANT1:
                    hostWinInPeriods++;
                    hostWinInOvertimeOrShootouts++;
                    break;
                case VARIANT2:
                    break;
                default:
                    Utils.impossibleIllegalState("hostScoreAverageFirstVSHostScoreAverageSecondLevel0");
            }

            switch (hostWinsAverageVSTotalHostWinsAverageLevel2) {
                case VARIANT0:
                    hostWinInPeriods++;
                    hostWinInOvertimeOrShootouts++;
                    break;
                case VARIANT1:
                    guestWinInPeriods++;
                    guestWinInOvertimeOrShootouts++;
                    break;
                case VARIANT2:
                    break;
                default:
                    Utils.impossibleIllegalState("hostScoreAverageFirstVSHostScoreAverageSecondLevel0");
            }

            switch (guestWinsAverageVSTotalGuestWinsAverageLevel3) {
                case VARIANT0:
                    guestWinInPeriods++;
                    guestWinInOvertimeOrShootouts++;
                    break;
                case VARIANT1:
                    hostWinInPeriods++;
                    hostWinInOvertimeOrShootouts++;
                    break;
                case VARIANT2:
                    break;
                default:
                    Utils.impossibleIllegalState("hostScoreAverageFirstVSHostScoreAverageSecondLevel0");
            }

            switch (hostScoreAverageFirstVSGuestScoreAverageSecondLevel4) {
                case VARIANT0:
                    hostWinInPeriods++;
                    hostWinInOvertimeOrShootouts++;
                    break;
                case VARIANT1:
                    guestWinInPeriods++;
                    guestWinInOvertimeOrShootouts++;
                    break;
                case VARIANT2:
                    break;
                default:
                    Utils.impossibleIllegalState("hostScoreAverageFirstVSHostScoreAverageSecondLevel0");
            }

            switch (hostDrawsAverageVSTotalHostDrawsAverageLevel5) {
                case VARIANT0:
                    hostWinInOvertimeOrShootouts++;
                    guestWinInOvertimeOrShootouts++;
                    break;
                case VARIANT1:
                    hostWinInPeriods++;
                    guestWinInPeriods++;
                    break;
                case VARIANT2:
                    break;
                default:
                    Utils.impossibleIllegalState("hostScoreAverageFirstVSHostScoreAverageSecondLevel0");
            }

            switch (guestDrawsAverageVSTotalGuestDrawsAverageLevel6) {
                case VARIANT0:
                    hostWinInOvertimeOrShootouts++;
                    guestWinInOvertimeOrShootouts++;
                    break;
                case VARIANT1:
                    hostWinInPeriods++;
                    guestWinInPeriods++;
                case VARIANT2:
                    break;
                default:
                    Utils.impossibleIllegalState("hostScoreAverageFirstVSHostScoreAverageSecondLevel0");
            }

            switch (hostWinsAverageVSGuestWinsAverageLevel7) {
                case VARIANT0:
                    hostWinInPeriods++;
                    hostWinInOvertimeOrShootouts++;
                    break;
                case VARIANT1:
                    guestWinInPeriods++;
                    guestWinInOvertimeOrShootouts++;
                    break;
                case VARIANT2:
                    break;
                default:
                    Utils.impossibleIllegalState("hostScoreAverageFirstVSHostScoreAverageSecondLevel0");
            }

            final int hostWin = hostWinInPeriods + hostWinInOvertimeOrShootouts;
            final int guestWin = guestWinInPeriods + guestWinInOvertimeOrShootouts;
            final int winInPeriods = hostWinInPeriods + guestWinInPeriods;
            int draw = hostWinInOvertimeOrShootouts + guestWinInOvertimeOrShootouts;

            final boolean isHostWin;
            if (hostWin > guestWin) {
                isHostWin = true;
            } else if (hostWin < guestWin) {
                isHostWin = false;
            } else {
                isHostWin = hostWinsAverageVSGuestWinsAverageLevel7 != Variants.VARIANT1;
                draw++;
            }

            final boolean isDraw;
            if (winInPeriods > draw) {
                isDraw = false;
            } else if (winInPeriods < draw) {
                isDraw = true;
            } else {
                isDraw = hostDrawsAverageVSTotalHostDrawsAverageLevel5 == Variants.VARIANT0
                        && guestDrawsAverageVSTotalGuestDrawsAverageLevel6 == Variants.VARIANT0;
            }

            final int min = Math.min(first, second);
            final KHLMatchInfo resultInfo;
            if (isHostWin) {
                if (isDraw) {
                    // todo сделать ничьи по периодам
                    if (first > second) {
                        resultInfo = new KHLMatchInfo(queryInfo, new KHLMatchInfo.Score(first, second));
                    } else {
                        resultInfo = new KHLMatchInfo(queryInfo, new KHLMatchInfo.Score(min + 1, min));
                    }
                } else {
                    // todo сделать победой хозяев в основное время
                    if (first > second) {
                        resultInfo = new KHLMatchInfo(queryInfo, new KHLMatchInfo.Score(first, second));
                    } else {
                        resultInfo = new KHLMatchInfo(queryInfo, new KHLMatchInfo.Score(min + 1, min));
                    }
                }
            } else {
                if (isDraw) {
                    // todo сделать ничьи по периодам
                    if (first < second) {
                        resultInfo = new KHLMatchInfo(queryInfo, new KHLMatchInfo.Score(first, second));
                    } else {
                        resultInfo = new KHLMatchInfo(queryInfo, new KHLMatchInfo.Score(min, min + 1));
                    }
                } else {
                    // todo сделать победой хозяев в основное время
                    if (first < second) {
                        resultInfo = new KHLMatchInfo(queryInfo, new KHLMatchInfo.Score(first, second));
                    } else {
                        resultInfo = new KHLMatchInfo(queryInfo, new KHLMatchInfo.Score(min, min + 1));
                    }
                }
            }

            if (resultInfo == null) {
                Utils.impossibleIllegalState("resultInfo is undefined");
            }
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
