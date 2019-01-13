package com.dkarageo.sladescompanion.vehicles.simulator;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import com.dkarageo.sladescompanion.db.MaintainerDBProxy;
import com.dkarageo.sladescompanion.units.Location;
import com.dkarageo.sladescompanion.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Simulation {

    private static final long UPDATE_INTERVAL = 1000L;  // 1 sec

    private Vehicle mVehicle;
    private boolean mIsRunning;
    private boolean mFirstRun;

    List<SimulationEventsListener> eventsListeners;

    LinkedList<Integer> latestLatencies;
    int latenciesSum;


    Simulation(Vehicle v) {
        mVehicle = v;
        eventsListeners = new ArrayList<>();
        latestLatencies = new LinkedList<>();
        mFirstRun = true;
    }

    public void start() {
        mIsRunning = true;
        new SimulationWorker().executeOnExecutor(Executors.newFixedThreadPool(16), mVehicle);
    }

    public void stop() {
        mIsRunning = false;
    }

    public long getUpdateInterval() { return UPDATE_INTERVAL; }

    public int getCurrentLatency() {
        if (latestLatencies.size() > 0) return latenciesSum / latestLatencies.size();
        else return 0;
    }

    public boolean isRunning() { return mIsRunning; }

    public void registerSimulationEventsListener(SimulationEventsListener l) {
        eventsListeners.add(l);
    }

    public void unregisterSimulationEventsListener(SimulationEventsListener l) {
        eventsListeners.remove(l);
    }

    public void notifyOnSimulationError(String error) {
        for (SimulationEventsListener l : eventsListeners) l.onSimulationError(error);
    }

    public void notifyOnSimulationLocationUpdate(Vehicle v, int latency) {
        for (SimulationEventsListener l : eventsListeners) l.onSimulationLocationUpdate(v, latency);
    }

    public interface SimulationEventsListener {
        void onSimulationError(String error);
        void onSimulationLocationUpdate(Vehicle v, int latency);
    }

    private class SimulationWorker extends AsyncTask<Vehicle, Integer, Void> {

        @Override
        protected Void doInBackground(Vehicle... vArgs) {

            Log.i("MySim", "Started async task.\n");

            Vehicle v = vArgs[0];

            MaintainerDBProxy dbProxy = MaintainerDBProxy.getMaintainerDBProxy();

            if (mFirstRun) {
                // Initially put the vehicle to db and acquire its generate id.
                long vehicleId = dbProxy.putVehicle(v);
                v.setVehicleId(vehicleId);

                mFirstRun = false;
            }

            Log.i("MySim", String.format("Car %s inserted to DB.\n", v.getModel()));

            latestLatencies.clear();
            latenciesSum = 0;

            while (mIsRunning) {
                v.setLocation(LocationGenerator.generateNextLocation(v.getLocation()));

                long startTime = SystemClock.elapsedRealtime();
                dbProxy.putLocation(v.getVehicleId(), v.getLocation());
                long latency = SystemClock.elapsedRealtime() - startTime;

                // Count the average of up to 30 previous latencies (moving average.
                if (latestLatencies.size() >= 30) latenciesSum -= latestLatencies.pop();
                latestLatencies.add((int) latency);
                latenciesSum += (int) latency;

                publishProgress(latenciesSum / latestLatencies.size());

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {}

                dbProxy.deleteAllLocations(v.getVehicleId());
            }

            dbProxy.deleteVehicle(v);

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... latency) {
            super.onProgressUpdate();
            int avgLatency = latency[0];
            notifyOnSimulationLocationUpdate(mVehicle, avgLatency);
        }
    }
}
