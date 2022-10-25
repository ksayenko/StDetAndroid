package com.honeywell.stdet;

public class Stdet_Unit_Def extends StdetDataTable {

    public static final String lngID="lngID";
    public static final String strUnitsID="strUnitsID";
    public static final String strUnitsDesc="strUnitsDesc";
    public static final String strUnit_Type="strUnit_Type";

/*
<xs:element name="lngID" type="xs:int" minOccurs="0"/>
<xs:element name="strUnitsID" type="xs:string" minOccurs="0"/>
<xs:element name="strUnitsDesc" type="xs:string" minOccurs="0"/>
<xs:element name="ynCurrent" type="xs:boolean" minOccurs="0"/>
<xs:element name="strUserModifyName" type="xs:string" minOccurs="0"/>
<xs:element name="dtLastModificationDate" type="xs:dateTime" minOccurs="0"/>
<xs:element name="strUnit_Type" type="xs:string" minOccurs="0"/>
 */
    public Stdet_Unit_Def(){
        super(HandHeld_SQLiteOpenHelper.UNIT_DEF);

        this.AddColumnToStructure(lngID,"Integer",true);
        this.AddColumnToStructure(strUnitsID,"String",false);
        this.AddColumnToStructure(strUnitsDesc,"String",false);
        this.AddColumnToStructure(strUnit_Type,"String",false);

    }

}
