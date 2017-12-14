package com.dionpapas.inventoryapp.data;

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

        int limit = 11;

        for (int i=1 ; i<limit ; i++){
            ContentValues cv = new ContentValues();
            int value1 = 2;
            int value2 = 1;
            cv.put(InventoryAppContract.PositionEntry.COLUMN_POSITION, "05361" + i);
            cv.put(InventoryAppContract.PositionEntry.COLUMN_ITEM, "12358" + i);
            cv.put(InventoryAppContract.PositionEntry.COLUMN_STOCK, value1 + i);
            cv.put(InventoryAppContract.PositionEntry.COLUMN_WMS, value2 + i);
            cv.put(InventoryAppContract.PositionEntry.COLUMN_DIFFERENCE, (value1 - value2) + i);
            list.add(cv);
        }

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
