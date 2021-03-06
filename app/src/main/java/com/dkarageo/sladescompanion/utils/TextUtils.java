package com.dkarageo.sladescompanion.utils;

public class TextUtils {
    public static String toCamelCase(String s){
        String[] parts = s.split("_");
        String camelCaseString = "";
        for (int i = 0; i < parts.length; ++i){
            camelCaseString = camelCaseString + toProperCase(parts[i]);
            if (i < parts.length-1) camelCaseString += " ";
        }
        return camelCaseString;
    }

    public static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }
}
