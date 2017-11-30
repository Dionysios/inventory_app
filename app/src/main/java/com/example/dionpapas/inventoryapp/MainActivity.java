package com.example.dionpapas.inventoryapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Toast;

import com.example.dionpapas.inventoryapp.data.InventoryAppContract;
import com.example.dionpapas.inventoryapp.data.InventoryDBHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    private static String LOG_TAG = "MainActivty";
    private SQLiteDatabase mDb;
    private PositionsListAdapter mAdapter;

   // PositionsListAdapter.ListItemClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        Log.d(LOG_TAG,"Reaching here");
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
                exportDB();
                return true;
                //return super.onOptionsItemSelected(item);
        }
    }

    public boolean fileExist(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }



    private void exportDB(){

        String filename = "export.csv";
        FileOutputStream outputStream;
        String columnString =   "\"PersonName\",\"Gender\",\"Street1\",\"postOffice\",\"Age\"";
        String dataString   =   "\"" + "test1" +"\",\"" + "test2" + "\",\"" + "test3" + "\",\"" + "test4"+ "\",\"" + "test5" + "\"";
        String combinedString = columnString + "\n" + dataString;

//        try {
//            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
//            try {
//                outputStream.write(combinedString.getBytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            outputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        boolean is_there = fileExist(filename);
//        Log.d(LOG_TAG,"File verified" + is_there);

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/");
        Log.d(LOG_TAG,"this is the dir" + myDir.toString());
        myDir.mkdirs();

        File file = new File (myDir, filename);
        if (file.exists ())
            file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(combinedString.getBytes());
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        send_email(filename);


//        String DATABASE_NAME = "inventory.db";
//        File sd = Environment.getExternalStorageDirectory();
//        FileChannel source=null;
//        FileChannel destination;
//        String backupDBPath = "back_up_" + DATABASE_NAME ;
//        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            File currentDB = getDatabasePath(DATABASE_NAME);
//            Log.d(LOG_TAG, "Let see if we found the database" + currentDB.getName() + " and " + currentDB.getAbsolutePath().toString());
//            Log.d(LOG_TAG, "Let see if where we are" + Environment.getExternalStorageDirectory().toString());
//            File backupDB = new File(sd, backupDBPath);
//            File f = new File(Environment.getExternalStorageDirectory() + "/databases/" + backupDBPath);
//            if(!f.exists()){
//                Log.d(LOG_TAG, "Creating file");
//             }else {
//                Log.d(LOG_TAG, "File exists");
//            }
//
//            try {
//                Log.d(LOG_TAG, currentDB.getName());
//                source = new FileInputStream(currentDB).getChannel();
//                destination = new FileOutputStream(backupDB).getChannel();
//                destination.transferFrom(source, 0, source.size());
//                source.close();
//                destination.close();
//                Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
//            } catch(IOException e) {
//                e.printStackTrace();
//                Log.d(LOG_TAG, "Error" + e);
//                Toast toast = Toast.makeText(getApplicationContext(), "Issue", Toast.LENGTH_SHORT);
//                toast.show();
//            }
//        }
    }
    
    public void send_email(String filename){
        File fileLocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);
        Uri path = Uri.fromFile(fileLocation);
      //  File file = getBaseContext().getFileStreamPath(fname);
        //Uri fileUri = Uri.fromFile(new File(context.getCacheDir()+ "/"+ filename));
        //Uri fileUri = Uri.fromFile(getBaseContext().getFileStreamPath(filename));

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // set the type to 'email'
        emailIntent .setType("vnd.android.cursor.dir/email");
        String to[] = {"nionios250@gmail.com"};
        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
        // the attachment
        emailIntent .putExtra(Intent.EXTRA_STREAM, path);
        // the mail subject
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject");
        startActivityForResult(Intent.createChooser(emailIntent , "Send email..."),1);
    }

}
