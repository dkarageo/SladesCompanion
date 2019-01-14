package com.dkarageo.sladescompanion.authorities;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dkarageo.sladescompanion.R;
import com.dkarageo.sladescompanion.db.MaintainerDBProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;


public class RoadsideUnitsPage extends Fragment {

    RecyclerView                 mRecyclerView;
    RoadsideUnitsRecyclerAdapter mAdapter;
    RecyclerView.LayoutManager   mLayoutManager;

    List<RoadsideUnit> mRoadsideUnits;

    SearchView mSearchBar;


    public RoadsideUnitsPage() {
        mRoadsideUnits = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.roadside_units_page, container, false);

        mRecyclerView = rootView.findViewById(R.id.roadside_units_recycler_view);
        if (mRecyclerView == null) {
            throw new RuntimeException("Unable to fetch recycler view for roadside units.");
        }
        // Use a linear layout manager.
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RoadsideUnitsRecyclerAdapter(mRoadsideUnits);
        mRecyclerView.setAdapter(mAdapter);

        mSearchBar = rootView.findViewById(R.id.roadside_units_search_bar);
        mSearchBar.setOnQueryTextListener(new SearchListener());

        if (savedInstanceState == null) {
            new RoadsideUnitsFetcher().executeOnExecutor(Executors.newFixedThreadPool(16));
        }

        return rootView;
    }

    class RoadsideUnitsFetcher extends AsyncTask<String, Void, List<RoadsideUnit>> {
        @Override
        protected List<RoadsideUnit> doInBackground(String... params) {
            List<RoadsideUnit> units = MaintainerDBProxy.getMaintainerDBProxy().getRoadsideUnits();
            return units;
        }

        @Override
        protected void onPostExecute(List<RoadsideUnit> units) {
            mRoadsideUnits.clear();
            mRoadsideUnits.addAll(units);
            mAdapter.updateDataset(mRoadsideUnits);
        }
    }


    class SearchListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            mAdapter.getFilter().filter(query);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            mAdapter.getFilter().filter(newText);
            return false;
        }
    }
}
