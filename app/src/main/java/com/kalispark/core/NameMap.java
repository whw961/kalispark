package com.kalispark.core;

import java.util.HashMap;
import java.util.Map;

public class NameMap {

    private static final Map<String, String> CN = new HashMap<>();

    static {
        CN.put("sqli_probe", "SQL 注入");
        CN.put("xss_probe", "XSS 跨站");
        CN.put("cmd_inject", "命令注入");
        CN.put("ssti_probe", "SSTI 模板注入");
        CN.put("xxe_probe", "XXE 实体注入");
        CN.put("ldap_probe", "LDAP 注入");
        CN.put("nosql_probe", "NoSQL 注入");
        CN.put("crlf_inject", "CRLF 注入");
        CN.put("ssrf_probe", "SSRF 服务端请求伪造");
        CN.put("open_redirect", "开放重定向");
        CN.put("path_traversal", "目录穿越");
        CN.put("file_upload", "文件上传测试");
        CN.put("weak_pass", "弱口令爆破");
        CN.put("cn_weak_pass", "中文弱口令");
        CN.put("quick_scan", "一键扫描");
        CN.put("quick_pentest", "一键渗透");
        CN.put("quick_brute", "一键破解");
        CN.put("quick_detect", "一键检测");
        CN.put("quick_default_cred", "默认口令检测");
        CN.put("quick_report", "生成报告");
        CN.put("idor_scan", "水平越权");
        CN.put("brute_login", "登录爆破");
        CN.put("jwt_attack", "JWT 攻击");
        CN.put("auth_bypass", "认证绕过");
        CN.put("idor", "越权访问");
        CN.put("http_methods", "HTTP 方法枚举");
        CN.put("host_header", "Host 头注入");
        CN.put("redis_unauth", "Redis 未授权");
        CN.put("mysql_unauth", "MySQL 未授权");
        CN.put("mongo_unauth", "MongoDB 未授权");
        CN.put("es_unauth", "Elasticsearch 未授权");
        CN.put("struts2", "Struts2 漏洞");
        CN.put("spring_rce", "Spring RCE");
        CN.put("shiro_rce", "Shiro 反序列化");
        CN.put("fastjson", "Fastjson 反序列化");
        CN.put("log4j", "Log4j2 漏洞");
        CN.put("weblogic", "WebLogic 漏洞");
        CN.put("dedecms", "织梦 CMS");
        CN.put("discuz", "Discuz 论坛");
        CN.put("phpcms", "PHPCMS");
        CN.put("empire", "帝国 CMS");
        CN.put("weaver", "泛微 OA");
        CN.put("seeyon", "致远 OA");
        CN.put("landray", "蓝凌 OA");
        CN.put("tongda", "通达 OA");
        CN.put("hikvision", "海康威视");
        CN.put("dahua", "大华设备");
        CN.put("huawei_dev", "华为设备");
        CN.put("h3c_dev", "H3C 设备");
        CN.put("tplink_dev", "TP-Link 设备");

        CN.put("net_info", "网络信息");
        CN.put("host_discovery", "主机发现");
        CN.put("port_scan", "端口扫描");
        CN.put("dns_check", "DNS 查询");
        CN.put("http_probe", "HTTP 探测");
        CN.put("wifi_scan", "WiFi 扫描");
        CN.put("ssl_info", "SSL 证书");
        CN.put("https_check", "HTTPS 强制");
        CN.put("subnet_calc", "子网计算");
        CN.put("high_ports", "高危端口");
        CN.put("ping_test", "连通测试");
        CN.put("port_watch", "端口监控");
        CN.put("file_integrity", "文件完整性");
        CN.put("baseline_check", "基线核查");
        CN.put("ssl_grade", "SSL 评级");
        CN.put("wifi_security", "WiFi 安全评估");
        CN.put("ble_scan", "蓝牙扫描");

        CN.put("subdomain_enum", "子域枚举");
        CN.put("banner_grab", "Banner 抓取");
        CN.put("waf_detect", "WAF 识别");
        CN.put("whois_lookup", "Whois 查询");
        CN.put("reverse_dns", "反查 DNS");
        CN.put("traceroute", "路由追踪");
        CN.put("robots_parser", "Robots 解析");
        CN.put("security_headers", "安全响应头");
        CN.put("email_harvest", "邮箱收集");
        CN.put("tech_fingerprint", "技术栈指纹");
        CN.put("port_banner", "端口 Banner");
        CN.put("cert_transparency", "证书透明日志");
        CN.put("http_headers", "HTTP 响应头");
        CN.put("cn_device_fp", "国产设备指纹");
        CN.put("cdn_detect", "CDN 识别");
        CN.put("cloud_detect", "云厂商识别");
        CN.put("icp_query", "ICP 备案");
        CN.put("company_info", "工商信息");
        CN.put("dns_history", "DNS 历史");
    }

    public static String cn(String key) {
        String v = CN.get(key);
        return v != null ? v : key;
    }
}
