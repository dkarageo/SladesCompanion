package com.dkarageo.sladescompanion.authorities;


import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dkarageo.sladescompanion.R;


public class AuthoritiesFragment extends Fragment {

    AuthoritiesPagerAdapter mPagerAdapter;
    ViewPager mViewPager;


    public AuthoritiesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_authorities, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            mPagerAdapter = new AuthoritiesPagerAdapter(getChildFragmentManager());
        } catch (NullPointerException e) { e.printStackTrace(); }

        mViewPager = getActivity().findViewById(R.id.authorities_viewpager);
        mViewPager.setAdapter(mPagerAdapter);

        TabLayout tabLayout = getActivity().findViewById(R.id.authorities_viewpager_tablayout);
        tabLayout.setupWithViewPager(mViewPager);
    }

    public class AuthoritiesPagerAdapter extends FragmentPagerAdapter {

        public AuthoritiesPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new RoadsideUnitsPage();
                case 1:
                    return new ObstaclesPage();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.authorities_tab_roadsideunits_title);
                case 1:
                    return getResources().getString(R.string.authorities_tab_obstacles_title);
                default:
                    return null;
            }
        }
    }
}
