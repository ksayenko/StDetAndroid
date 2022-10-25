package com.honeywell.stdet;

public class Stdet_DCP_Loc_Def extends StdetDataTable {

    /*
    <xs:element name="lngID" type="xs:int" minOccurs="0"/>
<xs:element name="ynCurrent" type="xs:boolean" minOccurs="0"/>
<xs:element name="strD_Loc_ID" type="xs:string" minOccurs="0"/>
<xs:element name="strFT_D_Loc_ID" type="xs:string" minOccurs="0"/>
<xs:element name="strFR_D_Loc_ID" type="xs:string" minOccurs="0"/>
<xs:element name="strD_LocAlias" type="xs:string" minOccurs="0"/>
<xs:element name="strD_LocDesc" type="xs:string" minOccurs="0"/>
<xs:element name="strD_TypeID" type="xs:string" minOccurs="0"/>
<xs:element name="datD_InstallDate" type="xs:dateTime" minOccurs="0"/>
<xs:element name="datD_RemDate" type="xs:dateTime" minOccurs="0"/>
<xs:element name="strS_LocID" type="xs:string" minOccurs="0"/>
<xs:element name="strPIDSheetNum" type="xs:string" minOccurs="0"/>
<xs:element name="strUpsEquipID" type="xs:string" minOccurs="0"/>
<xs:element name="strDwnsEquipID" type="xs:string" minOccurs="0"/>
<xs:element name="strLineID" type="xs:string" minOccurs="0"/>
<xs:element name="strValveID" type="xs:string" minOccurs="0"/>
<xs:element name="datD_Added" type="xs:dateTime" minOccurs="0"/>
<xs:element name="strComment" type="xs:string" minOccurs="0"/>
<xs:element name="strZone" type="xs:string" minOccurs="0"/>
<xs:element name="strEqID" type="xs:string" minOccurs="0"/>
<xs:element name="sys_loc_code" type="xs:string" minOccurs="0"/>
<xs:element name="strUserModifyName" type="xs:string" minOccurs="0"/>
<xs:element name="dtLastModificationDate" type="xs:dateTime" minOccurs="0"/>
<xs:element name="strStream" type="xs:string" minOccurs="0"/>
<xs:element name="facility_id" type="xs:int" minOccurs="0"/>
<xs:element name="ynActive" type="xs:boolean" minOccurs="0"/>
<xs:element name="uf_include_in_sidd" type="xs:boolean" minOccurs="0"/>
<xs:element name="uf_include_in_stdet" type="xs:boolean" minOccurs="0"/>
</xs:sequence>
     */
    public static final String lngID="lngID";
    public static final String strD_Loc_ID="strD_Loc_ID";
    public static final String sys_loc_code="sys_loc_code";
    public static final String strD_LocDesc="strD_LocDesc";
    public static final String strD_TypeID="strD_TypeID";
    public static final String ynCurrent="ynCurrent";

    public Stdet_DCP_Loc_Def(){
        super(HandHeld_SQLiteOpenHelper.DCP_LOC_DEF);

        this.AddColumnToStructure(lngID,"Integer",true);
        this.AddColumnToStructure(strD_Loc_ID,"String",false);
        this.AddColumnToStructure(strD_LocDesc,"String",false);
        this.AddColumnToStructure(strD_TypeID,"String",false);
        this.AddColumnToStructure(ynCurrent,"Boolean",false);
        this.AddColumnToStructure(sys_loc_code,"String",false);
    }

}