package com.honeywell.stdet;

public class Stdet_Elevation_Codes extends StdetDataTable {

    /*
    <xs:element name="facility_id" type="xs:int" minOccurs="0"/>
<xs:element name="elev_code" type="xs:string" minOccurs="0"/>
<xs:element name="elev_code_desc" type="xs:string" minOccurs="0"/>
<xs:element name="strUserModifyName" type="xs:string" minOccurs="0"/>
<xs:element name="dtLastModificationDate" type="xs:dateTime" minOccurs="0"/>
     */
    public static final String facility_id="facility_id";
    public static final String elev_code="elev_code";
    public static final String elev_code_desc="elev_code_desc";



    public Stdet_Elevation_Codes(){
        super(HandHeld_SQLiteOpenHelper.ELEVATIONCODES);

        this.AddColumnToStructure(facility_id,"Integer",true);
        this.AddColumnToStructure(elev_code,"String",true);
        this.AddColumnToStructure(elev_code_desc,"String",false);


    }

}
