package com.fdeight.sport.khl.data;

import com.fdeight.sport.utils.Utils;

import java.util.Date;
import java.util.List;

/**
 * Информация о матче.
 * Immutable объект.
 */
public class KHLMatchInfo {
    public enum Type {
        UNDEFINED,
        REGULAR,
        FINAL,
        SEMIFINAL,
        QUARTERFINAL,
        EIGHTFINAL,
        //
    }

    public enum Periods {
        PERIOD1,
        PERIOD2,
        PERIOD3,
        OVERTIME,
        SHOOTOUTS,
        //
    }

    public static final int PLAIN_PERIODS_COUNT = Periods.PERIOD3.ordinal() + 1;
    public static final int WITH_OVERTIME_PERIODS_COUNT = Periods.OVERTIME.ordinal() + 1;
    public static final int WITH_SHOOTOUTS_PERIODS_COUNT = Periods.SHOOTOUTS.ordinal() + 1;

    public static class Score {
        public final int first;
        public final int second;

        public Score(final int first, final int second) {
            Utils.checkNotNegative(first, () -> "First");
            Utils.checkNotNegative(second, () -> "Second");
            this.first = first;
            this.second = second;
        }

        @Override
        public String toString() {
            return String.format("%d:%d", first, second);
        }
    }

    public final Date date;
    /**
     * Вспомогательная переменная, помогающая задавать порядок для матчей с одинаковой датой.
     * Напрмиер, может определяться как номер строки в файле.
     */
    public final int tag;
    public final Type type;
    public final String firstTeam;
    public final String secondTeam;
    public final Score score;
    /**
     * Счет по периодам. Unmodifiable list.
     * Количество элементов от 3 ({@link KHLMatchInfo#PLAIN_PERIODS_COUNT})
     * до 5 ({@link KHLMatchInfo#WITH_SHOOTOUTS_PERIODS_COUNT}): 3 периода, овертайм (все овертаймы вместе), буллиты.
     */
    public final List<Score> scorePeriods;

    public KHLMatchInfo(final Date date, final int tag, final Type type,
                        final String firstTeam, final String secondTeam,
                        final Score score, final Score[] scorePeriods) {
        this.date = date;
        this.tag = tag;
        this.type = type;
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
        this.score = score;
        this.scorePeriods = List.of(scorePeriods);
        checkCorrectness();
    }

    public KHLMatchInfo(final Date date, final int tag, final Type type,
                        final String firstTeam, final String secondTeam) {
        this.date = date;
        this.tag = tag;
        this.type = type;
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
        this.score = null;
        this.scorePeriods = null;
    }

    public KHLMatchInfo(final KHLMatchInfo queryInfo,
                        final Score score) {
        this.date = queryInfo.date;
        this.tag = queryInfo.tag;
        this.type = queryInfo.type;
        this.firstTeam = queryInfo.firstTeam;
        this.secondTeam = queryInfo.secondTeam;
        this.score = score;
        this.scorePeriods = null;
    }

    private void checkCorrectness() {
        int first = 0;
        int second = 0;
        for (final Score scorePeriod : scorePeriods) {
            first += scorePeriod.first;
            second += scorePeriod.second;
        }
        Utils.checkEquals(score.first, first, () -> "First");
        Utils.checkEquals(score.second, second, () -> "Second");
        Utils.checkInterval(scorePeriods.size(), PLAIN_PERIODS_COUNT, WITH_SHOOTOUTS_PERIODS_COUNT,
                () -> "scorePeriods.size()");
        if (scorePeriods.size() == PLAIN_PERIODS_COUNT) {
            Utils.checkNotEquals(score.first, score.second, () -> "First and second without overtime");
        } else {
            final int difference = Math.abs(score.first - score.second);
            if (scorePeriods.size() == WITH_OVERTIME_PERIODS_COUNT) {
                Utils.checkEquals(difference, 1, () -> "Difference with overtime");
                Utils.checkEquals(scorePeriods.get(Periods.OVERTIME.ordinal()).first
                                + scorePeriods.get(Periods.OVERTIME.ordinal()).second,
                        1, () -> "Overtime without shootouts, first + second");
            } else if (scorePeriods.size() == WITH_SHOOTOUTS_PERIODS_COUNT) {
                if (type != Type.REGULAR) {
                    throw new IllegalStateException(String.format("Shootouts, but type = %s", type));
                }
                Utils.checkEquals(difference, 1, () -> "Difference with shootouts");
                Utils.checkEquals(scorePeriods.get(Periods.OVERTIME.ordinal()).first
                                + scorePeriods.get(Periods.OVERTIME.ordinal()).second,
                        0, () -> "Overtime with shootouts, first + second");
                Utils.checkEquals(scorePeriods.get(Periods.SHOOTOUTS.ordinal()).first
                                + scorePeriods.get(Periods.SHOOTOUTS.ordinal()).second,
                        1, () -> "Shootouts, first + second");
            } else {
                throw new IllegalStateException(String.format("Illegal scorePeriods.size() (%d)", scorePeriods.size()));
            }
        }
    }

    @Override
    public String toString() {
        return "KHLMatchInfo{" +
                "date=" + date +
                ", tag=" + tag +
                ", type=" + type +
                ", firstTeam='" + firstTeam + '\'' +
                ", secondTeam='" + secondTeam + '\'' +
                ", score=" + score +
                ", scorePeriods=" + scorePeriods +
                '}';
    }
}
