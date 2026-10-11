package com.kalispark.core;

import android.content.Context;

public class Ctx {
    public final Context app;
    public final OutputBus out;
    public final String subnet;
    public final String gateway;

    public Ctx(Context app, OutputBus out, String subnet, String gateway) {
        this.app = app;
        this.out = out;
        this.subnet = subnet;
        this.gateway = gateway;
    }
}
