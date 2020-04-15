package com.yy.util;

public class OsUtil {

    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isLinux() {
        return OS.contains("linux");
    }

    public static boolean isMacOS() {
        return OS.contains("mac") && OS.contains("os");
    }

    public static boolean isWindows() {
        return OS.contains("windows");
    }
}
