
    package com.honeywell.stdet;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.File;

    public class ParseXMLAndUploadToDBActivity extends Activity {
        private File directoryApp;

        public File GetDirectory() {
            return directoryApp;
        }

        public static String rslt = "";
        /**
         * Called when the activity is first created.
         */
        public HandHeld_SQLiteOpenHelper dbHelper;
        Context ct = this;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            directoryApp = getFilesDir();

            //Button btnDownloadData = (Button) findViewById(R.id.btnUploadXMLtoDB);
            final AlertDialog ad = new AlertDialog.Builder(this).create();

            //View v = findViewById(R.id.btnDownloadData);

            StdetFiles f = new StdetFiles(directoryApp);
            try {
                String resp = "Completed";
                StdetDataTables tables = f.ReadXMLToSTDETables();
                dbHelper = new HandHeld_SQLiteOpenHelper(ct, tables);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                dbHelper.getInsertFromTables(db);
                ad.setMessage(resp);
            } catch (Exception ex) {
                ad.setTitle("Error!");
                ad.setMessage(ex.toString());
            }
            ad.show();
        }


    }

