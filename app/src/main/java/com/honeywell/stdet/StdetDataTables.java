package com.honeywell.stdet;

import java.util.ArrayList;

public class StdetDataTables {
    private ArrayList<StdetDataTable> dataTables;

    public StdetDataTables(){
        setDataTables(new ArrayList<StdetDataTable>());
    }

    public void AddStdetDataTable(StdetDataTable dt) {
        dataTables.add(dt);
    }

    public ArrayList<StdetDataTable> getDataTables() {
        return dataTables;
    }

    public void setDataTables(ArrayList<StdetDataTable> dataTables) {
        this.dataTables = dataTables;
    }

    public void SetStdetTablesStructure() {
        dataTables = new ArrayList<StdetDataTable>();
        dataTables.add(new Stdet_Inst_Readings());
        dataTables.add(new Stdet_Data_Col_Ident());
        dataTables.add(new Stdet_Elevation_Codes());
        dataTables.add(new Stdet_Elevations());
        dataTables.add(new Stdet_DCP_Loc_Char());
        dataTables.add(new Stdet_DCP_Loc_Def());
        dataTables.add(new Stdet_Fac_Oper_Def());
        dataTables.add(new Stdet_Facility());
        dataTables.add(new Stdet_TableVers());
        dataTables.add(new Stdet_Unit_Def());

    }
}
