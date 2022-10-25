package com.honeywell.stdet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.view.MenuItem.OnMenuItemClickListener;
//import java.util.concurrent;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.AidcManager.CreatedCallback;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.InvalidScannerNameException;

public class MainActivity extends Activity {

    private static BarcodeReader barcodeReader;
    private AidcManager manager;

    private static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }

    private Button btnAutomaticBarcode;
    private Button btnClientBarcode;
    private Button btnInputForms;
    private Button btnScannerSelectBarcode;
    private Button btnFragmentView;
    private Button btnDownloadData;
    private Button btnParseXMLAndToDB;
    private Button btnUploadDataToServer;

    Context context;


    private File directoryApp;
    public File GetDirectory(){
        return directoryApp;
    }

   // public MainActivity() {
        //if(BuildConfig.DEBUG)
         //   StrictMode.enableDefaults();
   // }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        directoryApp =getFilesDir();
        context = this;
        // set lock the orientation
        // otherwise, the onDestory will trigger
        // when orientation changes
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        // create the AidcManager providing a Context and a
        // CreatedCallback implementation.
        AidcManager.create(this, new CreatedCallback() {

            @Override
            public void onCreated(AidcManager aidcManager) {
                manager = aidcManager;
                try {
                    barcodeReader = manager.createBarcodeReader();
                }

                catch (
            InvalidScannerNameException e){
                Toast.makeText(MainActivity.this, "Invalid Scanner Name Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
                catch (Exception e){
                Toast.makeText(MainActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            }
        });

        ActivitySetting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_CheckScanner) {
            Intent barcodeIntent = new Intent("android.intent.action.CLIENTBARCODEACTIVITY");
            startActivity(barcodeIntent);
            return true;
        }
        if (id == R.id.menu_SelectScanner) {
            Intent barcodeIntent = new Intent("android.intent.action.SCANNERSELECTBARCODEACTIVITY");
            startActivity(barcodeIntent);
            return true;
        }

        // a potentially time consuming task
        if (id == R.id.menu_UploadLookupData) {
            new ParseXMLAndUploadToDBAsyncTask(this).execute("run");
        }
        // a potentially time consuming task
        if (id == R.id.menu_DownloadData) {
            new DownloadDataAsyncTask(context).execute("run");
        }


        return super.onOptionsItemSelected(item);
    }

    static BarcodeReader getBarcodeObject() {
        return barcodeReader;
    }

    /**
     * Create buttons to launch demo activities.
     */
    public void ActivitySetting() {
        /*
        btnAutomaticBarcode = (Button) findViewById(R.id.buttonAutomaticBarcode);
        btnAutomaticBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the intent action string from AndroidManifest.xml
                Intent barcodeIntent = new Intent("android.intent.action.AUTOMATICBARCODEACTIVITY");
                startActivity(barcodeIntent);
            }
        });

        btnClientBarcode = (Button) findViewById(R.id.buttonClientBarcode);
        btnClientBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the intent action string from AndroidManifest.xml
                Intent barcodeIntent = new Intent("android.intent.action.CLIENTBARCODEACTIVITY");
                startActivity(barcodeIntent);
            }
        });
*/
        this.btnInputForms = (Button) findViewById(R.id.btnInputForms);
        btnInputForms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("------------onClick StDetInputActivity","12");
                // get the intent action string from AndroidManifest.xml
                Intent barcodeIntent = new Intent("android.intent.action.STDETINPUTBARCODEACTIVITY");
                startActivity(barcodeIntent);
            }
        });

        this.btnUploadDataToServer =   (Button) findViewById(R.id.buttonUploadReadings);
        btnUploadDataToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HandHeld_SQLiteOpenHelper  dbHelper =
                        new HandHeld_SQLiteOpenHelper(context,new StdetDataTables());
               SQLiteDatabase db = dbHelper.getReadableDatabase();
               String s = dbHelper.CreateFileToUpload(db,directoryApp);
                Path path = Paths.get(s);

                try {
                    CallSoapWS ws =  new CallSoapWS(directoryApp);
                    byte[] dataUpload = Files.readAllBytes(path);
                   ws.WS_UploadFile(dataUpload,s,"stdet_app", "stdet_pa$$");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
/*
        btnClientBarcode2 = (Button) findViewById(R.id.btnClientControl2);
        btnClientBarcode2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the intent action string from AndroidManifest.xml
                Intent barcodeIntent = new Intent("android.intent.action.CLIENTBARCODEACTIVITY2");
                startActivity(barcodeIntent);
            }
        });

        btnFragmentView = (Button) findViewById(R.id.btnFragmentActivity);
        btnFragmentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the intent action string from AndroidManifest.xml
                Intent barcodeIntent = new Intent("android.intent.action.FRAGMENTACTIVITY");
                startActivity(barcodeIntent);
            }
        });

        btnScannerSelectBarcode = (Button) findViewById(R.id.buttonScannerSelectBarcode);
        btnScannerSelectBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the intent action string from AndroidManifest.xml
                Intent barcodeIntent = new Intent(
                        "android.intent.action.SCANNERSELECTBARCODEACTIVITY");
                startActivity(barcodeIntent);
            }
        });
        btnDownloadData = (Button) findViewById(R.id.btnDownloadData);
        btnDownloadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the intent action string from AndroidManifest.xml
                Intent barcodeIntent = new Intent(
                        "android.intent.action.DOWNLOADACTIVITY");
                startActivity(barcodeIntent);
            }
        });
        btnParseXMLAndToDB = (Button) findViewById(R.id.btnUploadXMLtoDB);
        btnParseXMLAndToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the intent action string from AndroidManifest.xml
                Intent barcodeIntent = new Intent(
                        "android.intent.action.PARSEXMLACTIVITY");
                startActivity(barcodeIntent);
            }
        });
        */

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (barcodeReader != null) {
            // close BarcodeReader to clean up resources.
            barcodeReader.close();
            barcodeReader = null;
        }

        if (manager != null) {
            // close AidcManager to disconnect from the scanner service.
            // once closed, the object can no longer be used.
            manager.close();
        }
    }

}
