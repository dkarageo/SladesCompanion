package com.dkarageo.sladescompanion.vehicles;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dkarageo.sladescompanion.App;
import com.dkarageo.sladescompanion.R;


public class VehicleDetailPage extends Fragment {

    public static String COUNT_TAG = "com.dkarageo.sladescompanion.vehicle.VehicleDetailPage.count";

    // Handlers to UI elements.
    TextView  mVehicleTitle;
    ImageView mVehicleImg;
    TextView  mLicenseNo;
    TextView  mManufacturer;
    TextView  mModel;
    TextView  mAutoDrivingSysName;
    TextView  mAutoDrivingSysVer;
    TextView  mAutonomyLevel;
    Button    mOperationSwitch;
    Button    mDestroyButton;

    Vehicle mVehicle;  // Vehicle associated with current fragment.

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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.vehicle_detail_page, container, false);

        mVehicleTitle       = rootView.findViewById(R.id.vehicles_vehicle_title);
        mVehicleImg         = rootView.findViewById(R.id.vehicles_vehicle_image);
        mOperationSwitch    = rootView.findViewById(R.id.vehicles_vehicle_operation_switch);
        mLicenseNo          = rootView.findViewById(R.id.vehicles_vehicle_licenseNo);
        mManufacturer       = rootView.findViewById(R.id.vehicles_vehicle_manufacturer);
        mModel              = rootView.findViewById(R.id.vehicles_vehicle_model);
        mAutoDrivingSysName = rootView.findViewById(R.id.vehicles_vehicle_autodrivingsys_name);
        mAutoDrivingSysVer  = rootView.findViewById(R.id.vehicles_vehicle_autodrivingsys_version);
        mAutonomyLevel      = rootView.findViewById(R.id.vehicles_vehicle_autonomylevel);
        mDestroyButton      = rootView.findViewById(R.id.vehicles_vehicle_destroy_button);

        // Set listener for operation swtich button.
        mOperationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsRunning) stopSimulation();
                else startSimulation();
            }
        });

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
        mOperationSwitch.setBackground(
                getResources().getDrawable(
                        R.drawable.vehicle_detail_operation_switch_bg_started));
        mOperationSwitch.setTextColor(getResources().getColor(R.color.red));
        mOperationSwitch.setText(
                getString(R.string.vehicles_vehicle_operation_switch_started_text));
        mIsRunning = true;
    }

    private void stopSimulation() {
        mOperationSwitch.setBackground(
                getResources().getDrawable(
                        R.drawable.vehicle_detail_operation_switch_bg_stopped));
        mOperationSwitch.setTextColor(getResources().getColor(R.color.green));
        mOperationSwitch.setText(
                getString(R.string.vehicles_vehicle_operation_switch_stopped_text));
        mIsRunning = false;
    }


    public interface OnVehicleDetailPageDestroyListener {
        void onPageDestroy(VehicleDetailPage detailPage);
    }
}
