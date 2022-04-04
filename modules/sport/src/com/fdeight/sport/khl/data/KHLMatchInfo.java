package com.fdeight.sport.khl.data;

import com.fdeight.sport.utils.Utils;

import java.util.Date;

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
     * Длина массива равна от 3 до 5: 3 периода, овертайм (все овертаймы вместе), буллиты.
     */
    public final Score[] scorePeriods;

    public KHLMatchInfo(final Date date, final int tag, final Type type,
                        final String firstTeam, final String secondTeam,
                        final Score score, final Score[] scorePeriods) {
        this.date = date;
        this.tag = tag;
        this.type = type;
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
        this.score = score;
        this.scorePeriods = scorePeriods;
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
        if (scorePeriods.length == 3) {
            Utils.checkNotEquals(score.first, score.second, () -> "First and second without overtime");
        } else {
            final int difference = Math.abs(score.first - score.second);
            if (scorePeriods.length == 4) {
                Utils.checkEquals(difference, 1, () -> "Difference with overtime");
                Utils.checkEquals(scorePeriods[3].first + scorePeriods[3].second, 1,
                        () -> "Overtime without shootouts, first + second");
            } else if (scorePeriods.length == 5) {
                if (type != Type.REGULAR) {
                    throw new IllegalStateException(String.format("Shootouts, but type = %s", type));
                }
                Utils.checkEquals(difference, 1, () -> "Difference with shootouts");
                Utils.checkEquals(scorePeriods[3].first + scorePeriods[3].second, 0,
                        () -> "Overtime with shootouts, first + second");
                Utils.checkEquals(scorePeriods[4].first + scorePeriods[4].second, 1,
                        () -> "Shootouts, first + second");
            }
        }
    }
}
