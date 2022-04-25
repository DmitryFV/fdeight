package com.fdeight.sport.khl.data;

import com.fdeight.sport.utils.Utils;

import java.util.*;

public class KHLStorage {
    private static final Comparator<KHLMatchInfo> COMPARATOR = (o1, o2) -> {
        if (o1.date.before(o2.date)) return -1;
        if (o1.date.after(o2.date)) return 1;
        return -Integer.compare(o1.tag, o2.tag);
    };

    private final List<KHLMatchInfo> storage = new ArrayList<>();

    @Override
    public String toString() {
        return storage.toString();
    }

    public void add(final KHLMatchInfo info) {
        storage.add(info);
    }

    public int size() {
        return storage.size();
    }

    public void sort() {
        storage.sort(COMPARATOR);
    }

    public List<KHLMatchInfo> getUnmodifiableList() {
        return Collections.unmodifiableList(storage);
    }

    /**
     * Возвращает подмножество записей из хранилища, отфильтрованных по дате,
     * в диапазоне от минимального до максимального значений включительно.
     * Сортировка сохраняется согласно текущей сортировке хранилища.
     *
     * @return подмножество записей из хранилища.
     */
    public KHLStorage getSubStorageFiltredByDate(final Date min, final Date max) {
        final KHLStorage subStorage = new KHLStorage();
        for (final KHLMatchInfo info : storage) {
            if (info.date.before(min)) continue;
            if (info.date.after(max)) continue;
            subStorage.add(info);
        }
        return subStorage;
    }

    /**
     * Возвращает хранилище информации о матчах, по которым будет запрос (на прогноз результатов).
     * Это будет подмножество записей из текущего хранилища, но для получаемого хранилища
     * в этих записях не будет данных о счете.
     *
     * @return хранилище записей, по которым будет запрос.
     */
    public KHLStorage getQueryStorageFiltredByDate(final Date min, final Date max) {
        final KHLStorage subStorage = new KHLStorage();
        for (final KHLMatchInfo info : storage) {
            if (info.date.before(min)) continue;
            if (info.date.after(max)) continue;
            final KHLMatchInfo queryInfo = new KHLMatchInfo(info.date, info.tag, info.type,
                    info.firstTeam, info.secondTeam);
            subStorage.add(queryInfo);
        }
        return subStorage;
    }

    /**
     * Возвращает подмножество записей из хранилища, отфильтрованных по записям (по дате и командам)
     * из другого хранилища.
     * Сортировка сохраняется согласно текущей сортировке хранилища, из которого выбираются записи.
     *
     * @return подмножество записей из хранилища.
     */
    public KHLStorage getSubStorageFiltredByStorage(final KHLStorage khlStorage) {
        final KHLStorage subStorage = new KHLStorage();
        for (final KHLMatchInfo info : storage) {
            for (final KHLMatchInfo by : khlStorage.storage) {
                if (info.date.getTime() != by.date.getTime()) continue;
                if (!info.firstTeam.equals(by.firstTeam)) continue;
                if (!info.secondTeam.equals(by.secondTeam)) continue;
                subStorage.add(info);
                break;
            }
        }
        return subStorage;
    }

    public KHLMetric compare(final List<KHLMatchInfo> list) {
        return compare(list, true);
    }

    /**
     * Порядок элементов в хранилище и в списке, который надо проверить, должны совпадать.
     * Если размер хранилища и списка, который надо проверить, не равны, то список является подмножеством хранилища.
     * Если размеры хранилища и списка, который надо проверить, не обязаны быть равными, то они могут быть равными,
     * могут быть не равными.
     *
     * @param list                 список, который надо проверить.
     * @param isExpectedSizeEquals обязаны ли быть равными размеры хранилища и списка, который надо проверить.
     * @return метрики.
     */
    public KHLMetric compare(final List<KHLMatchInfo> list, final boolean isExpectedSizeEquals) {
        if (isExpectedSizeEquals) {
            Utils.checkEquals(storage.size(), list.size(), () -> "size");
        }
        int count = 0;
        int countDraws = 0;
        int countHostWinWithoutOvertime = 0;
        int right = 0;
        int rightDraws = 0;
        for (int i = 0; i < storage.size(); i++) {
            final KHLMatchInfo info = storage.get(i);
            final KHLMatchInfo otherInfo;
            if (isExpectedSizeEquals) {
                otherInfo = list.get(i);
                Utils.checkEquals(info.date.getTime(), otherInfo.date.getTime(), () -> "date");
                Utils.checkEquals(info.firstTeam, otherInfo.firstTeam, () -> "firstTeam");
                Utils.checkEquals(info.secondTeam, otherInfo.secondTeam, () -> "secondTeam");
            } else {
                KHLMatchInfo lOtherInfo = null;
                for (final KHLMatchInfo listInfo : list) {
                    if (info.date.getTime() != listInfo.date.getTime()) continue;
                    if (!info.firstTeam.equals(listInfo.firstTeam)) continue;
                    if (!info.secondTeam.equals(listInfo.secondTeam)) continue;
                    lOtherInfo = listInfo;
                    break;
                }
                if (lOtherInfo == null) continue;
                otherInfo = lOtherInfo;
            }
            count++;
            if (info.scorePeriods.size() == KHLMatchInfo.PLAIN_PERIODS_COUNT) {
                if (info.score.first - info.score.second > 0) {
                    countHostWinWithoutOvertime++;
                }
                final int signum = Integer.signum(info.score.first - info.score.second);
                Utils.checkNotEquals(signum, 0, () -> "signum");
                if (otherInfo.scorePeriods.size() == KHLMatchInfo.PLAIN_PERIODS_COUNT
                        && signum == Integer.signum(otherInfo.score.first - otherInfo.score.second)) {
                    right++;
                }
            } else {
                countDraws++;
                if (otherInfo.scorePeriods.size() > KHLMatchInfo.PLAIN_PERIODS_COUNT) {
                    right++;
                    rightDraws++;
                }
            }
        }
        Utils.checkEquals(count, list.size(), () -> "count vs list.size()");
        return new KHLMetric(count, countDraws, countHostWinWithoutOvertime, right, rightDraws);
    }
}
