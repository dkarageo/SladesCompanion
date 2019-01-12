package com.dkarageo.sladescompanion.vehicles;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dkarageo.sladescompanion.R;

import java.util.ArrayList;
import java.util.List;


public class VehiclesFragment extends Fragment
        implements VehicleDetailPage.OnVehicleDetailPageDestroyListener {

    VehiclesPagerAdapter mPagerAdapter;
    ViewPager mViewPager;

    List<VehicleDetailPage> mVehicleFrags = new ArrayList<>();
    VehiclesSummaryPage mSummaryFrag;
    int mCreatedVehiclesCount = 0;  // Count how many vehicles have been totally created.


    public VehiclesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_vehicles, container, false);

        if (savedInstanceState == null) {
            mSummaryFrag = new VehiclesSummaryPage();
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

    @Override
    public void onPageDestroy(VehicleDetailPage detailPage) {
        removeVehicleDetailPage(detailPage);
    }

    public class VehiclesPagerAdapter extends FragmentPagerAdapter {

//        private long baseId = 0;

        public VehiclesPagerAdapter(FragmentManager fm) { super(fm); }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) return mSummaryFrag;  // First fragment is always the summary one.
            else return mVehicleFrags.get(position - 1);
        }

        @Override
        public int getCount() {
            return mVehicleFrags.size() + 1;  // Also count the summary fragment.
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title;

            if (position == 0) title = getString(R.string.vehicles_tab_summary_title);
            else title = mVehicleFrags.get(position-1).getTitle();

            return title;
        }

        @Override
        public long getItemId(int position) {
            if (position == 0) return 0;
            else return ((VehicleDetailPage) getItem(position)).getPageIndicator();
        }

        @Override
        public int getItemPosition(Object object) { return PagerAdapter.POSITION_NONE; }

//        /**
//         * Notify that the position of a fragment has been changed.
//         * Create a new ID for each position to force recreation of the fragment
//         * @param n number of items which have been changed
//         */
//        public void notifyChangeInPosition(int n) {
//            // shift the ID returned by getItemId outside the range of all previous fragments
//            baseId += getCount() + 1;
//        }
    }

    class FABOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // When FAB is clicked, create a new vehicle screen.
            ++mCreatedVehiclesCount;
            VehicleDetailPage vehicleDetail = new VehicleDetailPage();
            vehicleDetail.setPageIndicator(mCreatedVehiclesCount);

            // Pass the counter for that vehicle.
            Bundle bundle = new Bundle();
            bundle.putInt(VehicleDetailPage.COUNT_TAG, mCreatedVehiclesCount);

            vehicleDetail.setArguments(bundle);

            addVehicleDetailPage(vehicleDetail);
        }
    }

    private void addVehicleDetailPage(VehicleDetailPage vehicleDetail) {
        mVehicleFrags.add(vehicleDetail);
        mPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(mVehicleFrags.size());
    }

    private void removeVehicleDetailPage(VehicleDetailPage vehicleDetail) {
        int pos = -1;
        for (int i = 0; i < mVehicleFrags.size(); ++i) {
            if (mVehicleFrags.get(i).getArguments().getInt(VehicleDetailPage.COUNT_TAG) ==
                vehicleDetail.getArguments().getInt(VehicleDetailPage.COUNT_TAG)) {
                pos = i;
                break;
            }
        }

        if (pos > -1) {
            mVehicleFrags.remove(pos);
//            mPagerAdapter.notifyChangeInPosition(1);
            mPagerAdapter.notifyDataSetChanged();
        }
    }
}
