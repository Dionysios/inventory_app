package com.example.dionpapas.inventoryapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
        //int stock = mCursor.getInt(mCursor.getColumnIndex(InventoryAppContract.PositionEntry.COLUMN_STOCK));
        //int quantity_wms = mCursor.getInt(mCursor.getColumnIndex(InventoryAppContract.PositionEntry.COLUMN_WMS));
        int difference = mCursor.getInt(mCursor.getColumnIndex(InventoryAppContract.PositionEntry.COLUMN_DIFFERENCE));
        //Date date = new Date(mCursor.getLong(6));
        String date = mCursor.getString(mCursor.getColumnIndex("timestamp"));

        holder.idTextView.setText(String.valueOf(id));
        holder.positionTextView.setText(position_name);
        holder.itemTextView.setText(item_name);
        holder.differenceTextview.setText(String.valueOf(difference));
       // SimpleDateFormat sdf = new SimpleDateFormat("MMM MM dd, yyyy h:mm a");
      //  String dateString = sdf.format(date);
        holder.dateTextView.setText(String.valueOf(date));
        //Give id as tag so not be displayed on UI
        holder.itemView.setTag(id);
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public class PositionsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView idTextView;

        TextView positionTextView;

        TextView itemTextView;

        TextView differenceTextview;

        TextView dateTextView;

        public PositionsViewHolder(View itemView) {
            super(itemView);
            idTextView = (TextView) itemView.findViewById(R.id.registration_id);
            positionTextView = (TextView) itemView.findViewById(R.id.position_tv);
            itemTextView = (TextView) itemView.findViewById(R.id.item_tv);
            differenceTextview = (TextView) itemView.findViewById(R.id.difference_tv);
            dateTextView = (TextView) itemView.findViewById(R.id.date_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int myNum = -1;
            try {
                myNum = Integer.parseInt(v.getTag().toString());
                Intent addTaskIntent = new Intent(mContext, AddRegistrationActivity.class);
                mContext.startActivity(addTaskIntent);
            } catch(NumberFormatException nfe) {
                myNum = -1;
            }
            Toast.makeText(v.getContext(),"Clicked " + myNum,Toast.LENGTH_SHORT).show();
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
