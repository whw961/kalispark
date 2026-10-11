package com.kalispark.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ModuleRegistry {

    private final List<BaseModule> all = new ArrayList<>();

    public void register(BaseModule m) {
        all.add(m);
    }

    public List<BaseModule> all() {
        return all;
    }

    public List<BaseModule> byCategory(String cat) {
        List<BaseModule> out = new ArrayList<>();
        for (BaseModule m : all) {
            if (cat.equals(m.category())) out.add(m);
        }
        Collections.sort(out, Comparator.comparingInt(BaseModule::order));
        return out;
    }

    public int total() { return all.size(); }
}
