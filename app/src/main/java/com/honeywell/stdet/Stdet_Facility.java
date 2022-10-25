package com.honeywell.stdet;

public class Stdet_Facility extends StdetDataTable {

    public static final String facility_id="facility_id";
    public static final String facility_code="facility_code";
    public static final String facility_name="facility_name";

    public Stdet_Facility(){
        super(HandHeld_SQLiteOpenHelper.FACILITY);

        this.AddColumnToStructure(facility_id,"Integer",true,0);
        this.AddColumnToStructure(facility_code,"String",false,1);
        this.AddColumnToStructure(facility_name,"String",false,2);

    }

}
