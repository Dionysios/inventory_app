package com.dionpapas.inventoryapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.design.widget.FloatingActionButton;

import com.dionpapas.inventoryapp.data.InventoryAppContract;
import com.dionpapas.inventoryapp.data.InventoryDBHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static String LOG_TAG = "MainActivty";
    private SQLiteDatabase mDb;
    private PositionsListAdapter mAdapter;
    Context mContext;
    private static final String SHARED_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".myfileprovider";
    private static final String SHARED_FOLDER = "shared";

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

        Cursor cursor = getAllPositions();
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
                mAdapter.swapCursor(getAllPositions());
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
        mAdapter.swapCursor(getAllPositions());
        //getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }


    private Cursor getAllPositions() {
        return mDb.query(
                InventoryAppContract.PositionEntry.TABLE_NAME_REGISTRATIONS,
                null,
                null,
                null,
                null,
                null,
                InventoryAppContract.PositionEntry.COLUMN_TIMESTAMP
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

    public boolean fileExist(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        Log.i(LOG_TAG,"this is the location file");
        return file.exists();
    }


    public void exportDB() throws IOException {
        String filename = "export.csv";
        // Create a random image and save it in private app folder
        File sharedFile = createFile();
        Log.d(LOG_TAG,"shared file" + sharedFile);
        send_email(sharedFile);
    }


    private File createFile() throws IOException {
        final File sharedFolder = new File(getFilesDir(), SHARED_FOLDER);
        sharedFolder.mkdirs();
       // final File sharedFile = File.createTempFile("export", ".csv", sharedFolder);
        String filename="export.csv";
        File sharedFile = new File(getExternalFilesDir (SHARED_FOLDER), filename);
        sharedFile.createNewFile();
        writeFile(sharedFile);
        return sharedFile;
    }

    private void writeFile(File destination ) {
//        FileOutputStream outputStream;
//        String columnString =   "\"PersonName\",\"Gender\",\"Street1\",\"postOffice\",\"Age\"";
//        String dataString   =   "\"" + "test1" +"\",\"" + "test2" + "\",\"" + "test3" + "\",\"" + "test4"+ "\",\"" + "test5" + "\""+
//        "\"" + "test6" +"\",\"" + "test7" + "\",\"" + "test8" + "\",\"" + "test9"+ "\",\"" + "test10" + "\"";
//        String combinedString = columnString + "\n" + dataString;
//
//        try {
//            Log.d(LOG_TAG,"File location" + destination.getName());
//            outputStream = openFileOutput(destination.getName(), Context.MODE_PRIVATE);
//            outputStream.write(combinedString.getBytes());
//            outputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        File file = new File(getExternalFilesDir(SHARED_FOLDER), destination.getName());

        String filename = "myfile";
        String string = "Hello world!";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(destination.getName(), Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readFile(File sharedFile){
        FileInputStream in = null;
        try {
            in = openFileInput(sharedFile.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader inputStreamReader = new InputStreamReader(in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                Log.d(LOG_TAG,"Reading file to see if it empty" + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void send_email(File file){
        Uri path = null;
        try {
            path = FileProvider.getUriForFile(mContext, SHARED_PROVIDER_AUTHORITY, file );
            //boolean is_there = fileExist(file.getName());
            //testing if file is empty
            //readFile(file);
            //Log.d(LOG_TAG,"File verified " + is_there + " pth is" + file.getAbsolutePath());
        } catch (IllegalArgumentException e) {
            Log.e("File Selector", "The selected file can't be shared: " + file.getName() + e);
        }
        Log.d(LOG_TAG,"URI is" + path);

        String to[] = {"nionios250@gmail.com"};
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        //shareIntent.setType("shared/*");
        shareIntent.setType("text/csv");
        //path = Uri.parse("content://com.dionpapas.inventoryapp.myfileprovider/shared/test.csv");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_EMAIL, to);
        shareIntent.putExtra(Intent.EXTRA_STREAM, path);
        mContext.startActivity(Intent.createChooser(shareIntent,"Send mail..."));
    }
}
