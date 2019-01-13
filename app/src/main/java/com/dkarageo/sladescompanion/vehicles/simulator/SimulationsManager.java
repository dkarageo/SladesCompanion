package com.dkarageo.sladescompanion.vehicles.simulator;

import com.dkarageo.sladescompanion.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class SimulationsManager {

    private static SimulationsManager mSimulationsManager;

    private List<Simulation> mSimulations;


    public static SimulationsManager getSimulationsManager() {
        if (mSimulationsManager == null) mSimulationsManager = new SimulationsManager();
        return mSimulationsManager;
    }

    private SimulationsManager() {
        mSimulations = new ArrayList<>();
    }

    public Simulation createSimulation(Vehicle v) {
        Simulation s = new Simulation(v);
        mSimulations.add(s);
        return s;
    }

    public void terminateAllSimulations() {
        for (Simulation s : mSimulations) {
            s.stop();
        }
    }
}
