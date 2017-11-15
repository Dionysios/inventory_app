package com.example.dionpapas.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dionpapas.inventoryapp.data.InventoryAppContract;
import com.example.dionpapas.inventoryapp.data.InventoryDBHelper;

/**
 * Created by dionpa on 2017-11-13.
 */

public class AddRegistrationActivity extends AppCompatActivity {
    private EditText mPositionName;
    private EditText mItemName;
    private EditText mQuantity;
    private EditText mStock;
    private EditText mWMS;
    private EditText mDifference;
    private SQLiteDatabase mDb;
    private static String LOG_TAG = "AddRegistrationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mPositionName = (EditText) this.findViewById(R.id.input_position);
        mItemName = (EditText) this.findViewById(R.id.input_item);
        //mQuantity = (EditText) this.findViewById(R.id.quantity_edit_text);
        mStock = (EditText) this.findViewById(R.id.input_stock);
        mWMS = (EditText) this.findViewById(R.id.input_wms);
        mDifference = (EditText) this.findViewById(R.id.input_difference);
        InventoryDBHelper dbHelper = new InventoryDBHelper(this);
        mDb = dbHelper.getWritableDatabase();

    }

    public void addToDB(View view) {
        if (mPositionName.getText().length() == 0 ||
                mPositionName.getText().length() == 0) {
            return;
        }

        int quantity_final = 1;

        try {
            quantity_final = Integer.parseInt(mQuantity.getText().toString());
        } catch (NumberFormatException ex) {
            Log.e(LOG_TAG, "Failed to parse party size text to number: " + ex.getMessage());
        }

        long reply = addNewItem(mPositionName.getText().toString(),mItemName.getText().toString(), quantity_final);
        Log.e(LOG_TAG, "Item has been created" + reply);
        if(reply != -1) {
            Toast.makeText(getBaseContext(), "Registration has been created" + reply, Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getBaseContext(), "Registration can not been created", Toast.LENGTH_LONG).show();
        }
        mPositionName.clearFocus();
        mItemName.getText().clear();
        mQuantity.getText().clear();
    }

    private long addNewItem(String position,String item, int quantity) {
        ContentValues cv = new ContentValues();
        cv.put(InventoryAppContract.PositionEntry.COLUMN_POSITION, position);
        cv.put(InventoryAppContract.PositionEntry.COLUMN_ITEM, item);
        cv.put(InventoryAppContract.PositionEntry.COLUMN_QUANTITY, quantity);
        return mDb.insert(InventoryAppContract.PositionEntry.TABLE_NAME_POSITIONS, null, cv);
    }
}
