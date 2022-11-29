package com.honeywell.stdet;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.File;


public class ParseXMLAndUploadToDBThread{
    Context context;
    public Handler mHandler;
    TextView txtInfo;
    ProgressBar progressBar;
    public Activity activity;
    boolean bDownloadFromWS;
    private Button btnInputForms;
    private Button btnReviewForms;
    private Button btnUploadDataToServer;

    public ParseXMLAndUploadToDBThread(Activity _activity, boolean _bDownloadFromWS) {

        activity = _activity;
        context = activity;
        directoryApp = context.getFilesDir();
        txtInfo =    (TextView) activity. findViewById(R.id.txtInfo);
        progressBar = (ProgressBar) activity. findViewById(R.id.progressBar);
        btnInputForms = (Button) activity.findViewById(R.id.btnInputForms);
        btnUploadDataToServer = (Button) activity.findViewById(R.id.buttonUploadReadings);
        btnReviewForms = (Button) activity.findViewById(R.id.buttonReviewReadings);
        txtInfo.setText(" Start");
        bDownloadFromWS = _bDownloadFromWS;

        populateDB();

    }

    private File directoryApp;

    public File GetDirectory() {
        return directoryApp;
    }

    public static String rslt = "";
    /**
     * Called when the activity is first created.
     */
    public HandHeld_SQLiteOpenHelper dbHelper;


    private void populateDB() {

        //ExecutorService es = Executors.newFixedThreadPool(1);
        // Display message only for better readability

        ExecutorService executor = Executors.newFixedThreadPool(3);//.newSingleThreadScheduledExecutor();
        progressBar.setVisibility(View.VISIBLE);
        final Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {

            int count;
            @Override
            public void run() {
                //Background work here
                try {
                System.out.println("Startings run");

                    btnInputForms.setEnabled(false);
                    btnUploadDataToServer.setEnabled(false);
                    btnReviewForms.setEnabled(false);
                    doInBackground();
                    //System.out.println("After  doInBackground();");
                   // onPostExecute(11);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //UI Thread work here
                             onPostExecute(11);
                            }

                        private void onPostExecute(Integer result) {
                            System.out.println("INSIDE THIS ONPOSTEXECUTE");
                            btnInputForms.setEnabled(true);
                            btnUploadDataToServer.setEnabled(true);
                            btnReviewForms.setEnabled(true);
                          
                        }


                    });
                } catch (Exception e) {
                    System.out.println("Error :: "+e.toString());

                }
            }
        });

        // Display message only for better readability
        System.out.println("Done");
        progressBar.setVisibility(View.INVISIBLE);
    }


    private void onPostExecute(int result) {
        System.out.println("INSIDE THAT ONPOSTEXECUTE");
        btnInputForms.setEnabled(true);
        btnUploadDataToServer.setEnabled(true);

        //txtInfo.setText(" Done");
        Log.i("------------onPostExecute", String.valueOf(result));

        final AlertDialog ad = new AlertDialog.Builder(context).create();

        if (result < 0) {
            AlertDialogShow("The data has been uploaded with errors",
                    "Error", "OK");
        } else {
            AlertDialogShow("The data has been uploaded with errors",
                    "Success", "OK");

        }


    }


    private void AlertDialogShow(String message, String title, String button) {
        AlertDialog ad = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();

    }



    private int doInBackground() {

        if (!directoryApp.exists())
            directoryApp.mkdir();

        System.out.println("Starting doInBackground");
        StdetFiles f = new StdetFiles(directoryApp);
        System.out.println("Starting new StdetFiles(directoryApp);");
        StdetDataTables tables;// = f.ReadXMLToSTDETables();


        //final AlertDialog ad = new AlertDialog.Builder(context).create();
        try {
            System.out.println("doInBackground");
            String resp = "LookUp Tables Loadeding";

            if (bDownloadFromWS) {
                //CHECK CONNECTION
                CallSoapWS ws1 = new CallSoapWS(null);
                String response = ws1.CheckConnection();
                boolean bConnection = true;
                if (response.startsWith("ERROR")) {
                    Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                    txtInfo.setText(response);
                    bConnection = false;
                    //btnInputForms.setEnabled(true);
                    //btnUploadDataToServer.setEnabled(true);
                }
                if (bConnection) {
                    CallSoapWS cs = new CallSoapWS(directoryApp);
                    try {
                        publishProgressTextView(" Starting bringing data from the webservice");
                        publishProgressBar(1);
                        resp = cs.WS_GetServerDate(true);
                        tables = cs.WS_GetALLDatasets();
                        publishProgressTextView(resp);
                    } catch (Exception ex) {
                        publishProgressTextView(ex.toString());
                        publishProgressBar(1);
                    }
                }
            }
            dbHelper = new HandHeld_SQLiteOpenHelper(context, new StdetDataTables());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            tables = new StdetDataTables();//f.ReadXMLToSTDETables();

            try {

                tables.AddStdetDataTable(new Stdet_Inst_Readings());
                tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.UNIT_DEF + ".xml"));
                publishProgressTextView("  Table  " + HandHeld_SQLiteOpenHelper.UNIT_DEF + " is reading to memory ");
                publishProgressBar(1);
                tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.FACILITY + ".xml"));
                publishProgressTextView("  Table  " + HandHeld_SQLiteOpenHelper.FACILITY + " is reading to memory ");
                publishProgressBar(2);
                tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.DATA_COL_IDENT + ".xml"));
                publishProgressTextView("  Table  " + HandHeld_SQLiteOpenHelper.DATA_COL_IDENT + " is reading to memory ");
                publishProgressBar(3);
                tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.ELEVATIONS + ".xml"));
                publishProgressTextView("  Table  " + HandHeld_SQLiteOpenHelper.ELEVATIONS + " is reading to memory ");
                publishProgressBar(4);
                tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.DCP_LOC_CHAR + ".xml"));
                publishProgressTextView("  Table  " + HandHeld_SQLiteOpenHelper.DCP_LOC_CHAR + " is reading to memory");
                publishProgressBar(5);
                tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.DCP_LOC_DEF + ".xml"));
                publishProgressTextView("  Table  " + HandHeld_SQLiteOpenHelper.DCP_LOC_DEF + " is reading to memory ");
                publishProgressBar(6);
                tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.EQUIP_OPER_DEF + ".xml"));
                publishProgressBar(7);
                publishProgressTextView("  Table  " + HandHeld_SQLiteOpenHelper.EQUIP_OPER_DEF + " is reading to memory ");
                tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.FAC_OPER_DEF + ".xml"));
                publishProgressBar(8);
                publishProgressTextView("  Table  " + HandHeld_SQLiteOpenHelper.FAC_OPER_DEF + " is reading to memory ");
                tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.TABLEVERS + ".xml"));
                publishProgressBar(9);
                publishProgressTextView("  Table  " + HandHeld_SQLiteOpenHelper.TABLEVERS + " is reading to memory ");
                tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.ELEVATIONCODES + ".xml"));
                publishProgressTextView("  Table  " + HandHeld_SQLiteOpenHelper.ELEVATIONCODES + " is reading to memory ");
                tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.EQUIP_OPER_DEF + ".xml"));
                publishProgressBar(10);
                publishProgressTextView("  Table  " + HandHeld_SQLiteOpenHelper.EQUIP_OPER_DEF + " is reading to memory ");

            } catch (Exception exception) {
                exception.printStackTrace();
                System.out.println(exception.toString());
                return -1;
            }
            publishProgressTextView(" Start Uploading to DB");
            publishProgressBar(11);

            //dbHelper.getInsertFromTables(db);

            int n = tables.getDataTables().size();
            System.out.println("!!!!!!!In getInsertFromTables : " + String.valueOf(n));

            for (int i = 0; i < n; i++) {


                if (tables != null && tables.getDataTables().get(i).getName() != null) {
                    String tbName = tables.getDataTables().get(i).getName();
                    publishProgressBar(11+i);
                    publishProgressTextView("Inserting Data for table "+ String.valueOf(i+1)+ ": " + tbName);
                    System.out.println("In getInsertFromTables " + String.valueOf(i) + " " + tbName);
                    if (!tbName.equalsIgnoreCase("NA")) {
                        //db.beginTransaction();
                        dbHelper.getInsertFromTable(db, tables.getDataTables().get(i));
                        //db.endTransaction();
                    }
                }
            }
            publishProgressBar(20);
            publishProgressTextView(" Download and Upload Task Completed");

            //ad.setMessage(resp);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.toString());
            return -1;
        }
        //ad.show();

        return 0;
    }

    private void publishProgressBar(Integer progress) {
        progressBar.setProgress(progress*5);
        Log.i("------------onProgressUpdate", progress.toString());
    }
    private void publishProgressTextView(String progress) {
        txtInfo.setText(progress);
        Log.i("------------onProgressUpdate", progress);
    }
}




