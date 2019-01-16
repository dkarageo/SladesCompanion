package com.dkarageo.sladescompanion.authorities;


import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dkarageo.sladescompanion.R;
import com.dkarageo.sladescompanion.db.MaintainerDBProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;


public class ObstaclesPage
        extends Fragment
        implements ObstaclesRecyclerAdapter.OnCleanupRequestListener {

    RecyclerView mRecyclerView;
    ObstaclesRecyclerAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    SearchView mSearchBar;

    List<Obstacle> mObstacles;


    public ObstaclesPage() {
        mObstacles = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.obstacles_page, container, false);

        mRecyclerView = rootView.findViewById(R.id.obstacles_recycler_view);
        if (mRecyclerView == null) {
            throw new RuntimeException("Unable to fetch recycler view for obstacles");
        }
        // Use a linear layout manager.
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ObstaclesRecyclerAdapter(mObstacles);
        mRecyclerView.setAdapter(mAdapter);

        mSearchBar = rootView.findViewById(R.id.obstacles_search_bar);
        mSearchBar.setOnQueryTextListener(new SearchListener());

        if (savedInstanceState == null) {
            new ObstaclesFetcher().executeOnExecutor(Executors.newFixedThreadPool(16));
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.registerOnCleanupRequestListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdapter.unregisterOnCleanupRequestListener(this);
    }

    @Override
    public void onCleanupRequested(Obstacle unitToUpdate) {
        new CleanupStateUpdater().executeOnExecutor(Executors.newFixedThreadPool(16),
                unitToUpdate);
    }


    class ObstaclesFetcher extends AsyncTask<String, Void, List<Obstacle>> {
        @Override
        protected List<Obstacle> doInBackground(String... params) {
            List<Obstacle> obstacles = MaintainerDBProxy.getMaintainerDBProxy().getObstacles(
                    true);
            return obstacles;
        }

        @Override
        protected void onPostExecute(List<Obstacle> obstacles) {
            if (obstacles != null) {
                mObstacles.clear();
                mObstacles.addAll(obstacles);
                mAdapter.updateDataset(mObstacles);
            }
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


    private class CleanupStateUpdater extends AsyncTask<Obstacle, Void, Void> {
        @Override
        protected Void doInBackground(Obstacle... params) {
            Obstacle obstacleToUpdate = params[0];
            MaintainerDBProxy dbProxy = MaintainerDBProxy.getMaintainerDBProxy();
            boolean rc = dbProxy.updateObstacleRequiresMaintanceState(obstacleToUpdate, false);
            if (rc) mObstacles.remove(obstacleToUpdate);
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            mAdapter.updateDataset(mObstacles);
        }
    }
}
