package com.honeywell.stdet;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import java.text.ParseException;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.AidcManager.CreatedCallback;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.InvalidScannerNameException;

public class MainActivity extends Activity {

    private static BarcodeReader barcodeReader;
    private AidcManager manager;

    @SuppressLint("StaticFieldLeak")
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

    private Stdet_Inst_Readings default_reading;


    private File directoryApp;

    public File GetDirectory() {
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
        directoryApp = getFilesDir();
        context = this;
        default_reading = Stdet_Inst_Readings.GetDefault();
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
                } catch (
                        InvalidScannerNameException e) {
                    Toast.makeText(MainActivity.this, "Invalid Scanner Name Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
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
        if (id == R.id.menu_LoginInfo) {
            Intent barcodeIntent = new Intent("android.intent.action.LOGINACTIVITY");
            startActivity(barcodeIntent);
            return true;
        }

        // a potentially time consuming task
        if (id == R.id.menu_UploadLookupData) {
            new ParseXMLAndUploadToDBAsyncTask(this).execute("run");
            return true;
        }
        // a potentially time consuming task
        if (id == R.id.menu_DownloadData) {
            new DownloadDataAsyncTask(context).execute("run");
            return true;
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

        this.btnInputForms = findViewById(R.id.btnInputForms);
        btnInputForms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("------------onClick StDetInputActivity", "12");
                // get the intent action string from AndroidManifest.xml
                Intent barcodeIntent = new Intent("android.intent.action.STDETINPUTBARCODEACTIVITY");
                barcodeIntent.putExtra("IR", default_reading);
                startActivity(barcodeIntent);
                System.out.println("In MAIN btnInputForms.setOnClickListener " + default_reading.getValueFromData(0, Stdet_Inst_Readings.strD_Col_ID));
            }
        });


        this.btnUploadDataToServer = findViewById(R.id.buttonUploadReadings);
        btnUploadDataToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HandHeld_SQLiteOpenHelper dbHelper =
                        new HandHeld_SQLiteOpenHelper(context, new StdetDataTables());
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Integer[] nrecords = new Integer[]{0};
                String message = "The data (" + nrecords[0] + " records) is ready to be uplaoded to the server.";
                //AlertDialogShow("The data (" + String.valueOf(records) + " records) is saved and ready to be uplaoded","Info","OK");
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                AlertDialogShow(message, "Info", "OK");
                String s = null;
                try {
                    s = dbHelper.CreateFileToUpload(db, directoryApp, nrecords);
                } catch (ParseException e) {
                    e.printStackTrace();
                    nrecords[0] = 0;
                }


                if (nrecords[0] > 0) {

                    try {
                        Path path = Paths.get(s);
                        CallSoapWS ws = new CallSoapWS(directoryApp);
                        byte[] dataUpload = Files.readAllBytes(path);
                        String[] credentials = dbHelper.getLoginInfo(db);
                        String name = credentials[0];
                        String encryptedPassword = credentials[1];
                        String pwd = StDEtEncrypt.decrypt(encryptedPassword);
                        String[] errormessage = new String[]{""};

                        Boolean bCanUpload = ws.WS_GetLogin(name, pwd, errormessage);
                        Boolean bUploaded;
                        if (bCanUpload) {
                            bUploaded = ws.WS_UploadFile2(dataUpload, s, name, pwd);
                            if (bUploaded) {
                                db.execSQL(Stdet_Inst_Readings.UpdateUploadedData());
                                AlertDialogShow(nrecords[0] + " Records Has Been Uploaded to the Server",
                                        "Info", "OK");
                            } else {
                                AlertDialogShow("Data hasn't been uploaded. Try one more time.", "ERROR!", "OK");
                            }

                        } else {
                            AlertDialogShow("Your Credentials aren't working. Go to Main Page | Menu | Check Login Credentials. : " + errormessage[0], "ERROR!", "OK");
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                }
            }
        });

    }

    private void AlertDialogShow(String message, String title, String button) {
        AlertDialog ad = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();

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
