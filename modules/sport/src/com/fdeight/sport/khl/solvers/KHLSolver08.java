package com.fdeight.sport.khl.solvers;

import com.fdeight.sport.khl.data.KHLMatchInfo;
import com.fdeight.sport.khl.data.KHLStorage;
import com.fdeight.sport.utils.Utils;

import java.util.*;

import static com.fdeight.sport.khl.data.KHLMatchInfo.NIL_NIL;

/**
 * Копия KHLSolver06 ("Индекс силы КХЛ"), но также добавлен индекс силы ничьей.
 * Идея скорее не сработала, при проверке в целом работает хуже исходного решателя.
 */
public class KHLSolver08 implements KHLSolver {
    /**
     * Начальное значение баллов, которое имеет каждая команда. Конкретная величина большого значения не имеет,
     * потому что все команды в равных условиях.
     */
    private static final double START_VALUE = 100;

    private static class PowerRankings {
        public double winValue = START_VALUE;
        public double drawValue = START_VALUE;

        public void add(final double deltaWin, final double shareDraw) {
            winValue += deltaWin;
            drawValue += shareDraw * drawValue;
        }
    }

    private static class Settings {
        /**
         * Доля баллов {@link PowerRankings#winValue}, которую одна команда отнимает у другой
         * за победу в основное время.
         */
        public final double shareWinWithoutOvertime = 0.25;
        /**
         * Доля баллов {@link PowerRankings#winValue}, которую одна команда отнимает у другой
         * за победу в овертайме или в серии бросков.
         */
        public final double shareWinWithOvertime = 0.125;
        /**
         * Доля баллов {@link PowerRankings#winValue}, на которую растет (если была ничья в основное время)
         * или уменьшается сила ничьей.
         */
        public final double shareDraw = 0.1;
        /**
         * Коэффициент, определяющий минимальную разность между средним значением индекса ничьей
         * по двум играющим командам и средним значением индекса ничьей по всем играющим командам,
         * при которой считаем, что будет ничья.
         * Если разница меньше - это победа в основное время.
         * Минимальная разница равна произведению данного коэффициента на среднее значение индекса ничьей
         * по всем командам.
         */
        public final double deltaAvgDrawCoef = 0.45;
    }

    /**
     * Хранилище информации о матчах, которые являются начальными даннными, по ним собираем статистику, обучаемся.
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

    private final Map<String, PowerRankings> teamPowerRankings;

    private final Settings settings;

    public KHLSolver08(final KHLStorage initStorage, final KHLStorage queryStorage) {
        this.initStorage = initStorage;
        this.queryStorage = queryStorage;
        resultStorage = new KHLStorage();
        teamPowerRankings = new TreeMap<>();
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
        computeTeamPowerRankings();
        computeResult();
    }

    private void computeTeamPowerRankings() {
        checkBeforeComputePowerRankings();
        final List<KHLMatchInfo> initList = initStorage.getUnmodifiableList();
        for (final KHLMatchInfo info : initList) {
            final PowerRankings hostPowerRankings = teamPowerRankings.computeIfAbsent(info.firstTeam,
                    key -> new PowerRankings());
            final PowerRankings guestPowerRankings = teamPowerRankings.computeIfAbsent(info.secondTeam,
                    key -> new PowerRankings());
            final double share;
            final double shareDraw;
            if (info.scorePeriods.size() == KHLMatchInfo.PLAIN_PERIODS_COUNT) {
                share = settings.shareWinWithoutOvertime;
                shareDraw = -settings.shareDraw;
            } else {
                share = settings.shareWinWithOvertime;
                shareDraw = settings.shareDraw;
            }
            final double hostDeltaWin;
            final double guestDeltaWin;
            if (info.score.first > info.score.second) {
                hostDeltaWin = guestPowerRankings.winValue * share;
                guestDeltaWin = -hostDeltaWin;
            } else {
                guestDeltaWin = hostPowerRankings.winValue * share;
                hostDeltaWin = -guestDeltaWin;
            }
            hostPowerRankings.add(hostDeltaWin, shareDraw);
            hostPowerRankings.add(guestDeltaWin, shareDraw);
        }
    }

    private void checkBeforeComputePowerRankings() {
        Utils.checkNotEquals(initStorage.size(), 0, () -> "initStorage.size()");
        Utils.checkEquals(teamPowerRankings.size(), 0, () -> "teamPowerRankings.size()");
    }

    private void computeResult() {
        checkBeforeComputeResult();
        // Random тут не очень важен, просто определить какой-то счет.
        final Random rnd = new Random(1);

        double sumDraw = 0;
        for (final PowerRankings powerRankings : teamPowerRankings.values()) {
            sumDraw += powerRankings.drawValue;
        }
        final double avgDrawValue = sumDraw / teamPowerRankings.size();

        final List<KHLMatchInfo> queryList = queryStorage.getUnmodifiableList();
        for (final KHLMatchInfo queryInfo : queryList) {
            final PowerRankings hostPowerRankings = Objects.requireNonNull(teamPowerRankings.get(queryInfo.firstTeam),
                    "hostPowerRankings undefined");
            final PowerRankings guestPowerRankings = Objects.requireNonNull(teamPowerRankings.get(queryInfo.secondTeam),
                    "guestPowerRankings undefined");

            final boolean isHostWin = hostPowerRankings.winValue >= guestPowerRankings.winValue;

            final boolean isDraw = (hostPowerRankings.drawValue + guestPowerRankings.drawValue) / 2
                    > avgDrawValue + settings.deltaAvgDrawCoef * avgDrawValue;

            final int first = rnd.nextInt(4);
            final int second = rnd.nextInt(4);
            final int avg = (first + second) / 2;

            final KHLMatchInfo resultInfo;
            if (isHostWin) {
                if (isDraw) {
                    resultInfo = new KHLMatchInfo(queryInfo, new KHLMatchInfo.Score(avg + 1, avg),
                            new KHLMatchInfo.Score[]{new KHLMatchInfo.Score(avg, avg),
                                    NIL_NIL, NIL_NIL,
                                    new KHLMatchInfo.Score(1, 0)});
                } else {
                    if (first > second) {
                        resultInfo = new KHLMatchInfo(queryInfo, new KHLMatchInfo.Score(first, second),
                                new KHLMatchInfo.Score[]{new KHLMatchInfo.Score(first, second), NIL_NIL, NIL_NIL});
                    } else {
                        resultInfo = new KHLMatchInfo(queryInfo, new KHLMatchInfo.Score(avg + 1, avg),
                                new KHLMatchInfo.Score[]{new KHLMatchInfo.Score(avg + 1, avg), NIL_NIL, NIL_NIL});
                    }
                }
            } else {
                if (isDraw) {
                    resultInfo = new KHLMatchInfo(queryInfo, new KHLMatchInfo.Score(avg, avg + 1),
                            new KHLMatchInfo.Score[]{new KHLMatchInfo.Score(avg, avg), NIL_NIL, NIL_NIL,
                                    new KHLMatchInfo.Score(0, 1)});
                } else {
                    if (first < second) {
                        resultInfo = new KHLMatchInfo(queryInfo, new KHLMatchInfo.Score(first, second),
                                new KHLMatchInfo.Score[]{new KHLMatchInfo.Score(first, second), NIL_NIL, NIL_NIL});
                    } else {
                        resultInfo = new KHLMatchInfo(queryInfo, new KHLMatchInfo.Score(avg, avg + 1),
                                new KHLMatchInfo.Score[]{new KHLMatchInfo.Score(avg, avg + 1), NIL_NIL, NIL_NIL});
                    }
                }
            }
            resultStorage.add(resultInfo);
        }
    }

    private void checkBeforeComputeResult() {
        checkTeamPowerRankings();
        Utils.checkNotEquals(queryStorage.size(), 0, () -> "queryStorage.size()");
        Utils.checkEquals(resultStorage.size(), 0, () -> "resultStorage.size()");
    }

    private void checkTeamPowerRankings() {
        Utils.checkNotEquals(teamPowerRankings.size(), 0, () -> "teamPowerRankings.size()");

        double sum = 0;
        for (final String key : teamPowerRankings.keySet()) {
            final PowerRankings powerRankings = teamPowerRankings.get(key);
            Utils.checkNotNegative(powerRankings.winValue, () -> String.format("%s powerRankings.value", key));
            sum += powerRankings.winValue;
        }
        Utils.checkEquals(sum, teamPowerRankings.size() * START_VALUE, () -> "Sum of power rankings");
    }

    @Override
    public List<KHLMatchInfo> getResultList() {
        checkBeforeGetResult();
        return resultStorage.getUnmodifiableList();
    }

    private void checkBeforeGetResult() {
        Utils.checkNotEquals(resultStorage.size(), 0, () -> "resultStorage.size()");
        Utils.checkEquals(queryStorage.size(), resultStorage.size(),
                () -> "queryStorage.size() vs resultStorage.size()");
    }
}
