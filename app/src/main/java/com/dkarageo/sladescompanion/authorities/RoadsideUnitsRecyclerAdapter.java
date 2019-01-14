package com.dkarageo.sladescompanion.authorities;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.dkarageo.sladescompanion.R;
import com.dkarageo.sladescompanion.utils.TextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class RoadsideUnitsRecyclerAdapter
        extends RecyclerView.Adapter<RoadsideUnitsRecyclerAdapter.RUViewHolder>
        implements Filterable {

    private List<RoadsideUnit> mUnitsToDisplay;  // A list containing all RUs to be displayed.

    private List<RoadsideUnit> mOriginalRoadsideUnits;
    private RoadsideUnitFilter mFilter;
    private CharSequence       mActiveFilter;

    public static class RUViewHolder extends RecyclerView.ViewHolder {
        public CardView mItem;
        public TextView mItemType;
        public TextView mItemId;
        public TextView mItemOperator;
        public TextView mItemManufacturer;
        public TextView mItemLastServiceDate;

        public RUViewHolder(View v) {
            super(v);
            mItem                = v.findViewById(R.id.roadside_units_item_card);
            mItemType            = v.findViewById(R.id.roadside_units_item_type);
            mItemId              = v.findViewById(R.id.roadside_units_item_id);
            mItemOperator        = v.findViewById(R.id.roadside_units_item_operator);
            mItemManufacturer    = v.findViewById(R.id.roadside_units_item_manufacturer);
            mItemLastServiceDate = v.findViewById(R.id.roadside_units_item_last_service);
        }
    }

    public RoadsideUnitsRecyclerAdapter(List<RoadsideUnit> roadsideUnits) {
        mUnitsToDisplay = roadsideUnits;
        mFilter         = new RoadsideUnitFilter();
        mActiveFilter   = null;
    }

    @Override
    public RUViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.roadside_units_recycler_layout, viewGroup, false
        );

        return new RUViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RUViewHolder holder, int position) {
        RoadsideUnit curUnit = mUnitsToDisplay.get(position);

        // Update contents of given view holder to the new one.
        holder.mItemType.setText(TextUtils.toCamelCase(curUnit.getSensorType()));
        holder.mItemId.setText(Long.toString(curUnit.getSensorId()));
        holder.mItemOperator.setText(curUnit.getOperator());
        holder.mItemManufacturer.setText(curUnit.getManufacturer());
        holder.mItemLastServiceDate.setText(
                new SimpleDateFormat("dd-MM-yyyy").format(curUnit.getLastServiceDate()));

        // Color the background of the card, when a roadside unit is not functioning properly.
        if (!curUnit.getIsFunctioningProperly()) {
            holder.mItem.setCardBackgroundColor(
                    ContextCompat.getColor(holder.mItem.getContext(), R.color.colorError));
        } else {
            holder.mItem.setCardBackgroundColor(
                    ContextCompat.getColor(holder.mItem.getContext(), R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return mUnitsToDisplay.size();
    }

    public void updateDataset(List<RoadsideUnit> roadsideUnits) {
        mOriginalRoadsideUnits = roadsideUnits;
        mFilter.filter(mActiveFilter);
    }

    @Override
    public Filter getFilter() { return mFilter; }


    private class RoadsideUnitFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // Save current filter, to replay it when updating underlying dataset.
            mActiveFilter = constraint;

            FilterResults results = new FilterResults();

            if (constraint == null || constraint.toString().equals("")) {
                results.values = mOriginalRoadsideUnits;
                results.count  = mOriginalRoadsideUnits.size();
            } else {

                List<RoadsideUnit> filteredObstacles = new ArrayList<>();
                String stringConstraint = constraint.toString();

                for (RoadsideUnit ru : mOriginalRoadsideUnits) {
                    String roadsideUnitType = ru.getSensorType();

                    if (roadsideUnitType.toLowerCase().contains(stringConstraint.toLowerCase())) {
                        filteredObstacles.add(ru);
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
            mUnitsToDisplay = (List<RoadsideUnit>) results.values;
            notifyDataSetChanged();
        }
    }
}
