package com.honeywell.stdet;

public class Stdet_Fac_Oper_Def extends StdetDataTable {

    public static final String lngID="lngID";
    public static final String strFO_StatusID="strFO_StatusID";
    public static final String strFO_StatusDesc="strFO_StatusDesc";


    /*
    <xs:element name="lngID" type="xs:int" minOccurs="0"/>
<xs:element name="ynCurrent" type="xs:boolean" minOccurs="0"/>
<xs:element name="strFO_StatusID" type="xs:string" minOccurs="0"/>
<xs:element name="strFO_StatusDesc" type="xs:string" minOccurs="0"/>
<xs:element name="strComment" type="xs:string" minOccurs="0"/>
<xs:element name="strUserModifyName" type="xs:string" minOccurs="0"/>
<xs:element name="dtLastModificationDate" type="xs:dateTime" minOccurs="0"/>
     */

    public Stdet_Fac_Oper_Def(){
        super(HandHeld_SQLiteOpenHelper.FAC_OPER_DEF);

        this.AddColumnToStructure(lngID,"Integer",true);
        this.AddColumnToStructure(strFO_StatusID,"String",false);
        this.AddColumnToStructure(strFO_StatusDesc,"String",false);

    }

}
