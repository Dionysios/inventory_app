package com.dionpapas.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.dionpapas.inventoryapp.data.InventoryAppContract.*;


/**
 * Created by dionpa on 2017-11-03.
 */

public class InventoryDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    public InventoryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold waitlist data
        final String SQL_CREATE_POSITIONS_TABLE = "CREATE TABLE " + PositionEntry.TABLE_NAME_REGISTRATIONS + " (" +
                PositionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PositionEntry.COLUMN_POSITION + " TEXT NOT NULL, " +
                PositionEntry.COLUMN_ITEM + " TEXT NOT NULL, " +
                PositionEntry.COLUMN_STOCK + " INTEGER DEFAULT 0, " +
                PositionEntry.COLUMN_WMS + " INTEGER DEFAULT 0," +
                PositionEntry.COLUMN_DIFFERENCE + " INTEGER DEFAULT 0," +
                PositionEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_POSITIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PositionEntry.TABLE_NAME_REGISTRATIONS);
        onCreate(sqLiteDatabase);
    }
}
