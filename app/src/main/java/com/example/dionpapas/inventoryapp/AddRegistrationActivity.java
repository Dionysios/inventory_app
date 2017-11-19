package com.example.dionpapas.inventoryapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.example.dionpapas.inventoryapp.data.InventoryAppContract;
import com.example.dionpapas.inventoryapp.data.InventoryDBHelper;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

/**
 * Created by dionpa on 2017-11-13.
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mPositionName = (EditText) this.findViewById(R.id.input_position);
        mItemName = (EditText) this.findViewById(R.id.input_item);
        mStock = (EditText) this.findViewById(R.id.input_stock);
        mWMS = (EditText) this.findViewById(R.id.input_wms);
        mDifference = (EditText) this.findViewById(R.id.input_difference);
        mStock.setText(String.valueOf(stock_final));
        mWMS.setText(String.valueOf(mWMS_final));
        mDifference.setText(String.valueOf(difference_final));

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
                }  catch (NumberFormatException ex) {
                    stock_final = 0;
                    Log.d(LOG_TAG, "Failed to parse party size text to number: " + ex.getMessage());
                }
                difference_final = stock_final - mWMS_final;
                mDifference.setText(String.valueOf(difference_final));
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        mWMS.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                try {
                    mWMS_final = Integer.parseInt(mWMS.getText().toString());
                }  catch (NumberFormatException ex) {
                    mWMS_final = 0;
                    Log.d(LOG_TAG, "Failed to parse party size text to number: " + ex.getMessage());
                }
                difference_final = stock_final - mWMS_final;
                mDifference.setText(String.valueOf(difference_final));
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

    }

    public void launchActivity(Class<?> clss , String field) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            intent.putExtra("field",field);
            startActivityForResult(intent, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        Log.d(LOG_TAG, "OnActivity Resut");
        if (requestCode == CommonStatusCodes.SUCCESS ) {
            // Make sure the request was successful
            String field = data.getStringExtra("field");
            Barcode barcode = data.getParcelableExtra("barcode");
            if (field.equals("position"))
                mPositionName.setText(barcode.displayValue);
            else
                mItemName.setText(barcode.displayValue);
            getIntent().removeExtra("field");
//            if (resultCode == RESULT_OK) {
//                // The user picked a contact.
//                // The Intent's data Uri identifies which contact was selected.
//
//                // Do something with the contact here (bigger example below)
//            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(mClss != null) {
                        Intent intent = new Intent(this, mClss);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    public void addToDB(View view) {
        if (mPositionName.getText().length() == 0 ||
                mPositionName.getText().length() == 0) {
            return;
        }
//        try {
//            stock_final = Integer.parseInt(mStock.getText().toString());
//            mWMS_final = Integer.parseInt(mWMS.getText().toString());
//            difference_final = Integer.parseInt(mDifference.getText().toString());
//        } catch (NumberFormatException ex) {
//            Log.d(LOG_TAG, "Failed to parse party size text to number: " + ex.getMessage());
//        }

        long reply = addNewItem(mPositionName.getText().toString(),mItemName.getText().toString(), stock_final, mWMS_final, difference_final);
        Log.d(LOG_TAG, "Item has been created" + reply);
        if(reply != -1) {
            Toast.makeText(getBaseContext(), "Registration has been created", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getBaseContext(), "Registration can not been created", Toast.LENGTH_LONG).show();
        }
        mPositionName.clearFocus();
        mItemName.getText().clear();
        mStock.getText().clear();
        mWMS.getText().clear();
        mDifference.getText().clear();
    }

    private long addNewItem(String position,String item, int stock, int wms, int difference ) {
        ContentValues cv = new ContentValues();
        cv.put(InventoryAppContract.PositionEntry.COLUMN_POSITION, position);
        cv.put(InventoryAppContract.PositionEntry.COLUMN_ITEM, item);
        cv.put(InventoryAppContract.PositionEntry.COLUMN_STOCK, stock);
        cv.put(InventoryAppContract.PositionEntry.COLUMN_WMS, wms);
        cv.put(InventoryAppContract.PositionEntry.COLUMN_DIFFERENCE, difference);
        return mDb.insert(InventoryAppContract.PositionEntry.TABLE_NAME_REGISTRATIONS, null, cv);
    }
}
