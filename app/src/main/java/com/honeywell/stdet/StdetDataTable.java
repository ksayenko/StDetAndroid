package com.honeywell.stdet;

import java.util.ArrayList;

public class StdetDataTable {

    //
    //

    private String name;

    private ArrayList<String> ColumnNames;
    private ArrayList<String> ColumnTypes;
    private ArrayList<Boolean> isColumnPK;

    private ArrayList<ArrayList<String>> dataTable;


    public StdetDataTable() {
        this("NA");
    }

    public StdetDataTable(String sname) {

        name = sname;
        ColumnNames = new ArrayList<String>();
        ColumnTypes = new ArrayList<String>();
        isColumnPK = new ArrayList<Boolean>();

        dataTable = new ArrayList<ArrayList<String>>(0);
    }
    public void AddColumnToStructure(String columnName, String ColumnType, Boolean isPK) {

        ColumnNames.add(columnName);
        ColumnTypes.add(ColumnType);
        isColumnPK.add(isPK);

    }

    public void AddColumnToStructure(String columnName, String ColumnType, Boolean isPK, int iElement) {

        ColumnNames.add(columnName);
        ColumnTypes.add(ColumnType);
        isColumnPK.add(isPK);

    }

    public void AddRowToData(ArrayList<String> thedataRow) {

        dataTable.add(thedataRow);
    }

    public int GetElementIndex(String columnname) {
        int rv = -1;
        int n = ColumnNames.size();

        for (int i = 0; i < n; i++) {
            String name = getColumnNames().get(i);
            if (columnname.equalsIgnoreCase(name)) {
                rv = i;
                break;
            }
        }

        return rv;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Integer getColumnsNumber() {
        return ColumnNames.toArray().length;
    }


    public ArrayList<String> getColumnNames() {
        return ColumnNames;
    }

    public void setColumnNames(ArrayList<String> columnNames) {
        ColumnNames = columnNames;
    }

    public ArrayList<String> getColumnTypes() {
        return ColumnTypes;
    }

    public void setColumnTypes(ArrayList<String> columnTypes) {
        ColumnTypes = columnTypes;
    }

    public ArrayList<Boolean> getIsColumnPK() {
        return isColumnPK;
    }

    public void setIsColumnPK(ArrayList<Boolean> isColumnPK) {
        this.isColumnPK = isColumnPK;
    }

    public String CreateTable() {
        int nColumns = ColumnNames.toArray().length;
        String sPK = " , PRIMARY KEY (";
        Integer iPk = 0;
        String sCREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + name +
                "(";

        for (int i = 0; i < nColumns; i++) {
            String sColName = getColumnNames().get(i);
            String sColType = getSqlLiteDataType(getColumnTypes().get(i));

            sCREATE_TABLE += sColName + " " + sColType;
            if (getIsColumnPK().get(i)) {
                if (iPk > 0)
                    sPK +=  ", ";
                sPK += sColName;
                iPk++;

            }
            if (i < nColumns - 1) {
                sCREATE_TABLE += ",";
            }
        }
        if (iPk > 0) {
            sCREATE_TABLE += sPK + ") ";
        }
        sCREATE_TABLE += " )";
        return sCREATE_TABLE;
    }

    public int GetNumberOfRecords() {
        int rv = -1;
        try {
            rv = dataTable.size();
        } catch (Exception ex) {
        }
        return rv;
    }

    public String getInsertIntoDB(int element) {
        String sInsert1 = "INSERT INTO  " + name + "(";
        String sInsert2 = "VALUES   (";
        String type;
        String sValue;
        int nColumns = ColumnNames.toArray().length;
        for (int i = 0; i < nColumns; i++) {
            sInsert1 += getColumnNames().get(i);
            sValue = dataTable.get(element).get(i);
            type = getColumnTypes().get(i);
            sValue = getConvertedValue(type, sValue);
            sInsert2 += sValue;

            if (i < nColumns - 1) {
                sInsert1 += ", ";
                sInsert2 += ", ";
            }
        }

        sInsert1 += " )";
        sInsert2 += " )";
        return sInsert1 + sInsert2;
    }

    private String getConvertedValue(String type, String sValue) {
        boolean bNull = false;
        if (sValue.equals("") || sValue.equalsIgnoreCase("null")) {
            sValue = "NULL";
            bNull = true;
        }
        if (!bNull && (type.equalsIgnoreCase("string") || type.equalsIgnoreCase("datetime")))
            sValue = "'" + sValue + "'";
        else if (type.equalsIgnoreCase("Boolean") && (sValue.equalsIgnoreCase("true")))
            sValue = "1";
        else if (type.equalsIgnoreCase("Boolean") && (sValue.equalsIgnoreCase("false")))
            sValue = "0";
        return sValue;
    }

    public String getSqlLiteDataType(String type) {
        if (type.equalsIgnoreCase("string"))
            return "TEXT";
        else if (type.equalsIgnoreCase("boolean"))
            return "INTEGER";
        else if (type.equalsIgnoreCase("integer"))
            return type;
        else if (type.equalsIgnoreCase("double"))
            return "REAL";
        else if (type.equalsIgnoreCase("datetime"))
            return "TEXT";
        else
            return type;
    }


    public ArrayList<String> getEmptyDataRow() {
        ArrayList<String> dataRow = new ArrayList<String>();
        int n = ColumnNames.size();
        for (int i = 0; i < n; i++)
            dataRow.add("");
        return dataRow;
    }

}
