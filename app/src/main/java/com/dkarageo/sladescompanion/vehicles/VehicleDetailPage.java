package com.dkarageo.sladescompanion.vehicles;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.support.v7.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dkarageo.sladescompanion.App;
import com.dkarageo.sladescompanion.R;
import com.dkarageo.sladescompanion.authorities.Obstacle;
import com.dkarageo.sladescompanion.vehicles.simulator.Simulation;
import com.dkarageo.sladescompanion.vehicles.simulator.SimulationsManager;


public class VehicleDetailPage extends Fragment
        implements Simulation.SimulationEventsListener {

    public static String COUNT_TAG = "com.dkarageo.sladescompanion.vehicle.VehicleDetailPage.count";

    // Handlers to UI elements.
    TextView   mVehicleTitle;
    ImageView  mVehicleImg;
    TextView   mLicenseNo;
    TextView   mManufacturer;
    TextView   mModel;
    TextView   mAutoDrivingSysName;
    TextView   mAutoDrivingSysVer;
    TextView   mAutonomyLevel;
    Button     mOperationSwitch;
    Button     mDestroyButton;
    GridLayout mSimParametersWrapper;
    TextView   mSimLatency;
    TextView   mSimUpdateInterval;
    TextView   mSimStoppedHint;
    TextView   mSimObstaclesSpotted;

    Vehicle mVehicle;  // Vehicle associated with current fragment.

    Simulation mSimulation;  // Simulation for current vehicle.

    int mPageIndicator;  // Indicator for that vehicle.

    OnVehicleDetailPageDestroyListener pageDestroyListener = null;
    boolean mIsRunning = false;  // Defines whether vehicle simulation is running.


    public VehicleDetailPage() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnVehicleDetailPageDestroyListener) {
            pageDestroyListener = (OnVehicleDetailPageDestroyListener) context;
        } else if (getParentFragment() instanceof OnVehicleDetailPageDestroyListener) {
            pageDestroyListener = (OnVehicleDetailPageDestroyListener) getParentFragment();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Generate a new random vehicle.
            mVehicle = VehicleGenerator.generateVehicle();
            mSimulation = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.vehicle_detail_page, container, false);

        mVehicleTitle         = rootView.findViewById(R.id.vehicles_vehicle_title);
        mVehicleImg           = rootView.findViewById(R.id.vehicles_vehicle_image);
        mOperationSwitch      = rootView.findViewById(R.id.vehicles_vehicle_operation_switch);
        mLicenseNo            = rootView.findViewById(R.id.vehicles_vehicle_licenseNo);
        mManufacturer         = rootView.findViewById(R.id.vehicles_vehicle_manufacturer);
        mModel                = rootView.findViewById(R.id.vehicles_vehicle_model);
        mAutoDrivingSysName   = rootView.findViewById(R.id.vehicles_vehicle_autodrivingsys_name);
        mAutoDrivingSysVer    = rootView.findViewById(R.id.vehicles_vehicle_autodrivingsys_version);
        mAutonomyLevel        = rootView.findViewById(R.id.vehicles_vehicle_autonomylevel);
        mDestroyButton        = rootView.findViewById(R.id.vehicles_vehicle_destroy_button);
        mSimParametersWrapper = rootView.findViewById((R.id.vehicles_vehicle_sim_parameters_table));
        mSimLatency           = rootView.findViewById((R.id.vehicles_vehicle_sim_latency));
        mSimUpdateInterval    = rootView.findViewById((R.id.vehicles_vehicle_sim_update_interval));
        mSimStoppedHint       = rootView.findViewById((R.id.vehicles_vehicle_sim_stopped_hint));
        mSimObstaclesSpotted  = rootView.findViewById((R.id.vehicles_vehicle_sim_spotted_obstacles));

        // Set listener for operation switch button and fix its state.
        mOperationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsRunning) stopSimulation();
                else startSimulation();
            }
        });
        if (mSimulation != null) {
            setOperationSwitchState(mSimulation.isRunning());
            displaySimulationParameters(mSimulation.isRunning());
        }

        // Set listener for vehicle destroy button.
        mDestroyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsRunning) stopSimulation();
                if (pageDestroyListener != null) {
                    pageDestroyListener.onPageDestroy(VehicleDetailPage.this);
                }
            }
        });

        displayVehicleInfo(mVehicle);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mSimulation != null) {
            mSimulation.registerSimulationEventsListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mSimulation != null) {
            mSimulation.unregisterSimulationEventsListener(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSimulation();
    }

    @Override
    public void onSimulationError(String error) {}

    @Override
    public void onSimulationLocationUpdate(Vehicle v, int latency) {
        mSimLatency.setText(Integer.toString(latency));
    }

    @Override
    public void onObstacleSpotted(int obstaclesCount) {
        mSimObstaclesSpotted.setText(Integer.toString(obstaclesCount));
    }

    public String getTitle() {
        return App.getContext().getResources().getString(R.string.vehicles_vehicle_title_prefix) +
                " " + Integer.toString(getArguments().getInt(COUNT_TAG));
    }

    public void setPageIndicator(int indicator) {
        mPageIndicator = indicator;
    }

    public int getPageIndicator() { return mPageIndicator; }

    private void displayVehicleInfo(Vehicle v) {
        mVehicleTitle.setText(getTitle());
        mVehicleImg.setImageDrawable(v.getVehicleImg());
        mLicenseNo.setText(v.getLicenseNo());
        mManufacturer.setText(v.getManufacturer());
        mModel.setText(v.getModel());
        mAutoDrivingSysName.setText(v.getAutoDrivingSystemName());
        mAutoDrivingSysVer.setText(Integer.toString(v.getAutoDrivingSystemVersion()));
        mAutonomyLevel.setText(Integer.toString(v.getAutonomyLevel()));
    }

    private void startSimulation() {
        if (mSimulation == null) {
            mSimulation =
                    SimulationsManager.getSimulationsManager().createSimulation(mVehicle);
        }
        mSimulation.registerSimulationEventsListener(this);
        mSimulation.start();
        mIsRunning = true;
        setOperationSwitchState(true);
        displaySimulationParameters(true);
    }

    private void stopSimulation() {
        if (mSimulation != null) {
            mSimulation.unregisterSimulationEventsListener(this);
            mSimulation.stop();
            mIsRunning = false;
            setOperationSwitchState(false);
            displaySimulationParameters(false);
        }
    }

    private void setOperationSwitchState(boolean started) {
        if (started) {
            mOperationSwitch.setBackground(
                    getResources().getDrawable(
                            R.drawable.vehicle_detail_operation_switch_bg_started));
            mOperationSwitch.setTextColor(getResources().getColor(R.color.red));
            mOperationSwitch.setText(
                    getString(R.string.vehicles_vehicle_operation_switch_started_text));
        } else {
            mOperationSwitch.setBackground(
                    getResources().getDrawable(
                            R.drawable.vehicle_detail_operation_switch_bg_stopped));
            mOperationSwitch.setTextColor(getResources().getColor(R.color.green));
            mOperationSwitch.setText(
                    getString(R.string.vehicles_vehicle_operation_switch_stopped_text));
        }
    }

    private void displaySimulationParameters(boolean display) {
        if (display) {
            mSimParametersWrapper.setVisibility(View.VISIBLE);
            mSimStoppedHint.setVisibility(View.GONE);

            if (mSimulation != null) {
                mSimUpdateInterval.setText(Long.toString(mSimulation.getUpdateInterval()));
                mSimObstaclesSpotted.setText(
                        Integer.toString(mSimulation.getEncounteredObstaclesCount()));

                int latency = mSimulation.getCurrentLatency();
                if (latency == 0) {
                    mSimLatency.setText(R.string.vehicles_vehicle_sim_update_latency_wait_hint);
                } else {
                    mSimLatency.setText(Integer.toString(latency));
                }
            }

        } else {
            mSimParametersWrapper.setVisibility(View.GONE);
            mSimStoppedHint.setVisibility(View.VISIBLE);
        }
    }

    public interface OnVehicleDetailPageDestroyListener {
        void onPageDestroy(VehicleDetailPage detailPage);
    }
}
