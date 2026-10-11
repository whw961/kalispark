package com.kalispark.auto;

import com.kalispark.core.AutoEngine;
import com.kalispark.core.BaseModule;
import com.kalispark.core.CnPasswords;
import com.kalispark.core.Ctx;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;

public class QuickDefaultCred extends BaseModule {

    @Override public String name() { return "quick_default_cred"; }
    @Override public String category() { return "auto"; }
    @Override public int order() { return 5; }
    @Override public boolean requiresAuth() { return true; }

    @Override
    public void run(Ctx ctx) {
        new AutoEngine()
            .add(new StepWebLogin())
            .add(new StepDevice())
            .run(ctx);
    }

    static class StepWebLogin implements AutoEngine.Step {
        @Override public String name() { return "Web 默认口令"; }

        @Override
        public void run(Ctx ctx, AutoEngine.Report r) throws Exception {
            String url = "http://" + ctx.gateway + "/login";
            int hits = 0;
            for (String pair : CnPasswords.DEFAULT_CREDS) {
                String[] kv = pair.split(":", 2);
                String u = kv.length > 0 ? kv[0] : "";
                String p = kv.length > 1 ? kv[1] : "";
                if (tryLogin(ctx, url, u, p)) hits++;
            }
            r.findings += hits;
            if (hits == 0) ctx.out.emit("  默认口令未命中");
        }

        private boolean tryLogin(Ctx ctx, String url, String u, String p) {
            try {
                String body = "username=" + URLEncoder.encode(u, "UTF-8")
                    + "&password=" + URLEncoder.encode(p, "UTF-8")
                    + "&user=" + URLEncoder.encode(u, "UTF-8")
                    + "&pass=" + URLEncoder.encode(p, "UTF-8");

                HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
                c.setConnectTimeout(2000);
                c.setReadTimeout(2000);
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

    static class StepDevice implements AutoEngine.Step {
        @Override public String name() { return "设备默认口令（HTTP Basic）"; }

        @Override
        public void run(Ctx ctx, AutoEngine.Report r) throws Exception {
            String[] paths = {"/", "/admin", "/cgi-bin/luci"};
            String baseUrl = "http://" + ctx.gateway;

            for (String path : paths) {
                for (String pair : CnPasswords.DEFAULT_CREDS) {
                    try {
                        String auth = android.util.Base64.encodeToString(
                            pair.getBytes("UTF-8"),
                            android.util.Base64.NO_WRAP);
                        HttpURLConnection c = (HttpURLConnection)
                            new URL(baseUrl + path).openConnection();
                        c.setConnectTimeout(2000);
                        c.setReadTimeout(2000);
                        c.setRequestProperty("Authorization", "Basic " + auth);
                        c.setRequestProperty("User-Agent", "KaliSpark/0.1");
                        int code = c.getResponseCode();
                        if (code == 200) {
                            String www = c.getHeaderField("WWW-Authenticate");
                            if (www == null) {
                                ctx.out.emit("  [命中] " + path + " " + pair);
                                r.findings++;
                                return;
                            }
                        }
                    } catch (Exception ignored) {}
                }
            }
            ctx.out.emit("  HTTP Basic 未命中");
        }
    }
}
