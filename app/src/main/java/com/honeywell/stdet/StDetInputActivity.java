package com.honeywell.stdet;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.ScannerNotClaimedException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.TriggerStateChangeEvent;
import com.honeywell.aidc.UnsupportedPropertyException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.cursoradapter.widget.SimpleCursorAdapter;


public class StDetInputActivity extends Activity implements BarcodeReader.BarcodeListener,
        BarcodeReader.TriggerListener {

    private com.honeywell.aidc.BarcodeReader barcodeReader;
    private ListView barcodeList;


    private Spinner spin_COL_ID;
    private Spinner spin_Loc_id;
    private Spinner spin_FAC_OP;
    private Spinner spin_UNITS;
    private Spinner spin_EQ_OP;


    private TextView txt_LocDesc;
    private EditText txt_Reading;
    private TextView txt_elev_code;
    private TextView txt_comment;

    Cursor Locs = null;
    ArrayList<String[]> alLocs = null;
    int iCol_Locid = 1;
    int iCol_Loc_Desc = 2;

    Cursor Cols = null;
    ArrayList<String[]> alCols = null;

    Cursor Units = null;
    ArrayList<String[]> alUnits = null;

    Cursor Eq_Oper_Status = null;
    ArrayList<String[]> alEq_Oper_Status = null;

    Cursor Fac_Oper_Status = null;
    ArrayList<String[]> alFac_Oper_Status = null;

    Cursor Elev = null;
    ArrayList<String[]> alElev = null;

    private String current_loc ="";
    String current_collector = "";
    String strDataModComment = "";
    Boolean bBarcodeLocation = false;

    Integer maxId = 0;

    Button btnInputForms;
    public  HandHeld_SQLiteOpenHelper dbHelper;
    public SQLiteDatabase db;
    Button btnSave;
    Button btnManual;
    Button btnClear;
    Button btnDone;

    Context ct = this;


    private Stdet_Inst_Readings ir_table =  new Stdet_Inst_Readings();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("------------onCreate StDetInputActivity","10");
        super.onCreate(savedInstanceState);
        Log.i("------------onCreate StDetInputActivity","1");
        setContentView(R.layout.activity_input_forms);

        //((TextView)findViewById(R.id.txtActivityTitle)).setText("Input Form");
        StdetDataTables tables= new StdetDataTables();
        tables.SetStdetTablesStructure();

        dbHelper =  new HandHeld_SQLiteOpenHelper(ct,tables);
        db = dbHelper.getReadableDatabase();

        maxId = dbHelper.GetMaxIRId(db);
        maxId++;

        Locs = dbHelper.GetLocations(db);
        Cols = dbHelper.GetColIdentity(db);
        alLocs = transferCursorToArrayList(Locs);
        alCols =  transferCursorToArrayList(Cols);
        Units = db.rawQuery("Select rowid as _id, strUnitsID from tbl_Unit_Def order by strUnitsID", null);
        alUnits =  transferCursorToArrayList(Units);

        Eq_Oper_Status = db.rawQuery("Select rowid as _id, strEqO_StatusID from tbl_Equip_Oper_Def order by strEqO_StatusID", null);
        alEq_Oper_Status = transferCursorToArrayList(Eq_Oper_Status);
        Fac_Oper_Status = db.rawQuery("Select rowid as _id, strFO_StatusID from tbl_Fac_Oper_Def order by strFO_StatusID", null);
        alFac_Oper_Status = transferCursorToArrayList(Eq_Oper_Status);

        Cursor Elev = null;
        ArrayList<String[]> alElev = null;

        Log.i("------------onCreate",Locs.getColumnName(1));
        spin_COL_ID = (Spinner) findViewById(R.id.spin_COL_ID);
        spin_Loc_id = (Spinner) findViewById(R.id.spin_Loc_id);
        txt_LocDesc = (TextView) findViewById(R.id.txt_Loc_desc);
        spin_FAC_OP =(Spinner) findViewById(R.id.spin_Fac_oper);
        spin_UNITS =  (Spinner) findViewById(R.id.spin_Unit);
        spin_EQ_OP  =  (Spinner) findViewById(R.id.spin_Eq_oper);

        txt_Reading = (EditText) findViewById(R.id.txt_Reading);
        txt_comment =(EditText) findViewById(R.id.txt_Comment);

        btnClear = (Button) findViewById(R.id.btn_clear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              clearForms();
            }
        });

        btnSave = (Button) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveForms();
            }
        });

        btnDone= (Button) findViewById(R.id.btn_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.getInsertTable(db,ir_table);
                ir_table =  new Stdet_Inst_Readings();
                clearForms();
            }
        });

        btnManual= (Button) findViewById(R.id.btn_Manual);
        btnManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearForms();
            }
        });


        spin_Loc_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);

                String desc= ((String[])alLocs.get(pos))[2];
                txt_LocDesc.setText(desc);
                if (!bBarcodeLocation)
                {
                    strDataModComment = "Manual";
                    bBarcodeLocation = false;
                }
                else{
                    strDataModComment = "";
                    bBarcodeLocation = false;
                }

                /** */
                Cursor loc_unit = dbHelper.GetUnit(db,current_loc);
               ArrayList<String[]> al_unit = transferCursorToArrayList(loc_unit);
               if (al_unit.size()>0) {

                   Integer id1 = getIndexFromArraylist(alUnits, al_unit.get(0)[1],1);
                   spin_UNITS.setSelection(id1);
               }

          }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        String[] fromLoc = new String[]{Stdet_DCP_Loc_Def.strD_Loc_ID};
        int[] toL = new int[]{android.R.id.text1};
        String[] fromCol = new String[]{Stdet_Data_Col_Ident.strD_Col_ID};
        String[] fromFO = new String[]{Stdet_Fac_Oper_Def.strFO_StatusID};
        String[] fromEO = new String[]{Stdet_Equip_Oper_Def.strEqO_StatusID};
        String[] fromU = new String[]{Stdet_Unit_Def.strUnitsID};

        SimpleCursorAdapter adCol =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, Cols, fromCol,toL,0);
        adCol.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_COL_ID.setAdapter(adCol);
        Log.i("------------onCreate","4");

        SimpleCursorAdapter adLocs =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, Locs, fromLoc,toL,0);
        adLocs.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_Loc_id.setAdapter(adLocs);

        SimpleCursorAdapter adFO =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, Fac_Oper_Status, fromFO,toL,0);
        adLocs.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_FAC_OP.setAdapter(adFO);

        SimpleCursorAdapter adEO =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, Eq_Oper_Status, fromEO,toL,0);
        adLocs.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_EQ_OP.setAdapter(adEO);

        SimpleCursorAdapter adU =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, Units, fromU,toL,0);
        adLocs.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_UNITS.setAdapter(adU);

        //db.close();
        //btnInputForms=(Button)findViewById(R.id.btnInputForms);
        //btnInputForms.setVisibility(View.GONE);

        // set lock the orientation
        // otherwise, the onDestory will trigger when orientation changes
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // get bar code instance from MainActivity
        barcodeReader = MainActivity.getBarcodeObject();

        if (barcodeReader != null) {

            // register bar code event listener
            barcodeReader.addBarcodeListener(this);
            Log.i("------------onCreate","barcodeReader !=null");

            // set the trigger mode to client control
            try {
                barcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                        BarcodeReader.TRIGGER_CONTROL_MODE_CLIENT_CONTROL);
            } catch (UnsupportedPropertyException e) {
                Toast.makeText(this, "Failed to apply properties", Toast.LENGTH_SHORT).show();
            }
            // register trigger state change listener
            barcodeReader.addTriggerListener(this);

            Map<String, Object> properties = new HashMap<String, Object>();
            // Set Symbologies On/Off
            properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, true);
            //properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_GS1_128_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_QR_CODE_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_CODE_39_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_UPC_A_ENABLE, true);
            properties.put(BarcodeReader.PROPERTY_EAN_13_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_AZTEC_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_CODABAR_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_PDF_417_ENABLED, true);
            // Set Max Code 39 barcode length
            properties.put(BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH, 10);
            // Turn on center decoding
            properties.put(BarcodeReader.PROPERTY_CENTER_DECODE, true);
            // Disable bad read response, handle in onFailureEvent
            properties.put(BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, false);
            // Apply the settings
            barcodeReader.setProperties(properties);
        }

        // get initial list
        barcodeList = (ListView) findViewById(R.id.listViewBarcodeData);
        Log.i("------------barcodeList",barcodeList.toString());
    }

    @Override
    public void onBarcodeEvent(final BarcodeReadEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // update UI to reflect the data
                Log.i("------------onBarcodeEvent", "onBarcodeEvent!!!!");
                List<String> list = new ArrayList<String>();
                current_loc  = event.getBarcodeData();
                Log.i("------------onBarcodeEvent", getCurrent_loc());

                final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                        StDetInputActivity.this, android.R.layout.simple_list_item_1, list);

                Integer id = getIndexFromArraylist(alLocs, getCurrent_loc(), 1);

                Log.i("------------onBarcodeEvent id =", id.toString());
                bBarcodeLocation = true;
                spin_Loc_id.setSelection(id);
                barcodeList.setAdapter(dataAdapter);
            }
        });
    }

    //private method of your class

    private int getIndex(Spinner spinner, String myString){
        System.out.println("getIndex spinner " + spinner.toString());
        System.out.println("getIndex myString " + myString);
        Integer n = spinner.getCount();
        System.out.println("getIndex n " + n.toString());
        SimpleCursorAdapter adapt = (SimpleCursorAdapter)spinner.getAdapter();

        for (int i=0;i<n;i++){
            String sValue = spinner.getItemAtPosition(i).toString();
            String sValue1 = adapt.getItem(i).toString();
            System.out.println("getIndex sValue " + sValue+  "--" + sValue1);
            if (sValue.equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }



    // When using Automatic Trigger control do not need to implement the
    // onTriggerEvent function
    @Override
    public void onTriggerEvent(TriggerStateChangeEvent event) {
        try {
            // only handle trigger presses
            // turn on/off aimer, illumination and decoding
            Log.i("------------onTriggerEvent","no Data");
            /*
            To get the "CR" in the barcode to be processed two settings needs to be changed:
"Settings - Honeywell Settings - Scanning - Internal Scanner - Default profile - Data Processing Settings. Set:
Wedge Method" to 'keyboard'
Wedge as keys to empty
             */
            barcodeReader.aim(event.getState());
            barcodeReader.light(event.getState());
            barcodeReader.decode(event.getState());

        } catch (ScannerNotClaimedException e) {
            e.printStackTrace();
            Toast.makeText(this, "Scanner is not claimed", Toast.LENGTH_SHORT).show();
        } catch (ScannerUnavailableException e) {
            e.printStackTrace();
            Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent arg0) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Log.i("no data","no Data");
                Toast.makeText(StDetInputActivity.this, "No data yet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (barcodeReader != null) {
            try {
                barcodeReader.claim();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (barcodeReader != null) {
            // release the scanner claim so we don't get any scanner
            // notifications while paused.
            barcodeReader.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (barcodeReader != null) {
            // unregister barcode event listener
            barcodeReader.removeBarcodeListener(this);

            // unregister trigger state change listener
            barcodeReader.removeTriggerListener(this);
        }
    }

    public ArrayList<String[]> transferCursorToArrayList(Cursor cursor) {
        ArrayList<String[]> arrayList = new ArrayList<String[]>();
        int nCol = cursor.getColumnCount();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // The Cursor is now set to the right position
            String[] strs = new String[nCol];
            for (Integer i = 0; i < nCol; i++) {
                strs[i]=  (String) cursor.getString(i);}
            arrayList.add(strs);
        }
        return arrayList;
    }

    private int getIndexFromArraylist(ArrayList<String[]> list, String myString, Integer column){
        System.out.println("getIndexFromArraylist  " + myString);
        Integer n = list.size();
        System.out.println("getIndexFromArraylist n " + n.toString());   ;

        for (Integer i=0;i<n;i++){
            String[] sValues = list.get(i);
            String sValue = sValues[column];
            String sId = sValues[0];
            System.out.print(sValue+" ");
            if (sValue.equalsIgnoreCase(myString)){
                return i;//Integer.valueOf(sId);
            }
        }

        return 0;
    }

    public String getCurrent_loc() {
        return current_loc;
    }

    public void setCurrent_loc(String current_loc) {
        this.current_loc = current_loc;
    }

   public void clearForms(){
       txt_Reading.setText("");
       txt_comment.setText("");

}
    public void saveForms(){
        Date currentTime = Calendar.getInstance().getTime();
        TextView temp;
        temp = (TextView) spin_Loc_id.getSelectedView();
        current_loc = temp.getText().toString();
        temp = (TextView) spin_COL_ID.getSelectedView();
        current_collector =temp.getText().toString();
        temp = (TextView) spin_EQ_OP.getSelectedView();
        String curent_eo = temp.getText().toString();
        temp = (TextView) spin_FAC_OP.getSelectedView();
        String curent_fo = temp.getText().toString();
        temp = (TextView) spin_UNITS.getSelectedView();
        String unit = temp.getText().toString();
        String reading = txt_Reading.getText().toString();
        String comm = txt_comment.getText().toString();

        String a = ir_table.getName();

        maxId = ir_table.AddToTable(maxId, "1", current_loc,reading,currentTime.toString(),
                current_collector,curent_eo,curent_fo,unit,"",comm,strDataModComment);
        clearForms();
        System.out.println("max id " + maxId.toString());
    }

    public Stdet_Inst_Readings getIr_table() {
        return ir_table;
    }

    public void setIr_table(Stdet_Inst_Readings ir_table) {
        this.ir_table = ir_table;
    }
}