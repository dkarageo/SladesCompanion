package com.dkarageo.sladescompanion.authorities;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dkarageo.sladescompanion.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RoadsideUnitsPage extends Fragment {


    public RoadsideUnitsPage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.roadside_units_page, container, false);
    }

}