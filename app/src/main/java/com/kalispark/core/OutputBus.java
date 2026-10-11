package com.kalispark.core;

import android.os.Handler;
import android.os.Looper;

public class OutputBus {

    public interface Listener {
        void onLine(String line);
    }

    private final Handler ui = new Handler(Looper.getMainLooper());
    private Listener listener;

    public void attach(Listener l) { this.listener = l; }

    public void emit(String line) {
        if (listener == null) return;
        ui.post(() -> listener.onLine(line + "\n"));
    }
}
