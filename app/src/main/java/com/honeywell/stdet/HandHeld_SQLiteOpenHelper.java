package com.honeywell.stdet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class HandHeld_SQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HandHeld.sqlite3";
    private static final int DATABASE_VERSION = 1;
    public static String DB_PATH;

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
        if (android.os.Build.VERSION.SDK_INT >= 4.2) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";

        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }

        tables = tbls;
        getReadableDatabase(); // <-- add this, which triggers onCreate/onUpdate
        AlterDB(this.getReadableDatabase());
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

    public void AlterDB(SQLiteDatabase db) {
        try {
            if (!checkColumnExists(db, HandHeld_SQLiteOpenHelper.INST_READINGS, Stdet_Inst_Readings.recordToUpload)) {
                Stdet_Inst_Readings ir = new Stdet_Inst_Readings();
                String sql = ir.alterIRTableSQLAddColumn(Stdet_Inst_Readings.recordToUpload);
                db.execSQL(sql);
            }
            if (!checkColumnExists(db, HandHeld_SQLiteOpenHelper.INST_READINGS, Stdet_Inst_Readings.device_name)) {
                Stdet_Inst_Readings ir = new Stdet_Inst_Readings();
                String sql = ir.alterIRTableSQLAddColumn(Stdet_Inst_Readings.device_name);
                db.execSQL(sql);
            }
            if (!checkColumnExists(db, HandHeld_SQLiteOpenHelper.INST_READINGS, Stdet_Inst_Readings.datIR_Date_NoSeconds)) {
                Stdet_Inst_Readings ir = new Stdet_Inst_Readings();
                String sql = ir.alterIRTableSQLAddColumn(Stdet_Inst_Readings.datIR_Date_NoSeconds);
                db.execSQL(sql);
            }
            if (!checkColumnExists(db, HandHeld_SQLiteOpenHelper.INST_READINGS, Stdet_Inst_Readings.default_datetimeformat)) {
                Stdet_Inst_Readings ir = new Stdet_Inst_Readings();
                String sql = ir.alterIRTableSQLAddColumn(Stdet_Inst_Readings.default_datetimeformat);
                db.execSQL(sql);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("AlterDB INST_READINGS " + ex);

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
        AlterDB(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void getInsertFromTables(SQLiteDatabase db) {
        int n = tables.getDataTables().size();
        System.out.println("In getInsertFromTables ");

        for (int i = 0; i < n; i++) {


            if (tables != null && tables.getDataTables().get(i).getName() != null) {
                String tbName = tables.getDataTables().get(i).getName();
                System.out.println("In getInsertFromTables " + String.valueOf(i) + " " + tbName);
                if (!tbName.equalsIgnoreCase("NA")) {
                    getInsertFromTable(db, tables.getDataTables().get(i));
                }
            }
        }

    }

    public Reading getReading(SQLiteDatabase db, String lngId) {
        Reading r = null;
        String qry = "Select  " + Stdet_Inst_Readings.facility_id + ", " +
                Stdet_Inst_Readings.strD_Loc_ID + ", " +
                Stdet_Inst_Readings.strD_Col_ID + ", " +
                Stdet_Inst_Readings.datIR_Date + ", " +
                Stdet_Inst_Readings.strComment + ", " +
                Stdet_Inst_Readings.strDataModComment + ", " +
                Stdet_Inst_Readings.dblIR_Value + ", " +
                Stdet_Inst_Readings.elev_code + ", " +
                Stdet_Inst_Readings.strEqO_StatusID + ", " +
                Stdet_Inst_Readings.strFO_StatusID + ", " +
                Stdet_Inst_Readings.strIR_Units + " FROM " +
                HandHeld_SQLiteOpenHelper.INST_READINGS + " Where " +
                Stdet_Inst_Readings.lngID + " = " + lngId;

        Cursor c = db.rawQuery(qry, null);
        if (c.getCount() > 0) {
            r = new Reading();
            c.moveToFirst();
            r.setLngID(Integer.parseInt(lngId));
            if (!c.isNull(0))
                r.setFacility_id(c.getInt(0));
            if (!c.isNull(1))
                r.setStrD_Loc_ID(c.getString(1));
            if (!c.isNull(2))
                r.setStrD_Col_ID(c.getString(2));
            if (!c.isNull(3)) {
                r.setDatIR_Date(c.getString(3));
            }
            if (!c.isNull(4))
                r.setStrComment(c.getString(4));
            if (!c.isNull(5))
                r.setStrDataModComment(c.getString(5));
            if (!c.isNull(6)) {
                double dreading = c.getDouble(6);
                //the max digits after the dot 55.9897384643555
                DecimalFormat df = new DecimalFormat("#.################");
                r.setDblIR_Value(df.format(dreading));
            }
            if (!c.isNull(7))
                r.setElev_code(c.getString(7));
            if (!c.isNull(8))
                r.setStrEqO_StatusID(c.getString(8));
            if (!c.isNull(9))
                r.setStrFO_StatusID(c.getString(9));
            if (!c.isNull(10))
                r.setStrIR_Units(c.getString(10));
        }
        c.close();
        //add! min max
        String[] min_max = getMinMax(db, r.getStrD_Loc_ID());
        if (min_max != null) {
            r.setLocMin(min_max[0]);
            r.setLocMax(min_max[1]);
        }
        return r;
    }

    public Boolean getInsertTable(SQLiteDatabase db, StdetDataTable table) {

        int n = table.GetNumberOfRecords();
        String insert = "", delete;
        Boolean isInserted = true;
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

             /*
            How can I speed up my database inserts?

            You can use the following methods to speed up inserts:
            If you are inserting many rows from the same client at the same time,
            use INSERT statements with multiple
            VALUES lists to insert several rows at a time. T
            his is considerably faster (many times faster in some cases)
            than using separate single-row INSERT statements.
             */

            if (n > 100 && table.getTableType() == StdetDataTable.TABLE_TYPE.LOOKUP) {
                int k = 0;
                int jump = 100;
                while (k < n) {
                    try {
                        int k_end = ((n - k) >= 100 ? k + jump - 1 : n - 1);
                        System.out.println(k + " - " + k_end);
                        insert = table.getInsertIntoDB(k, k_end);
                        SQLiteStatement statement = db.compileStatement(insert);
                        statement.execute();
                        statement.close();
                        System.out.println(k + " - " + k_end + " insert BIG " + insert);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println("insert BIG " + insert + ex);
                        isInserted = false;
                    }
                    k = k + jump;
                }

            } else {
                for (int i = 0; i < n; i++) {
                    try {
                        insert = table.getInsertIntoDB(i);
                        System.out.println("insert " + insert);
                        db.execSQL(insert);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println("insert " + insert + ex);
                        isInserted =  false;
                    }

                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR getInsertFromTable " + ex);
            return false;

        }

        return isInserted;
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
                    && !tablename.equalsIgnoreCase(HandHeld_SQLiteOpenHelper.LOGININFO)) {
                System.out.println("getInsertFromTable " + delete);
                db.execSQL(delete);
            }

 /*
            How can I speed up my database inserts?

            You can use the following methods to speed up inserts:
            If you are inserting many rows from the same client at the same time,
            use INSERT statements with multiple
            VALUES lists to insert several rows at a time. T
            his is considerably faster (many times faster in some cases)
            than using separate single-row INSERT statements.
             */

            if (n > 100 && table.getTableType() == StdetDataTable.TABLE_TYPE.LOOKUP) {
                int k = 0;
                int jump = 100;
                while (k < n) {
                    try {
                        System.out.println(k);
                        int k_end = ((n - k) >= 100 ? k + jump - 1 : n - 1);
                        insert = table.getInsertIntoDB(k, k_end);
                        SQLiteStatement statement = db.compileStatement(insert);
                        statement.execute();
                        statement.close();
                        System.out.println(k + " - " + k_end + " insert BIG " + insert);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println("insert BIG " + insert + ex);
                    }
                    k = k + jump;
                }
            } else {
                for (int i = 0; i < n; i++) {
                    try {
                        insert = table.getInsertIntoDB(i);
                        SQLiteStatement statement = db.compileStatement(insert);
                        statement.execute();
                        statement.close();
                        //db.execSQL(insert);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println("insert " + insert + ex);
                    }

                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("getInsertFromTable " + ex);

        }
    }
    ///////// Get The Data

    public Cursor getLocations(SQLiteDatabase db) {
        String qry = "Select  rowid _id, strD_loc_id, strD_LocDesc, '1' as ord, " + Stdet_DCP_Loc_Def.strD_TypeID +
                " from tbl_DCP_Loc_def "
                + " where " + Stdet_DCP_Loc_Def.ynCurrent + " <> 0 "
                + " and "
                + Stdet_DCP_Loc_Def.strD_TypeID + " in ('BIN', 'EWL', 'FPCT', 'FR', 'FT', 'IMHF',"
                + " 'IMTM', 'MWL', 'NAOH', 'PD', 'PH', 'PR', 'TL',  'WFR', 'WFT', 'WL', 'WPR') "
                + " UNION ALL SELECT -1,'NA','NA','0','W' "
                + " order by ord, strD_Loc_ID ";
        Cursor c = db.rawQuery(qry, null);
        return c;
    }

    public String[] getElevationCodeValue(SQLiteDatabase db, String loc, String elev_code) {
        String[] sElevCodeValue = new String[]{"NA", "Max Depth", "NA"};
        String qry = "Select elev_code, elev_value, elev_code_desc from ut_elevations e inner join tbl_DCP_Loc_Def l on l.sys_loc_code = e.sys_loc_code where e.elev_code='" + elev_code + "'";
        qry += " and strD_Loc_ID='" + loc + "'";

        Cursor c = db.rawQuery(qry, null);
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
        String[] sElevCodeValue = new String[]{"NA", "Max Depth", "NA"};
        String qry = "Select elev_code, elev_value, elev_code_desc " +
                "from ut_elevations e inner join tbl_DCP_Loc_Def l " +
                " on l.sys_loc_code = e.sys_loc_code where e.wl_meas_point = 1  ";
        qry += "and strD_Loc_ID='" + loc + "'";
        qry += " and l.strD_Loc_ID like '%Wl%'";
        System.out.println(qry);

        Cursor c = db.rawQuery(qry, null);
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

    public int getUpdateReading(SQLiteDatabase db, Reading r) {
        String temp;
        ContentValues values = new ContentValues();
        values.put(Stdet_Inst_Readings.dblIR_Value, r.getDblIR_Value());
        values.put(Stdet_Inst_Readings.elev_code, r.getElev_code());

        temp = r.getStrComment();
        if (Objects.equals(temp, ""))
            values.putNull(Stdet_Inst_Readings.strComment);
        else
            values.put(Stdet_Inst_Readings.strComment, temp);

        values.put(Stdet_Inst_Readings.strFO_StatusID, r.getStrFO_StatusID());
        values.put(Stdet_Inst_Readings.strEqO_StatusID, r.getStrEqO_StatusID());
        values.put(Stdet_Inst_Readings.strIR_Units, r.getStrIR_Units());

        int rowsUpdated = db.update(HandHeld_SQLiteOpenHelper.INST_READINGS, values, Stdet_Inst_Readings.lngID + "=" + r.getLngID(), null);

        return rowsUpdated;

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

        qry = "Select rowid as _id, elev_code,elev_code_desc, '1' as ord from ut_elevation_codes " +
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
                //  " UNION ALL SELECT -1,'NA', '0' " +
                " order by ord, strFO_StatusID";

        return db.rawQuery(qry, null);
    }

    public Cursor getEOS(SQLiteDatabase db) {
        String qry = "";

        qry = "Select rowid as _id, strEqO_StatusID, '1' as ord from tbl_Equip_Oper_Def " +
                //" UNION ALL SELECT -1,'NA', '0' " +
                " order by ord, strEqO_StatusID";

        return db.rawQuery(qry, null);

    }

    public Cursor getLocMinMax(SQLiteDatabase db, String loc) {
        String qry = "select  rowid as _id, strD_ParUnits from tbl_DCP_Loc_Char where ";
        qry += " strD_Loc_ID='" + loc + "'";
        qry += " UNION ALL SELECT -1,'NA'";
        return db.rawQuery(qry, null);
    }

    public Cursor getIRRecordsShortList(SQLiteDatabase db) {
        return getIRRecordsShortList(db, "order by " +
                Stdet_Inst_Readings.default_datetimeformat + " desc , "
                + Stdet_Inst_Readings.datIR_Date + " DESC");


    }

    public Cursor getIRRecordsShortList(SQLiteDatabase db, String orderby) {
        String qry = "select  rowid as _id, " +
                Stdet_Inst_Readings.strD_Loc_ID + ", " +
                Stdet_Inst_Readings.datIR_Date + ", " +
                Stdet_Inst_Readings.dblIR_Value + ", " +
                Stdet_Inst_Readings.lngID + " from " +
                HandHeld_SQLiteOpenHelper.INST_READINGS +
                " where (uploaded is null or uploaded =0) and (" + Stdet_Inst_Readings.recordToUpload + " = 1 or " + Stdet_Inst_Readings.recordToUpload + " is null) " + orderby;
        System.out.println(qry);
        return db.rawQuery(qry, null);
    }

    public int deleteRecords(SQLiteDatabase db, String lngid) {
        // String qry = " delete from "+   HandHeld_SQLiteOpenHelper.INST_READINGS +
        //        " where lngid = " + lngid;
        return db.delete(HandHeld_SQLiteOpenHelper.INST_READINGS, Stdet_Inst_Readings.lngID + "=?", new String[]{lngid});
    }


    public Cursor getIRRecords(SQLiteDatabase db) {
        return getIRRecords(db, "order by " + Stdet_Inst_Readings.datIR_Date + " desc");
    }

    public Cursor getIRRecords(SQLiteDatabase db, String orderby) {
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
                Stdet_Inst_Readings.elev_code_desc + ", " +
                Stdet_Inst_Readings.device_name +
                " from " +
                HandHeld_SQLiteOpenHelper.INST_READINGS +
                " where (uploaded is null or uploaded =0) and (" + Stdet_Inst_Readings.recordToUpload + " = 1 or " + Stdet_Inst_Readings.recordToUpload + " is null) " + orderby;
        return db.rawQuery(qry, null);
    }

    public int getRowsInLookupTables(SQLiteDatabase db) {
        StdetDataTables tabels = new StdetDataTables();
        int count = 0;
        for (int i = 0; i < tables.getDataTables().size(); i++) {
            //--tbl_Equip_Oper_Def
            if (tables.getDataTables().get(i).getTableType() == StdetDataTable.TABLE_TYPE.LOOKUP) {
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
                HandHeld_SQLiteOpenHelper.LOGININFO;
        Cursor c = db.rawQuery(qry, null);
        String[] credentials = new String[]{"", ""};
        if (c.getCount() > 0) {
            c.moveToFirst();
            credentials[0] = c.getString(1);
            credentials[1] = c.getString(2);
        }
        return credentials;
    }

    public void updateLoginInformationInDB(SQLiteDatabase db, String name, String enPwd) {
        Stdet_LoginInfo login = new Stdet_LoginInfo();
        login.AddToTable(name, enPwd);
        String create = login.createTableSQL();
        db.execSQL(create);
        getInsertTable(db, login);

    }


    public Integer getMaxIRID(SQLiteDatabase db) {
        int rv = 0;
        String qry = "select  max (lngId) from tbl_Inst_Readings";
        try {
            Cursor c = db.rawQuery(qry, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                rv = c.getInt(0);
            }
            c.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
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
        }
    }

    public void UpdateIRIfNeeded(SQLiteDatabase db) {

        String device_name = HandHeld_SQLiteOpenHelper.getDeviceName();
        //update device
        String update1 = "update " + HandHeld_SQLiteOpenHelper.INST_READINGS
                + " set " + Stdet_Inst_Readings.device_name + " = "
                + getStringQuotedValue(device_name)
                + " where (uploaded is null  or uploaded = 0) and  (recordToUpload = 1 or recordToUpload is null)=1 "
                + " and " + Stdet_Inst_Readings.device_name + " is null";
        db.execSQL(update1);
    }


    public String GeneralQueryFirstValue(SQLiteDatabase db, String sql) {
        String rv = "";
        Cursor records = db.rawQuery(sql, null);
        int nRecords = records.getCount();
        if (nRecords > 0) {
            records.moveToFirst();
            rv = records.getString(0);
            return rv;
        }
        return "";
    }

    public String PotentialDuplicatesMesssage(SQLiteDatabase db) {
        StringBuilder returnMessage = new StringBuilder();
        String sql = Stdet_Inst_Readings.FindPotentialDuplicates();
        try {
            Cursor records = db.rawQuery(sql, null);
            int nRecords = records.getCount();
            Integer nCol = records.getColumnCount();

            if (nRecords == 0)
                return returnMessage.toString();

            Integer i_strD_Loc_ID = records.getColumnIndex(Stdet_Inst_Readings.strD_Loc_ID);
            if (i_strD_Loc_ID < 0)
                i_strD_Loc_ID = 0;
            Integer i_count_dup = records.getColumnIndex("count_dup");
            if (i_count_dup < 0)
                i_count_dup = 0;

            returnMessage.append("Potential Duplicates Found For Locations :");
            for (records.moveToFirst(); !records.isAfterLast(); records.moveToNext()) {
                // The Cursor is now set to the right position
                returnMessage.append(records.getString(i_strD_Loc_ID)).append("(").append(records.getString(i_count_dup)).append(" records), ");

            }
            returnMessage = new StringBuilder(replaceLast(returnMessage.toString(), ",", "."));

        } catch (Exception ex) {
            ex.printStackTrace();

        }
        return returnMessage.toString();
    }

    public String MarkNotToUploadCompleteDuplicates(SQLiteDatabase db) throws ParseException {
        // update NumberOfDuplicates - 1 not to upload if complete duplicates found
        String sql = Stdet_Inst_Readings.FindCompleteDuplicates();
        StringBuilder returnMessage = new StringBuilder();
        Cursor records = db.rawQuery(sql, null);
        Integer nRecordsUpdated = 0;

        int nRecords = records.getCount();
        Integer nCol = records.getColumnCount();

        if (nRecords == 0)
            return returnMessage.toString();

        returnMessage = new StringBuilder("Records for Location - Date were marked as not to upload : ");

        String s_facility_id;
        String s_datIR_Date;
        String s_datIR_Time;
        String s_strD_Col_ID;
        String s_strD_Loc_ID;
        String s_strFO_StatusID;
        String s_dblIR_Value;
        String s_strIR_Units;
        String s_strComment;
        String s_strEqO_StatusID;
        String s_strDataModComment;
        String s_elev_code = "";


        Integer i_facility_id = records.getColumnIndex(Stdet_Inst_Readings.facility_id);
        if (i_facility_id < 0)
            i_facility_id = 0;
        Integer i_datIR_Date = records.getColumnIndex(Stdet_Inst_Readings.datIR_Date_NoSeconds);
        if (i_datIR_Date < 0)
            i_datIR_Date = 0;

        Integer i_strD_Col_ID = records.getColumnIndex(Stdet_Inst_Readings.strD_Col_ID);
        if (i_strD_Col_ID < 0)
            i_strD_Col_ID = 0;
        Integer i_strD_Loc_ID = records.getColumnIndex(Stdet_Inst_Readings.strD_Loc_ID);
        if (i_strD_Loc_ID < 0)
            i_strD_Loc_ID = 0;
        Integer i_strFO_StatusID = records.getColumnIndex(Stdet_Inst_Readings.strFO_StatusID);
        if (i_strFO_StatusID < 0)
            i_strFO_StatusID = 0;

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

        Integer i_strDataModComment = records.getColumnIndex(Stdet_Inst_Readings.strDataModComment);
        if (i_strDataModComment < 0)
            i_strDataModComment = 0;

        Integer i_elev_code = records.getColumnIndex(Stdet_Inst_Readings.elev_code);
        if (i_elev_code < 0)
            i_elev_code = 0;

        Integer i_count_dup = records.getColumnIndex("count_dup");
        if (i_count_dup < 0)
            i_count_dup = 0;


        for (records.moveToFirst(); !records.isAfterLast(); records.moveToNext()) {
            // The Cursor is now set to the right position


            s_facility_id = getStringQuotedValue(records, i_facility_id);
            s_datIR_Date = getStringQuotedValue(records, i_datIR_Date);
            s_dblIR_Value = getStringQuotedValueFromDouble(records, i_dblIR_Value, null);
            s_strD_Loc_ID = getStringQuotedValue(records, i_strD_Loc_ID);
            s_strEqO_StatusID = getStringQuotedValue(records, i_strEqO_StatusID);
            s_strComment = getStringQuotedValueWithNULL(records, i_strComment);
            s_strDataModComment = getStringQuotedValueWithNULL(records, i_strDataModComment);
            s_elev_code = getStringQuotedValueWithNULL(records, i_elev_code);

            s_strFO_StatusID = getStringQuotedValueWithNULL(records, i_strFO_StatusID);
            s_strD_Col_ID = getStringQuotedValue(records, i_strD_Col_ID);
            s_strIR_Units = getStringQuotedValueWithNULL(records, i_strIR_Units);

            Integer iDup = records.getInt(i_count_dup);

            String sqlUpdate = " UPDATE " + HandHeld_SQLiteOpenHelper.INST_READINGS
                    + " SET " + Stdet_Inst_Readings.recordToUpload + " = 0"
                    + " WHERE " + Stdet_Inst_Readings.lngID + " IN ("
                    + " SELECT lngid from " + HandHeld_SQLiteOpenHelper.INST_READINGS + " where "
                    + Stdet_Inst_Readings.strD_Col_ID + " = " + s_strD_Col_ID + " and "
                    + Stdet_Inst_Readings.datIR_Date_NoSeconds + " = " + s_datIR_Date + " and "
                    + Stdet_Inst_Readings.facility_id + " = " + s_facility_id + " and "
                    + Stdet_Inst_Readings.strD_Loc_ID + " = " + s_strD_Loc_ID + " and ";

            if (s_strFO_StatusID.equalsIgnoreCase("NULL"))
                sqlUpdate += " " + Stdet_Inst_Readings.strFO_StatusID + " IS NULL and";
            else
                sqlUpdate += " " + Stdet_Inst_Readings.strFO_StatusID + " = " + s_strFO_StatusID + " and ";

            if (s_strEqO_StatusID.equalsIgnoreCase("NULL"))
                sqlUpdate += " " + Stdet_Inst_Readings.strEqO_StatusID + " IS NULL and";
            else
                sqlUpdate += " " + Stdet_Inst_Readings.strEqO_StatusID + " = " + s_strEqO_StatusID + " and ";

            sqlUpdate += " " + Stdet_Inst_Readings.dblIR_Value + " = " + s_dblIR_Value + " and ";

            if (s_strIR_Units.equalsIgnoreCase("NULL"))
                sqlUpdate += " " + Stdet_Inst_Readings.strIR_Units + " IS NULL and";
            else
                sqlUpdate += " " + Stdet_Inst_Readings.strIR_Units + " = " + s_strIR_Units + " and ";

            if (s_strComment.equalsIgnoreCase("NULL"))
                sqlUpdate += " " + Stdet_Inst_Readings.strComment + " IS NULL and";
            else
                sqlUpdate += " " + Stdet_Inst_Readings.strComment + " = " + s_strComment + " and ";

            if (s_strDataModComment.equalsIgnoreCase("NULL"))
                sqlUpdate += " " + Stdet_Inst_Readings.strDataModComment + " IS NULL and";
            else
                sqlUpdate += " " + Stdet_Inst_Readings.strDataModComment + " = " + s_strDataModComment + " and ";


            if (s_elev_code.equalsIgnoreCase("NULL"))
                sqlUpdate += " " + Stdet_Inst_Readings.elev_code + " IS NULL and";
            else

                sqlUpdate += " " + Stdet_Inst_Readings.elev_code + " = " + s_elev_code;

            sqlUpdate += "  order by  " + Stdet_Inst_Readings.lngID + " desc LIMIT " + String.valueOf(iDup - 1) + " );";
            nRecordsUpdated = nRecordsUpdated + (iDup - 1);
            returnMessage.append(records.getString(i_strD_Loc_ID)).append(" - ").append(records.getString(i_datIR_Date)).append(", ");

            try {
                db.execSQL(sqlUpdate);

            } catch (Exception exception) {
                exception.printStackTrace();
                System.out.println(exception);
            }
        }
        returnMessage = new StringBuilder(replaceLast(returnMessage.toString(), ",", "."));
        returnMessage.append(" Total ").append(nRecordsUpdated.toString()).append(" records marked.");
        return returnMessage.toString();
    }

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }


    public String CreateFileToUpload(SQLiteDatabase db, File directoryApp, Integer[] nRecords, Context context) throws ParseException {
        File newCSV = null;

        Calendar c = Calendar.getInstance();
        try {
            CallSoapWS ws = new CallSoapWS(directoryApp);
            String datetimeserver = ws.WS_GetServerDate(false);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            c.setTime(sdf.parse(datetimeserver));
        } catch (Exception ex) {
            c = Calendar.getInstance();
        }

// in java in Calendar Months are indexed from 0 not 1 so 10 is November and 11 will be December.
        int y = c.get(Calendar.YEAR);
        String sy = Integer.toString(y).substring(2);
        java.text.SimpleDateFormat sfMonth = new java.text.SimpleDateFormat("MM");
        int m = c.get(Calendar.MONTH);
        String sm = sfMonth.format(c.getTime());
        //String sm = Integer.toString(m);
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
        if (Integer.parseInt(sm) < 10 && !sm.startsWith("0"))
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

            //update IR table with device name and or no seconds if needed
            String message1 = MarkNotToUploadCompleteDuplicates(db);
            if (message1 != "")
                Toast.makeText(context, message1, Toast.LENGTH_SHORT).show();
            //update IR table with device name and or no seconds if needed
            UpdateIRIfNeeded(db);

            Cursor records = this.getIRRecords(db);
            nRecords[0] = records.getCount();
            Integer nCol = records.getColumnCount();
            message1 = records.toString() + "  records getting ready to upload";
            Toast.makeText(context, message1, Toast.LENGTH_SHORT).show();

            String s_facility_id;
            String s_datIR_Date;
            String s_datIR_Time;
            String s_strD_Col_ID;
            String s_strD_Loc_ID;
            String s_strFO_StatusID;
            String s_strEqID;
            String s_dblIR_Value;
            String s_strIR_Units;
            String s_strComment;
            String s_strEqO_StatusID;
            String s_fSuspect = "";
            String s_strDataModComment;
            //String s_uf_strWL_D_Loc_ID ;
            //String s_wl_meas_point = "";
            String s_elev_code = "";
            String s_device_name = "";

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
            int i_device_name = records.getColumnIndex(Stdet_Inst_Readings.device_name);
            if (i_device_name < 0)
                i_device_name = 0;

            String header = Stdet_Inst_Readings.CSVHeader();


            myOutWriter.write(header);
            myOutWriter.write(10);//decimal value 10 represents newline in ASCII

            for (records.moveToFirst(); !records.isAfterLast(); records.moveToNext()) {
                // The Cursor is now set to the right position
                String row = "";


                s_facility_id = getStringQuotedValue(records, i_facility_id);
                s_datIR_Date = getStringQuotedValueAndRemoveSecondsFromDatetime(records, i_datIR_Date);
                s_datIR_Time = getStringQuotedValueAndRemoveSecondsFromDatetime(records, i_datIR_Time);
                s_dblIR_Value = getStringQuotedValueFromDouble(records, i_dblIR_Value, null);
                s_strD_Loc_ID = getStringQuotedValue(records, i_strD_Loc_ID);
                s_strEqO_StatusID = getStringQuotedValue(records, i_strEqO_StatusID);
                s_strComment = getStringQuotedValue(records, i_strComment);
                s_strDataModComment = getStringQuotedValue(records, i_strDataModComment);
                //s_uf_strWL_D_Loc_ID = getStringQuotedValue(records,i_uf_strWL_D_Loc_ID);
                //s_wl_meas_point = getStringQuotedValue(records,i_wl_meas_point) ;
                s_elev_code = getStringQuotedValue(records, i_elev_code);
                //s_elev_code_desc =getStringQuotedValue(records,i_elev_code_desc) ;
                s_strFO_StatusID = getStringQuotedValue(records, i_strFO_StatusID);
                s_strD_Col_ID = getStringQuotedValue(records, i_strD_Col_ID);
                s_strEqID = getStringQuotedValue(records, i_strEqID);
                s_strIR_Units = getStringQuotedValue(records, i_strIR_Units);
                s_fSuspect = getStringQuotedValueFromBooleanYesNo(records, i_fSuspect);
                s_device_name = getStringQuotedValue(records, i_device_name);

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
                        s_elev_code + "," +
                        s_device_name;

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

    private String getStringQuotedValueFromDouble(Cursor records, Integer i, DecimalFormat df) {
        String e = "\"";
        String s = "";
        if (df == null)
            df = new DecimalFormat("#.################");
        if (records.getString(i) != null && df == null)
            s = (String) records.getString(i);
        if (records.getString(i) != null && df != null) {
            double dValue = records.getDouble(i);
            s = df.format(dValue);
        }
        return e + s.trim() + e;
    }

    private String getStringQuotedValueAndRemoveSecondsFromDatetime(Cursor records, Integer i) {
        String e = "\"";
        String s = "";
        Date dt = null;
        String timeStamp = "";
        SimpleDateFormat sdf = null;
        try {
            if (records.getString(i) != null) {
                s = (String) records.getString(i);
                sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US);
                dt = sdf.parse(s);
                timeStamp = new SimpleDateFormat("MM/dd/yyyy hh:mm a").format(dt);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return e + timeStamp.trim() + e;
    }

    private String getStringQuotedValue(Cursor records, Integer i) {
        String e = "\"";
        String s = "";
        if (records.getString(i) != null)
            s = (String) records.getString(i);
        return e + s.trim() + e;
    }

    private String getStringQuotedValue(String s) {
        String e = "\"";
        return e + s.trim() + e;
    }

    private String getStringQuotedValueWithNULL(Cursor records, Integer i) {
        String e = "\"";
        String s = "";
        if (records.getString(i) != null)
            s = (String) records.getString(i);
        else
            return "NULL";
        return e + s.trim() + e;
    }

    private String getStringQuotedValueFromBooleanYesNo(Cursor records, Integer i) {
        String e = "\"";
        String s;
        Integer i1 = records.getInt(i);
        if (records.getInt(i) == 1)
            s = "Yes";
        else
            s = "No";

        //e + ((Integer) records.getInt(i_fSuspect) == 1 ? "Yes" : "No") + e;
        return e + s + e;
    }

    public String sqlCheckColumnExists(String tablename, String columnname) {
        String sql = "SELECT COUNT(*) AS CNTREC FROM pragma_table_info('" + tablename + "') WHERE name='" + columnname + "'";
        return sql;
    }

    public boolean checkColumnExists(SQLiteDatabase db, String tablename, String columnname) {
        boolean rv = false;
        String sql = sqlCheckColumnExists(tablename, columnname);
        Cursor c = db.rawQuery(sql, null);
        try {
            c.moveToFirst();
            if (!c.isNull(0)) {
                int i = c.getInt(0);
                if (i > 0)
                    rv = true;
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return rv;
    }

    public String addNewColumnIfNotExists() {
        return "";
    }

    /** Returns the consumer friendly device name */

    static String getDeviceName1() {
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method getMethod = systemPropertiesClass.getMethod("get", String.class);
            Object object = new Object();
            Object obj = getMethod.invoke(object, "ro.product.device");
            return (obj == null ? "" : (String) obj);
        } catch (Exception e) {
            e.printStackTrace();
            return "NA";
        }
    }


    @SuppressLint("MissingPermission")
    public static String getDeviceName2() {
        try {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(mBluetoothAdapter==null) {
                System.out.println("----Device does not support bluetooth---");
                return "NA";
            }
            return mBluetoothAdapter.getName();
        } catch (Exception e) {
            e.printStackTrace();
            return "NA";
        }
    }
    public static String getDeviceName() {
        String device = "NA";
        device = getDeviceName2();

        if (Objects.equals(device, "NA"))
            device = getDeviceName1();

        try {
            if (Objects.equals(device, "NA"))
                device = Build.DEVICE;
        } catch (Exception ex) {
            System.out.println(ex);
        }

        String hardware = "hardware NA";
        try {
            hardware = Build.HARDWARE;
        } catch (Exception ex) {
            System.out.println(ex);
        }
        String id = "ID NA";
        try {
            id = Build.ID;
        } catch (Exception ex) {
            System.out.println(ex);
        }

        String manufacturer = "MANUFACTURER NA";
        try {
            manufacturer = Build.MANUFACTURER;
        } catch (Exception ex) {
            System.out.println(ex);
        }

        String model = "MODEL NA";
        try {
            model = Build.MODEL;
        } catch (Exception ex) {
            System.out.println(ex);
        }

        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        String rv = device + " - " + id + " " + hardware + "' "
                + capitalize(manufacturer) + " " + model;
        return rv.replace("'", "_");
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }
}

  


