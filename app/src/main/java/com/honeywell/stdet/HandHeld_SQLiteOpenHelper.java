package com.honeywell.stdet;

import android.app.AlarmManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;


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

    public static final String LOGININFO = "LoginInfo";

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
                create_table = tables.getDataTables().get(i).createTableSQL();
                String tableName = tables.getDataTables().get(i).getName();

                if (!tableName.equalsIgnoreCase("NA")) {
                    db.execSQL(create_table);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("onCreate DB " + ex);

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
                    System.out.println("onUpgrade DB " + ex);

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
        String insert = "", delete;
        try {
            String create = table.createTableSQL();
            String tablename = table.getName();
            System.out.println("getInsertTable " + create);
            db.execSQL(create);
            delete = "Delete from " + tablename;

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
                    System.out.println("insert " + insert + ex);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("getInsertFromTable " + ex);

        }
    }

    public void getInsertFromTable(SQLiteDatabase db, StdetDataTable table) {
        int n = table.GetNumberOfRecords();
        String create = table.createTableSQL();
        String tablename = table.getName();
        System.out.println("getInsertFromTable " + create);
        db.execSQL(create);
        String insert = "", delete;
        try {

            delete = "Delete from " + table.getName();
            if (!tablename.equalsIgnoreCase(HandHeld_SQLiteOpenHelper.INST_READINGS)
                && !tablename.equalsIgnoreCase(HandHeld_SQLiteOpenHelper.LOGININFO)){
                System.out.println("getInsertFromTable " + delete);
                db.execSQL(delete);
            }

            for (int i = 0; i < n; i++) {
                try {
                    insert = table.getInsertIntoDB(i);
                    db.execSQL(insert);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("insert " + insert + ex);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("getInsertFromTable " + ex);

        }
    }
    ///////// Get The Data

    public Cursor getLocations(SQLiteDatabase db) {
        String qry = "Select  rowid _id, strD_loc_id, strD_LocDesc, '1' as ord from tbl_DCP_Loc_def UNION ALL SELECT -1,'NA','NA','0' "
                + " order by ord, strD_loc_id";
        return db.rawQuery(qry, null);
    }

    public String[] getElevationCodeValue(SQLiteDatabase db, String loc, String elev_code) {
        String[] sElevCodeValue = new String[]{"NA","Max Depth","NA"};
        String qry = "Select elev_code, elev_value, elev_code_desc from ut_elevations e inner join tbl_DCP_Loc_Def l on l.sys_loc_code = e.sys_loc_code where e.elev_code='" + elev_code + "'";
        qry += " and strD_Loc_ID='" + loc + "'";

        Cursor c =  db.rawQuery(qry, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            if (!c.isNull(0))
                sElevCodeValue[0] = c.getString(0);
            if (!c.isNull(1))
                sElevCodeValue[1] = c.getString(1);
            if (!c.isNull(2))
                sElevCodeValue[2] = c.getString(2);
        }
        c.close();

        return sElevCodeValue;
    }
    public String[] getElevationCodeValue(SQLiteDatabase db, String loc) {
        String[] sElevCodeValue = new String[]{"NA","Max Depth","NA"};
        String qry = "Select elev_code, elev_value, elev_code_desc from ut_elevations e inner join tbl_DCP_Loc_Def l on l.sys_loc_code = e.sys_loc_code where e.wl_meas_point = 1  ";
        qry += "and strD_Loc_ID='" + loc + "'";

        Cursor c =  db.rawQuery(qry, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            if (!c.isNull(0))
                sElevCodeValue[0] = c.getString(0);
            if (!c.isNull(1))
                sElevCodeValue[1] = c.getString(1);
            if (!c.isNull(2))
                sElevCodeValue[2] = c.getString(2);
        }
        c.close();
        return sElevCodeValue;
    }


    public String[] getMinMax(SQLiteDatabase db, String loc) {
        String[] minmax = new String[]{"", ""};
        String qry = "select  t1.strD_ParValue as loc_min, t2.strD_ParValue as loc_Max from tbl_DCP_Loc_Char t1 " +
                " join  tbl_DCP_Loc_Char t2 on t1.strD_Loc_ID = t2.strD_Loc_ID " +
                " where  t1.strD_Loc_ID='" + loc + "' and t1.strD_ParName = 'Loc_Min' and t2.strD_ParName = 'Loc_Max'";
        Cursor cminmax = db.rawQuery(qry, null);
        if (cminmax.getCount() > 0) {
            cminmax.moveToFirst();
            if (!cminmax.isNull(0))
                minmax[0] = cminmax.getString(0);
            if (!cminmax.isNull(1))
                minmax[1] = cminmax.getString(1);
        }
        cminmax.close();
        return minmax;
    }

    public Cursor getElevationCodes(SQLiteDatabase db) {
        String qry;

            qry = "Select rowid as _id, elev_code,elev_code_desc, '1' as ord from ut_elevation_codes "+
                    " UNION ALL SELECT -1,'NA','NA', '0' order by ord,  elev_code";

        return db.rawQuery(qry, null);
    }

    public Cursor getUnits(SQLiteDatabase db, String loc) {
        String qry;
        if (Objects.equals(loc, "")) {
            qry = "Select rowid as _id, strUnitsID, '1' as ord from tbl_Unit_Def " +
                    " UNION ALL SELECT -1,'NA', '0' order by ord,  strUnitsID";
        } else {
            qry = "select  rowid as _id, strD_ParValue from tbl_DCP_Loc_Char where ";
            qry += " strD_Loc_ID='" + loc + "'";
            qry += " and strD_ParName ='Units' ";

        }
        Cursor c = db.rawQuery(qry, null);
        if (c.getCount() == 0) {
            qry = "select  -1,'NA'";
            c = db.rawQuery(qry, null);
        }
        return c;
    }

    public Cursor getFOS(SQLiteDatabase db) {
        String qry = "";

        qry = "Select rowid as _id, strFO_StatusID, '1' as ord from tbl_Fac_Oper_Def " +
                " UNION ALL SELECT -1,'NA', '0' order by ord, strFO_StatusID";

        return db.rawQuery(qry, null);
    }

    public Cursor getEOS(SQLiteDatabase db) {
        String qry = "";

        qry = "Select rowid as _id, strEqO_StatusID, '1' as ord from tbl_Equip_Oper_Def " +
                " UNION ALL SELECT -1,'NA', '0' order by ord, strEqO_StatusID";

        return db.rawQuery(qry, null);

    }
    public Cursor getLocMinMax(SQLiteDatabase db, String loc) {
        String qry = "select  rowid as _id, strD_ParUnits from tbl_DCP_Loc_Char where ";
        qry += " strD_Loc_ID='" + loc + "'";
        qry+= " UNION ALL SELECT -1,'NA'";
        return db.rawQuery(qry, null);
    }

    public Cursor getIRRecords(SQLiteDatabase db) {
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

    public int getRowsInLookupTables(SQLiteDatabase db) {
        StdetDataTables tabels = new StdetDataTables();
        int count = 0;
        for (int i = 0; i < tables.getDataTables().size(); i++) {
            //--tbl_Equip_Oper_Def
            if(tables.getDataTables().get(i).getTableType()==StdetDataTable.TABLE_TYPE.LOOKUP) {
                String create = tables.getDataTables().get(i).createTableSQL();
                String countrowssql = tables.getDataTables().get(i).getRowCountSQL();

                db.execSQL(create);
                Cursor c = db.rawQuery(countrowssql, null);
                c.moveToFirst();
                count += c.getInt(0);
                c.close();
            }

        }
        return count;

    }

    public String[] getLoginInfo(SQLiteDatabase db) {
        Stdet_LoginInfo t = new Stdet_LoginInfo();
        db.execSQL(t.createTableSQL());// in case it doesn't exixts yet

        String qry = "select  rowid as _id, " + Stdet_LoginInfo.UserName + ", " +
                Stdet_LoginInfo.Password + " from " +
                HandHeld_SQLiteOpenHelper.LOGININFO ;
        Cursor c= db.rawQuery(qry, null);
        String[] credentials =  new String[]{"",""};
        if (c.getCount() > 0) {
            c.moveToFirst();
            credentials[0] = c.getString(1);
            credentials[1] = c.getString(2);
        }
        return credentials;
    }

    public void updateLoginInformationInDB(SQLiteDatabase db, String name, String enPwd)
    {
        Stdet_LoginInfo login =  new Stdet_LoginInfo();
        login.AddToTable(name, enPwd);
        String create = login.createTableSQL();
        db.execSQL(create);
        getInsertTable(db,login);

    }

    public Integer getMaxIRID(SQLiteDatabase db) {
        int rv = 0;
        String qry = "select  max (lngId) from tbl_Inst_Readings";
        Cursor c = db.rawQuery(qry, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            rv = c.getInt(0);
        }
        c.close();
        return rv;
    }

    public ArrayList<String[]> GetLocationCharacteristics(SQLiteDatabase db, String loc) {

        return null;
    }


    public Cursor GetColIdentity(SQLiteDatabase db) {
        //Cursor c1 = db.rawQuery("Select strD_Col_ID from tbl_Data_Col_Ident", null);
        return db.rawQuery("Select rowid _id, strD_Col_ID, '1' as ord from tbl_Data_Col_Ident  " +
                " UNION ALL SELECT -1,'NA', '0' order by ord, strD_Col_ID", null);
    }

    public ArrayList<String[]> CursorToArrayList(Cursor cursor) {
        ArrayList<String[]> arrayList = new ArrayList<>();
        int nCol = cursor.getColumnCount();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // The Cursor is now set to the right position
            String[] strs = new String[nCol];
            for (int i = 0; i < nCol; i++) {
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




    public String CreateFileToUpload(SQLiteDatabase db, File directoryApp, Integer[] nRecords) throws ParseException {
        File newCSV = null;

        Calendar c = Calendar.getInstance();
        try {
            CallSoapWS ws = new CallSoapWS(directoryApp);
            String datetimeserver = ws.WS_GetServerDate(false);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            c.setTime(sdf.parse(datetimeserver));
        }catch(Exception ex){
            c = Calendar.getInstance();
        }


        int y = c.get(Calendar.YEAR);
        String sy = Integer.toString(y).substring(2);
        int m = c.get(Calendar.MONTH);
        String sm = Integer.toString(m);
        int d = c.get(Calendar.DAY_OF_MONTH);
        String sd = Integer.toString(d);
        int h = c.get(Calendar.HOUR_OF_DAY);
        String sh = Integer.toString(h);
        int mm = c.get(Calendar.MINUTE);
        String smm = Integer.toString(mm);
        int sec = c.get(Calendar.SECOND);
        String ssec = Integer.toString(sec);
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
        String fullfilename = newCSV.getAbsolutePath();
        try {
            fos = new FileOutputStream(fullfilename);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fos);

            Cursor records = this.getIRRecords(db);
            nRecords[0] = records.getCount();
            Integer nCol = records.getColumnCount();

            String s_facility_id;
            String s_datIR_Date ;
            String s_datIR_Time ;
            String s_strD_Col_ID ;
            String s_strD_Loc_ID ;
            String s_strFO_StatusID ;
            String s_strEqID;
            String s_dblIR_Value ;
            String s_strIR_Units;
            String s_strComment ;
            String s_strEqO_StatusID ;
            String s_fSuspect = "";
            String s_strDataModComment ;
            //String s_uf_strWL_D_Loc_ID ;
            //String s_wl_meas_point = "";
            String s_elev_code = "";
            //String s_elev_code_desc = "";

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
            int i_uf_strWL_D_Loc_ID = records.getColumnIndex(Stdet_Inst_Readings.uf_strWL_D_Loc_ID);
            if (i_uf_strWL_D_Loc_ID < 0)
                i_uf_strWL_D_Loc_ID = 0;
            int i_wl_meas_point = records.getColumnIndex(Stdet_Inst_Readings.wl_meas_point);
            if (i_wl_meas_point < 0)
                i_wl_meas_point = 0;
            Integer i_elev_code = records.getColumnIndex(Stdet_Inst_Readings.elev_code);
            if (i_elev_code < 0)
                i_elev_code = 0;
            int i_elev_code_desc = records.getColumnIndex(Stdet_Inst_Readings.elev_code_desc);
            if (i_elev_code_desc < 0)
                i_elev_code_desc = 0;


            String header = Stdet_Inst_Readings.CSVHeader();
            /*
                    Stdet_Inst_Readings.facility_id + ", " +
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
                    */


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
                //s_uf_strWL_D_Loc_ID = getStringQuotedValue(records,i_uf_strWL_D_Loc_ID);
                //s_wl_meas_point = getStringQuotedValue(records,i_wl_meas_point) ;
                s_elev_code = getStringQuotedValue(records,i_elev_code) ;
                //s_elev_code_desc =getStringQuotedValue(records,i_elev_code_desc) ;
                s_strFO_StatusID =getStringQuotedValue(records,i_strFO_StatusID);
                s_strD_Col_ID = getStringQuotedValue(records,i_strD_Col_ID) ;
                s_strEqID =getStringQuotedValue(records,i_strEqID);
                s_strIR_Units = getStringQuotedValue(records,i_strIR_Units) ;
                s_fSuspect =getStringQuotedValueFromBooleanYesNo(records,i_fSuspect);


                row = s_facility_id + "," +
                        s_strEqID + "," +
                        s_strD_Col_ID + "," +
                        s_datIR_Date + "," +
                        s_datIR_Time + "," +
                        s_strD_Loc_ID + "," +
                        s_dblIR_Value + "," +
                        s_strIR_Units + "," +
                        s_strFO_StatusID + "," +
                        s_strEqO_StatusID + "," +
                        s_fSuspect + "," +
                        s_strComment + "," +
                        s_strDataModComment + "," +
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
            System.out.println(exception);

            return null;
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println(exception);
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
        String s ;
        Integer i1 =records.getInt(i);
        if (records.getInt(i) == 1)
            s = "Yes";
        else
            s =  "No";

        //e + ((Integer) records.getInt(i_fSuspect) == 1 ? "Yes" : "No") + e;
        return e + s + e;
    }
}

  


