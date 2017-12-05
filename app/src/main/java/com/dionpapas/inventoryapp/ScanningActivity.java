package com.dionpapas.inventoryapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

/**
 * Created by dionpa on 2017-11-19.
 */

public class ScanningActivity extends AppCompatActivity {
    SurfaceView cameraView;
    private CameraSource cameraSource;
    private String field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        createCameraSource();
    }

    private void createCameraSource() {
        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(getApplicationContext())
                        //.setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                        .build();
        if (!barcodeDetector.isOperational()) {
            //TODO Should be toast here
            Log.d("ScanningActivity", "Could not set up barcodeDetector");
            //txtView.setText("Could not set up the barcodeDetector!");
            return;
        }

        Bundle extras = getIntent().getExtras();
        ;

        if (extras != null) {
            field = extras.getString("field");
        }


        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(640, 480)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    cameraSource.start(cameraView.getHolder());
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }

            });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                Log.d("ScanningActivity","this is the value" + barcodes);
                //Barcode thisCode = barcodes.valueAt(0);
                if(barcodes.size() > 0){
                    //Toast.makeText(getApplicationContext(), "Item found", Toast.LENGTH_SHORT);
                    Log.d("ScanningActivity","this is the value" +  barcodes.valueAt(0));
                    Intent intent = new Intent();
                    intent.putExtra("barcode", barcodes.valueAt(0));
                    intent.putExtra("field", field);
                    setResult(CommonStatusCodes.SUCCESS, intent);
                    finish();
                }
                //TODO append value to textView;
                //Log.d("ScanningActivity","this is the value" + thisCode.rawValue.toString());
                //TextView txtView = (TextView) findViewById(R.id.txtContent);
                //txtView.setText(thisCode.rawValue);
            }
        });
    }
}
