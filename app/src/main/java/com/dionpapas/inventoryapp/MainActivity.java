package com.dionpapas.inventoryapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import static com.dionpapas.inventoryapp.data.InventoryAppContract.PositionEntry.COLUMN_TIMESTAMP;

public class MainActivity extends AppCompatActivity {

    private static String LOG_TAG = "MainActivty";
    private SQLiteDatabase mDb;
    private PositionsListAdapter mAdapter;
    Context mContext;
    private static final String SHARED_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".myfileprovider";
    private static final String SHARED_FOLDER = "shared";
    private String to[] = {"theodoropoulosvas@gmail.com"};
    private String emailBody= "Here comes the database export";
    private String emailSubject;

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
        Cursor cursor = getAllRegistrations();
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
                mAdapter.swapCursor(getAllRegistrations());
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


    }

    @Override
    protected void onResume() {
        super.onResume();
        // re-queries for all tasks
        mAdapter.swapCursor(getAllRegistrations());
        //getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }


    private Cursor getAllRegistrations() {
        return mDb.query(
                InventoryAppContract.PositionEntry.TABLE_NAME_REGISTRATIONS,
                null,
                null,
                null,
                null,
                null,
                COLUMN_TIMESTAMP
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
        String filename = "export_"+currentDate.toString() +".csv";
        emailSubject = "Database export on " + currentDate.toString();
        File shared = createFile(filename);
        writeFile(shared);
    }


    private File createFile(String filename) throws IOException {
        final File sharedFolder = new File(getFilesDir(), SHARED_FOLDER);
        sharedFolder.mkdirs();
        File sharedFile = new File(getExternalFilesDir (SHARED_FOLDER), filename);
        if(!sharedFile.exists())
            sharedFile.createNewFile();
            String columnString =   "\"Position\",\"Item\",\"Time\",\"Difference\"";
            FileOutputStream fos = new FileOutputStream(sharedFile);
            fos.write(columnString.getBytes());
            fos.close();
        return sharedFile;
    }

    private void writeFile(File destination ) {
 //       String columnString =   "\"Position\",\"Item\",\"Time\",\"Difference\"";
//        String combinedString = columnString + "\n" + dataString;

//        public static final String COLUMN_POSITION = "position_name";
//        public static final String COLUMN_ITEM = "item";
//        public static final String COLUMN_STOCK = "stock";
//        public static final String COLUMN_WMS = "wms";
//        public static final String COLUMN_DIFFERENCE= "difference";
//        public static final String COLUMN_TIMESTAMP = "timestamp";

        try {
            FileOutputStream fos = new FileOutputStream(destination);
            Cursor cursor = getAllRegistrations();
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String position = cursor.getString(cursor.getColumnIndex(COLUMN_POSITION)) + ",";
                    String item = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM)) + ",";
                    String timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)) + ",";
                    String difference = cursor.getString(cursor.getColumnIndex(COLUMN_DIFFERENCE))+ "\n";
                    String combinedString = position + item + timestamp + difference;

                    fos.write(combinedString.getBytes());
                    cursor.moveToNext();
                }
            }
//            outputStream = openFileOutput(destination.getName(), Context.MODE_PRIVATE);
            fos.close();
            Toast.makeText(getBaseContext(),"Data saved to file",Toast.LENGTH_SHORT).show();
            send_email(destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void readFile(File sharedFile){
//        FileInputStream in = null;
//        try {
//            in = openFileInput(sharedFile.getName());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        InputStreamReader inputStreamReader = new InputStreamReader(in);
//        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//        StringBuilder sb = new StringBuilder();
//        String line;
//        try {
//            while ((line = bufferedReader.readLine()) != null) {
//                sb.append(line);
//                Log.d(LOG_TAG,"Reading file to see if it empty" + line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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
