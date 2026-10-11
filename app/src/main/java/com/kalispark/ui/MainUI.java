package com.kalispark.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kalispark.R;
import com.kalispark.attack.SqliProbe;
import com.kalispark.auto.QuickBrute;
import com.kalispark.auto.QuickDetect;
import com.kalispark.auto.QuickDefaultCred;
import com.kalispark.auto.QuickReport;
import com.kalispark.attack.IdorScan;
import com.kalispark.auto.QuickPentest;
import com.kalispark.auto.QuickScan;
import com.kalispark.core.Auth;
import com.kalispark.core.BaseModule;
import com.kalispark.core.Ctx;
import com.kalispark.core.ModuleRegistry;
import com.kalispark.core.NameMap;
import com.kalispark.core.OutputBus;
import com.kalispark.defense.NetInfo;
import com.kalispark.defense.PortScan;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainUI {

    private static final int COLOR_DEFENSE = 0xFF00E676;
    private static final int COLOR_ATTACK = 0xFFFF5252;
    private static final int COLOR_RECON = 0xFF40C4FF;
    private static final int COLOR_AUTO = 0xFFFFC107;
    private static final int COLOR_DIM = 0xFF8AA0B4;
    private static final int COLOR_SUB = 0xFF6A7A8A;
    private static final int COLOR_DARK = 0xFF080B10;

    private final Context ctx;
    private final ModuleRegistry registry;
    private final OutputBus bus;
    private final ExecutorService pool = Executors.newCachedThreadPool();

    private LinearLayout tabBar;
    private LinearLayout moduleArea;
    private Ctx appCtx;

    public MainUI(Context ctx, ModuleRegistry registry, OutputBus bus) {
        this.ctx = ctx;
        this.registry = registry;
        this.bus = bus;
    }

    public void attach(LinearLayout tabBar, LinearLayout moduleArea) {
        this.tabBar = tabBar;
        this.moduleArea = moduleArea;
        registerModules();
        buildTabs();
        showTab("defense");
    }

    public void setCtx(Ctx c) { this.appCtx = c; }

    private void registerModules() {
        registry.register(new NetInfo());
        registry.register(new PortScan());
        registry.register(new SqliProbe());
        registry.register(new IdorScan());

        registry.register(new QuickScan());
        registry.register(new QuickPentest());
        registry.register(new QuickBrute());
        registry.register(new QuickDetect());
        registry.register(new QuickDefaultCred());
        registry.register(new QuickReport());
    }

    private void buildTabs() {
        addTab("defense", "防御");
        addTab("attack", "攻击");
        addTab("recon", "侦察");
        addTab("auto", "一键");
    }

    private void addTab(final String key, String label) {
        TextView t = new TextView(ctx);
        t.setText(label);
        t.setTextSize(13);
        t.setTypeface(Typeface.DEFAULT_BOLD);
        t.setPadding(56, 22, 56, 22);
        t.setBackgroundResource(R.drawable.tab_bg);
        t.setTextColor(COLOR_DIM);
        t.setTag(key);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(6, 0, 6, 0);
        t.setLayoutParams(lp);

        t.setOnClickListener(v -> showTab(key));
        tabBar.addView(t);
    }

    private void showTab(String key) {
        for (int i = 0; i < tabBar.getChildCount(); i++) {
            View v = tabBar.getChildAt(i);
            boolean sel = key.equals(v.getTag());
            v.setSelected(sel);
            ((TextView) v).setTextColor(sel ? COLOR_DARK : COLOR_DIM);
        }
        moduleArea.removeAllViews();
        for (final BaseModule m : registry.byCategory(key)) {
            moduleArea.addView(buildCard(m));
        }
    }

    private View buildCard(final BaseModule m) {
        LinearLayout card = new LinearLayout(ctx);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(bgFor(m.category()));
        card.setPadding(36, 28, 28, 28);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 12);
        card.setLayoutParams(lp);

        LinearLayout head = new LinearLayout(ctx);
        head.setOrientation(LinearLayout.HORIZONTAL);
        head.setGravity(android.view.Gravity.CENTER_VERTICAL);

        TextView badge = new TextView(ctx);
        badge.setText("◆");
        badge.setTextSize(12);
        badge.setTextColor(colorFor(m.category()));
        head.addView(badge);

        TextView cat = new TextView(ctx);
        cat.setText("  " + catCn(m.category()));
        cat.setTextSize(11);
        cat.setTextColor(colorFor(m.category()));
        cat.setLetterSpacing(0.15f);
        head.addView(cat);

        card.addView(head);

        TextView title = new TextView(ctx);
        title.setText(NameMap.cn(m.name()));
        title.setTextSize(16);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(0xFFE4EAF2);
        title.setPadding(0, 10, 0, 0);
        card.addView(title);

        TextView sub = new TextView(ctx);
        sub.setText(m.requiresAuth() ? "需授权 · 点击运行" : "免授权 · 点击运行");
        sub.setTextSize(11);
        sub.setTextColor(COLOR_SUB);
        sub.setPadding(0, 6, 0, 0);
        card.addView(sub);

        card.setOnClickListener(v -> invoke(m));
        return card;
    }

    private int bgFor(String cat) {
        switch (cat) {
            case "attack": return R.drawable.card_bg_attack;
            case "recon": return R.drawable.card_bg_recon;
            case "auto": return R.drawable.card_bg_auto;
            default: return R.drawable.card_bg_defense;
        }
    }

    private String catCn(String cat) {
        switch (cat) {
            case "attack": return "攻击";
            case "recon": return "侦察";
            case "auto": return "一键";
            default: return "防御";
        }
    }

    private int colorFor(String cat) {
        switch (cat) {
            case "attack": return COLOR_ATTACK;
            case "recon": return COLOR_RECON;
            case "auto": return COLOR_AUTO;
            default: return COLOR_DEFENSE;
        }
    }

    private void invoke(final BaseModule m) {
        if (appCtx == null) {
            bus.emit("[!] 尚未就绪");
            return;
        }
        if (m.requiresAuth()) {
            Auth.confirm(ctx, appCtx.gateway, () -> runAsync(m));
        } else {
            runAsync(m);
        }
    }

    private void runAsync(final BaseModule m) {
        bus.emit("");
        bus.emit("▶ " + NameMap.cn(m.name()));
        pool.submit(() -> {
            try {
                m.run(appCtx);
            } catch (Throwable t) {
                bus.emit("[!] " + t);
            }
        });
    }
}
