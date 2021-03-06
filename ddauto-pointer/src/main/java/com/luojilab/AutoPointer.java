package com.luojilab;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.PopupWindow;

import com.google.common.base.Preconditions;
import com.luojilab.init.AutoPointerInitializer;
import com.luojilab.utils.AutoPointerSwitch;
import com.luojilab.view.DDDecorView;

/**
 * user liushuo
 * date 2017/4/6
 */

public class AutoPointer {
    public static final String TAG = "AutoPointer";

    public enum SERVER_ENV {
        TEST("测试环境"),
        SIMULATION("仿真环境"),
        ONLINE("线上环境");

        private String mDes;

        SERVER_ENV(String des) {
            this.mDes = des;
        }

        @Override
        public String toString() {
            return mDes;
        }

    }

    private static SERVER_ENV sServerEnv;

    static {
        initServerEnv();
    }

    /*是否发送调试埋点*/
    private static boolean sDebugPoint = !AutoPointer.isOnlineEnv();

    /*自动打点功能是否启用,全局有效,默认值为false(小部分用户测试稳定性)*/
    private static boolean sEnableAutoPoint = false;

    private AutoPointer() {
    }

    private static void initServerEnv() {
        String type = AutoPointerInitializer.getInstance().getServerEnvironment();
        switch (type) {
            case "线上":
                sServerEnv = SERVER_ENV.ONLINE;
                break;
            case "仿真":
                sServerEnv = SERVER_ENV.SIMULATION;
                break;
            case "测试":
                sServerEnv = SERVER_ENV.TEST;
                break;
            default:
                sServerEnv = SERVER_ENV.ONLINE;
                break;
        }
    }

    public static boolean isOnlineEnv() {
        return sServerEnv == SERVER_ENV.ONLINE;
    }

    @NonNull
    public static SERVER_ENV getServerEnv() {
        return sServerEnv;
    }

    public static boolean isAutoPointEnable() {
        return sEnableAutoPoint;
    }

    public static void enableAutoPoint(boolean enable) {
        sEnableAutoPoint = enable;

        AutoPointerSwitch.getInstance().enableAutoPoint(enable);
    }

    public static boolean isDebugPoint() {
        return sDebugPoint;
    }

    /**
     * 开启调试埋点功能(埋点发送全量数据)
     *
     * @param enable
     */
    public static void enableDebugPoint(boolean enable) {
        sDebugPoint = enable;
    }

    @NonNull
    public static DataConfigureImp wrapWindowCallback(@NonNull Window window) {
        Preconditions.checkNotNull(window, "Window is null in AutoPointer.wrapWindowCallback()");

        Window.Callback callback = window.getCallback();
        View decorView = window.getDecorView();

        WindowCallbackWrapper wrapper = new WindowCallbackWrapper(decorView, callback);
        window.setCallback(wrapper);
        return wrapper;
    }

    @NonNull
    public static DataConfigureImp wrapWindowCallback(@NonNull Activity activity) {
        Preconditions.checkNotNull(activity, "activity is null in AutoPointer.wrapWindowCallback()");

        Window window = activity.getWindow();

        return wrapWindowCallback(window);
    }

    @NonNull
    public static DataConfigureImp wrapWindowCallback(@NonNull Dialog dialog) {
        Preconditions.checkNotNull(dialog, "dialog is null in AutoPointer.wrapWindowCallback()");

        Window window = dialog.getWindow();

        return wrapWindowCallback(window);
    }

    @NonNull
    public static DataConfigureImp wrapWindowCallback(@NonNull PopupWindow popup) {
        Preconditions.checkNotNull(popup, "pop window is null in AutoPointer.wrapWindowCallback()");

        View contentView = popup.getContentView();
        Preconditions.checkNotNull(contentView, "wrap PopupWindow before it has a ContentView");

        DDDecorView decorView = new DDDecorView(contentView.getContext());
        decorView.addView(contentView);

        popup.setContentView(decorView);

        WindowCallbackWrapper wrapper = new WindowCallbackWrapper(decorView, null);
        decorView.setCallbackWrapper(wrapper);
        return wrapper;
    }

}



