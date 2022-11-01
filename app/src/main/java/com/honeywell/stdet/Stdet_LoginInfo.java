package com.honeywell.stdet;

import java.util.ArrayList;

public class Stdet_LoginInfo extends StdetDataTable {

    public static final String UserName="UserName";
    public static final String Password="Password";



    public Stdet_LoginInfo(){
        super(HandHeld_SQLiteOpenHelper.LOGININFO);

        this.setTableType(TABLE_TYPE.SYSTEM);
        this.AddColumnToStructure(UserName,"String",true);
        this.AddColumnToStructure(Password,"dateTime",false);

    }



    public void AddToTable( String userName, String password) {
        ArrayList<String> record = new ArrayList<>();
        int n = this.getColumnsNumber();
        for (int j = 0; j < n; j++)
            record.add("");

        record.set(GetElementIndex(UserName), userName);
        record.set(GetElementIndex(Password), password);

        this.AddRowToData(record);

    }

}

