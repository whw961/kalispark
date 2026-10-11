package com.kalispark.auto;

import com.kalispark.core.AutoEngine;
import com.kalispark.core.BaseModule;
import com.kalispark.core.Ctx;

import java.net.InetSocketAddress;
import java.net.Socket;

public class QuickDetect extends BaseModule {

    private static final int[] AV_PORTS = {
        1344, 7007, 7777, 8080, 9080,  // 部分杀软管理端口
        1443, 1444, 8010, 8011         // 部分 EDR 端口
    };

    @Override public String name() { return "quick_detect"; }
    @Override public String category() { return "auto"; }
    @Override public int order() { return 4; }

    @Override
    public void run(Ctx ctx) {
        new AutoEngine()
            .add(new StepAvPort())
            .add(new StepProxy())
            .add(new StepDNS())
            .run(ctx);
    }

    static class StepAvPort implements AutoEngine.Step {
        @Override public String name() { return "杀软服务端口"; }

        @Override
        public void run(Ctx ctx, AutoEngine.Report r) throws Exception {
            int found = 0;
            for (int p : AV_PORTS) {
                try (Socket s = new Socket()) {
                    s.connect(new InetSocketAddress("127.0.0.1", p), 200);
                    ctx.out.emit("  本机端口 " + p + " 开放");
                    found++;
                } catch (Exception ignored) {}
            }
            if (found == 0) ctx.out.emit("  未检测到常见杀软服务端口");
            r.findings += found;
        }
    }

    static class StepProxy implements AutoEngine.Step {
        @Override public String name() { return "代理检测"; }

        @Override
        public void run(Ctx ctx, AutoEngine.Report r) throws Exception {
            String[] proxies = {"127.0.0.1:8888", "127.0.0.1:8080", "127.0.0.1:3128"};
            for (String p : proxies) {
                String[] parts = p.split(":");
                try (Socket s = new Socket()) {
                    s.connect(new InetSocketAddress(parts[0],
                        Integer.parseInt(parts[1])), 300);
                    ctx.out.emit("  检测到本地代理: " + p);
                    r.findings++;
                } catch (Exception ignored) {}
            }
        }
    }

    static class StepDNS implements AutoEngine.Step {
        @Override public String name() { return "DNS 劫持检测"; }

        @Override
        public void run(Ctx ctx, AutoEngine.Report r) throws Exception {
            try {
                java.net.InetAddress[] addrs = java.net.InetAddress
                    .getAllByName("www.baidu.com");
                if (addrs.length > 0) {
                    ctx.out.emit("  baidu.com -> " + addrs[0].getHostAddress());
                }
            } catch (Exception e) {
                ctx.out.emit("  DNS 解析异常: " + e.getMessage());
            }
        }
    }
}
