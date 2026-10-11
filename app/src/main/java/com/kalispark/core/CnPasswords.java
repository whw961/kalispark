package com.kalispark.core;

public class CnPasswords {

    // Common top Chinese passwords from public breach statistics
    public static final String[] COMMON = {
        "123456", "12345678", "123456789", "1234567890",
        "111111", "000000", "666666", "888888", "123123",
        "abc123", "qwerty", "qwerty123", "password", "passw0rd",
        "a123456", "a123456789", "aa123456", "q1w2e3r4",
        "88888888", "66666666", "123123123", "111111111",
        "iloveyou", "woaini", "woaini1314", "woaini520",
        "5201314", "1314520", "520520", "521521",
        "wangyi", "wanglei", "zhangwei", "liwei",
        "qwe123", "qweasd", "zxcvbnm", "asdfgh", "1qaz2wsx",
        "xiaoming", "xiaohong", "lingling", "xingxing",
        "hahaha", "hehehe", "lalala", "xixixi"
    };

    // Router / IoT default credentials (public vendor manuals)
    public static final String[] DEFAULT_CREDS = {
        "admin:admin", "admin:password", "admin:123456",
        "admin:admin123", "admin:888888", "admin:666666",
        "root:root", "root:admin", "root:123456", "root:toor",
        "user:user", "user:123456", "guest:guest", "guest:123456",
        "admin:admin888", "admin:admin@123", "admin:admin123456",
        "admin:password123", "admin:12345678", "admin:111111",
        "support:support", "operator:operator", "manager:manager",
        "admin:", "root:", "administrator:administrator"
    };

    // Chinese social/ID-based patterns
    public static final String[] CN_PATTERNS = {
        "5201314", "1314520", "woaini1314", "woaini520",
        "1314520woaini", "520131400", "woaini1314520",
        "5201314520", "13145201314", "5205201314"
    };
}
