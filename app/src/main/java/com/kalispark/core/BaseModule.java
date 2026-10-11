package com.kalispark.core;

public abstract class BaseModule {

    public abstract String name();
    public abstract String category();
    public abstract void run(Ctx ctx) throws Exception;

    public boolean requiresAuth() { return false; }
    public int order() { return 100; }
}
