package com.kalispark.core;

import java.util.ArrayList;
import java.util.List;

public class AutoEngine {

    public interface Step {
        String name();
        void run(Ctx ctx, Report report) throws Exception;
    }

    public static class Report {
        public final List<String> lines = new ArrayList<>();
        public int openPorts = 0;
        public int aliveHosts = 0;
        public int findings = 0;

        public void add(String line) {
            lines.add(line);
        }
    }

    private final List<Step> steps = new ArrayList<>();

    public AutoEngine add(Step s) {
        steps.add(s);
        return this;
    }

    public void run(Ctx ctx) {
        Report r = new Report();
        long start = System.currentTimeMillis();

        ctx.out.emit("");
        ctx.out.emit("═══════════════════════════");

        for (Step s : steps) {
            ctx.out.emit("▶ " + s.name());
            try {
                s.run(ctx, r);
            } catch (Exception e) {
                ctx.out.emit("  [错误] " + e.getMessage());
            }
        }

        long ms = System.currentTimeMillis() - start;
        ctx.out.emit("");
        ctx.out.emit("─── 汇总 ───");
        ctx.out.emit("存活主机 : " + r.aliveHosts);
        ctx.out.emit("开放端口 : " + r.openPorts);
        ctx.out.emit("发现项   : " + r.findings);
        ctx.out.emit("耗时     : " + (ms / 1000) + " 秒");
        ctx.out.emit("═══════════════════════════");
        ctx.out.emit("");
    }
}
