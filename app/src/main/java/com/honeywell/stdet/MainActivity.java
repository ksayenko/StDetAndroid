package com.honeywell.stdet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem.OnMenuItemClickListener;
//import java.util.concurrent;


import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.google.android.material.snackbar.Snackbar;
import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.AidcManager.CreatedCallback;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.InvalidScannerNameException;

public class MainActivity extends Activity {

    private static final int WRITE_REQUEST_CODE =1 ;
    private static final int REQUEST_CODE_GETMESSAGE =1014 ;
    private static BarcodeReader barcodeReader;
    private AidcManager manager;

    @SuppressLint("StaticFieldLeak")
    private static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }


    private Button btnInputForms;
    private Button btnReviewForms;
    private Button btnUploadDataToServer;
    private TextView  txtInfo;
    private TextView txtAppInfo;
    private ProgressBar progressBar;

    Context context;

    private Reading default_reading;
    int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;


    private File directoryApp;

    public File GetDirectory() {
        return directoryApp;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        directoryApp = getFilesDir();
        context = this;
        default_reading = Reading.GetDefaultReading();

        StdetDataTables tables= new StdetDataTables();
        tables.SetStdetTablesStructure();
        HandHeld_SQLiteOpenHelper dbHelper =  new HandHeld_SQLiteOpenHelper(context,tables);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
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

        if (id == R.id.menu_LoginInfo) {
            Intent barcodeIntent = new Intent("android.intent.action.LOGINACTIVITY");
            startActivity(barcodeIntent);
            return true;
        }

        // a potentially time consuming task
        if (id == R.id.menu_DownloadData) {
            progressBar.setVisibility(View.VISIBLE);
            new ParseXMLAndUploadToDBThread(this, true);
            return true;
        }
        if (id == R.id.menu_workWithSDCard) {

            boolean isSdcard = false;
            int iCopied = 0;
            int iMoved = 0;
            ArrayList<File> csvFiles = getListFiles(directoryApp, "csv");
            File pathToDB = new File(directoryApp.getParentFile() + "//databases");
            ArrayList<File> sqlFiles = getListFiles(pathToDB, "sqlite3");

            File folderStorage = null;
            File sdcard1 =new File("/storage/sdcard1");

            File[] folders_sdcard1 = (new File("/storage/sdcard1")).listFiles();
            // Choose folders_sdcard1
            File sdcard_dtsc =new File("/storage/sdcard1/Documents/DTSC Files");
            if (!sdcard_dtsc.exists())
                sdcard_dtsc.mkdir();
            File[] folders_sdcard_dtsc = (new File("/storage/sdcard1/Documents/DTSC Files")).listFiles();


             if (folders_sdcard_dtsc != null) {
                folderStorage = sdcard_dtsc;
            }
             else if (folders_sdcard1 != null) {
                 folderStorage = folders_sdcard1[0];
             }

            if (folderStorage != null && csvFiles != null) {
                try {

                    for (int i = 0; i < csvFiles.size(); i++) {
                        iMoved += moveFile(csvFiles.get(i).getParentFile().getPath(), csvFiles.get(i).getName(), folderStorage.getPath());

                    }
                    for (int i = 0; i < sqlFiles.size(); i++) {
                        iCopied += copyFile(sqlFiles.get(i).getParentFile().getPath(), sqlFiles.get(i).getName(), folderStorage.getPath());

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println(ex.toString());
                }
                Toast.makeText(context, String.valueOf(iMoved) + " files have been moved and " +
                        String.valueOf(iCopied) + " files have been copied ", Toast.LENGTH_SHORT).show();
            }
        }


        return super.onOptionsItemSelected(item);
    }

    private int moveFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        int rv = 0;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + "/" + inputFile);
            out = new FileOutputStream(outputPath + "/" + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;
            rv = 1;
            // delete the original file
            boolean bdeleted  = new File(inputPath + "/" + inputFile).delete();
            if (!bdeleted)
                rv = 0;
        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
            rv = 0;
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
            rv = 0;
        }
        return rv;

    }

    private int copyFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        int rv = 0;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath +"/"+inputFile);
            out = new FileOutputStream(outputPath +"/"+ inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;
            rv = 1;

        }  catch (FileNotFoundException fnfe1) {

            rv = 0;
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
            rv = 0;
        }
        return rv;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case WRITE_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Granted.


                } else {
                    //Denied.
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean isExternalStorageAvailable() {

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(permissions, WRITE_REQUEST_CODE);

        String state = Environment.getExternalStorageState();
        System.out.println(" Environment.getExternalStorageState();" + state);
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but
            // all we need
            // to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }

        if (mExternalStorageAvailable == true
                && mExternalStorageWriteable == true) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            Bundle extras = getIntent().getExtras();
            if (requestCode == REQUEST_CODE_GETMESSAGE  && resultCode  == RESULT_OK && extras != null) {

                default_reading = (Reading) getIntent().getSerializableExtra("IR");
                if (default_reading == null)
                    default_reading = Reading.GetDefaultReading();
                Toast.makeText(context, default_reading.getStrD_Col_ID(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Toast.makeText(context, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private ArrayList<File> getListFiles(File parentDir, String extension) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        if (files != null)
            for (File file : files) {
                if (file.isDirectory()) {
                    inFiles.addAll(getListFiles(file, extension));
                } else {
                    if (file.getName().endsWith(extension)) {
                        inFiles.add(file);
                    }
                }
            }
        return inFiles;
    }

    static BarcodeReader getBarcodeObject() {
        return barcodeReader;
    }

    /**
     * Create buttons to launch demo activities.
     */
    public void ActivitySetting() {

        this.txtInfo= findViewById(R.id.txtInfo);
        txtInfo.setText("Additional options available under the menu (three dots) above");

        this.btnInputForms = findViewById(R.id.btnInputForms);
        btnInputForms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("------------onClick StDetInputActivity", "12");
                // get the intent action string from AndroidManifest.xml
                Intent barcodeIntent = new Intent("android.intent.action.STDETINPUTBARCODEACTIVITY");
                barcodeIntent.putExtra("IR", default_reading);
                startActivityForResult(barcodeIntent,REQUEST_CODE_GETMESSAGE);
                System.out.println("In MAIN btnInputForms.setOnClickListener " + default_reading.getStrD_Loc_ID());


            }
        });

        this.btnReviewForms = findViewById(R.id.buttonReviewReadings);
        btnReviewForms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("------------onClick StDetEditListActivity", "12");
                // get the intent action string from AndroidManifest.xml
                Intent barcodeIntent = new Intent("android.intent.action.STDETEDITLISTACTIVITY");
                startActivity(barcodeIntent);
                System.out.println("In MAIN btnReviewForms.setOnClickListener " + default_reading.getStrD_Col_ID());
            }
        });


        this.btnUploadDataToServer = findViewById(R.id.buttonUploadReadings);
        btnUploadDataToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //CHECK CONNECTION
                CallSoapWS ws1 = new CallSoapWS(null);
                String response = ws1.CheckConnection();
                boolean bConnection = true;
                if (response.startsWith("ERROR")) {
                    Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                    txtInfo.setText(response);
                    bConnection = false;
                }
                if (bConnection) {

                    HandHeld_SQLiteOpenHelper dbHelper =
                            new HandHeld_SQLiteOpenHelper(context, new StdetDataTables());
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    Integer[] nrecords = new Integer[]{0};
                    String message = "The data (" + nrecords[0] + " records) is ready to be uplaoded to the server.";
                    //AlertDialogShow("The data (" + String.valueOf(records) + " records) is saved and ready to be uplaoded","Info","OK");
                    //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    //AlertDialogShow(message, "Info", "OK");
                    String s = null;
                    try {
                        s = dbHelper.CreateFileToUpload(db, directoryApp, nrecords);
                        //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
                            // For decryption not ise null or empty string
                            if (encryptedPassword ==null || encryptedPassword == "")
                                encryptedPassword = "NA";
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
            }
        });

        txtAppInfo = findViewById(R.id.txtAppInfo);
        txtAppInfo.setText("Version :" + versionName);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

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
