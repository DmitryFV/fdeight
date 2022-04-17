package com.fdeight.sport.khl.solvers;

import com.fdeight.sport.khl.data.KHLMatchInfo;
import com.fdeight.sport.khl.data.KHLStorage;
import com.fdeight.sport.utils.Utils;

import java.util.List;
import java.util.Random;

import static com.fdeight.sport.khl.data.KHLMatchInfo.NIL_NIL;

/**
 * Случайный решатель, для контроля.
 * Вероятности вычисляются по исходному хранилищу.
 */
public class KHLSolver04 {
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

    private final Stat draw;

    private final Stat hostWinWithoutOvertime;

    private final Random rnd;

    public KHLSolver04(final long seed, final KHLStorage initStorage, final KHLStorage queryStorage) {
        this.initStorage = initStorage;
        this.queryStorage = queryStorage;
        resultStorage = new KHLStorage();
        draw = new Stat();
        hostWinWithoutOvertime = new Stat();
        rnd = new Random(seed);
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
        computeStats();
        computeResult();
    }

    private void computeStats() {
        checkBeforeComputeStats();
        final List<KHLMatchInfo> initList = initStorage.getUnmodifiableList();
        for (final KHLMatchInfo info : initList) {
            if (info.scorePeriods.size() > KHLMatchInfo.PLAIN_PERIODS_COUNT) {
                draw.add(1);
                hostWinWithoutOvertime.add(0);
            } else if (info.score.first > info.score.second) {
                draw.add(0);
                hostWinWithoutOvertime.add(1);
            } else {
                draw.add(0);
                hostWinWithoutOvertime.add(0);
            }
        }
    }

    private void checkBeforeComputeStats() {
        Utils.checkNotEquals(initStorage.size(), 0, () -> "initStorage.size()");
    }

    private void computeResult() {
        checkBeforeComputeResult();
        final List<KHLMatchInfo> queryList = queryStorage.getUnmodifiableList();
        final double pDraw = draw.value / (double) draw.count;
        final double pHostWinWithoutOvertime = hostWinWithoutOvertime.value / (double) hostWinWithoutOvertime.count;
        for (final KHLMatchInfo queryInfo : queryList) {
            final boolean isDraw = rnd.nextDouble() < pDraw;
            final boolean isHostWinWithoutOvertime = rnd.nextDouble() < pHostWinWithoutOvertime;
            final int first = rnd.nextInt(4);
            final int second = rnd.nextInt(4);
            final int avg = (first + second) / 2;
            final KHLMatchInfo resultInfo;
            if (isDraw) {
                final int first1 = rnd.nextDouble() < 0.5 ? 1 : 0;
                final int second1 = 1 - first1;
                resultInfo = new KHLMatchInfo(queryInfo, new KHLMatchInfo.Score(avg + first1, avg + second1),
                        new KHLMatchInfo.Score[]{new KHLMatchInfo.Score(avg, avg),
                                NIL_NIL, NIL_NIL,
                                new KHLMatchInfo.Score(first1, second1)});

            } else {
                if (isHostWinWithoutOvertime) {
                    resultInfo = new KHLMatchInfo(queryInfo, new KHLMatchInfo.Score(avg + 1, avg),
                            new KHLMatchInfo.Score[]{new KHLMatchInfo.Score(avg + 1, avg), NIL_NIL, NIL_NIL});
                } else {
                    resultInfo = new KHLMatchInfo(queryInfo, new KHLMatchInfo.Score(avg, avg + 1),
                            new KHLMatchInfo.Score[]{new KHLMatchInfo.Score(avg, avg + 1), NIL_NIL, NIL_NIL});
                }
            }
            resultStorage.add(resultInfo);
        }
    }

    private void checkBeforeComputeResult() {
        checkStats();
        Utils.checkNotEquals(queryStorage.size(), 0, () -> "queryStorage.size()");
        Utils.checkEquals(resultStorage.size(), 0, () -> "resultStorage.size()");
    }

    private void checkStats() {
        Utils.checkEquals(draw.count, initStorage.size(), () -> "draw.count");
        Utils.checkEquals(hostWinWithoutOvertime.count, initStorage.size(), () -> "hostWinWithoutOvertime.count");
    }

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
