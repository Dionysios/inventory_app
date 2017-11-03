package com.example.dionpapas.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.dionpapas.inventoryapp.data.InventoryAppContract;
import com.example.dionpapas.inventoryapp.data.InventoryDBHelper;
import com.example.dionpapas.inventoryapp.data.TestUtils;

public class MainActivity extends AppCompatActivity {

    private static String LOG_TAG = "MainActivty";
    private SQLiteDatabase mDb;
    private PositionsListAdapter mAdapter;
    private EditText mPositionName;
    private EditText mItemName;
    private EditText mQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView waitlistRecyclerView;

        // Set local attributes to corresponding views
        waitlistRecyclerView = (RecyclerView) this.findViewById(R.id.all_guests_list_view);
        mPositionName = (EditText) this.findViewById(R.id.position_edit_text);
        mItemName = (EditText) this.findViewById(R.id.item_edit_text);
        mQuantity = (EditText) this.findViewById(R.id.quantity_edit_text);
        // Set layout for the RecyclerView, because it's a list we are using the linear layout
        waitlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        InventoryDBHelper dbHelper = new InventoryDBHelper(this);

        mDb = dbHelper.getWritableDatabase();

        //Fill the database with fake data
        //TestUtils.insertFakeData(mDb);

        Cursor cursor = getAllPositions();
        // Create an adapter for that cursor to display the data
        mAdapter = new PositionsListAdapter(this, cursor);
        // Link the adapter to the RecyclerView
        waitlistRecyclerView.setAdapter(mAdapter);

        // COMPLETED (3) Create a new ItemTouchHelper with a SimpleCallback that handles both LEFT and RIGHT swipe directions
        // Create an item touch helper to handle swiping items off the list
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            // COMPLETED (4) Override onMove and simply return false inside
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //do nothing, we only care about swiping
                return false;
            }

            // COMPLETED (5) Override onSwiped
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // COMPLETED (8) Inside, get the viewHolder's itemView's tag and store in a long variable id
                //get the id of the item being swiped
                long id = (long) viewHolder.itemView.getTag();
                // COMPLETED (9) call removeGuest and pass through that id
                //remove from DB
                removePosition(id);
                // COMPLETED (10) call swapCursor on mAdapter passing in getAllGuests() as the argument
                //update the list
                mAdapter.swapCursor(getAllPositions());
            }

            //COMPLETED (11) attach the ItemTouchHelper to the waitlistRecyclerView
        }).attachToRecyclerView(waitlistRecyclerView);

    }

    public void addToWaitlist(View view) {
        if (mPositionName.getText().length() == 0 ||
                mPositionName.getText().length() == 0) {
            return;
        }

        int quantity_final = 1;

        try {
            quantity_final = Integer.parseInt(mQuantity.getText().toString());
        } catch (NumberFormatException ex) {
            // COMPLETED (12) Make sure you surround the Integer.parseInt with a try catch and log any exception
            Log.e(LOG_TAG, "Failed to parse party size text to number: " + ex.getMessage());
        }

        addNewItem(mPositionName.getText().toString(),mItemName.getText().toString(), quantity_final);

        mAdapter.swapCursor(getAllPositions());

        // COMPLETED (20) To make the UI look nice, call .getText().clear() on both EditTexts, also call clearFocus() on mNewPartySizeEditText
        //clear UI text fields
        mPositionName.clearFocus();
        mItemName.getText().clear();
        mQuantity.getText().clear();
    }
    private Cursor getAllPositions() {
        // COMPLETED (6) Inside, call query on mDb passing in the table name and projection String [] order by COLUMN_TIMESTAMP
        return mDb.query(
                InventoryAppContract.PositionEntry.TABLE_NAME_POSITIONS,
                null,
                null,
                null,
                null,
                null,
                InventoryAppContract.PositionEntry.COLUMN_TIMESTAMP
        );
    }

    private long addNewItem(String position,String item, int quantity) {

        ContentValues cv = new ContentValues();

        cv.put(InventoryAppContract.PositionEntry.COLUMN_POSITION, position);
        cv.put(InventoryAppContract.PositionEntry.COLUMN_ITEM, item);
        cv.put(InventoryAppContract.PositionEntry.COLUMN_QUANTITY, quantity);

        return mDb.insert(InventoryAppContract.PositionEntry.TABLE_NAME_POSITIONS, null, cv);
    }

    private boolean removePosition(long id) {
        // COMPLETED (2) Inside, call mDb.delete to pass in the TABLE_NAME and the condition that WaitlistEntry._ID equals id
        return mDb.delete(InventoryAppContract.PositionEntry.TABLE_NAME_POSITIONS, InventoryAppContract.PositionEntry._ID + "=" + id, null) > 0;
    }

}
