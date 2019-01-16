package com.dkarageo.sladescompanion.preferences;

import android.os.Bundle;

import com.dkarageo.sladescompanion.R;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;


public class PreferencesFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_layout, rootKey);

        EditTextPreference dbHostname = (EditTextPreference) findPreference("preferences_db_hostname");
    }
}
