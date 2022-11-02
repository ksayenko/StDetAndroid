package com.honeywell.stdet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

    public static final String uploaded="uploaded";
    public static final String uploadedDatetime="uploadedDatetime";




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

    }

    public Integer AddToTable( ) {

        Integer maxID = -1;
        String facility_id1 = "1";
        String loc1 = "NA";
        String reading_value1 = "0.0";
        String datetime1 = "01/01/2000";
        String col1 = "NA";
        String eq_status1 = "NA";
        String fo_status1 = "NA";
        String unit1 = "NA";
        String el_code1 = "NA";
        String comment1 = "";
        String strDataModComment1 = "";

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
        reading.set(GetElementIndex(strD_Col_ID), col1);
        reading.set(GetElementIndex(strEqO_StatusID), eq_status1);
        reading.set(GetElementIndex(strFO_StatusID), fo_status1);
        reading.set(GetElementIndex(strIR_Units), unit1);
        reading.set(GetElementIndex(elev_code), el_code1);
        reading.set(GetElementIndex(strComment), comment1);
        reading.set(GetElementIndex(strDataModComment), strDataModComment1);

        this.AddRowToData(reading);

        return maxID++;
    }

    public Integer AddToTable( Integer maxID, String facility_id1, String loc1, String reading_value1,
                            String datetime1, String col1, String eq_status1,
                            String fo_status1, String unit1, String el_code1,
                            String comment1, String strDataModComment1) {
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
        reading.set(GetElementIndex(strD_Col_ID), col1);
        reading.set(GetElementIndex(strEqO_StatusID), eq_status1);
        reading.set(GetElementIndex(strFO_StatusID), fo_status1);
        reading.set(GetElementIndex(strIR_Units), unit1);
        reading.set(GetElementIndex(elev_code), el_code1);
        reading.set(GetElementIndex(strComment), comment1);
        reading.set(GetElementIndex(strDataModComment), strDataModComment1);

        this.AddRowToData(reading);

        return maxID++;
    }

    public static String CSVHeader() {
        String header = facility_id + "," + strEqID + "," + strD_Col_ID
                + "," + datIR_Date + "," + datIR_Time + "," + strD_Loc_ID
                + "," + dblIR_Value + "," + strIR_Units + "," + strFO_StatusID
                + "," + strEqO_StatusID + "," + fSuspect
                + "," + strComment + "," + strDataModComment + "," + elev_code;
        return header;
    }

    public static String SelectDataToUpload()        {
        String select =  "Select facility_id,strEqID,strD_Col_ID,datIR_Date,datIR_Time,strD_Loc_ID,"
                + "dblIR_Value,strIR_Units,strFO_StatusID,strEqO_StatusID,fSuspect,"
                + "strComment,strDataModComment,elev_code  "
               + " from "+HandHeld_SQLiteOpenHelper.INST_READINGS +
                " where uploaded is null";
        return select;
        }

    public static String UpdateUploadedData() {
        Date currentTime = Calendar.getInstance().getTime();
        String update = "UPDATE " + HandHeld_SQLiteOpenHelper.INST_READINGS
                + " SET uploaded = 1 , uploadedDatetime = '" + currentTime + "'" +
                " where uploaded is null";
        return update;
    }

   public static Stdet_Inst_Readings GetDefault()
   {
       Stdet_Inst_Readings default_object = new Stdet_Inst_Readings();
       default_object.AddToTable();
       return  default_object;

   }
}
