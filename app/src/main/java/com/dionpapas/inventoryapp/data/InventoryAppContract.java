package com.dionpapas.inventoryapp.data;

/**
 * Created by dionpa on 2017-11-03.
 */
import android.provider.BaseColumns;

public class InventoryAppContract {

    public static final class PositionEntry implements BaseColumns {
        public static final String TABLE_NAME_REGISTRATIONS = "registrations";
        public static final String COLUMN_POSITION = "position_name";
        public static final String COLUMN_ITEM = "item";
        public static final String COLUMN_STOCK = "stock";
        public static final String COLUMN_WMS = "wms";
        public static final String COLUMN_DIFFERENCE= "difference";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
