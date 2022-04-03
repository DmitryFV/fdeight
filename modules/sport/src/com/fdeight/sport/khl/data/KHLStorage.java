package com.fdeight.sport.khl.data;

import java.util.Date;
import java.util.TreeMap;

public class KHLStorage {
    public final TreeMap<Date, KHLMatchInfo> storage = new TreeMap<>();

    public void add(final KHLMatchInfo info) {
        storage.put(info.date, info);
    }
}
