package com.heyheyda.tradeagent.util;


import com.heyheyda.tradeagent.BuildConfig;

public class Log {

    private static final String DEFAULT_TAG = BuildConfig.APPLICATION_ID;
    private static boolean isBuildInDebugMode = BuildConfig.DEBUG;

    /**
     * To prevent someone from accidentally instantiating the contract class, give it an empty constructor.
     */
    private Log() {}

    public static void d(String message) {
        if (isBuildInDebugMode) android.util.Log.d(DEFAULT_TAG, message);
    }

    public static void d(String tag, String message) {
        if (isBuildInDebugMode) android.util.Log.d(tag, message);
    }

    public static void d(String tag, String message, Throwable throwable) {
        if (isBuildInDebugMode) android.util.Log.d(tag, message, throwable);
    }

    public static void i(String message) {
        if (isBuildInDebugMode) android.util.Log.i(DEFAULT_TAG, message);
    }

    public static void i(String tag, String message) {
        if (isBuildInDebugMode) android.util.Log.i(tag, message);
    }

    public static void i(String tag, String message, Throwable throwable) {
        if (isBuildInDebugMode) android.util.Log.i(tag, message, throwable);
    }

    public static void e(String message) {
        if (isBuildInDebugMode) android.util.Log.e(DEFAULT_TAG, message);
    }

    public static void e(String tag, String message) {
        if (isBuildInDebugMode) android.util.Log.e(tag, message);
    }

    public static void e(String tag, String message, Throwable throwable) {
        if (isBuildInDebugMode) android.util.Log.e(tag, message, throwable);
    }

    public static void w(String message) {
        if (isBuildInDebugMode) android.util.Log.w(DEFAULT_TAG, message);
    }

    public static void w(String tag, String message) {
        if (isBuildInDebugMode) android.util.Log.w(tag, message);
    }

    public static void w(String tag, String message, Throwable throwable) {
        if (isBuildInDebugMode) android.util.Log.w(tag, message, throwable);
    }

    public static void printStackTrace(Throwable throwable) {
        if (isBuildInDebugMode) throwable.printStackTrace();
    }
}
