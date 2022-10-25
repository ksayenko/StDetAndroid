package com.honeywell.stdet;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;


public class HandHeld_SQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HandHeld.sqlite3";
    private static final int DATABASE_VERSION = 1;

    //default facility names:
    public static final String FACILITY_ID = "1";
    public static final String FACILITY_NAME = "SFDBSQL Facility";

    public static final String INST_READINGS = "tbl_Inst_Readings";
    public static final String DATA_COL_IDENT = "tbl_Data_Col_Ident";
    public static final String DCP_LOC_CHAR = "tbl_DCP_Loc_Char";
    public static final String DCP_LOC_DEF = "tbl_DCP_Loc_Def";
    public static final String EQUIP_OPER_DEF = "tbl_Equip_Oper_Def";
    public static final String FAC_OPER_DEF = "tbl_Fac_Oper_Def";
    public static final String UNIT_DEF = "tbl_Unit_Def";
    public static final String TABLEVERS = "tbl_TableVers";
    public static final String FACILITY = "dt_facility";
    public static final String ELEVATIONS = "ut_elevations";
    public static final String ELEVATIONCODES = "ut_elevation_codes";

    private StdetDataTables tables;
    public static SQLiteDatabase db;


    // filename prefix for the Readings dataset xml file
    public static final String FILEPREFIX = "CEPDB2_"; // Instrument Reading, Second File Mask, version 2
    // filename prefix for converted csv files
    public static final String CSVPREFIX_INR = "INR_";  // instrument readings, First File Mask, no version
    //        public const string CSVPREFIX_EQI = "EQI2_";  // equipment identification, version 2
    //        public const string CSVPREFIX_EQC = "EQC2_";  // equipment characteristics, version 2
    // filename for the xsd schema definition files

    public HandHeld_SQLiteOpenHelper(Context context, StdetDataTables tbls) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        tables = tbls;
        getReadableDatabase(); // <-- add this, which triggers onCreate/onUpdate
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        int n = tables.getDataTables().size();
        String create_table;
        for (int i = 0; i < n; i++) {
            try {
                create_table = tables.getDataTables().get(i).CreateTable();
                String tableName = tables.getDataTables().get(i).getName();

                if (!tableName.equalsIgnoreCase("NA")) {
                    db.execSQL(create_table);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("onCreate DB " + ex.toString());

            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            int n = tables.getDataTables().size();
            String drop_table;
            for (int i = 0; i < n; i++) {
                try {
                    drop_table = "DROP TABLE IF EXISTS " + tables.getDataTables().get(i).getName();
                    db.execSQL(drop_table);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("onUpgrade DB " + ex.toString());

                }
            }
            onCreate(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void getInsertFromTables(SQLiteDatabase db) {
        int n = tables.getDataTables().size();
        for (int i = 0; i < n; i++) {
            if (tables != null && tables.getDataTables().get(i).getName() != null) {
                String tbName = tables.getDataTables().get(i).getName();
                if (!tbName.equalsIgnoreCase("NA")) {
                    getInsertFromTable(db, tables.getDataTables().get(i));
                }
            }
        }
    }


    public void getInsertTable(SQLiteDatabase db, StdetDataTable table) {
        int n = table.GetNumberOfRecords();
        String insert = "", delete = "";
        try {
            String create = table.CreateTable();
            String tablename = table.getName();
            System.out.println("getInsertTable " + create);
            db.execSQL(create);
            delete = "Delete from " + tablename;
            System.out.println("getInsertTable " + delete);
            if (!tablename.equalsIgnoreCase(HandHeld_SQLiteOpenHelper.INST_READINGS)) {
                System.out.println("getInsertTable " + delete);
                db.execSQL(delete);
            }

            for (int i = 0; i < n; i++) {
                try {
                    insert = table.getInsertIntoDB(i);
                    db.execSQL(insert);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("insert " + insert + ex.toString());
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("getInsertFromTable " + ex.toString());

        }
    }

    public void getInsertFromTable(SQLiteDatabase db, StdetDataTable table) {
        int n = table.GetNumberOfRecords();
        String create = table.CreateTable();
        String tablename = table.getName();
        System.out.println("getInsertFromTable " + create);
        db.execSQL(create);
        String insert = "", delete = "";
        try {

            delete = "Delete from " + table.getName();
            if (!tablename.equalsIgnoreCase(HandHeld_SQLiteOpenHelper.INST_READINGS)) {
                System.out.println("getInsertFromTable " + delete);
                db.execSQL(delete);
            }

            for (int i = 0; i < n; i++) {
                try {
                    insert = table.getInsertIntoDB(i);
                    db.execSQL(insert);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("insert " + insert + ex.toString());
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("getInsertFromTable " + ex.toString());

        }
    }
    ///////// Get The Data

    public Cursor GetLocations(SQLiteDatabase db) {
        return db.rawQuery("Select  rowid _id, strD_loc_id, strD_LocDesc from tbl_DCP_Loc_def", null);
    }

    public Cursor GetElev(SQLiteDatabase db, String loc) {
        String qry = "Select rowid _id, sys_loc_code, elev_code, elev_value from ut_elevations where wl_mwas_point = 1 ";
        qry += "and sys_loc_code='" + loc + "'";
        return db.rawQuery(qry, null);
    }

    public Cursor GetUnit(SQLiteDatabase db, String loc) {
        String qry = "select  rowid as _id, strD_ParUnits from tbl_DCP_Loc_Char where ";
        qry += " strD_Loc_ID='" + loc + "'";
        return db.rawQuery(qry, null);
    }

    public Cursor GetIR(SQLiteDatabase db) {
        String qry = "select  rowid as _id, " + Stdet_Inst_Readings.facility_id + ", " +
                Stdet_Inst_Readings.strD_Col_ID + ", " +
                Stdet_Inst_Readings.datIR_Date + ", " +
                Stdet_Inst_Readings.datIR_Time + ", " +
                Stdet_Inst_Readings.strD_Loc_ID + ", " +
                Stdet_Inst_Readings.strFO_StatusID + ", " +
                Stdet_Inst_Readings.strEqO_StatusID + ", " +
                Stdet_Inst_Readings.strEqID + ", " +
                Stdet_Inst_Readings.dblIR_Value + ", " +
                Stdet_Inst_Readings.strIR_Units + ", " +
                Stdet_Inst_Readings.fSuspect + ", " +
                Stdet_Inst_Readings.strComment + ", " +
                Stdet_Inst_Readings.strDataModComment + ", " +
                Stdet_Inst_Readings.uf_strWL_D_Loc_ID + ", " +
                Stdet_Inst_Readings.wl_meas_point + ", " +
                Stdet_Inst_Readings.elev_code + ", " +
                Stdet_Inst_Readings.elev_code_desc + " from " +
                HandHeld_SQLiteOpenHelper.INST_READINGS +
                " where uploaded is null or uploaded =0";
        return db.rawQuery(qry, null);
    }

    public Integer GetMaxIRId(SQLiteDatabase db) {
        Integer rv = 0;
        String qry = "select  max (lngId) from tbl_Inst_Readings";
        Cursor c = db.rawQuery(qry, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            rv = c.getInt(0);
        }
        return rv;
    }

    public ArrayList<String[]> GetLocationCharacteristics(SQLiteDatabase db, String loc) {

        return null;
    }


    public Cursor GetColIdentity(SQLiteDatabase db) {
        //Cursor c1 = db.rawQuery("Select strD_Col_ID from tbl_Data_Col_Ident", null);
        Cursor c = db.rawQuery("Select rowid _id, strD_Col_ID from tbl_Data_Col_Ident", null);
        return c;
    }

    public ArrayList<String[]> CursorToArrayList(Cursor cursor) {
        ArrayList<String[]> arrayList = new ArrayList<String[]>();
        int nCol = cursor.getColumnCount();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // The Cursor is now set to the right position
            String[] strs = new String[nCol];
            for (Integer i = 0; i < nCol; i++) {
                strs[i] = (String) cursor.getString(i);
            }
            arrayList.add(strs);
        }
        return arrayList;
    }

    public static void cursorToStringArray(Cursor c,
                                           ArrayList<String> arrayList,
                                           String columnName) {
        int columnIndex = c.getColumnIndex(columnName);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            arrayList.add(c.getString(columnIndex));
        }//from   ww w .j a v a2  s. c o  m
    }


    public String CreateFileToUpload(SQLiteDatabase db, File directoryApp) {
        File newCSV = null;

        Calendar c = Calendar.getInstance();
        Integer y = c.get(Calendar.YEAR);
        String sy = y.toString().substring(2);
        Integer m = c.get(Calendar.MONTH);
        String sm = m.toString();
        Integer d = c.get(Calendar.DAY_OF_MONTH);
        String sd = d.toString();
        Integer h = c.get(Calendar.HOUR_OF_DAY);
        String sh = h.toString();
        Integer mm = c.get(Calendar.MINUTE);
        String smm = mm.toString();
        Integer sec = c.get(Calendar.SECOND);
        String ssec = sec.toString();
        if (d < 10)
            sd = "0" + sd;
        if (m < 10)
            sm = "0" + sm;
        if (h < 10)
            sh = "0" + sh;
        if (mm < 10)
            smm = "0" + smm;
        if (sec < 10)
            ssec = "0" + ssec;
        String dattime_addon = sy + sm + sd + "_" + sh + smm + ssec;
        String filename = HandHeld_SQLiteOpenHelper.CSVPREFIX_INR +
                HandHeld_SQLiteOpenHelper.FILEPREFIX + "_" + dattime_addon + ".csv";
        newCSV = new File(directoryApp + "/" + filename);
        FileOutputStream fos;
        String fullfilename = newCSV.getAbsolutePath().toString();
        try {
            fos = new FileOutputStream(fullfilename);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fos);

            Cursor records = this.GetIR(db);
            Integer nCol = records.getColumnCount();

            String s_facility_id = "";
            String s_datIR_Date = "";
            String s_datIR_Time = "";
            String s_strD_Col_ID = "";
            String s_strD_Loc_ID = "";
            String s_strFO_StatusID = "";
            String s_strEqID = "";
            String s_dblIR_Value = "";
            String s_strIR_Units = "";
            String s_strComment = "";
            String s_strEqO_StatusID = "";
            String s_fSuspect = "";
            String s_strDataModComment = "";
            String s_uf_strWL_D_Loc_ID = "";
            String s_wl_meas_point = "";
            String s_elev_code = "";
            String s_elev_code_desc = "";

            Integer i_facility_id = records.getColumnIndex(Stdet_Inst_Readings.facility_id);
            if (i_facility_id < 0)
                i_facility_id = 0;
            Integer i_datIR_Date = records.getColumnIndex(Stdet_Inst_Readings.datIR_Date);
            if (i_datIR_Date < 0)
                i_datIR_Date = 0;
            Integer i_datIR_Time = records.getColumnIndex(Stdet_Inst_Readings.datIR_Time);
            if (i_datIR_Time < 0)
                i_datIR_Time = 0;
            Integer i_strD_Col_ID = records.getColumnIndex(Stdet_Inst_Readings.strD_Col_ID);
            if (i_strD_Col_ID < 0)
                i_strD_Col_ID = 0;
            Integer i_strD_Loc_ID = records.getColumnIndex(Stdet_Inst_Readings.strD_Loc_ID);
            if (i_strD_Loc_ID < 0)
                i_strD_Loc_ID = 0;
            Integer i_strFO_StatusID = records.getColumnIndex(Stdet_Inst_Readings.strFO_StatusID);
            if (i_strFO_StatusID < 0)
                i_strFO_StatusID = 0;
            Integer i_strEqID = records.getColumnIndex(Stdet_Inst_Readings.strEqID);
            if (i_strEqID < 0)
                i_strEqID = 0;
            Integer i_dblIR_Value = records.getColumnIndex(Stdet_Inst_Readings.dblIR_Value);
            if (i_dblIR_Value < 0)
                i_dblIR_Value = 0;
            Integer i_strIR_Units = records.getColumnIndex(Stdet_Inst_Readings.strIR_Units);
            if (i_strIR_Units < 0)
                i_strIR_Units = 0;
            Integer i_strComment = records.getColumnIndex(Stdet_Inst_Readings.strComment);
            if (i_strComment < 0)
                i_strComment = 0;
            Integer i_strEqO_StatusID = records.getColumnIndex(Stdet_Inst_Readings.strEqO_StatusID);
            if (i_strEqO_StatusID < 0)
                i_strEqO_StatusID = 0;
            Integer i_fSuspect = records.getColumnIndex(Stdet_Inst_Readings.fSuspect);
            if (i_fSuspect < 0)
                i_fSuspect = 0;
            Integer i_strDataModComment = records.getColumnIndex(Stdet_Inst_Readings.strDataModComment);
            if (i_strDataModComment < 0)
                i_strDataModComment = 0;
            Integer i_uf_strWL_D_Loc_ID = records.getColumnIndex(Stdet_Inst_Readings.uf_strWL_D_Loc_ID);
            if (i_uf_strWL_D_Loc_ID < 0)
                i_uf_strWL_D_Loc_ID = 0;
            Integer i_wl_meas_point = records.getColumnIndex(Stdet_Inst_Readings.wl_meas_point);
            if (i_wl_meas_point < 0)
                i_wl_meas_point = 0;
            Integer i_elev_code = records.getColumnIndex(Stdet_Inst_Readings.elev_code);
            if (i_elev_code < 0)
                i_elev_code = 0;
            Integer i_elev_code_desc = records.getColumnIndex(Stdet_Inst_Readings.elev_code_desc);
            if (i_elev_code_desc < 0)
                i_elev_code_desc = 0;


            String header = Stdet_Inst_Readings.facility_id + ", " +
                    Stdet_Inst_Readings.strEqID + ", " +
                    Stdet_Inst_Readings.strD_Col_ID + ", " +
                    Stdet_Inst_Readings.datIR_Date + ", " +
                    Stdet_Inst_Readings.datIR_Time + ", " +
                    Stdet_Inst_Readings.dblIR_Value + ", " +
                    Stdet_Inst_Readings.strIR_Units + ", " +
                    Stdet_Inst_Readings.strD_Loc_ID + ", " +
                    Stdet_Inst_Readings.strFO_StatusID + ", " +
                    Stdet_Inst_Readings.strEqO_StatusID + ", " +
                    Stdet_Inst_Readings.fSuspect + ", " +
                    Stdet_Inst_Readings.strComment + ", " +
                    Stdet_Inst_Readings.strDataModComment + ", " +
                    //Stdet_Inst_Readings.uf_strWL_D_Loc_ID + ", " +
                    //Stdet_Inst_Readings.wl_meas_point + ", " +
                    Stdet_Inst_Readings.elev_code ;//+ ", " +
                    //Stdet_Inst_Readings.elev_code_desc;

            myOutWriter.write(header);
            myOutWriter.write(10);//decimal value 10 represents newline in ASCII

            for (records.moveToFirst(); !records.isAfterLast(); records.moveToNext()) {
                // The Cursor is now set to the right position
                String row = "";


                s_facility_id = getStringQuotedValue(records, i_facility_id);
                s_datIR_Date = getStringQuotedValue(records,i_datIR_Date);
                s_datIR_Time = getStringQuotedValue(records,i_datIR_Time);
                s_dblIR_Value = getStringQuotedValue(records,i_dblIR_Value) ;
                s_strD_Loc_ID =getStringQuotedValue(records,i_strD_Loc_ID);
                s_strEqO_StatusID = getStringQuotedValue(records,i_strEqO_StatusID);
                s_strComment = getStringQuotedValue(records,i_strComment) ;
                s_strDataModComment = getStringQuotedValue(records,i_strDataModComment);
                s_uf_strWL_D_Loc_ID = getStringQuotedValue(records,i_uf_strWL_D_Loc_ID);
                s_wl_meas_point = getStringQuotedValue(records,i_wl_meas_point) ;
                s_elev_code = getStringQuotedValue(records,i_elev_code) ;
                s_elev_code_desc =getStringQuotedValue(records,i_elev_code_desc) ;
                s_strFO_StatusID =getStringQuotedValue(records,i_strFO_StatusID);
                s_strD_Col_ID = getStringQuotedValue(records,i_strD_Col_ID) ;
                s_strEqID =getStringQuotedValue(records,i_strEqID);
                s_strIR_Units = getStringQuotedValue(records,i_strIR_Units) ;
                s_fSuspect =getStringQuotedValueFromBooleanYesNo(records,i_fSuspect);


                row = s_facility_id + ", " +
                        s_strEqID + ", " +
                        s_strD_Col_ID + ", " +
                        s_datIR_Date + ", " +
                        s_datIR_Time + ", " +
                        s_strD_Loc_ID + "," +
                        s_dblIR_Value + "," +
                        s_strIR_Units + ", " +
                        s_strFO_StatusID + ", " +
                        s_strEqO_StatusID + ", " +
                        s_fSuspect + ", " +
                        s_strComment + ", " +
                        s_strDataModComment + ", " +
                        s_elev_code;

                System.out.println(row);

                myOutWriter.write(row);
                myOutWriter.write(10);//decimal value 10 represents newline in ASCII
            }
            myOutWriter.close();
            fos.flush();
            fos.close();

        } catch (IOException exception) {
            exception.printStackTrace();
            System.out.println(exception.toString());

            return null;
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println(exception.toString());
            return "";
        }


        return fullfilename;


    }

    private String getStringQuotedValue(Cursor records, Integer i) {
        String e = "\"";
        String s = "";
        if (records.getString(i) != null)
            s = (String) records.getString(i);
        return e + s.trim() + e;
    }
    private String getStringQuotedValueFromBooleanYesNo(Cursor records, Integer i) {
        String e = "\"";
        String s = "";
        Integer i1 =records.getInt(i);
        if (i1 != null && records.getInt(i) == 1)
            s = "Yes";
        else
            s =  "No";

        //e + ((Integer) records.getInt(i_fSuspect) == 1 ? "Yes" : "No") + e;
        return e + s + e;
    }
}

  


