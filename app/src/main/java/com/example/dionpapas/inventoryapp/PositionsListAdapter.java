package com.example.dionpapas.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dionpapas.inventoryapp.data.InventoryAppContract;

/**
 * Created by dionpa on 2017-11-03.
 */

public class PositionsListAdapter extends RecyclerView.Adapter<PositionsListAdapter.PositionsViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public PositionsListAdapter(Context mContext, Cursor mCursor) {
        this.mContext = mContext;
        this.mCursor = mCursor;
    }

    @Override
    public PositionsListAdapter.PositionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.position_list_item, parent, false);
        return new PositionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PositionsListAdapter.PositionsViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position))
            return; // bail if returned null
        long id = mCursor.getLong(mCursor.getColumnIndex(InventoryAppContract.PositionEntry._ID));
        String position_name = mCursor.getString(mCursor.getColumnIndex(InventoryAppContract.PositionEntry.COLUMN_POSITION));
        String item_name = mCursor.getString(mCursor.getColumnIndex(InventoryAppContract.PositionEntry.COLUMN_ITEM));
        int quantity = mCursor.getInt(mCursor.getColumnIndex(InventoryAppContract.PositionEntry.COLUMN_QUANTITY));
        int quantity_wms = mCursor.getInt(mCursor.getColumnIndex(InventoryAppContract.PositionEntry.COLUMN_WMS));
        int difference = mCursor.getInt(mCursor.getColumnIndex(InventoryAppContract.PositionEntry.COLUMN_DIFFERENCE));

        holder.positionTextView.setText(position_name);
        holder.itemTextView.setText(item_name);
        holder.quantityTextview.setText(String.valueOf(quantity));
        //Give id as tag so not be displayed on UI
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public class PositionsViewHolder extends RecyclerView.ViewHolder{

        TextView positionTextView;

        TextView itemTextView;

        TextView quantityTextview;

        public PositionsViewHolder(View itemView) {
            super(itemView);
            positionTextView = (TextView) itemView.findViewById(R.id.position_name);
            itemTextView = (TextView) itemView.findViewById(R.id.item_name);
            quantityTextview = (TextView) itemView.findViewById(R.id.quantity_tv);
        }
    }

    public void swapCursor(Cursor newCursor) {

        if (mCursor != null) mCursor.close();

        mCursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }
}
