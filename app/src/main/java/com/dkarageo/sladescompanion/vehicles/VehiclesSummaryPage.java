package com.dkarageo.sladescompanion.vehicles;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dkarageo.sladescompanion.R;
import com.dkarageo.sladescompanion.db.MaintainerDBProxy;
import com.dkarageo.sladescompanion.vehicles.simulator.SimulationsManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class VehiclesSummaryPage extends Fragment {

    private static final int UI_SIMULATION_DATA_UPDATE_INTERVAL = 1000;  // 1 sec
    private static final int UI_SYSTEM_DATA_UPDATE_INTERVAL = 2000;  // 2 secs

    // UI Handlers
    TextView mConnectedCars;
    TextView mObstaclesSpotted;
    TextView mActiveSimulations;
    TextView mTotalLatency;

    // Data acquired from DB.
    int     mVehiclesCount;
    int     mObstaclesCount;
    boolean mAreDBDataValid;  // Indicates that db data have been fetched at least one time.

    Handler mUIUpdateHandler;
    Runnable mUISimulationDataUpdateTask = new Runnable() {
        @Override
        public void run() {
            updateSimulationsData();
            mUIUpdateHandler.postDelayed(mUISimulationDataUpdateTask,
                                         UI_SIMULATION_DATA_UPDATE_INTERVAL);
        }
    };
    Runnable mUISystemDataUpdateTask = new Runnable() {
        @Override
        public void run() {
            new RemoteDataFetcher().execute();
            mUIUpdateHandler.postDelayed(mUISystemDataUpdateTask,
                                         UI_SYSTEM_DATA_UPDATE_INTERVAL);
        }
    };

    public VehiclesSummaryPage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUIUpdateHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(
                R.layout.vehicles_summary_page, container, false);

        mConnectedCars     = rootView.findViewById(R.id.vehicles_summary_connected_cars);
        mObstaclesSpotted  = rootView.findViewById(R.id.vehicles_summary_obstacles_spotted);
        mActiveSimulations = rootView.findViewById(R.id.vehicles_summary_active_simulations);
        mTotalLatency      = rootView.findViewById(R.id.vehicles_summary_total_latency);

        updateSimulationsData();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mUIUpdateHandler.removeCallbacks(mUISimulationDataUpdateTask);
        mUIUpdateHandler.postDelayed(mUISimulationDataUpdateTask,
                                     UI_SIMULATION_DATA_UPDATE_INTERVAL);

        // If there are cached data, wait update interval to refresh screen. Otherwise, fetch
        // immediately.
        if (mAreDBDataValid) updateSystemData();
        else new RemoteDataFetcher().execute();
        mUIUpdateHandler.postDelayed(mUISystemDataUpdateTask,
                UI_SYSTEM_DATA_UPDATE_INTERVAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        mUIUpdateHandler.removeCallbacks(mUISimulationDataUpdateTask);
    }

    private void updateSimulationsData() {
        SimulationsManager sm = SimulationsManager.getSimulationsManager();
        mActiveSimulations.setText(Integer.toString(sm.getActiveSimulationsCount()));
        mTotalLatency.setText(Integer.toString(sm.getTotalAverageLatencyForActiveSimulations()));
    }

    private void updateSystemData() {
        if (mAreDBDataValid) {
            mConnectedCars.setText(Integer.toString(mVehiclesCount));
            mObstaclesSpotted.setText(Integer.toString(mObstaclesCount));
        }
    }

    private class RemoteDataFetcher extends AsyncTask<Void, Void, Integer[]> {
        @Override
        protected Integer[] doInBackground(Void... params) {
            MaintainerDBProxy proxy = MaintainerDBProxy.getMaintainerDBProxy();
            int vehiclesCount = proxy.getVehiclesCount();
            int obstaclesCount = proxy.getObstaclesCount();
            return new Integer[] { vehiclesCount, obstaclesCount};
        }

        @Override
        protected void onPostExecute(Integer[] counts) {
            // No need to synchronize.
            mVehiclesCount = counts[0];
            mObstaclesCount = counts[1];
            mAreDBDataValid = true;
            updateSystemData();
        }
    }
}
