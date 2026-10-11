package com.kalispark.auto;

import com.kalispark.core.AutoEngine;
import com.kalispark.core.BaseModule;
import com.kalispark.core.Ctx;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class QuickScan extends BaseModule {

    private static final int[] PORTS = {
        21, 22, 23, 25, 53, 80, 110, 135, 139, 143, 443, 445,
        993, 995, 1433, 1521, 3306, 3389, 5432, 5900,
        6379, 8080, 8443, 8888, 9000, 9090, 27017
    };

    @Override public String name() { return "quick_scan"; }
    @Override public String category() { return "auto"; }
    @Override public int order() { return 1; }

    @Override
    public void run(Ctx ctx) {
        new AutoEngine()
            .add(new StepHost())
            .add(new StepPort())
            .add(new StepService())
            .run(ctx);
    }

    // Step 1: host discovery
    static class StepHost implements AutoEngine.Step {
        @Override public String name() { return "主机发现"; }

        @Override
        public void run(Ctx ctx, AutoEngine.Report r) throws Exception {
            ExecutorService pool = Executors.newFixedThreadPool(64);
            AtomicInteger found = new AtomicInteger();
            CountDownLatch latch = new CountDownLatch(254);

            for (int i = 1; i <= 254; i++) {
                final String host = ctx.subnet + "." + i;
                pool.submit(() -> {
                    try {
                        if (ping(host, 80) || ping(host, 443)
                            || ping(host, 22) || ping(host, 53)) {
                            ctx.out.emit("  " + host);
                            found.incrementAndGet();
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
            pool.shutdown();
            try { latch.await(); } catch (InterruptedException ignored) {}
            r.aliveHosts = found.get();
        }

        private boolean ping(String host, int port) {
            try (Socket s = new Socket()) {
                s.connect(new InetSocketAddress(host, port), 300);
                return true;
            } catch (Exception e) { return false; }
        }
    }

    // Step 2: port scan gateway
    static class StepPort implements AutoEngine.Step {
        @Override public String name() { return "端口扫描"; }

        @Override
        public void run(Ctx ctx, AutoEngine.Report r) throws Exception {
            ExecutorService pool = Executors.newFixedThreadPool(32);
            AtomicInteger open = new AtomicInteger();
            CountDownLatch latch = new CountDownLatch(PORTS.length);

            for (int p : PORTS) {
                pool.submit(() -> {
                    try {
                        try (Socket s = new Socket()) {
                            s.connect(new InetSocketAddress(ctx.gateway, p), 400);
                            ctx.out.emit("  开放 " + p);
                            open.incrementAndGet();
                        }
                    } catch (Exception ignored) {
                    } finally {
                        latch.countDown();
                    }
                });
            }
            pool.shutdown();
            try { latch.await(); } catch (InterruptedException ignored) {}
            r.openPorts = open.get();
        }
    }

    // Step 3: service fingerprint
    static class StepService implements AutoEngine.Step {
        @Override public String name() { return "服务识别"; }

        @Override
        public void run(Ctx ctx, AutoEngine.Report r) throws Exception {
            String[] banners = {"21", "22", "80", "8080"};
            for (String p : banners) {
                try (Socket s = new Socket()) {
                    int port = Integer.parseInt(p);
                    s.connect(new InetSocketAddress(ctx.gateway, port), 800);
                    s.setSoTimeout(800);
                    byte[] buf = new byte[128];
                    int n = s.getInputStream().read(buf);
                    if (n > 0) {
                        String b = new String(buf, 0, n)
                            .replaceAll("[\\r\\n]+", " ").trim();
                        ctx.out.emit("  " + p + ": " + b);
                        r.findings++;
                    }
                } catch (Exception ignored) {}
            }
        }
    }
}
