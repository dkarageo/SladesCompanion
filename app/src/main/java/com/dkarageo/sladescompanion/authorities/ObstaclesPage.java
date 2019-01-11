package com.dkarageo.sladescompanion.authorities;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dkarageo.sladescompanion.R;
import com.dkarageo.sladescompanion.db.MaintainerDBProxy;

import java.util.ArrayList;
import java.util.List;


public class ObstaclesPage extends Fragment {

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

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

        if (savedInstanceState == null) new ObstaclesFetcher().execute();

        return rootView;
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
            mObstacles.clear();
            mObstacles.addAll(obstacles);
            mAdapter.notifyDataSetChanged();
        }
    }
}
