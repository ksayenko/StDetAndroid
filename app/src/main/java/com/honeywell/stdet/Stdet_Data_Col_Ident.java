package com.honeywell.stdet;

public class Stdet_Data_Col_Ident  extends StdetDataTable {

    public static final String lngID="lngID";
    public static final String strD_Col_ID="strD_Col_ID";
    public static final String strD_Col_LName="strD_Col_LName";
    public static final String strD_Col_FName="strD_Col_FName";


    public Stdet_Data_Col_Ident(){
        super(HandHeld_SQLiteOpenHelper.DATA_COL_IDENT);

        this.AddColumnToStructure(lngID,"Integer",true);
        this.AddColumnToStructure(strD_Col_ID,"String",false);
        this.AddColumnToStructure(strD_Col_LName,"String",false);
        this.AddColumnToStructure(strD_Col_FName,"String",false);

    }

}
