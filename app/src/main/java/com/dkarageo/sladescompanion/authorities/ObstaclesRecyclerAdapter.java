package com.dkarageo.sladescompanion.authorities;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.dkarageo.sladescompanion.R;
import com.dkarageo.sladescompanion.utils.TextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class ObstaclesRecyclerAdapter
        extends RecyclerView.Adapter<ObstaclesRecyclerAdapter.ObstacleViewHolder>
        implements Filterable {

    // A list containing all obstacles to be displayed.
    private List<Obstacle> mObstaclesToDisplay;

    private List<Obstacle> mOriginalObstacles;
    private ObstacleFilter mFilter;
    private CharSequence   mActiveFilter;

    private List<OnCleanupRequestListener> mCleanupListeners;

    public static class ObstacleViewHolder extends RecyclerView.ViewHolder {
        TextView itemType;
        TextView itemId;
        TextView firstlySpottedDate;
        TextView lastlySpottedDate;
        Button   mCleanupButton;

        public ObstacleViewHolder(View v) {
            super(v);

            itemType           = v.findViewById(R.id.obstacles_item_type);
            itemId             = v.findViewById(R.id.obstacles_item_id);
            firstlySpottedDate = v.findViewById(R.id.obstacles_item_firstly_spotted_date);
            lastlySpottedDate  = v.findViewById(R.id.obstacles_item_lastly_spotted_date);
            mCleanupButton     = v.findViewById(R.id.obstacles_item_cleanup_button);
        }
    }

    public ObstaclesRecyclerAdapter(List<Obstacle> obstacles) {
        mOriginalObstacles  = obstacles;
        mObstaclesToDisplay = obstacles;
        mFilter             = new ObstacleFilter();
        mActiveFilter       = null;
        mCleanupListeners   = new ArrayList<>();
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
        holder.itemType.setText(TextUtils.toCamelCase(curObst.getObstacleType()));
        holder.itemId.setText(Long.toString(curObst.getObstacleId()));
        holder.firstlySpottedDate.setText(
                new SimpleDateFormat("dd-MM-yyyy").format(curObst.getFirstlySpottedOn()));
        holder.lastlySpottedDate.setText(
                new SimpleDateFormat("dd-MM-yyyy").format(curObst.getLastlySpottedOn()));

        holder.mCleanupButton.setOnClickListener(new OnCleanupButtonClickedListener(curObst));
    }

    @Override
    public int getItemCount() {
        return mObstaclesToDisplay.size();
    }

    public void updateDataset(List<Obstacle> obstacles) {
        mOriginalObstacles = obstacles;
        mFilter.filter(mActiveFilter);
    }

    @Override
    public Filter getFilter() { return mFilter; }

    public void registerOnCleanupRequestListener(OnCleanupRequestListener l) {
        mCleanupListeners.add(l);
    }

    public void unregisterOnCleanupRequestListener(OnCleanupRequestListener l) {
        mCleanupListeners.remove(l);
    }

    private class ObstacleFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // Save current filter, to replay it when updating underlying dataset.
            mActiveFilter = constraint;

            FilterResults results = new FilterResults();

            if (constraint == null || constraint.toString().equals("")) {
                results.values = mOriginalObstacles;
                results.count  = mOriginalObstacles.size();
            } else {

                List<Obstacle> filteredObstacles = new ArrayList<>();
                String stringConstraint = constraint.toString();

                for (Obstacle o : mOriginalObstacles) {
                    String obstacleType = o.getObstacleType();

                    if (obstacleType.toLowerCase().contains(stringConstraint.toLowerCase())) {
                        filteredObstacles.add(o);
                    }
                }

                results.values = filteredObstacles;
                results.count  = filteredObstacles.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            mObstaclesToDisplay = (List<Obstacle>) results.values;
            notifyDataSetChanged();
        }
    }

    private class OnCleanupButtonClickedListener implements View.OnClickListener {
        Obstacle mRegisteredObstacle;

        public OnCleanupButtonClickedListener(Obstacle registeredObstacle) {
            mRegisteredObstacle = registeredObstacle;
        }

        public void onClick(View v) {
            // Find obstacle in original dataset.
            Obstacle original = null;
            for (Obstacle o: mOriginalObstacles) {
                if (o.getObstacleId() == mRegisteredObstacle.getObstacleId()) {
                    original = o;
                    break;
                }
            }

            for (OnCleanupRequestListener l : mCleanupListeners) {
                l.onCleanupRequested(original);
            }
        }
    }

    public interface OnCleanupRequestListener {
        void onCleanupRequested(Obstacle obstacle);
    }
}
