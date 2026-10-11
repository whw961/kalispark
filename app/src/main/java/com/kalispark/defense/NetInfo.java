package com.kalispark.defense;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import com.kalispark.core.BaseModule;
import com.kalispark.core.Ctx;

public class NetInfo extends BaseModule {

    @Override public String name() { return "net_info"; }
    @Override public String category() { return "defense"; }
    @Override public int order() { return 1; }

    @Override
    public void run(Ctx ctx) {
        ctx.out.emit("=== 网络信息 ===");
        WifiManager wm = (WifiManager) ctx.app
            .getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm == null) { ctx.out.emit("无 WiFi 服务"); return; }
        DhcpInfo d = wm.getDhcpInfo();
        if (d == null) { ctx.out.emit("无 DHCP 信息"); return; }
        ctx.out.emit("本机 IP   : " + ipStr(d.ipAddress));
        ctx.out.emit("网关      : " + ipStr(d.gateway));
        ctx.out.emit("子网掩码  : " + ipStr(d.netmask));
        ctx.out.emit("DNS1      : " + ipStr(d.dns1));
        ctx.out.emit("DNS2      : " + ipStr(d.dns2));
    }

    private String ipStr(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "."
            + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }
}
