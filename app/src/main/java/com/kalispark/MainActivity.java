package com.kalispark;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kalispark.core.Ctx;
import com.kalispark.core.ModuleRegistry;
import com.kalispark.core.OutputBus;
import com.kalispark.ui.MainUI;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private OutputBus bus;
    private ModuleRegistry registry;
    private TextView console;
    private ScrollView consoleScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bus = new OutputBus();
        registry = new ModuleRegistry();

        console = findViewById(R.id.console);
        consoleScroll = findViewById(R.id.consoleScroll);
        LinearLayout tabBar = findViewById(R.id.tabBar);
        LinearLayout moduleArea = findViewById(R.id.moduleArea);
        TextView clearBtn = findViewById(R.id.clearBtn);

        clearBtn.setText("清屏");

        bus.attach(line -> {
            console.append(line);
            consoleScroll.post(() ->
                consoleScroll.fullScroll(ScrollView.FOCUS_DOWN));
        });

        clearBtn.setOnClickListener(v -> console.setText(""));

        requestPerms();

        String subnet = getSubnet();
        String gateway = getGateway();
        Ctx ctx = new Ctx(this, bus, subnet, gateway);

        MainUI ui = new MainUI(this, registry, bus);
        ui.attach(tabBar, moduleArea);
        ui.setCtx(ctx);

        bus.emit("闪灵 v0.1.0 已启动");
        bus.emit("网段   " + subnet + ".0/24");
        bus.emit("网关   " + gateway);
        bus.emit("");
    }

    private void requestPerms() {
        List<String> need = new ArrayList<>();
        String[] perms = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET
        };
        for (String p : perms) {
            if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED)
                need.add(p);
        }
        if (!need.isEmpty()) {
            requestPermissions(need.toArray(new String[0]), 1);
        }
    }

    private String getGateway() {
        WifiManager wm = (WifiManager) getApplicationContext()
            .getSystemService(WIFI_SERVICE);
        if (wm == null) return "192.168.1.1";
        DhcpInfo d = wm.getDhcpInfo();
        if (d == null) return "192.168.1.1";
        return ipStr(d.gateway);
    }

    private String getSubnet() {
        String gw = getGateway();
        int dot = gw.lastIndexOf('.');
        return dot > 0 ? gw.substring(0, dot) : "192.168.1";
    }

    private String ipStr(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "."
            + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }
}
