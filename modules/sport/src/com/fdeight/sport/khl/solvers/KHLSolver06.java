package com.fdeight.sport.khl.solvers;

import com.fdeight.sport.khl.data.KHLMatchInfo;
import com.fdeight.sport.khl.data.KHLStorage;
import com.fdeight.sport.utils.Utils;

import java.util.*;

import static com.fdeight.sport.khl.data.KHLMatchInfo.NIL_NIL;

/**
 * Решатель, использующий "Индекс силы КХЛ" (Power Rankings).
 * https://www.khl.ru/news/2022/01/12/495833.html:
 * "Индекс силы – это рейтинг, подсчитывающийся математически на основе показателей в текущем сезоне.
 * Регулярный чемпионат все команды начали с одинаковым количеством баллов;
 * за победу в основное время одна команда отнимает у другой 25% её баллов,
 * в случае победы в овертайме или в серии бросков — лишь 12,5%.
 * Таким образом Индекс силы показывает, какие команды в настоящий момент находятся «на ходу»,
 * обыгрывают фаворитов и выдают длительные победные серии."
 */
public class KHLSolver06 implements KHLSolver {
    /**
     * Начальное значение баллов, которое имеет каждая команда. Конкретная величина большого значения не имеет,
     * потому что все команды в равных условиях.
     */
    private static final double START_VALUE = 100;

    private static class PowerRankings {
        public double value = START_VALUE;
    }

    private static class Settings {
        /**
         * Доля баллов, которое одна команда отнимает у другой за победу в основное время.
         */
        public final double shareWinWithoutOvertime = 0.25;
        /**
         * Доля баллов, которое одна команда отнимает у другой за победу в овертайме или в серии бросков.
         */
        public final double shareWinWithOvertime = 0.125;
        /**
         * Коэффициент, определяющий максимальную разницу в индексе силы, при которой считаем, что будет ничья.
         * Если разница больше - это победа в основное время.
         * Максимальная разница равна произведению данного коэффициента на {@link #START_VALUE}.
         */
        public final double deltaDrawCoef = 0.15;
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

    private final Map<String, PowerRankings> teamPowerRankings;

    private final Settings settings;

    public KHLSolver06(final KHLStorage initStorage, final KHLStorage queryStorage) {
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
            if (info.scorePeriods.size() == KHLMatchInfo.PLAIN_PERIODS_COUNT) {
                share = settings.shareWinWithoutOvertime;
            } else {
                share = settings.shareWinWithOvertime;
            }
            if (info.score.first > info.score.second) {
                final double delta = guestPowerRankings.value * share;
                hostPowerRankings.value += delta;
                guestPowerRankings.value -= delta;
            } else {
                final double delta = hostPowerRankings.value * share;
                hostPowerRankings.value -= delta;
                guestPowerRankings.value += delta;
            }
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
        final List<KHLMatchInfo> queryList = queryStorage.getUnmodifiableList();
        for (final KHLMatchInfo queryInfo : queryList) {
            final PowerRankings hostPowerRankings = Objects.requireNonNull(teamPowerRankings.get(queryInfo.firstTeam),
                    "hostStats undefined");
            final PowerRankings guestPowerRankings = Objects.requireNonNull(teamPowerRankings.get(queryInfo.secondTeam),
                    "guestStats undefined");

            final boolean isHostWin;
            final boolean isDraw;
            if (hostPowerRankings.value > guestPowerRankings.value + settings.deltaDrawCoef * START_VALUE) {
                isHostWin = true;
                isDraw = false;
            } else if (hostPowerRankings.value + settings.deltaDrawCoef * START_VALUE < guestPowerRankings.value) {
                isHostWin = false;
                isDraw = false;
            } else {
                isHostWin = hostPowerRankings.value > guestPowerRankings.value;
                isDraw = true;
            }

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
            Utils.checkNotNegative(powerRankings.value, () -> String.format("%s powerRankings.value", key));
            sum += powerRankings.value;
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
