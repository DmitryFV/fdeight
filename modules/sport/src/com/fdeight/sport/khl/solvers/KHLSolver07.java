package com.fdeight.sport.khl.solvers;

import com.fdeight.sport.khl.data.KHLMatchInfo;
import com.fdeight.sport.khl.data.KHLStorage;
import com.fdeight.sport.utils.Utils;

import java.util.*;

/**
 * Решатель, использующий другие решатели.
 */
public class KHLSolver07 {
    /**
     * Хранилище информации о матчах, по которым идет запрос на прогноз результатов.
     * В информации о матчах в этом хранилище отсутствуют данные о счете.
     */
    private final KHLStorage queryStorage;
    /**
     * Хранилище информации с прогнозом по матчам, по которым был запрос на прогноз результатов.
     */
    private final KHLStorage resultStorage;

    private final List<KHLSolver> khlSolvers;

    public KHLSolver07(final KHLStorage queryStorage, final List<KHLSolver> khlSolvers) {
        this.queryStorage = queryStorage;
        resultStorage = new KHLStorage();
        this.khlSolvers = new ArrayList<>(khlSolvers);
    }

    public void solve() {
        computeResult();
    }

    private void computeResult() {
        checkBeforeComputeResult();
        final List<List<KHLMatchInfo>> resultLists = new ArrayList<>();
        for (final KHLSolver khlSolver : khlSolvers) {
            resultLists.add(khlSolver.getResultList());
        }
        final List<KHLMatchInfo> queryList = queryStorage.getUnmodifiableList();
        for (int i = 0; i < queryList.size(); i++) {
            final KHLMatchInfo queryInfo = queryList.get(i);
            KHLMatchInfo resultInfo = null;
            for (final List<KHLMatchInfo> resultList : resultLists) {
                final KHLMatchInfo solverInfo = Objects.requireNonNull(resultList.get(i), "Solver info");
                Utils.checkEquals(queryInfo.date.getTime(), solverInfo.date.getTime(), () -> "date");
                Utils.checkEquals(queryInfo.firstTeam, solverInfo.firstTeam, () -> "firstTeam");
                Utils.checkEquals(queryInfo.secondTeam, solverInfo.secondTeam, () -> "secondTeam");
                if (resultInfo == null) {
                    resultInfo = solverInfo;
                    continue;
                }
                final boolean isDrawResult = resultInfo.scorePeriods.size() == KHLMatchInfo.PLAIN_PERIODS_COUNT;
                final boolean isDrawSolver = solverInfo.scorePeriods.size() == KHLMatchInfo.PLAIN_PERIODS_COUNT;
                if (isDrawResult != isDrawSolver) {
                   resultInfo = null;
                   break;
                }
                if (isDrawResult) continue;
                final boolean isHostWinResult = resultInfo.score.first > resultInfo.score.second;
                final boolean isHostWinSolver = solverInfo.score.first > solverInfo.score.second;
                if (isHostWinResult != isHostWinSolver) {
                    resultInfo = null;
                    break;
                }
            }
            if (resultInfo != null) {
                resultStorage.add(resultInfo);
            }
        }
    }

    private void checkBeforeComputeResult() {
        Utils.checkNotEquals(queryStorage.size(), 0, () -> "queryStorage.size()");
        Utils.checkEquals(resultStorage.size(), 0, () -> "resultStorage.size()");
    }

    public List<KHLMatchInfo> getResultList() {
        return resultStorage.getUnmodifiableList();
    }
}
