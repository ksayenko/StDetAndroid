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

public class DownloadDataActivity extends Activity {
private File directoryApp;
public File GetDirectory(){
        return directoryApp;
}
public static String rslt="";    /** Called when the activity is first created. */
public  HandHeld_SQLiteOpenHelper dbHelper;
Context ct = this;

@Override
public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        directoryApp =getFilesDir();

        //Button btnDownloadData=(Button)findViewById(R.id.btnDownloadData);
final  AlertDialog ad=new AlertDialog.Builder(this).create();

        //btnDownloadData.setOnClickListener(new OnClickListener() {
/*
@Override public void onClick(View arg0) {
        // TODO Auto-generated method stub
        */

        //View v = findViewById(R.id.btnDownloadData);
        CallSoapWS cs=new CallSoapWS(directoryApp);


        try
        {
                String resp=cs.WS_GetServerDate(true);
                StdetDataTables tables= cs.WS_GetALLDatasets();
                dbHelper =  new HandHeld_SQLiteOpenHelper(ct,tables);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                dbHelper.getInsertFromTables(db);
                ad.setMessage(resp);
                db.close();
        }catch(Exception ex)
        {
                ad.setTitle("Error!");
                ad.setMessage(ex.toString());
        }
        ad.show();
}
       // });
}
