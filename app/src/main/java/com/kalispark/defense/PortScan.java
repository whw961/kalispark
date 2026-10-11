package com.kalispark.defense;

import com.kalispark.core.BaseModule;
import com.kalispark.core.Ctx;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PortScan extends BaseModule {

    private static final int[] PORTS = {
        21, 22, 23, 25, 53, 80, 110, 135, 139, 143, 443, 445,
        993, 995, 1433, 1521, 3306, 3389, 5432, 5900,
        6379, 8080, 8443, 8888, 9000, 9090, 27017
    };

    @Override public String name() { return "port_scan"; }
    @Override public String category() { return "defense"; }
    @Override public int order() { return 2; }

    @Override
    public void run(Ctx ctx) {
        String target = ctx.gateway;
        ctx.out.emit("=== 端口扫描: " + target + " ===");

        ExecutorService pool = Executors.newFixedThreadPool(32);
        AtomicInteger open = new AtomicInteger();

        for (int p : PORTS) {
            pool.submit(() -> {
                if (ping(target, p)) {
                    ctx.out.emit("开放 " + p);
                    open.incrementAndGet();
                }
            });
        }
        pool.shutdown();
        try { pool.awaitTermination(20, TimeUnit.SECONDS); } catch (Exception ignored) {}
        ctx.out.emit("完成，开放 " + open.get() + " 个");
    }

    private boolean ping(String host, int port) {
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(host, port), 500);
            return true;
        } catch (Exception e) { return false; }
    }
}
