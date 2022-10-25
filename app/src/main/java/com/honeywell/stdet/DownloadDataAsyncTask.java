package com.honeywell.stdet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;

public class DownloadDataAsyncTask extends AsyncTask {

        public Handler mHandler;
        private File directoryApp;

        public File GetDirectory() {
                return directoryApp;
        }

        Context context;

        public static String rslt = "";
        /**
         * Called when the activity is first created.
         */
        public HandHeld_SQLiteOpenHelper dbHelper;

        public DownloadDataAsyncTask(Context ct) {
                context = ct;
                directoryApp = context.getFilesDir();
        }


        @Override
        protected Integer doInBackground(Object... objects) {
                //final AlertDialog ad = new AlertDialog.Builder(context).create();
                try {
                        CallSoapWS cs = new CallSoapWS(directoryApp);

                        System.out.println("do in backgroind");
                        String resp = "Download of LookUp Tables has been Completed";

                        //Looper.prepare();
                        StdetFiles f = new StdetFiles(directoryApp);
                        //Looper.loop();

                        StdetDataTables tables = cs.WS_GetALLDatasets();
                        dbHelper = new HandHeld_SQLiteOpenHelper(context, tables);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        dbHelper.getInsertFromTables(db);
                        db.close();
                        //Looper.loop();
                        //ad.setMessage(resp);
                } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println(ex.toString());
                        return -1;
                }
                //ad.show();
                return 0;
        }

        protected void onProgressUpdate(Integer progress) {
                Log.i("------------onProgressUpdate", "onProgressUpdate");
        }


        protected void onPostExecute(Long result) {
                Log.i("------------onPostExecute", String.valueOf(result));
                final AlertDialog ad = new AlertDialog.Builder(context).create();
                if (result < 0) {
                        ad.setTitle("Error!");
                        ad.setMessage("The data has been downloaded with errors");
                } else {
                        ad.setTitle("Sucsess!");
                        ad.setMessage("The data has been downloaded correctly");
                }
                ad.show();
        }

}
