package com.honeywell.stdet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.cursoradapter.widget.SimpleCursorAdapter;


public class StDetInputActivity extends Activity implements BarcodeReader.BarcodeListener,
        BarcodeReader.TriggerListener {


    public enum VALIDDATION {VALID,ERROR,WARNING}
    private com.honeywell.aidc.BarcodeReader barcodeReader;
    private ListView barcodeList;

    private Spinner spin_COL_ID;
    private Spinner spin_Loc_id;
    private Spinner spin_FAC_OP;
    private Spinner spin_UNITS;
    private Spinner spin_EQ_OP;
    private Spinner spin_elev_code;

    private TextView txt_LocDesc;
    private EditText txt_Reading;

    private TextView txt_elev_code2;
    private EditText edit_depth;
    private TextView txt_comment;
    private String locMin, locMax;

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

    private String current_loc = "";
    String current_collector = "";
    String strDataModComment = "";
    String curent_eo = "";
    String curent_fo = "";
    String curent_elevationcode = "";
    String current_comment = "";
    String current_reading = "";
    String current_unit = "";
    Boolean bBarcodeLocation = false;

    Integer maxId = 0;

    Button btnInputForms;
    public HandHeld_SQLiteOpenHelper dbHelper;
    public SQLiteDatabase db;
    Button btnSave;
    Button btnManual;
    Button btnClear;
    Button btnDone;

    Context ct = this;
    Boolean bSavedToDBData = false;
    Boolean bAcceptWarning = false;


    private Stdet_Inst_Readings default_reading;
    private Stdet_Inst_Readings ir_table =  new Stdet_Inst_Readings();

    private double UNDEFINED = -99999.999;
    Boolean[] bDialogChoice = {false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bSavedToDBData = false;
        bAcceptWarning = false;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            System.out.println("we have default reading");
            default_reading = (Stdet_Inst_Readings) getIntent().getSerializableExtra("IR");
        }
        else {
            System.out.println("no default reading");
            default_reading = Stdet_Inst_Readings.GetDefault();
        }


        Log.i("------------onCreate StDetInputActivity", "10");
        super.onCreate(savedInstanceState);
        Log.i("------------onCreate StDetInputActivity", "1");
        setContentView(R.layout.activity_input_forms);

        //((TextView)findViewById(R.id.txtActivityTitle)).setText("Input Form");
        StdetDataTables tables = new StdetDataTables();
        tables.SetStdetTablesStructure();

        dbHelper = new HandHeld_SQLiteOpenHelper(ct, tables);
        db = dbHelper.getReadableDatabase();

        int rowsInDB = dbHelper.getRowsInLookupTables(db);
        if (rowsInDB < 1) {
            AlertDialogShow("The Lookup Tables aren't populated, go to Menu | Download and Populate Lookup DB","ERROR!");
        }


        maxId = dbHelper.getMaxIRID(db);
        maxId++;

        Locs = dbHelper.getLocations(db);
        Cols = dbHelper.GetColIdentity(db);
        alLocs = transferCursorToArrayList(Locs);
        alCols = transferCursorToArrayList(Cols);
        Units = dbHelper.getUnits(db, "");
        alUnits = transferCursorToArrayList(Units);

        Eq_Oper_Status = dbHelper.getEOS(db);
        alEq_Oper_Status = transferCursorToArrayList(Eq_Oper_Status);
        Fac_Oper_Status = dbHelper.getFOS(db);
        alFac_Oper_Status = transferCursorToArrayList(Fac_Oper_Status);
        Elev = dbHelper.getElevationCodes(db);
        alElev = transferCursorToArrayList(Elev);


        Log.i("------------onCreate", Locs.getColumnName(1));
        spin_COL_ID = (Spinner) findViewById(R.id.spin_COL_ID);
        spin_Loc_id = (Spinner) findViewById(R.id.spin_Loc_id);
        txt_LocDesc = (TextView) findViewById(R.id.txt_Loc_desc);
        spin_FAC_OP = (Spinner) findViewById(R.id.spin_Fac_oper);
        spin_UNITS = (Spinner) findViewById(R.id.spin_Unit);
        spin_EQ_OP = (Spinner) findViewById(R.id.spin_Eq_oper);

        txt_elev_code2 = (TextView) findViewById(R.id.lbl_elev_code_desc);
        spin_elev_code = (Spinner) findViewById(R.id.spin_elev_code);

        txt_Reading = (EditText) findViewById(R.id.txt_Reading);
        txt_Reading.requestFocus();
        txt_Reading.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
               bAcceptWarning=false;
            }
        });
        txt_comment = (EditText) findViewById(R.id.txt_Comment);
        edit_depth = (EditText) findViewById(R.id.text_depth);
        edit_depth.setEnabled(false);
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
                System.out.println(bAcceptWarning);
                VALIDDATION iChecked = saveForms(bAcceptWarning);
                if (iChecked == VALIDDATION.WARNING)
                    bAcceptWarning = true;
                System.out.println(bAcceptWarning);
                System.out.println(iChecked);
            }
        });

        btnDone = (Button) findViewById(R.id.btn_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dbHelper.getInsertTable(db, ir_table);
                int records = ir_table.GetNumberOfRecords();
                String message = "The data (" + String.valueOf(records) + " records) is saved and ready to be uplaoded.";
                //AlertDialogShow("The data (" + String.valueOf(records) + " records) is saved and ready to be uplaoded","Info","OK");
                Toast.makeText(ct, message, Toast.LENGTH_SHORT).show();
                ir_table = default_reading;
                clearForms();
                bSavedToDBData =true;
                onBackPressed();
            }
        });

         spin_elev_code.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                String desc = ((String[]) alElev.get(pos))[2];
                txt_elev_code2.setText(desc);
                TextView temp = (TextView) spin_elev_code.getSelectedView();
                curent_elevationcode = temp.getText().toString();
                //bAcceptWarning = false;
                String[] elev_code_value = dbHelper.getElevationCodeValue(db, current_loc, curent_elevationcode);
                edit_depth.setText(elev_code_value[1]);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spin_Loc_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);

                String desc = ((String[]) alLocs.get(pos))[2];
                current_loc = ((String[]) alLocs.get(pos))[1];
                txt_LocDesc.setText(desc);
                if (!bBarcodeLocation) {
                    strDataModComment = "Manual";
                    bBarcodeLocation = false;
                } else {
                    strDataModComment = "";
                    bBarcodeLocation = false;
                }

                Cursor loc_unit = dbHelper.getUnits(db, current_loc);
                ArrayList<String[]> al_unit = transferCursorToArrayList(loc_unit);
                if (al_unit.size() > 0) {
                    current_unit = al_unit.get(0)[1];
                    int id1 = getIndexFromArraylist(alUnits, current_unit, 1);
                    spin_UNITS.setSelection(id1);
                }
                int id2e, id2f;
                spin_elev_code.setEnabled(false);
                if (current_loc.startsWith("WL")) {
                    curent_eo = "PumpOff";
                    id2e = getIndexFromArraylist(alEq_Oper_Status, curent_eo, 1);
                    spin_EQ_OP.setSelection(id2e);
                    curent_fo = "Oper";
                    id2f = getIndexFromArraylist(alFac_Oper_Status, curent_fo, 1);
                    spin_FAC_OP.setSelection(id2f);
                    spin_elev_code.setEnabled(true);

                } else if (current_loc.startsWith("FT")) {
                    curent_eo = "PumpOff";
                    id2e = getIndexFromArraylist(alEq_Oper_Status, curent_eo, 1);
                    spin_EQ_OP.setSelection(id2e);
                    curent_fo = "Oper";
                    id2f = getIndexFromArraylist(alFac_Oper_Status, curent_fo, 1);
                    spin_FAC_OP.setSelection(id2f);
                }

                String[] Loc_minmax = dbHelper.getMinMax(db, current_loc);
                locMax = Loc_minmax[1];
                locMin = Loc_minmax[0];


                String[] elev_code_value = dbHelper.getElevationCodeValue(db, current_loc);
                System.out.println("current_loc  " + current_loc);
                System.out.println("current_loc  " + elev_code_value[0]);
                edit_depth.setText(elev_code_value[1]);
                int id3 = getIndexFromArraylist(alElev, elev_code_value[0], 1);
                spin_elev_code.setSelection(id3);

                bAcceptWarning = false;
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
        String[] fromEl = new String[]{Stdet_Elevation_Codes.elev_code};

        SimpleCursorAdapter adCol =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, Cols, fromCol, toL, 0);
        adCol.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_COL_ID.setAdapter(adCol);
        current_collector = default_reading.getValueFromData(0, Stdet_Data_Col_Ident.strD_Col_ID);
        System.out.println("from default current_collector "+current_collector);
        int idCol = getIndexFromArraylist(alCols, current_collector, 1);
        spin_COL_ID.setSelection(idCol);


        Log.i("------------onCreate", "4");

        SimpleCursorAdapter adLocs =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, Locs, fromLoc, toL, 0);
        adLocs.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_Loc_id.setAdapter(adLocs);
        spin_Loc_id.setSelection(0);

        SimpleCursorAdapter adFO =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, Fac_Oper_Status, fromFO, toL, 0);
        adLocs.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_FAC_OP.setAdapter(adFO);

        SimpleCursorAdapter adEO =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, Eq_Oper_Status, fromEO, toL, 0);
        adLocs.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_EQ_OP.setAdapter(adEO);

        SimpleCursorAdapter adU =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, Units, fromU, toL, 0);
        adLocs.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_UNITS.setAdapter(adU);

        SimpleCursorAdapter adelev =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, Elev, fromEl, toL, 0);
        adelev.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_elev_code.setAdapter(adelev);

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
            Log.i("------------onCreate", "barcodeReader !=null");

            // set the trigger mode to client control
            try {
                barcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                        BarcodeReader.TRIGGER_CONTROL_MODE_CLIENT_CONTROL);
            } catch (UnsupportedPropertyException e) {
                Toast.makeText(this, "Failed to apply properties", Toast.LENGTH_SHORT).show();
            }
            // register trigger state change listener
            barcodeReader.addTriggerListener(this);

            Map<String, Object> properties = new HashMap<>();
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
        Log.i("------------barcodeList", barcodeList.toString());
    }

    @Override
    public void onBarcodeEvent(final BarcodeReadEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // update UI to reflect the data
                Log.i("------------onBarcodeEvent", "onBarcodeEvent!!!!");
                List<String> list = new ArrayList<>();
                current_loc = event.getBarcodeData();
                Log.i("------------onBarcodeEvent", getCurrent_loc());

                final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                        StDetInputActivity.this, android.R.layout.simple_list_item_1, list);

                int id = getIndexFromArraylist(alLocs, getCurrent_loc(), 1);

                Log.i("------------onBarcodeEvent id =", Integer.toString(id));
                if (id > 0) {
                    bBarcodeLocation = true;
                }
                spin_Loc_id.setSelection(id);
                barcodeList.setAdapter(dataAdapter);
                bSavedToDBData = false;
            }
        });
    }

    //private method of your class

    private int getIndex(Spinner spinner, String myString) {
        System.out.println("getIndex spinner " + spinner.toString());
        System.out.println("getIndex myString " + myString);
        int n = spinner.getCount();

        SimpleCursorAdapter adapt = (SimpleCursorAdapter) spinner.getAdapter();

        for (int i = 0; i < n; i++) {
            String sValue = spinner.getItemAtPosition(i).toString();
            String sValue1 = adapt.getItem(i).toString();
            System.out.println("getIndex sValue " + sValue + "--" + sValue1);
            if (sValue.equalsIgnoreCase(myString)) {
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
            Log.i("------------onTriggerEvent", "no Data");
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
                //List<String> list = new ArrayList<String>();
                //final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                //         StDetInputActivity.this, android.R.layout.simple_list_item_1, list);
                Log.i("no data", "no Data");
                int id = getIndexFromArraylist(alLocs, "NA", 1);

                Log.i("------------onBarcodeEvent id =", Integer.toString(id));

                bBarcodeLocation = false;
                spin_Loc_id.setSelection(id);
                //barcodeList.setAdapter(dataAdapter);
                Toast.makeText(StDetInputActivity.this, "No data yet", Toast.LENGTH_SHORT).show();
                bSavedToDBData = false;
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
            db.close();
        }
    }

    public ArrayList<String[]> transferCursorToArrayList(Cursor cursor) {
        ArrayList<String[]> arrayList = new ArrayList<String[]>();
        int nCol = cursor.getColumnCount();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // The Cursor is now set to the right position
            String[] strs = new String[nCol];
            for (int i = 0; i < nCol; i++) {
                strs[i] = (String) cursor.getString(i);
            }
            arrayList.add(strs);
        }
        return arrayList;
    }

    private int getIndexFromArraylist(ArrayList<String[]> list, String myString, Integer column) {

        int n = list.size();


        for (int i = 0; i < n; i++) {
            String[] sValues = list.get(i);
            String sValue = sValues[column];
            String sId = sValues[0];

            if (sValue.equalsIgnoreCase(myString)) {
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

    public void clearForms() {
        txt_Reading.setText("");
        txt_comment.setText("");

        int id = 0;
        id = getIndexFromArraylist(alLocs, "NA", 1);
        Log.i("------------clearForms =", Integer.toString(id));
        spin_Loc_id.setSelection(id);
        //id = getIndexFromArraylist(alCols, "NA", 1);
        //spin_COL_ID.setSelection(id);
        id = getIndexFromArraylist(alFac_Oper_Status, "NA", 1);
        spin_FAC_OP.setSelection(id);
        id = getIndexFromArraylist(alEq_Oper_Status, "NA", 1);
        spin_EQ_OP.setSelection(id);
        id = getIndexFromArraylist(alUnits, "NA", 1);
        spin_UNITS.setSelection(id);

        bBarcodeLocation = false;

        txt_Reading.requestFocus();
    }

    public VALIDDATION saveForms(boolean bAcceptWarning) {
        Date currentTime = Calendar.getInstance().getTime();
        String timeStamp = new SimpleDateFormat("MM/dd/yyyy hh:mm a").format(Calendar.getInstance().getTime());
        TextView temp;
        temp = (TextView) spin_Loc_id.getSelectedView();
        current_loc = temp.getText().toString();

        temp = (TextView) spin_COL_ID.getSelectedView();
        current_collector = temp.getText().toString();
        default_reading.setValueInData(0, Stdet_Inst_Readings.strD_Col_ID, current_collector);
        System.out.println("In Input during save current_collector " + default_reading.getValueFromData(0, Stdet_Inst_Readings.strD_Col_ID) );

        temp = (TextView) spin_EQ_OP.getSelectedView();
        curent_eo = temp.getText().toString();
        temp = (TextView) spin_FAC_OP.getSelectedView();
        curent_fo = temp.getText().toString();
        temp = (TextView) spin_UNITS.getSelectedView();
        current_unit = temp.getText().toString();
        current_reading = txt_Reading.getText().toString();
        current_comment = txt_comment.getText().toString();
        temp = (TextView) spin_elev_code.getSelectedView();
        curent_elevationcode = temp.getText().toString();
        String[] error_mesage = new String[]{""};
        VALIDDATION bresult = isRecordValid(error_mesage);
        if (bresult == VALIDDATION.ERROR) {
            AlertDialogShow(error_mesage[0],"ERROR");
        }
        else if (bresult ==VALIDDATION.WARNING && !bAcceptWarning ) {
            AlertDialogShow("Please check\n" + error_mesage[0] +"\nPress 'Save' one more time to confirm the data as VALID or update the input data.","Warning");
        }
        else if (bresult == VALIDDATION.VALID|| (bresult ==VALIDDATION.WARNING && bAcceptWarning) ) {
            System.out.println(error_mesage[0]);
            maxId = ir_table.AddToTable("1", current_loc, current_reading, timeStamp,
                    current_collector, curent_eo, curent_fo, current_unit, curent_elevationcode, current_comment, strDataModComment);
            maxId++;
            clearForms();
            System.out.println("NEW max id " + maxId.toString());
        }

        return bresult;
    }

    public Stdet_Inst_Readings getIr_table() {
        return ir_table;
    }

    public void setIr_table(Stdet_Inst_Readings ir_table) {
        this.ir_table = ir_table;
    }

    private boolean isNA(String sValue) {
        boolean isna = sValue == null || sValue.equals("") || sValue.equalsIgnoreCase("NA");
        return isna;
    }

    public VALIDDATION isRecordValid(String[] error_message) {
        String message = "";
        VALIDDATION isValid = VALIDDATION.VALID;
        double reading;
        try {
            reading = Double.parseDouble(current_reading);
        } catch (Exception ex) {
            reading = 0.0;
        }

        if (isNA(current_collector)) {
            message += "Please select a Data Collector Id. ";
            spin_COL_ID.requestFocus();
            isValid = VALIDDATION.ERROR;
        } else if (isNA(current_loc)) {
            message += "Please input a Location Id. ";
            spin_Loc_id.requestFocus();
            isValid =VALIDDATION.ERROR;
        } else if (isNA(curent_fo)) {
            //message += "Please select a Facility Oper Status. ";
            //spin_FAC_OP.requestFocus();
            //isValid =VALIDDATION.ERROR;
        } else if (isNA(curent_eo)) {
            //message += "Please select an Equipment Oper Status. ";
            //spin_FAC_OP.requestFocus();
            //isValid = VALIDDATION.ERROR;
        } else if (current_loc.startsWith("WL") && isNA(curent_elevationcode)) {
            message += "Water level values require an elevation code. Please select a Elevation Code designator manually. ";
            spin_elev_code.requestFocus();
            isValid = VALIDDATION.ERROR;
        } else if (reading == 0.0 && curent_eo.equalsIgnoreCase("NotOper")) {
            String im1 = "A Reading value of 0, together with a 'NotOper' Equip Oper Status indicates a non-valid reading.";
            message += im1;
            isValid = VALIDDATION.WARNING;
            txt_Reading.requestFocus();

        } else if (reading == 0.0 && !curent_eo.equalsIgnoreCase("NotOper")) {
            message += "A Reading value of 0 is detected!";
            txt_Reading.requestFocus();
            //to do not valid reeading confirm
            String[] innermessage = new String[]{""};
            isValid = VALIDDATION.ERROR;
            message += innermessage[0];
        } else {
            String[] innermessage = new String[]{""};
            isValid = isReadingWithinRange(reading, innermessage);
            message += innermessage[0];
        }

        error_message[0] = message;
        return isValid;

    }

    public VALIDDATION isReadingWithinRange(Double reading, String[] error_message) {

        VALIDDATION isValid = VALIDDATION.VALID;
        String message = "";

        // returning the record is valid if the value in the database for loc_min or loc_max is wrong or empty string

        if (locMin =="" || locMax ==""){
            error_message[0] = "No valid records for loc_min or loc_max in the database";
            return VALIDDATION.VALID;

        }
        double min = 0.0, max = 0.0, val = 0.0;
        try {
            min = Double.parseDouble(locMin);
        } catch (Exception ignored) {
            error_message[0] = "No valid records for loc_min or loc_max in the database";
              return VALIDDATION.VALID;
        }
        try {
            max = Double.parseDouble(locMax);
        } catch (Exception ignored) {
            error_message[0] = "No valid records for loc_min or loc_max in the database";
            return VALIDDATION.VALID;
        }

        //Cursor.Current = Cursors.WaitCursor;
        try {
            //NOTE: We can no longer range check flow totalizers now that we switched to location characteristics
            if (current_loc.startsWith("FT"))    //if this is a water level location
            {
                if (reading < 0) {
                    message = "The Reading value is not a positive number!";
                    isValid = VALIDDATION.ERROR;
                } //else
                    //isValid = VALIDDATION.VALID;
            }

            if (min == UNDEFINED || max == UNDEFINED)   // no defined range
                isValid = VALIDDATION.ERROR;
            else if (reading >= min && reading <= max)          // within bounds
                isValid = VALIDDATION.VALID;
            else {
                message = "The Reading value falls outside the defined range: " + locMin + ".." + locMax;
                isValid = VALIDDATION.WARNING;
                System.out.println(message);
            }
            //Cursor.Current = Cursors.Default;
        } catch (Exception ex) {
            txt_Reading.requestFocus();
        }
        System.out.println("Within range message " + message);
        error_message[0] = message;
        return isValid;
    }

    private void AlertDialogShow(String message, String title){
        AlertDialogShow(message, title, "OK");
    }
    private void AlertDialogShow(String message, String title, String button) {
        AlertDialog ad = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
        try {
            wait(10);
        } catch (Exception ignored) {
        }
    }


    @Override
    public void onBackPressed() {

        if (!bSavedToDBData)
            btnDone.performClick();
        else
            super.onBackPressed();
    }



}