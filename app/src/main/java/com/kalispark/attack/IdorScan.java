package com.kalispark.attack;

import com.kalispark.core.BaseModule;
import com.kalispark.core.Ctx;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class IdorScan extends BaseModule {

    private static final String[] IDS = {
        "1", "2", "3", "10", "100", "0", "-1",
        "admin", "root", "me", "self", "current"
    };

    @Override public String name() { return "idor_scan"; }
    @Override public String category() { return "attack"; }
    @Override public int order() { return 20; }
    @Override public boolean requiresAuth() { return true; }

    @Override
    public void run(Ctx ctx) {
        String base = "http://" + ctx.gateway + "/api/user/";
        ctx.out.emit("=== 水平越权检测 ===");

        int hits = 0;
        for (String id : IDS) {
            String body = get(base + id);
            if (body == null || body.isEmpty()) continue;
            int len = body.length();
            ctx.out.emit("  [" + id + "] 长度=" + len);
            hits++;
        }
        if (hits == 0) {
            ctx.out.emit("  未发现可访问的用户接口");
        } else {
            ctx.out.emit("  可访问 " + hits + " 个 ID，需人工确认是否越权");
        }
        ctx.out.emit("完成");
    }

    private String get(String url) {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
            c.setConnectTimeout(3000);
            c.setReadTimeout(3000);
            c.setRequestProperty("User-Agent", "KaliSpark/0.1");
            int code = c.getResponseCode();
            if (code >= 400) return null;
            BufferedReader br = new BufferedReader(
                new InputStreamReader(c.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String l;
            while ((l = br.readLine()) != null) sb.append(l);
            return sb.toString();
        } catch (Exception e) { return null; }
    }
}
