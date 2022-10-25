package com.honeywell.stdet;

public class Stdet_Equip_Oper_Def extends StdetDataTable {

    /*
    <xs:element name="lngID" type="xs:int" minOccurs="0"/>
<xs:element name="ynCurrent" type="xs:boolean" minOccurs="0"/>
<xs:element name="strEqO_StatusID" type="xs:string" minOccurs="0"/>
<xs:element name="strEqO_StatusDesc" type="xs:string" minOccurs="0"/>
<xs:element name="strComment" type="xs:string" minOccurs="0"/>
<xs:element name="strUserModifyName" type="xs:string" minOccurs="0"/>
<xs:element name="dtLastModificationDate" type="xs:dateTime" minOccurs="0"/>
     */
    public static final String lngID="lngID";
    public static final String strEqO_StatusID="strEqO_StatusID";
    public static final String strEqO_StatusDesc="strEqO_StatusDesc";


    public Stdet_Equip_Oper_Def(){
        super(HandHeld_SQLiteOpenHelper.EQUIP_OPER_DEF);

        this.AddColumnToStructure(lngID,"Integer",true);
        this.AddColumnToStructure(strEqO_StatusID,"String",false);
        this.AddColumnToStructure(strEqO_StatusDesc,"String",false);

    }

}
