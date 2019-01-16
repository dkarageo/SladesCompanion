package com.dkarageo.sladescompanion.preferences;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dkarageo.sladescompanion.App;


public class PreferencesController {

    public static String getDBHostname() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        return sp.getString("preference_db_hostname", null);
    }

    public static int getDBPort() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        return sp.getInt("preference_db_port", 0);
    }

    public static String getDBName() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        return sp.getString("preference_db_dbname", null);
    }

    public static String getDBUsername() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        return sp.getString("preference_db_username", null);
    }
    public static String getDBPassword() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        return sp.getString("preference_db_password", null);
    }
}
