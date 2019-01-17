package com.dkarageo.sladescompanion.preferences;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.dkarageo.sladescompanion.R;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.preferences_wrapper, new PreferencesFragment())
                .commit();

        try {
            getSupportActionBar().setCustomView(R.layout.action_bar_layout);
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException ex) {}
    }
}
