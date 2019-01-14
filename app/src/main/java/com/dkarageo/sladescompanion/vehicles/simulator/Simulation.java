package com.dkarageo.sladescompanion.vehicles.simulator;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import com.dkarageo.sladescompanion.authorities.Obstacle;
import com.dkarageo.sladescompanion.db.MaintainerDBProxy;
import com.dkarageo.sladescompanion.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class Simulation {

    private static final long UPDATE_INTERVAL = 1000L;  // 1 sec

    private Vehicle mVehicle;
    private boolean mIsRunning;
    private boolean mFirstRun;

    private List<SimulationEventsListener> mEventsListeners;

    private LinkedList<Integer> mLatestLatencies;
    private int mLatenciesSum;

    private List<Obstacle> mEncounteredObstacles;


    Simulation(Vehicle v) {
        mVehicle = v;
        mEventsListeners = new ArrayList<>();
        mLatestLatencies = new LinkedList<>();
        mFirstRun = true;
        mEncounteredObstacles = new ArrayList<>();
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
        if (mLatestLatencies.size() > 0) return mLatenciesSum / mLatestLatencies.size();
        else return 0;
    }

    public int getEncounteredObstaclesCount() {
        return mEncounteredObstacles.size();
    }

    public boolean isRunning() { return mIsRunning; }

    public void registerSimulationEventsListener(SimulationEventsListener l) {
        mEventsListeners.add(l);
    }

    public void unregisterSimulationEventsListener(SimulationEventsListener l) {
        mEventsListeners.remove(l);
    }

    public void notifyOnSimulationError(String error) {
        for (SimulationEventsListener l : mEventsListeners) l.onSimulationError(error);
    }

    public void notifyOnSimulationLocationUpdate(Vehicle v, int latency) {
        for (SimulationEventsListener l : mEventsListeners) l.onSimulationLocationUpdate(v, latency);
    }

    public interface SimulationEventsListener {
        void onSimulationError(String error);
        void onSimulationLocationUpdate(Vehicle v, int latency);
        void onObstacleSpotted(int obstaclesCount);
    }

    private Obstacle encountersNewObstacle() {
        Obstacle newObstacle = null;

        // 5% probability to meet an obstacle
        if (ThreadLocalRandom.current().nextInt(0, 20) == 1) {
            newObstacle = ObstacleGenerator.generateObstacle();

            mEncounteredObstacles.add(newObstacle);
            long obstacleId = MaintainerDBProxy.getMaintainerDBProxy().putObstacle(newObstacle);
            newObstacle.setObstacleId(obstacleId);
        }

        return newObstacle;
    }

    private boolean deleteEncounteredObstacles() {
        boolean rc = true;
        for (Obstacle o : mEncounteredObstacles) {
            rc &= MaintainerDBProxy.getMaintainerDBProxy().deleteObstacle(o);
        }
        mEncounteredObstacles.clear();
        return rc;
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

            mLatestLatencies.clear();
            mLatenciesSum = 0;

            long nextTargetTime = SystemClock.elapsedRealtime();

            while (mIsRunning) {
                nextTargetTime += 1000;

                v.setLocation(LocationGenerator.generateNextLocation(v.getLocation()));

                long startTime = SystemClock.elapsedRealtime();
                dbProxy.putLocation(v.getVehicleId(), v.getLocation());
                long latency = SystemClock.elapsedRealtime() - startTime;

                // Count the average of up to 30 previous latencies (moving average.
                if (mLatestLatencies.size() >= 30) mLatenciesSum -= mLatestLatencies.pop();
                mLatestLatencies.add((int) latency);
                mLatenciesSum += (int) latency;

                publishProgress(mLatenciesSum / mLatestLatencies.size());

                encountersNewObstacle();

                long curTime = SystemClock.elapsedRealtime();
                if (nextTargetTime - curTime > 0) {
                    try {
                        Thread.sleep(nextTargetTime - curTime);
                    } catch (InterruptedException ex) {}
                }

                dbProxy.deleteAllLocations(v.getVehicleId());
            }

            deleteEncounteredObstacles();
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
