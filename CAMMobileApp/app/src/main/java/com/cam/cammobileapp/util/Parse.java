package com.cam.cammobileapp.util;

public class Parse {

    public static boolean toBool(String var) {
        if (var.equals("1")) {
            return true;
        } else if (var.equals("0")) {
            return false;
        }
        throw new IllegalArgumentException("'" + var + "' is not '1' or '0' and cannot be parsed to boolean.");
    }

    public static int toInt(String var) {
        return Integer.parseInt(var);
    }

    public static float toFloat(String var) {
        return Float.parseFloat(var);
    }

    public static String toString(boolean var) {
        return var ? "1" : "0";
    }

    public static String toString(int var) {
        return Integer.toString(var);
    }

    public static String toString(float var) {
        return Float.toString(var);
    }

    public static String toString(char var) {
        return String.valueOf(var);
    }

    public static String toString(String delimiter, Object... var) {
        int length = var.length;
        String[] s = new String[length];
        for (int i = 0; i < var.length; i++) {
            if (var[i] instanceof Boolean) {
                s[i] = toString((boolean) var[i]);
            } else if (var[i] instanceof Integer) {
                s[i] = toString((int) var[i]);
            } else if (var[i] instanceof Float) {
                s[i] = toString((float) var[i]);
            } else if (var[i] instanceof Character) {
                s[i] = toString((char) var[i]);
            } else if (var[i] instanceof String) {
                s[i] = (String) var[i];
            } else {
                throw new IllegalArgumentException(
                        "Cannot parse index " + i + " of type '" + var[i].getClass().getName() + "'");
            }
        }

        String joined = "";
        for(int i = 0; i < length; i++) {
            joined += s;
            if (i != length - 1) {
                joined += delimiter;
            }
        }

        return joined;
    }
}
