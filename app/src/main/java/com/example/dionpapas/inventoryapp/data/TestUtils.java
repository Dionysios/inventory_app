package com.example.dionpapas.inventoryapp.data;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dionpa on 2017-11-03.
 */

public class TestUtils {
    public static void insertFakeData(SQLiteDatabase db){
        if(db == null){
            return;
        }
        //create a list of fake guests
        List<ContentValues> list = new ArrayList<ContentValues>();

        ContentValues cv = new ContentValues();
        cv.put(InventoryAppContract.PositionEntry.COLUMN_POSITION, "05361");
        cv.put(InventoryAppContract.PositionEntry.COLUMN_ITEM, "12358");
        cv.put(InventoryAppContract.PositionEntry.COLUMN_STOCK, 25);
        cv.put(InventoryAppContract.PositionEntry.COLUMN_WMS, 16);
        cv.put(InventoryAppContract.PositionEntry.COLUMN_DIFFERENCE, 9);

        list.add(cv);

        cv = new ContentValues();
        cv.put(InventoryAppContract.PositionEntry.COLUMN_POSITION, "05361");
        cv.put(InventoryAppContract.PositionEntry.COLUMN_ITEM, "5354534534");
        cv.put(InventoryAppContract.PositionEntry.COLUMN_STOCK, 20);
        cv.put(InventoryAppContract.PositionEntry.COLUMN_WMS, 16);
        cv.put(InventoryAppContract.PositionEntry.COLUMN_DIFFERENCE, 4);
        list.add(cv);

//        cv = new ContentValues();
//        cv.put(WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME, "Jessica");
//        cv.put(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE, 99);
//        list.add(cv);
//
//        cv = new ContentValues();
//        cv.put(WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME, "Larry");
//        cv.put(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE, 1);
//        list.add(cv);
//
//        cv = new ContentValues();
//        cv.put(WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME, "Kim");
//        cv.put(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE, 45);
//        list.add(cv);

        //insert all guests in one transaction
        try
        {
            db.beginTransaction();
            //clear the table first
            db.delete (InventoryAppContract.PositionEntry.TABLE_NAME_REGISTRATIONS,null,null);
            //go through the list and add one by one
            for(ContentValues c:list){
                db.insert(InventoryAppContract.PositionEntry.TABLE_NAME_REGISTRATIONS, null, c);
            }
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            //too bad :(
        }
        finally
        {
            db.endTransaction();
        }

    }
}
