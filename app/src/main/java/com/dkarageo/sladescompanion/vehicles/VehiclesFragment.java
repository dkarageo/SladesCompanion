package com.dkarageo.sladescompanion.vehicles;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dkarageo.sladescompanion.R;

import java.util.ArrayList;
import java.util.List;


public class VehiclesFragment extends Fragment {

    VehiclesPagerAdapter mPagerAdapter;
    ViewPager mViewPager;

    List<VehicleDetailPage> vehicleFrags = new ArrayList<>();
    VehiclesSummaryPage summaryFrag;


    public VehiclesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_vehicles, container, false);

        if (savedInstanceState == null) {
            summaryFrag = new VehiclesSummaryPage();
        }

        // Setup view pager.
        mPagerAdapter = new VehiclesPagerAdapter(getChildFragmentManager());

        mViewPager = rootView.findViewById(R.id.vehicles_viewpager);
        mViewPager.setAdapter(mPagerAdapter);

        // Setup FAB.
        FloatingActionButton fab = rootView.findViewById(R.id.vehicles_fab);
        fab.setOnClickListener(new FABOnClickListener());

        return rootView;
    }

    public class VehiclesPagerAdapter extends FragmentPagerAdapter {

        public VehiclesPagerAdapter(FragmentManager fm) { super(fm); }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) return summaryFrag;  // First fragment is always the summary one.
            else return vehicleFrags.get(position - 1);
        }

        @Override
        public int getCount() {
            return vehicleFrags.size() + 1;  // Also count the summary fragment.
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title;

            if (position == 0) title = getString(R.string.vehicles_tab_summary_title);
            else {
                title = getString(R.string.vehicles_tab_vehicle_detail_title) + " " +
                        Integer.toString(position);
            }

            return title;
        }
    }

    class FABOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // When FAB is clicked, create a new vehicle screen.
            vehicleFrags.add(new VehicleDetailPage());
            mPagerAdapter.notifyDataSetChanged();
            mViewPager.setCurrentItem(vehicleFrags.size());
        }
    }
}
