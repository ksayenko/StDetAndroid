    package com.honeywell.stdet;

    import android.app.Activity;
    import android.app.AlertDialog;
    import android.app.ProgressDialog;
    import android.content.Context;
    import android.database.sqlite.SQLiteDatabase;
    import android.os.AsyncTask;
    import android.os.Handler;
    import android.os.Looper;
    import android.os.Message;
    import android.util.Log;
    import android.widget.Button;
    import android.widget.TextView;

    import java.io.File;

    public class ParseXMLAndUploadToDBAsyncTask extends AsyncTask<String, Integer, Integer>{
        Context context;
        public Handler mHandler;
        TextView txtInfo;
        public Activity activity;

        public ParseXMLAndUploadToDBAsyncTask(Activity _activity) {

            activity = _activity;
            context = activity;
            directoryApp = context.getFilesDir();
            txtInfo =    (TextView) activity. findViewById(R.id.txtInfo);
            txtInfo.setText("In the ParseXMLAndUploadToDBAsyncTask");
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
            //Looper.prepare();
            StdetFiles f = new StdetFiles(directoryApp);
            //Looper.loop();

            StdetDataTables tables = f.ReadXMLToSTDETables();
            dbHelper = new HandHeld_SQLiteOpenHelper(context, tables);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            dbHelper.getInsertFromTables(db);
        }

        @Override
        protected Integer doInBackground(String... strings) {
            //final AlertDialog ad = new AlertDialog.Builder(context).create();
            try {
                System.out.println("do in backgroind");
                String resp = "LookUp Tables Loaded";

                //Looper.prepare();
                StdetFiles f = new StdetFiles(directoryApp);
                //Looper.loop();

                StdetDataTables tables = new StdetDataTables();
                try {

                    if (!directoryApp.exists())
                        directoryApp.mkdir();


                    tables.AddStdetDataTable(new Stdet_Inst_Readings());
                    tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.UNIT_DEF + ".xml"));

                    publishProgress(1);
                    tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.FACILITY + ".xml"));
                    publishProgress(2);
                    tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.DATA_COL_IDENT + ".xml"));
                    publishProgress(3);
                    tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.ELEVATIONS + ".xml"));
                    publishProgress(4);
                    tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.DCP_LOC_CHAR + ".xml"));
                    publishProgress(5);
                    tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.DCP_LOC_DEF + ".xml"));
                    publishProgress(6);
                    tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.EQUIP_OPER_DEF + ".xml"));
                    publishProgress(7);
                    tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.FAC_OPER_DEF + ".xml"));
                    publishProgress(8);
                    tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.TABLEVERS + ".xml"));
                    publishProgress(9);
                    tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.ELEVATIONCODES + ".xml"));
                    publishProgress(10);
                    tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.EQUIP_OPER_DEF + ".xml"));
                    publishProgress(11);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    System.out.println(exception.toString());
                    return null;
                }
                dbHelper = new HandHeld_SQLiteOpenHelper(context, tables);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                dbHelper.getInsertFromTables(db);

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

        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

            txtInfo.setText(" Table #  "+ progress[0].toString() + " Uploaded");
            Log.i("------------onProgressUpdate", progress[0].toString());

        }



        protected void onPostExecute(Integer... result) {
            super.onPostExecute(result[0]);
            txtInfo.setText(" Done");
            Log.i("------------onPostExecute", String.valueOf(result[0]));
            final AlertDialog ad = new AlertDialog.Builder(context).create();
            if (result[0] < 0) {
                ad.setTitle("Error!");
                ad.setMessage("The data has been uploaded with errors");
            } else {
                ad.setTitle("Sucsess!");
                ad.setMessage("The data has been uploaded correctly");
            }
            ad.show();


        }
    }




