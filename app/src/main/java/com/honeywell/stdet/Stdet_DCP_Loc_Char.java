package com.honeywell.stdet;

public class Stdet_DCP_Loc_Char extends StdetDataTable {

    /*
    <xs:element name="lngID" type="xs:int" minOccurs="0"/>
<xs:element name="facility_id" type="xs:int" minOccurs="0"/>
<xs:element name="strD_Loc_ID" type="xs:string" minOccurs="0"/>
<xs:element name="strD_ParName" type="xs:string" minOccurs="0"/>
<xs:element name="strD_ParValue" type="xs:string" minOccurs="0"/>
<xs:element name="strD_ParUnits" type="xs:string" minOccurs="0"/>
<xs:element name="strUserModifyName" type="xs:string" minOccurs="0"/>
<xs:element name="dtLastModificationDate" type="xs:dateTime" minOccurs="0"/>
     */
    public static final String lngID="lngID";
    public static final String facility_id="facility_id";
    public static final String strD_Loc_ID="strD_Loc_ID";
    public static final String strD_ParName="strD_ParName";
    public static final String strD_ParValue="strD_ParValue";
    public static final String strD_ParUnits="strD_ParUnits";

    public Stdet_DCP_Loc_Char(){
        super(HandHeld_SQLiteOpenHelper.DCP_LOC_CHAR);

        this.AddColumnToStructure(lngID,"Integer",true);
        this.AddColumnToStructure(facility_id,"Integer",false);
        this.AddColumnToStructure(strD_Loc_ID,"String",false);
        this.AddColumnToStructure(strD_ParName,"String",false);
        this.AddColumnToStructure(strD_ParValue,"String",false);
        this.AddColumnToStructure(strD_ParUnits,"String",false);

    }

}