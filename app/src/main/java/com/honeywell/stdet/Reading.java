package com.honeywell.stdet;
import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Reading implements Serializable {

    public String getLocMin() {
        return locMin;
    }

    public void setLocMin(String locMin) {
        this.locMin = locMin;
    }

    public String getLocMax() {
        return locMax;
    }

    public void setLocMax(String locMax) {
        this.locMax = locMax;
    }

    public enum VALIDATION {VALID,ERROR,WARNING}

    private Integer lngID = -1;
    private Integer facility_id = 1;
    private String strD_Col_ID = "NA";
    private String datIR_Date = "01/01/2000";
    private String datIR_Time = "01/01/2000";
    private String strD_Loc_ID = "NA";
    private String strFO_StatusID = "NA";
    private String strEqO_StatusID = "NA";

    private String dblIR_Value = null;
    private String strIR_Units = "NA";
    private String strComment = "";
    private String strDataModComment = "";
    private String elev_code = "NA";

    private String locMin = null;
    private String locMax = null;

    private double UNDEFINED = -99999.999;

    public Reading(){}

    public static Reading GetDefaultReading(){
        Reading r =  new Reading();
        return r;
    }
    public Reading(Integer lngID,
                   String strD_Loc_ID,
                   String strD_Col_ID,
                   String datIR_Date,
                   String strFO_StatusID, String strEqO_StatusID,
                   String dblIR_Value, String strIR_Units,
                   String elev_code, String elev_code_desc,
                   String strComment, String strDataModComment ) {
        this.lngID = lngID;
        this.facility_id = 1;
        this.strD_Col_ID = strD_Col_ID;
        this.datIR_Date = datIR_Date;
        this.datIR_Time = datIR_Date;
        this.strD_Loc_ID = strD_Loc_ID;
        this.strFO_StatusID = strFO_StatusID;
        this.strEqO_StatusID = strEqO_StatusID;
        this.dblIR_Value = dblIR_Value;
        this.strIR_Units = strIR_Units;
        this.strComment = strComment;
        this.strDataModComment = strDataModComment;
        this.elev_code = elev_code;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reading reading = (Reading) o;
        return lngID.equals(reading.lngID) &&
                facility_id.equals(reading.facility_id) &&
                strD_Col_ID.equals(reading.strD_Col_ID) &&
                datIR_Date.equals(reading.datIR_Date) &&
                datIR_Time.equals(reading.datIR_Time) &&
                strD_Loc_ID.equals(reading.strD_Loc_ID) &&
                strFO_StatusID.equals(reading.strFO_StatusID) &&
                strEqO_StatusID.equals(reading.strEqO_StatusID) &&
                dblIR_Value.equals(reading.dblIR_Value) &&
                strIR_Units.equals(reading.strIR_Units) &&
                strComment.equals(reading.strComment) &&
                strDataModComment.equals(reading.strDataModComment) &&
                elev_code.equals(reading.elev_code) ;
    }

    @Override
    public String toString() {
        return "Reading{" +
                "lngID=" + lngID +
                ", facility_id=" + facility_id +
                ", strD_Col_ID='" + strD_Col_ID + '\'' +
                ", datIR_Date=" + datIR_Date +
                ", datIR_Time=" + datIR_Time +
                ", strD_Loc_ID='" + strD_Loc_ID + '\'' +
                ", strFO_StatusID='" + strFO_StatusID + '\'' +
                ", strEqO_StatusID='" + strEqO_StatusID + '\'' +
                ", dblIR_Value=" + dblIR_Value +
                ", strIR_Units='" + strIR_Units + '\'' +
                ", strComment='" + strComment + '\'' +
                ", strDataModComment='" + strDataModComment + '\'' +
                ", elev_code='" + elev_code + '\'' +
                '}';
    }

    public VALIDATION isReadingWithinRange(Double reading, String[] error_message) {

        VALIDATION isValid = VALIDATION.VALID;
        String message = "";

        // returning the record is valid if the value in the database for loc_min or loc_max is wrong or empty string

        if (locMin =="" || locMax ==""){
            error_message[0] = "No valid records for loc_min or loc_max in the database";
            return VALIDATION.VALID;

        }
        double min = 0.0, max = 0.0, val = 0.0;
        try {
            min = Double.parseDouble(locMin);
        } catch (Exception ignored) {
            error_message[0] = "No valid records for loc_min or loc_max in the database";
            return VALIDATION.VALID;
        }
        try {
            max = Double.parseDouble(locMax);
        } catch (Exception ignored) {
            error_message[0] = "No valid records for loc_min or loc_max in the database";
            return VALIDATION.VALID;
        }

        //Cursor.Current = Cursors.WaitCursor;
        try {
            //NOTE: We can no longer range check flow totalizers now that we switched to location characteristics
            if (strD_Loc_ID.startsWith("FT"))    //if this is a water level location
            {
                if (reading < 0) {
                    message = "The Reading value is not a positive number!";
                    isValid = VALIDATION.ERROR;
                } //else
                //isValid = VALIDDATION.VALID;
            }

            if (min == UNDEFINED || max == UNDEFINED)   // no defined range
                isValid = VALIDATION.ERROR;
            else if (reading >= min && reading <= max)          // within bounds
                isValid = VALIDATION.VALID;
            else {
                message = "The Reading value falls outside the defined range: " + locMin + ".." + locMax;
                isValid = VALIDATION.WARNING;
                System.out.println(message);
            }
            //Cursor.Current = Cursors.Default;
        } catch (Exception ex) {

        }
        System.out.println("Within range message " + message);
        error_message[0] = message;
        return isValid;
    }

    public VALIDATION isRecordValid(String[] error_message, String[] whereToFocus) {
        String message = "";
        String whereToFocus1="";
        VALIDATION isValid = VALIDATION.VALID;
        double reading;

        try {
            reading = Double.parseDouble(dblIR_Value);
        } catch (Exception ex) {
            reading = 0.0;
        }

        if (isNA(strD_Col_ID)) {
            message += "Please select a Data Collector Id. ";
            whereToFocus1 = "COL_ID";
            isValid = VALIDATION.ERROR;
        } else if (isNA(strD_Loc_ID)) {
            message += "Please input a Location Id. ";
            whereToFocus1 = "LOC_ID";
            isValid = VALIDATION.ERROR;
        } /*else if (isNA(strFO_StatusID)) {
            //message += "Please select a Facility Oper Status. ";
            //spin_FAC_OP.requestFocus();
            //isValid =VALIDDATION.ERROR;
        } else if (isNA(strEqO_StatusID)) {
            //message += "Please select an Equipment Oper Status. ";
            //spin_FAC_OP.requestFocus();
            //isValid = VALIDDATION.ERROR;
        } */else if (strD_Loc_ID.startsWith("WL") && isNA(elev_code)) {
            message += "Water level values require an elevation code. Please select a Elevation Code designator manually. ";
            whereToFocus1 = "ELEV_CODE";
            isValid = VALIDATION.ERROR;
        }/* else if (reading == 0.0 && strEqO_StatusID.equalsIgnoreCase("NotOper")) {
            String im1 = "A Reading value of 0, together with a 'NotOper' Equip Oper Status indicates a non-valid reading.";
            message += im1;
            isValid = VALIDATION.WARNING;
            whereToFocus1 = "READING";

        }   else if (reading == 0.0 && !strEqO_StatusID.equalsIgnoreCase("NotOper")) {
            message += "A Reading value of 0 is detected!";
            whereToFocus1 = "READING";
            //to do not valid reeading confirm
            String[] innermessage = new String[]{""};
            isValid = VALIDATION.ERROR;
            message += innermessage[0];

        }*/
        //Now allow all locations top have a 0 as a possible value with a warning 12082022 KS
        else if (reading == 0.0){// && (strD_Loc_ID.startsWith("WL") || strD_Loc_ID.startsWith("FT"))) {
            String im1 = "A Reading value of 0, for location "+ strD_Loc_ID + " is Detected.";
            message += im1;
            isValid = VALIDATION.WARNING;
            whereToFocus1 = "READING";

        }else {
            String[] innermessage = new String[]{""};
            isValid = isReadingWithinRange(reading, innermessage);
            message += innermessage[0];
        }

        error_message[0] = message;
        whereToFocus[0]=whereToFocus1;
        return isValid;

    }
    private boolean isNA(String sValue) {
        boolean isna = sValue == null || sValue.equals("") || sValue.equalsIgnoreCase("NA");
        return isna;
    }
    @Override
    public int hashCode() {
        return Objects.hash(lngID, facility_id, strD_Col_ID,
                datIR_Date, datIR_Time, strD_Loc_ID,
                strFO_StatusID, strEqO_StatusID, dblIR_Value, strIR_Units,
                strComment, strDataModComment,
                elev_code);
    }

    public Integer getLngID() {
        return lngID;
    }

    public void setLngID(Integer lngID) {
        this.lngID = lngID;
    }

    public Integer getFacility_id() {
        return facility_id;
    }

    public void setFacility_id(Integer facility_id) {
        this.facility_id = facility_id;
    }

    public String getStrD_Col_ID() {
        return strD_Col_ID;
    }

    public void setStrD_Col_ID(String strD_Col_ID) {
        this.strD_Col_ID = strD_Col_ID;
    }

    public String getDatIR_Date() {
        return datIR_Date;
    }

    public void setDatIR_Date(String datIR_Date) {
        this.datIR_Date = datIR_Date;
    }

    public String getDatIR_Time() {
        return datIR_Time;
    }

    public void setDatIR_Time(String datIR_Time) {
        this.datIR_Time = datIR_Time;
    }

    public String getStrD_Loc_ID() {
        return strD_Loc_ID;
    }

    public void setStrD_Loc_ID(String strD_Loc_ID) {
        this.strD_Loc_ID = strD_Loc_ID;
    }

    public String getStrFO_StatusID() {
        return strFO_StatusID;
    }

    public void setStrFO_StatusID(String strFO_StatusID) {
        this.strFO_StatusID = strFO_StatusID;
    }

    public String getStrEqO_StatusID() {
        return strEqO_StatusID;
    }

    public void setStrEqO_StatusID(String strEqO_StatusID) {
        this.strEqO_StatusID = strEqO_StatusID;
    }

    public String getDblIR_Value() {
        return dblIR_Value;
    }

    public void setDblIR_Value(String dblIR_Value) {
        if (Objects.equals(dblIR_Value, ""))
            dblIR_Value = "0.0";
        this.dblIR_Value = dblIR_Value;
    }

    public String getStrIR_Units() {
        return strIR_Units;
    }

    public void setStrIR_Units(String strIR_Units) {
        this.strIR_Units = strIR_Units;
    }

    public String getStrComment() {
        return strComment;
    }

    public void setStrComment(String strComment) {
        this.strComment = strComment;
    }

    public String getStrDataModComment() {
        return strDataModComment;
    }

    public void setStrDataModComment(String strDataModComment) {
        this.strDataModComment = strDataModComment;
    }

    public String getElev_code() {
        return elev_code;
    }

    public void setElev_code(String elev_code) {
        this.elev_code = elev_code;
    }




}
