package com.dkarageo.sladescompanion.vehicles;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dkarageo.sladescompanion.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class VehicleDetailPage extends Fragment {


    public VehicleDetailPage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.vehicle_detail_page, container, false);
    }

}
