package com.kalispark.auto;

import com.kalispark.core.AutoEngine;
import com.kalispark.core.BaseModule;
import com.kalispark.core.Ctx;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;

public class QuickBrute extends BaseModule {

    private static final String[] USERS = {
        "admin", "root", "user", "test", "guest"
    };

    private static final String[] PASSES = {
        "admin", "123456", "password", "root", "admin123",
        "12345678", "qwerty", "111111", "000000", "test",
        "woaini1314", "5201314", "admin888", "admin@123"
    };

    @Override public String name() { return "quick_brute"; }
    @Override public String category() { return "auto"; }
    @Override public int order() { return 3; }
    @Override public boolean requiresAuth() { return true; }

    @Override
    public void run(Ctx ctx) {
        new AutoEngine()
            .add(new StepRedis())
            .add(new StepMySQL())
            .add(new StepSSH())
            .add(new StepWebLogin())
            .run(ctx);
    }

    static class StepRedis implements AutoEngine.Step {
        @Override public String name() { return "Redis 未授权"; }
        @Override
        public void run(Ctx ctx, AutoEngine.Report r) throws Exception {
            try (Socket s = new Socket()) {
                s.connect(new InetSocketAddress(ctx.gateway, 6379), 2000);
                s.setSoTimeout(2000);
                OutputStream os = s.getOutputStream();
                InputStream is = s.getInputStream();
                os.write("PING\r\n".getBytes());
                os.flush();
                byte[] buf = new byte[64];
                int n = is.read(buf);
                if (n > 0) {
                    String resp = new String(buf, 0, n).trim();
                    if (resp.contains("PONG")) {
                        ctx.out.emit("  [高危] Redis 未授权");
                        r.findings++;
                    }
                }
            } catch (Exception ignored) {}
        }
    }

    static class StepMySQL implements AutoEngine.Step {
        @Override public String name() { return "MySQL 探测"; }
        @Override
        public void run(Ctx ctx, AutoEngine.Report r) throws Exception {
            try (Socket s = new Socket()) {
                s.connect(new InetSocketAddress(ctx.gateway, 3306), 2000);
                s.setSoTimeout(2000);
                byte[] buf = new byte[128];
                int n = s.getInputStream().read(buf);
                if (n > 4) {
                    int version = buf[3];
                    ctx.out.emit("  MySQL 服务在线，协议版本 " + version);
                    r.findings++;
                }
            } catch (Exception ignored) {}
        }
    }

    static class StepSSH implements AutoEngine.Step {
        @Override public String name() { return "SSH 探测"; }
        @Override
        public void run(Ctx ctx, AutoEngine.Report r) throws Exception {
            try (Socket s = new Socket()) {
                s.connect(new InetSocketAddress(ctx.gateway, 22), 2000);
                s.setSoTimeout(2000);
                byte[] buf = new byte[128];
                int n = s.getInputStream().read(buf);
                if (n > 0) {
                    String banner = new String(buf, 0, n).trim();
                    ctx.out.emit("  " + banner);
                    r.findings++;
                }
            } catch (Exception ignored) {}
        }
    }

    static class StepWebLogin implements AutoEngine.Step {
        @Override public String name() { return "Web 登录爆破"; }

        @Override
        public void run(Ctx ctx, AutoEngine.Report r) throws Exception {
            String url = "http://" + ctx.gateway + "/login";
            int hits = 0;

            for (String u : USERS) {
                for (String p : PASSES) {
                    if (tryLogin(ctx, url, u, p)) hits++;
                }
            }
            r.findings += hits;
            if (hits == 0) ctx.out.emit("  未发现弱口令");
        }

        private boolean tryLogin(Ctx ctx, String url, String u, String p) {
            try {
                String body = "username=" + URLEncoder.encode(u, "UTF-8")
                    + "&password=" + URLEncoder.encode(p, "UTF-8");

                HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
                c.setConnectTimeout(2500);
                c.setReadTimeout(2500);
                c.setRequestMethod("POST");
                c.setDoOutput(true);
                c.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
                c.setRequestProperty("User-Agent", "KaliSpark/0.1");

                try (OutputStream os = c.getOutputStream()) {
                    os.write(body.getBytes("UTF-8"));
                }

                int code = c.getResponseCode();
                String loc = c.getHeaderField("Location");
                if (code == 302 && loc != null
                    && !loc.toLowerCase().contains("login")) {
                    ctx.out.emit("  [命中] " + u + " : " + p);
                    return true;
                }
            } catch (Exception ignored) {}
            return false;
        }
    }
}
