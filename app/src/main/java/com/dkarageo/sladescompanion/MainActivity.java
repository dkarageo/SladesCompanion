package com.dkarageo.sladescompanion;

import com.dkarageo.sladescompanion.authorities.AuthoritiesFragment;
import com.dkarageo.sladescompanion.db.MaintainerDBProxy;
import com.dkarageo.sladescompanion.preferences.PreferencesActivity;
import com.dkarageo.sladescompanion.vehicles.VehiclesFragment;
import com.dkarageo.sladescompanion.vehicles.simulator.SimulationsManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navBar;

    // Keep a reference of SimulationsManager singleton, to be sure that it lives as long as main
    // activity lives.
    private SimulationsManager mSimulationsManager;

    // A map for all active fragments navigable from bottom navigation bar.
    private Map<String, Fragment> mainScreens = new HashMap<>();
    // An indicator set to true when a fragment change is caused by a press to back button (and
    // e.g. not from a click to navigation bar buttons).
    private boolean switchCausedByBackButton = false;


    // Listener for clicks on bottom navigation bar items.
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selected;

            if (!switchCausedByBackButton) {
                switch (item.getItemId()) {
                    case R.id.navbar_vehicles:
                        if (mainScreens.containsKey("vehicles_screen")) {
                            selected = mainScreens.get("vehicles_screen");
                        } else {
                            selected = new VehiclesFragment();
                            mainScreens.put("vehicles_screen", selected);
                        }
                        break;

                    case R.id.navbar_authorities:
                        if (mainScreens.containsKey("authorities_screen")) {
                            selected = mainScreens.get("authorities_screen");
                        } else {
                            selected = new AuthoritiesFragment();
                            mainScreens.put("authorities_screen", selected);
                        }
                        break;

                    default:
                        return false;
                }

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainActivity_contentWrapper, selected);
                ft.addToBackStack(Integer.toString(item.getItemId()));
                ft.commit();
            }

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // Set vehicles' screen to be the initial screen.
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment vehiclesScreen = new VehiclesFragment();
            ft.add(R.id.mainActivity_contentWrapper, vehiclesScreen);
            mainScreens.put("vehicles_screen", vehiclesScreen);
            ft.commit();
        }

        mSimulationsManager = SimulationsManager.getSimulationsManager();

        navBar = findViewById(R.id.navbar);
        if (navBar == null) throw new RuntimeException("Failed to acquire bottom navigation bar.");
        navBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        try {
            getSupportActionBar().setCustomView(R.layout.action_bar_layout);
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        } catch (NullPointerException ex) {}
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFirstRun()) {
            new RemoteConnectionChecker().
                    executeOnExecutor(Executors.newFixedThreadPool(16));
        }
    }

    @Override
    public void onBackPressed() {

        FragmentManager fm = getSupportFragmentManager();
        switchCausedByBackButton = true;

        int backStackCount;
        if ((backStackCount = fm.getBackStackEntryCount()) > 0) {
            // Update active item at bottom navigation bar when pressing back button.
            int menuItemId;

            // On first transaction, the previous fragment is always the home screen.
            // All fragment transactions are expected to contain in their 'name' the id of menu
            // item their containing fragment wants to be active (while the fragment is active).
            if (backStackCount > 1) {
                FragmentManager.BackStackEntry t = fm.getBackStackEntryAt(backStackCount - 2);
                menuItemId = Integer.parseInt(t.getName());
            } else {
                menuItemId = R.id.navbar_vehicles;
            }

            navBar.setSelectedItemId(menuItemId);

            fm.popBackStack();
        } else {
            super.onBackPressed();
        }

        switchCausedByBackButton = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSimulationsManager.terminateAllSimulations();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.action_bar_preferences:
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayNoConnectivityDialog(boolean firstRun) {
        NoConnectivityDialogFragment
                .newInstance(firstRun)
                .show(getSupportFragmentManager(), "no_connectivity_dialog");
    }

    private boolean isFirstRun() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstRun = sp.getBoolean("com.dkarageo.sladescompanion.first_run", true);

        if (firstRun) {
            displayNoConnectivityDialog(true);
            sp.edit().putBoolean("com.dkarageo.sladescompanion.first_run", false).apply();
        }

        return firstRun;
    }


    private class RemoteConnectionChecker extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return MaintainerDBProxy.getMaintainerDBProxy().isConnectionValid();
        }

        @Override
        protected void onPostExecute(Boolean isConnectionValid) {
            if (!isConnectionValid) displayNoConnectivityDialog(false);
        }
    }
}
