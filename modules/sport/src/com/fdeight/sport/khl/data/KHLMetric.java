package com.fdeight.sport.khl.data;

public class KHLMetric {
    private final int count;
    private final int countDraws;
    private final int countHostWinWithoutOvertime;
    private final int right;
    private final int rightDraws;
    private final int predictedDraws;
    private final int predictedHostWinWithoutOvertime;

    public KHLMetric(final int count, final int countDraws, final int countHostWinWithoutOvertime,
                     final int right, final int rightDraws,
                     final int predictedDraws, final int predictedHostWinWithoutOvertime) {
        this.count = count;
        this.countDraws = countDraws;
        this.countHostWinWithoutOvertime = countHostWinWithoutOvertime;
        this.right = right;
        this.rightDraws = rightDraws;
        this.predictedDraws = predictedDraws;
        this.predictedHostWinWithoutOvertime = predictedHostWinWithoutOvertime;
    }

    @Override
    public String toString() {
        return "{" +
                "count=" + count +
                ", draws=" + countDraws +
                ", hostWinWoOvertime=" + countHostWinWithoutOvertime +
                ", right=" + right +
                ", rightDraws=" + rightDraws +
                ", pRight=[" + (count == 0 ? "UNDEFINED" : "" + (right * 100 / count)) + "]" +
                ", pRightDraws=" + (count == 0 ? "UNDEFINED" : "" + (rightDraws * 100 / count)) +
                ", pRightDrawsByDraws=" + (countDraws == 0 ? "UNDEFINED" : "" + (rightDraws * 100 / countDraws)) +
                ", prDraws=" + predictedDraws +
                ", prHostWinWithoutOvertime=" + predictedHostWinWithoutOvertime +
                '}';
    }

    public double getResult() {
        return right / (double) count;
    }
}
