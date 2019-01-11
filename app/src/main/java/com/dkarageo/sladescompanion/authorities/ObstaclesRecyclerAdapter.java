package com.dkarageo.sladescompanion.authorities;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dkarageo.sladescompanion.R;

import java.text.SimpleDateFormat;
import java.util.List;


public class ObstaclesRecyclerAdapter
        extends RecyclerView.Adapter<ObstaclesRecyclerAdapter.ObstacleViewHolder> {

    // A list containing all obstacles to be displayed.
    private List<Obstacle> mObstaclesToDisplay;


    public static class ObstacleViewHolder extends RecyclerView.ViewHolder {
        TextView itemType;
        TextView itemId;
        TextView firstlySpottedDate;
        TextView lastlySpottedDate;

        public ObstacleViewHolder(View v) {
            super(v);

            itemType           = v.findViewById(R.id.obstacles_item_type);
            itemId             = v.findViewById(R.id.obstacles_item_id);
            firstlySpottedDate = v.findViewById(R.id.obstacles_item_firstly_spotted_date);
            lastlySpottedDate  = v.findViewById(R.id.obstacles_item_lastly_spotted_date);
        }
    }

    public ObstaclesRecyclerAdapter(List<Obstacle> obstacles) {
        mObstaclesToDisplay = obstacles;
    }

    @Override
    public ObstacleViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.obstacles_recycler_layout, viewGroup, false
        );

        return new ObstacleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ObstacleViewHolder holder, int position) {
        Obstacle curObst = mObstaclesToDisplay.get(position);

        // Update contents of given view holder to the new one.
        holder.itemType.setText(curObst.getObstacleType());
        holder.itemId.setText(Long.toString(curObst.getObstacleId()));
        holder.firstlySpottedDate.setText(
                new SimpleDateFormat("dd-MM-yyyy").format(curObst.getFirstlySpottedOn()));
        holder.lastlySpottedDate.setText(
                new SimpleDateFormat("dd-MM-yyyy").format(curObst.getLastlySpottedOn()));
    }

    @Override
    public int getItemCount() {
        return mObstaclesToDisplay.size();
    }
}
