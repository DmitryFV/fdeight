package com.fdeight.sport.khl.data;

public class KHLMetric {
    private final int count;
    private final int countDraws;
    private final int right;
    private final int rightDraws;

    public KHLMetric(final int count, final int countDraws, final int right, final int rightDraws) {
        this.count = count;
        this.countDraws = countDraws;
        this.right = right;
        this.rightDraws = rightDraws;
    }

    @Override
    public String toString() {
        return "KHLMetric{" +
                "count=" + count +
                ", countDraws=" + countDraws +
                ", right=" + right +
                ", rightDraws=" + rightDraws +
                ", percentRight=[" + (count == 0 ? "UNDEFINED" : "" + (right * 100 / count)) + "]" +
                ", percentDrawsByCount=" + (count == 0 ? "UNDEFINED" : "" + (rightDraws * 100 / count)) +
                ", percentDrawsByDraws=" + (countDraws == 0 ? "UNDEFINED" : "" + (rightDraws * 100 / countDraws)) +
                '}';
    }

    public double getResult() {
        return right / (double) count;
    }
}
