package com.example.dionpapas.inventoryapp.data;

/**
 * Created by dionpa on 2017-11-03.
 */
import android.provider.BaseColumns;

public class InventoryAppContract {

    public static final class PositionEntry implements BaseColumns {
        // COMPLETED (2) Inside create a static final members for the table name and each of the db columns
        //Table positions
        public static final String TABLE_NAME_POSITIONS = "positions";

        public static final String COLUMN_POSITION = "position_name";
        public static final String COLUMN_ITEM = "item";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_WMS = "quantity_wms";
        public static final String COLUMN_DIFFERENCE= "difference";
        public static final String COLUMN_TIMESTAMP = "timestamp";
//        //Table Items
//        public static final String TABLE_NAME_ITEMS = "items";
//        public static final String COLUMN_Position_name = "position_name";
//        public static final String COLUMN_ITEM_NAME = "item_name";
//        public static final String COLUMN_ITEM_QUANTITY = "item_quantity";
    }
}
