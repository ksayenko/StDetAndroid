package com.honeywell.stdet;

public class Stdet_TableVers extends StdetDataTable {

    public static final String strTableName="strTableName";
    public static final String dtLastModificationDate="dtLastModificationDate";
    public static final String ynUsedByStdet="ynUsedByStdet";

/*
<xs:element name="strTableName" type="xs:string" minOccurs="0"/>
<xs:element name="dtLastModificationDate" type="xs:dateTime" minOccurs="0"/>
<xs:element name="ynUsedByStdet" type="xs:boolean" minOccurs="0"/>
 */
    public Stdet_TableVers(){
        super(HandHeld_SQLiteOpenHelper.TABLEVERS);


        this.AddColumnToStructure(strTableName,"String",true);
        this.AddColumnToStructure(dtLastModificationDate,"dateTime",false);
        this.AddColumnToStructure(ynUsedByStdet,"Boolean",false);

    }

}