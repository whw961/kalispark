package com.kalispark.core;

import android.app.AlertDialog;
import android.content.Context;

public class Auth {

    public interface Callback {
        void onAuthorized();
    }

    public static void confirm(Context ctx, String target, Callback cb) {
        new AlertDialog.Builder(ctx)
            .setTitle("授权确认")
            .setMessage("目标：" + target
                + "\n\n请确认你已获得对该目标进行测试的合法授权。")
            .setPositiveButton("已授权", (d, w) -> cb.onAuthorized())
            .setNegativeButton("取消", null)
            .show();
    }
}
