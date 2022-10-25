package com.honeywell.stdet;

public class Stdet_Elevations extends StdetDataTable {

    public static final String ut_elevation_facility_id="facility_id";
    public static final String ut_elevation_sys_loc_code="sys_loc_code";
    public static final String ut_elevation_elev_code="elev_code";
    public static final String ut_elevation_elev_value="elev_value";
    public static final String ut_elevation_elev_unit="elev_unit";
    public static final String ut_elevation_wl_meas_point="wl_meas_point";
    public static final String ut_elevation_elev_code_desc="elev_code_desc";
    public static final String ut_elevation_uf_strWL_D_LOC_ID="uf_strWL_D_LOC_ID";

    public Stdet_Elevations(){
        super(HandHeld_SQLiteOpenHelper.ELEVATIONS);

        this.AddColumnToStructure(ut_elevation_facility_id,"Integer",true,0);
        this.AddColumnToStructure(ut_elevation_sys_loc_code,"String",true,1);
        this.AddColumnToStructure(ut_elevation_elev_code,"String",true,2);
        this.AddColumnToStructure(ut_elevation_elev_value,"Double",false,3);
        this.AddColumnToStructure(ut_elevation_elev_unit,"String",false,4);
        this.AddColumnToStructure(ut_elevation_wl_meas_point,"Boolean",false,5);
        this.AddColumnToStructure(ut_elevation_elev_code_desc,"String",false,6);
        this.AddColumnToStructure(ut_elevation_uf_strWL_D_LOC_ID,"String",false,7);

    }

}
