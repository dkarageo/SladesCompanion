package com.dkarageo.sladescompanion.vehicles.simulator;

import android.os.AsyncTask;
import android.util.Log;

import com.dkarageo.sladescompanion.db.MaintainerDBProxy;
import com.dkarageo.sladescompanion.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class Simulation {

    private Vehicle mVehicle;
    private boolean mIsRunning;
    private boolean mFirstRun;

    List<SimulationEventsListener> eventsListeners;

    Simulation(Vehicle v) {
        mVehicle = v;
        eventsListeners = new ArrayList<>();
        mFirstRun = true;
    }

    public void start() {
        mIsRunning = true;
        new SimulationWorker().execute(mVehicle);
    }

    public void stop() {
        mIsRunning = false;
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

    public interface SimulationEventsListener {
        void onSimulationError(String error);
    }

    private class SimulationWorker extends AsyncTask<Vehicle, Void, Void> {

        @Override
        protected Void doInBackground(Vehicle... vArgs) {

            Vehicle v = vArgs[0];

            if (mFirstRun) {
                // Initially put the vehicle to db and acquire its generate id.
                long vehicleId = MaintainerDBProxy.getMaintainerDBProxy().putVehicle(v);
                v.setVehicleId(vehicleId);

                mFirstRun = false;
            }

            Log.i("MySim", String.format("Car %s inserted to DB.\n", v.getModel()));

            while (mIsRunning) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {}
            }

            return null;
        }
    }
}
