package com.honeywell.stdet;

import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Stdet_Inst_Readings extends StdetDataTable {

    /*
           #region Defined public constants for column names of 'tbl_Inst_Readings'
        public static readonly string FAC_ID = "facility_id";
        public static readonly string ID = "lngID";          // auto-increment
        public static readonly string D_Col_ID = "strD_Col_ID";
        public static readonly string IR_Date = "datIR_Date";     // datetime
        public static readonly string IR_Time = "datIR_Time";     // datetime
        public static readonly string D_Loc_ID = "strD_Loc_ID";
        public static readonly string FO_StatusID = "strFO_StatusID";
        public static readonly string EqO_StatusID = "strEqO_StatusID";
        public static readonly string EqID = "strEqID";
        public static readonly string IR_Value = "dblIR_Value";    // numeric
        public static readonly string IR_Units = "strIR_Units";
        public static readonly string Suspect = "fSuspect";       // boolean
        public static readonly string Comment = "strComment";
        public static readonly string DataModComment = "strDataModComment";
        public static readonly string ElevationCode = "elev_code";
        public static readonly string ElevationCodeDesc = "elev_code_desc";
        public static readonly string WL_D_Loc_ID = "uf_strWL_D_Loc_ID";
        public static readonly string WL_MEAS_POINT = "wl_meas_point";
     */


    public static final String lngID="lngID";
    public static final String facility_id="facility_id";
    public static final String strD_Col_ID="strD_Col_ID";
    public static final String datIR_Date="datIR_Date";
    public static final String datIR_Time="datIR_Time";
    public static final String strD_Loc_ID="strD_Loc_ID";
    public static final String strFO_StatusID="strFO_StatusID";
    public static final String strEqO_StatusID="strEqO_StatusID";
    public static final String strEqID="strEqID";
    public static final String dblIR_Value="dblIR_Value";
    public static final String strIR_Units="strIR_Units";
    public static final String fSuspect="fSuspect";
    public static final String strComment="strComment";
    public static final String strDataModComment="strDataModComment";
    public static final String elev_code="elev_code";
    public static final String elev_code_desc="elev_code_desc";
    public static final String uf_strWL_D_Loc_ID="uf_strWL_D_Loc_ID";
    public static final String wl_meas_point="wl_meas_point";

    public static final String device_name="device_name";

    public static final String uploaded="uploaded";
    public static final String uploadedDatetime="uploadedDatetime";
    public static final String recordToUpload="recordToUpload";

    public static final String datIR_Date_NoSeconds="datIR_Date_NoSeconds";

    public static final String default_datetimeformat="default_datetimeformat";

    //datetime formats
    public static final String Datetime_pattern_default="YYYY-MM-dd HH:mm:SS";
    public static final String Datetime_pattern_with_sec="MM/dd/yyyy hh:mm:ss a";
    public static final String Datetime_pattern_no_sec="MM/dd/yyyy hh:mm a";
    public static final String Datetime_pattern_dateonly="MM/dd/yyyy";

    public Stdet_Inst_Readings(){
        super(HandHeld_SQLiteOpenHelper.INST_READINGS);
        this.setTableType(TABLE_TYPE.READING);

        this.AddColumnToStructure(lngID,"Integer",true);
        this.AddColumnToStructure(facility_id,"Integer",false);
        this.AddColumnToStructure(strD_Col_ID,"String",false);
        this.AddColumnToStructure(datIR_Date,"Datetime",false);
        this.AddColumnToStructure(datIR_Time,"Datetime",false);

        this.AddColumnToStructure(strD_Loc_ID,"String",false);
        this.AddColumnToStructure(strFO_StatusID,"String",false);
        this.AddColumnToStructure(strEqO_StatusID,"String",false);
        this.AddColumnToStructure(strEqID,"String",false);
        this.AddColumnToStructure(dblIR_Value,"Double",false);
        this.AddColumnToStructure(strIR_Units,"String",false);
        this.AddColumnToStructure(fSuspect,"Boolean",false);
        this.AddColumnToStructure(strComment,"String",false);
        this.AddColumnToStructure(strDataModComment,"String",false);
        this.AddColumnToStructure(elev_code,"String",false);
        this.AddColumnToStructure(elev_code_desc,"String",false);
        this.AddColumnToStructure(uf_strWL_D_Loc_ID,"String",false);
        this.AddColumnToStructure(wl_meas_point,"Boolean",false);

        this.AddColumnToStructure(uploaded,"Boolean",false);
        this.AddColumnToStructure(uploadedDatetime,"Datetime",false);
        this.AddColumnToStructure(recordToUpload,"Boolean",false);

        this.AddColumnToStructure(device_name,"String",false);
        this.AddColumnToStructure(datIR_Date_NoSeconds,"String",false);
        this.AddColumnToStructure(default_datetimeformat,"String",false);


    }

    @Override
    public String getInsertIntoDB(int element){
        String r = super.getInsertIntoDB(element);

        System.out.println(r);

        return r;
    }

    public void AddToTable( ) {

        Integer maxID = -1;
        String facility_id1 = "1";
        String loc1 = "NA";
        String reading_value1 = "0.0";
        String datetime1 = "01/01/2000";
        String datetime_nosec = "01/01/2000";
        String col1 = "NA";
        String eq_status1 = "NA";
        String fo_status1 = "NA";
        String unit1 = "NA";
        String el_code1 = "NA";
        String comment1 = "";
        String strDataModComment1 = "";
        String recordToUpload1 = "1";
        String sDeviceName = HandHeld_SQLiteOpenHelper.getDeviceName();

        ArrayList<String> reading = new ArrayList<>();
        int n = this.getColumnsNumber();
        for (int j = 0; j < n; j++)
            reading.add("");

        reading.set(GetElementIndex(lngID), String.valueOf(maxID));
        reading.set(GetElementIndex(facility_id), facility_id1);
        reading.set(GetElementIndex(strD_Loc_ID), loc1);
        reading.set(GetElementIndex(dblIR_Value), reading_value1);
        reading.set(GetElementIndex(datIR_Date), datetime1);
        reading.set(GetElementIndex(datIR_Time), datetime1);
        reading.set(GetElementIndex(datIR_Date_NoSeconds),
                Stdet_Inst_Readings.RemoveSecondsFromDateTime(datetime1));
        reading.set(GetElementIndex(default_datetimeformat),
                Stdet_Inst_Readings.ConvertDatetimeFormat(datetime1,
                        Stdet_Inst_Readings.Datetime_pattern_with_sec,
                        Stdet_Inst_Readings.Datetime_pattern_default ));

        reading.set(GetElementIndex(strD_Col_ID), col1);
        reading.set(GetElementIndex(strEqO_StatusID), eq_status1);
        reading.set(GetElementIndex(strFO_StatusID), fo_status1);
        reading.set(GetElementIndex(strIR_Units), unit1);
        reading.set(GetElementIndex(elev_code), el_code1);
        reading.set(GetElementIndex(strComment), comment1);
        reading.set(GetElementIndex(strDataModComment), strDataModComment1);
        reading.set(GetElementIndex(recordToUpload), recordToUpload1);
        reading.set(GetElementIndex(device_name), sDeviceName);


        this.AddRowToData(reading);
     }

    public Integer AddToTable(Reading theReading) {
        ArrayList<String> reading = new ArrayList<>();
        int n = this.getColumnsNumber();
        int nRecords = this.GetNumberOfRecords();
        String sDeviceName = HandHeld_SQLiteOpenHelper.getDeviceName();

        for (int j = 0; j < n; j++)
            reading.add("");

        reading.set(GetElementIndex(lngID), theReading.getLngID().toString());
        reading.set(GetElementIndex(facility_id), theReading.getFacility_id().toString());
        reading.set(GetElementIndex(strD_Loc_ID), theReading.getStrD_Loc_ID());
        reading.set(GetElementIndex(dblIR_Value), theReading.getDblIR_Value());
        reading.set(GetElementIndex(datIR_Date), theReading.getDatIR_Date());
        reading.set(GetElementIndex(datIR_Time), theReading.getDatIR_Time());
        reading.set(GetElementIndex(datIR_Date_NoSeconds),
                theReading.gedatIR_Date_NoSeconds());
        reading.set(GetElementIndex(default_datetimeformat),
                Stdet_Inst_Readings.ConvertDatetimeFormat(theReading.getDatIR_Date(),
                        Stdet_Inst_Readings.Datetime_pattern_with_sec,
                        Stdet_Inst_Readings.Datetime_pattern_default ));

        reading.set(GetElementIndex(strD_Col_ID), theReading.getStrD_Col_ID());
        reading.set(GetElementIndex(strEqO_StatusID), theReading.getStrEqO_StatusID());
        reading.set(GetElementIndex(strFO_StatusID), theReading.getStrFO_StatusID());
        reading.set(GetElementIndex(strIR_Units), theReading.getStrIR_Units());
        reading.set(GetElementIndex(elev_code), theReading.getElev_code());
        reading.set(GetElementIndex(strComment), theReading.getStrComment());
        reading.set(GetElementIndex(strDataModComment), theReading.getStrDataModComment());
        reading.set(GetElementIndex(recordToUpload), "1");
        reading.set(GetElementIndex(device_name), sDeviceName);

        this.AddRowToData(reading);

        return theReading.getLngID() +1;
    }

    public Integer AddToTable(String facility_id1, String loc1, String reading_value1,
                            String datetime1, String col1, String eq_status1,
                            String fo_status1, String unit1, String el_code1,
                            String comment1, String strDataModComment1) {
        ArrayList<String> reading = new ArrayList<>();
        int n = this.getColumnsNumber();
        int previous_maxid = -1;
        int nRecords = this.GetNumberOfRecords();
        if(nRecords  > 0) {
            previous_maxid = Integer.parseInt(this.getValueFromData(nRecords - 1, lngID));
        }
        Integer maxID =  (int) (new Date().getTime()/1000);
        String sDeviceName = HandHeld_SQLiteOpenHelper.getDeviceName();

        for (int j = 0; j < n; j++)
            reading.add("");

        reading.set(GetElementIndex(lngID), String.valueOf(maxID));
        reading.set(GetElementIndex(facility_id), facility_id1);
        reading.set(GetElementIndex(strD_Loc_ID), loc1);
        reading.set(GetElementIndex(dblIR_Value), reading_value1);
        reading.set(GetElementIndex(datIR_Date), datetime1);
        reading.set(GetElementIndex(datIR_Time), datetime1);
        reading.set(GetElementIndex(datIR_Date_NoSeconds),
                Stdet_Inst_Readings.RemoveSecondsFromDateTime(datetime1));
        reading.set(GetElementIndex(default_datetimeformat),
                Stdet_Inst_Readings.ConvertDatetimeFormat(datetime1,
                        Stdet_Inst_Readings.Datetime_pattern_with_sec,
                        Stdet_Inst_Readings.Datetime_pattern_default ));
        reading.set(GetElementIndex(strD_Col_ID), col1);
        reading.set(GetElementIndex(strEqO_StatusID), eq_status1);
        reading.set(GetElementIndex(strFO_StatusID), fo_status1);
        reading.set(GetElementIndex(strIR_Units), unit1);
        reading.set(GetElementIndex(elev_code), el_code1);
        reading.set(GetElementIndex(strComment), comment1);
        reading.set(GetElementIndex(strDataModComment), strDataModComment1);
        reading.set(GetElementIndex(recordToUpload), "1");
        reading.set(GetElementIndex(device_name), sDeviceName);

        this.AddRowToData(reading);

        return maxID++;
    }

    public static String RemoveSecondsFromDateTime(String sDateTime) {
       return ConvertDatetimeFormat(sDateTime,
               Stdet_Inst_Readings.Datetime_pattern_with_sec,
               Stdet_Inst_Readings.Datetime_pattern_no_sec);

    }

    public static String ConvertDatetimeFormat(String sDateTime, String formatFrom, String formatTo) {
        SimpleDateFormat sdf = null;
        Date dt = null;
        String timeStamp = "";
        try {
            sdf = new SimpleDateFormat(formatFrom, Locale.US);

            dt = sdf.parse(sDateTime);
            timeStamp = new SimpleDateFormat(formatTo).format(dt);
        } catch (Exception ex) {
            System.out.println(ex);
            return sDateTime;
        }

        return timeStamp;
    }

    public static String CSVHeader() {
        String header = facility_id + "," + strEqID + "," + strD_Col_ID
                + "," + datIR_Date + "," + datIR_Time + "," + strD_Loc_ID
                + "," + dblIR_Value + "," + strIR_Units + "," + strFO_StatusID
                + "," + strEqO_StatusID + "," + fSuspect
                + "," + strComment + "," + strDataModComment + "," + elev_code + ","+ device_name;
        return header;
    }


    public static String SelectDataToUpload()        {
        String select =  "Select facility_id,strEqID,strD_Col_ID,datIR_Date_NoSeconds," +
                " datIR_Date_NoSeconds,strD_Loc_ID,"
                + "dblIR_Value,strIR_Units,strFO_StatusID,strEqO_StatusID,fSuspect,"
                + "strComment,strDataModComment,elev_code, device_name  "
               + " from "+HandHeld_SQLiteOpenHelper.INST_READINGS +
                " where uploaded is null and recordToUpload=1";
        return select;
        }

    public static String UpdateUploadedData() {
        Date currentTime = Calendar.getInstance().getTime();
        String timeStamp = new SimpleDateFormat(Stdet_Inst_Readings.Datetime_pattern_default).format(Calendar.getInstance().getTime());

        String update = "UPDATE " + HandHeld_SQLiteOpenHelper.INST_READINGS
                + " SET uploaded = 1 , uploadedDatetime = '" + timeStamp + "'" +
                 " where ( " + Stdet_Inst_Readings.uploaded + " is null  or " + Stdet_Inst_Readings.uploaded + " = 0) " +
                "  and  (" + Stdet_Inst_Readings.recordToUpload + " = 1 or " + Stdet_Inst_Readings.recordToUpload + " is null) ";
        return update;
    }


    public static String PotentialNewDups(String Location, String TheDate) {
        String select = "select count(*) count_dup "
                + " from " + HandHeld_SQLiteOpenHelper.INST_READINGS
                + " where ( " + Stdet_Inst_Readings.uploaded + " is null  or " + Stdet_Inst_Readings.uploaded + " = 0) " +
                "  and  (" + Stdet_Inst_Readings.recordToUpload + " = 1 or " + Stdet_Inst_Readings.recordToUpload + " is null) " +
                " and  " + Stdet_Inst_Readings.strD_Loc_ID + " = '"+Location+"' and " + Stdet_Inst_Readings.datIR_Date + " like '"+TheDate+"%'";

        return select;
    }

    public static String FindPotentialDuplicates() {
        String select = "select " + Stdet_Inst_Readings.strD_Loc_ID + ","
                + Stdet_Inst_Readings.datIR_Date_NoSeconds + ","
                //+ Stdet_Inst_Readings.strD_Col_ID
                + " count(*) count_dup "
                + " from " + HandHeld_SQLiteOpenHelper.INST_READINGS
                + " where ( " + Stdet_Inst_Readings.uploaded + " is null  or " + Stdet_Inst_Readings.uploaded + " = 0) " +
                "  and  (" + Stdet_Inst_Readings.recordToUpload + " = 1 or " + Stdet_Inst_Readings.recordToUpload + " is null) "
                + " group by "
                //+ Stdet_Inst_Readings.strD_Col_ID + ","
                + Stdet_Inst_Readings.datIR_Date_NoSeconds + ","
                + Stdet_Inst_Readings.strD_Loc_ID
                + " having count(*) > 1 ";
        return select;
    }


    public static String FindCompleteDuplicates() {

        //dt_water_level table have smalldatetime for the measurement_date so we are passing a date
        // without seconds
        String select = "select facility_id, strd_col_id, datIR_Date_NoSeconds, strd_loc_id, strFO_StatusID, strEqO_StatusID, dblir_value, "
                + " strIR_Units,strComment, strDataModComment,elev_code, count(*) count_dup "
                + " from " + HandHeld_SQLiteOpenHelper.INST_READINGS
                + " where ( " + Stdet_Inst_Readings.uploaded + " is null  or " + Stdet_Inst_Readings.uploaded + " = 0) "
                + "  and  (" + Stdet_Inst_Readings.recordToUpload + " = 1 or " + Stdet_Inst_Readings.recordToUpload + " is null) "
                + " group by " + Stdet_Inst_Readings.facility_id + ","
                + Stdet_Inst_Readings.strD_Col_ID + ","
                + Stdet_Inst_Readings.datIR_Date_NoSeconds + ","
                + Stdet_Inst_Readings.strD_Loc_ID + "," + Stdet_Inst_Readings.strFO_StatusID + ","
                + Stdet_Inst_Readings.strEqO_StatusID + "," + Stdet_Inst_Readings.dblIR_Value + ","
                + Stdet_Inst_Readings.strIR_Units + "," + Stdet_Inst_Readings.strComment + ","
                + Stdet_Inst_Readings.strDataModComment + "," + Stdet_Inst_Readings.elev_code
                + " having count(*) > 1 ";


        return select;
    }

    public Boolean IsPotentialDuplicateInInnerTable(String Location, String TheDate){
        for (int i = 0; i < GetNumberOfRecords(); i++ ) {
            String loc = getValueFromData(i,Stdet_Inst_Readings.strD_Loc_ID);
            String dt = getValueFromData(i,Stdet_Inst_Readings.datIR_Date).substring(0,10);
            if (loc.equals(Location) && dt.equals(TheDate))
                return true;
        }
        return false;
    }








}
