package com.fdeight.sport.khl.data;

import com.fdeight.sport.utils.Utils;

import java.util.Arrays;
import java.util.Date;

/**
 * Информация о матче.
 * Почти immutable объект.
 * Потенциально слабое место (не immutable в теории) - массив счета по периодам.
 * Массив сделан private, чтобы уменьшить риски здесь.
 * Делать unmodifiable list вместо массива представляется излишним.
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
     * Счет по периодам.
     * Длина массива равна от 3 ({@link KHLMatchInfo#PLAIN_PERIODS_COUNT})
     * до 5 ({@link KHLMatchInfo#WITH_SHOOTOUTS_PERIODS_COUNT}): 3 периода, овертайм (все овертаймы вместе), буллиты.
     */
    private final Score[] scorePeriods;

    public KHLMatchInfo(final Date date, final int tag, final Type type,
                        final String firstTeam, final String secondTeam,
                        final Score score, final Score[] scorePeriods) {
        this.date = date;
        this.tag = tag;
        this.type = type;
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
        this.score = score;
        this.scorePeriods = Arrays.copyOf(scorePeriods, scorePeriods.length);
        checkCorrectness();
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
        Utils.checkInterval(scorePeriods.length, PLAIN_PERIODS_COUNT, WITH_SHOOTOUTS_PERIODS_COUNT,
                () -> "scorePeriods.length");
        if (scorePeriods.length == PLAIN_PERIODS_COUNT) {
            Utils.checkNotEquals(score.first, score.second, () -> "First and second without overtime");
        } else {
            final int difference = Math.abs(score.first - score.second);
            if (scorePeriods.length == WITH_OVERTIME_PERIODS_COUNT) {
                Utils.checkEquals(difference, 1, () -> "Difference with overtime");
                Utils.checkEquals(scorePeriods[Periods.OVERTIME.ordinal()].first + scorePeriods[3].second,
                        1, () -> "Overtime without shootouts, first + second");
            } else if (scorePeriods.length == WITH_SHOOTOUTS_PERIODS_COUNT) {
                if (type != Type.REGULAR) {
                    throw new IllegalStateException(String.format("Shootouts, but type = %s", type));
                }
                Utils.checkEquals(difference, 1, () -> "Difference with shootouts");
                Utils.checkEquals(scorePeriods[Periods.OVERTIME.ordinal()].first + scorePeriods[3].second,
                        0, () -> "Overtime with shootouts, first + second");
                Utils.checkEquals(scorePeriods[Periods.SHOOTOUTS.ordinal()].first + scorePeriods[4].second,
                        1, () -> "Shootouts, first + second");
            } else {
                throw new IllegalStateException(String.format("Illegal scorePeriods.length (%d)", scorePeriods.length));
            }
        }
    }
}
