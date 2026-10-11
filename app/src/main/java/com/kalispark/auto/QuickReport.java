package com.kalispark.auto;

import com.kalispark.core.BaseModule;
import com.kalispark.core.Ctx;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QuickReport extends BaseModule {

    @Override public String name() { return "quick_report"; }
    @Override public String category() { return "auto"; }
    @Override public int order() { return 9; }

    @Override
    public void run(Ctx ctx) {
        ctx.out.emit("");
        ctx.out.emit("═══════════════════════════");
        ctx.out.emit("      测 试 报 告");
        ctx.out.emit("═══════════════════════════");
        ctx.out.emit("");

        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
            .format(new Date());
        ctx.out.emit("【基本信息】");
        ctx.out.emit("  报告时间 : " + time);
        ctx.out.emit("  测试网段 : " + ctx.subnet + ".0/24");
        ctx.out.emit("  测试网关 : " + ctx.gateway);
        ctx.out.emit("  工具版本 : KaliSpark 0.1.0");
        ctx.out.emit("");

        ctx.out.emit("【执行模块】");
        ctx.out.emit("  · 网络信息");
        ctx.out.emit("  · 主机发现");
        ctx.out.emit("  · 端口扫描");
        ctx.out.emit("  · 服务识别");
        ctx.out.emit("  · SQL 注入");
        ctx.out.emit("  · CORS 检测");
        ctx.out.emit("  · 默认口令");
        ctx.out.emit("");

        ctx.out.emit("【风险说明】");
        ctx.out.emit("  开放端口如 21/23/445/3389 等应限制访问");
        ctx.out.emit("  Redis 6379 无密码可被写入任意文件");
        ctx.out.emit("  默认口令可导致设备被完全控制");
        ctx.out.emit("  水平越权可读取他人数据");
        ctx.out.emit("");

        ctx.out.emit("【修复建议】");
        ctx.out.emit("  1. 修改所有设备默认口令，使用强密码");
        ctx.out.emit("  2. 关闭不必要的端口，限制访问来源");
        ctx.out.emit("  3. 登录接口增加验证码和频率限制");
        ctx.out.emit("  4. 接口增加用户身份校验");
        ctx.out.emit("  5. 定期更新固件和系统补丁");
        ctx.out.emit("");

        ctx.out.emit("═══════════════════════════");
        ctx.out.emit("  报告结束");
        ctx.out.emit("═══════════════════════════");
    }
}
