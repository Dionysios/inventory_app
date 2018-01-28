package com.dionpapas.inventoryapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dionpapas.inventoryapp.data.InventoryAppContract;
import com.dionpapas.inventoryapp.data.InventoryDBHelper;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by dionpa on 2017-12-05.
 */


public class AddRegistrationActivity extends AppCompatActivity {
    private EditText mPositionName;
    private EditText mItemName;
    private EditText mStock;
    private EditText mWMS;
    private EditText mDifference;
    private SQLiteDatabase mDb;
    private static String LOG_TAG = "AddRegistrationActivity";
    int stock_final = 0;
    int mWMS_final = 0;
    int difference_final = 0;
    private static final int CAMERA_PERMISSION = 1;
    private Class<?> mClss;
    private boolean isparameterUpdate = false;
    private int mid4update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        //Get from here the activity was called
        Intent incomingIntent = getIntent();
        if (incomingIntent.hasExtra("parameterUpdate")) {
            isparameterUpdate = incomingIntent.getBooleanExtra("parameterUpdate", false);
            mid4update = incomingIntent.getIntExtra("registrationId", 0);
        }
        Log.d(LOG_TAG, "This is called with : " + isparameterUpdate);

        mPositionName = (EditText) this.findViewById(R.id.input_position);
        mItemName = (EditText) this.findViewById(R.id.input_item);
        mStock = (EditText) this.findViewById(R.id.input_stock);
        mWMS = (EditText) this.findViewById(R.id.input_wms);
        mDifference = (EditText) this.findViewById(R.id.input_difference);
        //mStock.setText(String.valueOf(stock_final));
        //mWMS.setText(String.valueOf(mWMS_final));
        //mDifference.setText(String.valueOf(difference_final));

        InventoryDBHelper dbHelper = new InventoryDBHelper(this);
        mDb = dbHelper.getWritableDatabase();

        ImageButton btn = (ImageButton) findViewById(R.id.imageButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity(ScanningActivity.class, "position");
            }
        });

        ImageButton btn2 = (ImageButton) findViewById(R.id.imageButton2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity(ScanningActivity.class, "item");
            }
        });

        mStock.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                try {
                    stock_final = Integer.parseInt(mStock.getText().toString());
                } catch (NumberFormatException ex) {
                    stock_final = 0;
                    Log.d(LOG_TAG, "Failed to parse party size text to number: " + ex.getMessage());
                }
                difference_final = stock_final - mWMS_final;
                mDifference.setText(String.valueOf(difference_final));
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        mWMS.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                try {
                    mWMS_final = Integer.parseInt(mWMS.getText().toString());
                } catch (NumberFormatException ex) {
                    mWMS_final = 0;
                    Log.d(LOG_TAG, "Failed to parse party size text to number: " + ex.getMessage());
                }
                difference_final = stock_final - mWMS_final;
                mDifference.setText(String.valueOf(difference_final));
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        //if it is update append values
        if (isparameterUpdate == true) {
            String id = String.valueOf(mid4update);
            Cursor mCursor = getRegistration(id);
            if (mCursor.moveToFirst()) {
                mPositionName.setText(mCursor.getString(mCursor.getColumnIndex(InventoryAppContract.PositionEntry.COLUMN_POSITION)));
                mItemName.setText(mCursor.getString(mCursor.getColumnIndex(InventoryAppContract.PositionEntry.COLUMN_ITEM)));
                mStock.setText(mCursor.getString(mCursor.getColumnIndex(InventoryAppContract.PositionEntry.COLUMN_STOCK)));
                mWMS.setText(mCursor.getString(mCursor.getColumnIndex(InventoryAppContract.PositionEntry.COLUMN_WMS)));
                mDifference.setText(mCursor.getString(mCursor.getColumnIndex(InventoryAppContract.PositionEntry.COLUMN_DIFFERENCE)));
            }
            mCursor.close();
        }

    }

    public void launchActivity(Class<?> clss, String field) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            intent.putExtra("field", field);
            startActivityForResult(intent, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        Log.d(LOG_TAG, "OnActivity Resut");
        if (requestCode == CommonStatusCodes.SUCCESS) {
            // Make sure the request was successful
            String field = data.getStringExtra("field");
            Barcode barcode = data.getParcelableExtra("barcode");
            if (field.equals("position"))
                mPositionName.setText(barcode.displayValue);
            else
                mItemName.setText(barcode.displayValue);
            getIntent().removeExtra("field");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mClss != null) {
                        Intent intent = new Intent(this, mClss);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    public void sendToDB(View view) {
        //Mandatory fields
        if (mPositionName.getText().length() == 0 ||
                mPositionName.getText().length() == 0) {
            return;
        }

        if (isparameterUpdate == true) {
            long reply = updateRegistration(String.valueOf(mid4update), mPositionName.getText().toString(), mItemName.getText().toString(), stock_final, mWMS_final, difference_final);
            if (reply != -1) {
                Toast.makeText(getBaseContext(), "Registration has been updated", Toast.LENGTH_LONG).show();
                isparameterUpdate = false;
            } else {
                Toast.makeText(getBaseContext(), "Registration can not be updated", Toast.LENGTH_LONG).show();
            }
        } else {
            long reply = addNewRegistration(mPositionName.getText().toString(), mItemName.getText().toString(), stock_final, mWMS_final, difference_final);
            if (reply != -1) {
                Toast.makeText(getBaseContext(), "Registration has been created", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getBaseContext(), "Registration can not been created", Toast.LENGTH_LONG).show();
            }
        }
        clearValues();
    }

    public void clearValues() {
        mPositionName.getText().clear();
        mItemName.getText().clear();
        mStock.getText().clear();
        mWMS.getText().clear();
        mDifference.getText().clear();
    }

    private long updateRegistration(String id, String position, String item, int stock, int wms, int difference) {
        ContentValues cv = new ContentValues();
        cv.put(InventoryAppContract.PositionEntry.COLUMN_POSITION, position);
//        cv.put(InventoryAppContract.PositionEntry.COLUMN_POSITION, position);
        cv.put(InventoryAppContract.PositionEntry.COLUMN_ITEM, item);
        cv.put(InventoryAppContract.PositionEntry.COLUMN_STOCK, stock);
        cv.put(InventoryAppContract.PositionEntry.COLUMN_WMS, wms);
        cv.put(InventoryAppContract.PositionEntry.COLUMN_DIFFERENCE, difference);
        return mDb.update(InventoryAppContract.PositionEntry.TABLE_NAME_REGISTRATIONS, cv, "_id=?", new String[]{id});
    }

    private long addNewRegistration(String position, String item, int stock, int wms, int difference) {
        ContentValues cv = new ContentValues();
        cv.put(InventoryAppContract.PositionEntry.COLUMN_POSITION, position);
        cv.put(InventoryAppContract.PositionEntry.COLUMN_ITEM, item);
        cv.put(InventoryAppContract.PositionEntry.COLUMN_STOCK, stock);
        cv.put(InventoryAppContract.PositionEntry.COLUMN_WMS, wms);
        cv.put(InventoryAppContract.PositionEntry.COLUMN_DIFFERENCE, difference);
        return mDb.insert(InventoryAppContract.PositionEntry.TABLE_NAME_REGISTRATIONS, null, cv);
    }

    private Cursor getRegistration(String id) {
        String selection = "_id=?";
        String[] selectionArgs = new String[]{id};
        return mDb.query(
                InventoryAppContract.PositionEntry.TABLE_NAME_REGISTRATIONS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                InventoryAppContract.PositionEntry.COLUMN_TIMESTAMP
        );
    }
}

