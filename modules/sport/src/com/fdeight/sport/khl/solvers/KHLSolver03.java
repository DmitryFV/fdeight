package com.fdeight.sport.khl.solvers;

import com.fdeight.sport.khl.data.KHLMatchInfo;
import com.fdeight.sport.khl.data.KHLStorage;
import com.fdeight.sport.utils.Utils;

import java.util.*;

import static com.fdeight.sport.khl.data.KHLMatchInfo.NIL_NIL;

/**
 * Случайный решатель, для контроля.
 * Вероятности фиксированны.
 */
public class KHLSolver03 {
    /**
     * Хранилище информации о матчах, по которым идет запрос на прогноз результатов.
     * В информации о матчах в этом хранилище отсутствуют данные о счете.
     */
    private final KHLStorage queryStorage;
    /**
     * Хранилище информации с прогнозом по матчам, по которым был запрос на прогноз результатов.
     */
    private final KHLStorage resultStorage;

    private final Random rnd;

    public KHLSolver03(final long seed, final KHLStorage queryStorage) {
        this.queryStorage = queryStorage;
        resultStorage = new KHLStorage();
        rnd = new Random(seed);
    }

    public void solve() {
        computeResult();
    }

    private void computeResult() {
        checkBeforeComputeResult();
        final List<KHLMatchInfo> queryList = queryStorage.getUnmodifiableList();
        for (final KHLMatchInfo queryInfo : queryList) {
            final boolean isHostWin = rnd.nextDouble() < 0.6;
            final boolean isDraw = rnd.nextDouble() < 0.06;
            final int first = rnd.nextInt(5);
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
        Utils.checkNotEquals(queryStorage.size(), 0, () -> "queryStorage.size()");
        Utils.checkEquals(resultStorage.size(), 0, () -> "resultStorage.size()");
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
