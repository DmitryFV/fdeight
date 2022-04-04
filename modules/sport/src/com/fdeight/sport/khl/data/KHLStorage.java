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
}
