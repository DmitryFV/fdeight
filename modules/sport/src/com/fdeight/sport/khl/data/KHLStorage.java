package com.fdeight.sport.khl.data;

import java.util.*;

public class KHLStorage {
    private static final Comparator<KHLMatchInfo> COMPARATOR = (o1, o2) -> {
        if (o1.date.before(o2.date)) return -1;
        if (o1.date.after(o2.date)) return 1;
        return -Integer.compare(o1.tag, o2.tag);
    };

    private final List<KHLMatchInfo> storage = new ArrayList<>();

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
}
