package com.dionpapas.inventoryapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dionpapas.inventoryapp.data.InventoryAppContract;
import com.dionpapas.inventoryapp.data.InventoryDBHelper;
import com.dionpapas.inventoryapp.data.TestUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.dionpapas.inventoryapp.data.InventoryAppContract.PositionEntry.COLUMN_DIFFERENCE;
import static com.dionpapas.inventoryapp.data.InventoryAppContract.PositionEntry.COLUMN_ITEM;
import static com.dionpapas.inventoryapp.data.InventoryAppContract.PositionEntry.COLUMN_POSITION;
import static com.dionpapas.inventoryapp.data.InventoryAppContract.PositionEntry.COLUMN_STOCK;
import static com.dionpapas.inventoryapp.data.InventoryAppContract.PositionEntry.COLUMN_TIMESTAMP;
import static com.dionpapas.inventoryapp.data.InventoryAppContract.PositionEntry.COLUMN_WMS;

public class MainActivity extends AppCompatActivity {

    private static String LOG_TAG = "MainActivty";
    private SQLiteDatabase mDb;
    private PositionsListAdapter mAdapter;
    Context mContext;
    private static final String SHARED_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".myfileprovider";
    private static final String SHARED_FOLDER = "shared";
    private String to[] = {"theodoropoulosvas@gmail.com"};
   // private String to[] = {"nionios250@gmail.com"};
    private String emailBody= "Here comes the database export";
    private String emailSubject;
    private boolean mFileExists;
    private String filename = "export.csv";
    private boolean clicked = false;
    private String orderBy = " ASC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        RecyclerView waitlistRecyclerView;
        waitlistRecyclerView = (RecyclerView) this.findViewById(R.id.all_guests_list_view);
        waitlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        InventoryDBHelper dbHelper = new InventoryDBHelper(this);
        mDb = dbHelper.getWritableDatabase();
        //Insert fake data
        //TestUtils.insertFakeData(mDb);
        //Get all data
        Cursor cursor = getAllRegistrations(orderBy);
        // Create an adapter for that cursor to display the data
        mAdapter = new PositionsListAdapter(this, cursor);
        // Link the adapter to the RecyclerView
        waitlistRecyclerView.setAdapter(mAdapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //do nothing, we only care about swiping
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //get the id of the item being swiped
                long id = (long) viewHolder.itemView.getTag();
                //remove from DB
                removePosition(id);
                //update the list
                mAdapter.swapCursor(getAllRegistrations(orderBy));
            }

        }).attachToRecyclerView(waitlistRecyclerView);

        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addTaskIntent = new Intent(MainActivity.this, AddRegistrationActivity.class);
                startActivity(addTaskIntent);
            }
        });

        final TextView mdateTextView = (TextView) this.findViewById(R.id.date_text);

        mdateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "Clicking the textView");
                if(!clicked){
                    mdateTextView.setBackgroundColor(Color.parseColor("#e5e5e5")); // silver
                    clicked = true;
                    orderBy = " DESC";
                    mAdapter.swapCursor(getAllRegistrations(orderBy));
                }else {
                    mdateTextView.setBackgroundColor(Color.parseColor("#80FFFFFF")); // white
                    clicked = false;
                    orderBy = " ASC";
                    mAdapter.swapCursor(getAllRegistrations(orderBy));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // re-queries for all tasks
        mAdapter.swapCursor(getAllRegistrations(orderBy));
        //getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }


    private Cursor getAllRegistrations(String orderBy) {
        return mDb.query(
                InventoryAppContract.PositionEntry.TABLE_NAME_REGISTRATIONS,
                null,
                null,
                null,
                null,
                null,
                COLUMN_TIMESTAMP + orderBy
        );
    }

    private boolean removePosition(long id) {
        return mDb.delete(InventoryAppContract.PositionEntry.TABLE_NAME_REGISTRATIONS, InventoryAppContract.PositionEntry._ID + "=" + id, null) > 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            default:
                try {
                    exportDB();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
                //return super.onOptionsItemSelected(item);
        }
    }

    public void exportDB() throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
        String currentDate = sdf.format(new Date());
        //String filename = "export_"+currentDate.toString() +".csv";

        emailSubject = "Database export on " + currentDate.toString();
        File shared = createFile(filename);
        writeFile(shared);
    }


    private File createFile(String filename) throws IOException {
        final File sharedFolder = new File(getFilesDir(), SHARED_FOLDER);
        sharedFolder.mkdirs();
        File sharedFile = new File(getExternalFilesDir (SHARED_FOLDER), filename);
        mFileExists = sharedFile.exists();
        if(!mFileExists) {
            sharedFile.createNewFile();
            String columnString = "\"Position\",\"Item\",\"Stock\",\"WMS\",\"Difference\",\"Date\"" + "\n";
            FileOutputStream fos = new FileOutputStream(sharedFile);
            fos.write(columnString.getBytes());
            fos.close();
            Log.d(LOG_TAG, "Creating file with colums");
        } else {
            Log.d(LOG_TAG, "File already exists");
        }
        return sharedFile;
    }

    private void writeFile(File destination ) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(destination,true);
            Cursor cursor = getAllRegistrations(orderBy);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String position = cursor.getString(cursor.getColumnIndex(COLUMN_POSITION)) + ",";
                    String item = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM)) + ",";
                    String stock = cursor.getString(cursor.getColumnIndex(COLUMN_STOCK)) + ",";
                    String WMS = cursor.getString(cursor.getColumnIndex(COLUMN_WMS)) + ",";
                    String difference = cursor.getString(cursor.getColumnIndex(COLUMN_DIFFERENCE))+ ",";
                    String timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)) + "\n";
                    String combinedString = position + item + stock + WMS + difference + timestamp;
                    fos.write(combinedString.getBytes());
                    long id = cursor.getLong(cursor.getColumnIndex(InventoryAppContract.PositionEntry._ID));
                    removePosition(id);
                    cursor.moveToNext();
                }
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//      outputStream = openFileOutput(destination.getName(), Context.MODE_PRIVATE);
        Toast.makeText(getBaseContext(),"Exporting data to file, cleaning db",Toast.LENGTH_SHORT).show();
        send_email(destination);
    }

    public void send_email(File file){
        Uri path = null;
        try {
            path = FileProvider.getUriForFile(mContext, SHARED_PROVIDER_AUTHORITY, file );

        } catch (IllegalArgumentException e) {
            Log.e("File Selector", "The selected file can't be shared: " + file.getName() + e);
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/csv");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_EMAIL, to);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, emailBody);
        shareIntent.putExtra(Intent.EXTRA_STREAM, path);
        mContext.startActivity(Intent.createChooser(shareIntent,"Send mail..."));
    }
}
