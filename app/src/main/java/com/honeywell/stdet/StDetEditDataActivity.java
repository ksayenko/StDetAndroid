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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.UnsupportedPropertyException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.cursoradapter.widget.SimpleCursorAdapter;



public class StDetEditDataActivity extends Activity {


    Reading input_reading;

    private TextView txt_COL_ID;
    private TextView txt_Loc_id;
    private Spinner spin_FAC_OP;
    private Spinner spin_UNITS;
    private Spinner spin_EQ_OP;
    private Spinner spin_elev_code;
    private TextView txt_elev_code2;
    private EditText edit_depth;

    private TextView txt_DateTime;
    private EditText txt_Reading;

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
    String strDateTime = "";
    Integer lngid;


    Button btnInputForms;
    public HandHeld_SQLiteOpenHelper dbHelper;
    public SQLiteDatabase db;
    Button btnUpdate;
    Button btnCancel;


    Context ct = this;
    Boolean bSavedToDBData = false;
    Boolean bAcceptWarning = false;

    //private Stdet_Inst_Readings ir_table =  new Stdet_Inst_Readings();

    Boolean[] bDialogChoice = {false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bSavedToDBData = false;
        bAcceptWarning = false;
        input_reading = new Reading();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            System.out.println("we have Input reading");
            input_reading = (Reading) getIntent().getSerializableExtra("IR");
        }
        else {
            System.out.println("no default reading");
            //input_reading = Reading.GetDefaultReading();
        }

        current_loc = input_reading.getStrD_Loc_ID();
        current_collector = input_reading.getStrD_Col_ID();
        strDataModComment = input_reading.getStrDataModComment();
        curent_eo = input_reading.getStrEqO_StatusID();
        curent_fo = input_reading.getStrFO_StatusID();
        curent_elevationcode =input_reading.getElev_code();
        current_comment = input_reading.getStrComment();
        current_reading =input_reading.getDblIR_Value();
        current_unit = input_reading.getStrIR_Units();
        strDateTime = input_reading.getDatIR_Date();
        lngid = input_reading.getLngID();

        Log.i("------------onCreate StDetInputActivity", "10");
        super.onCreate(savedInstanceState);
        Log.i("------------onCreate StDetInputActivity", "1");
        setContentView(R.layout.activity_edit_forms);

        //((TextView)findViewById(R.id.txtActivityTitle)).setText("Input Form");
        StdetDataTables tables = new StdetDataTables();
        tables.SetStdetTablesStructure();

        dbHelper = new HandHeld_SQLiteOpenHelper(ct, tables);
        db = dbHelper.getReadableDatabase();

        int rowsInDB = dbHelper.getRowsInLookupTables(db);
        if (rowsInDB < 1) {
            AlertDialogShow("The Lookup Tables aren't populated, go to Menu | Download and Populate Lookup DB","ERROR!");
        }

        Units = dbHelper.getUnits(db, "");
        alUnits = transferCursorToArrayList(Units);

        Eq_Oper_Status = dbHelper.getEOS(db);
        alEq_Oper_Status = transferCursorToArrayList(Eq_Oper_Status);
        Fac_Oper_Status = dbHelper.getFOS(db);
        alFac_Oper_Status = transferCursorToArrayList(Fac_Oper_Status);
        Elev = dbHelper.getElevationCodes(db);
        alElev = transferCursorToArrayList(Elev);

        txt_COL_ID = (TextView) findViewById(R.id.txt_COL_ID);
        txt_Loc_id = (TextView) findViewById(R.id.txt_Loc_id);
        txt_DateTime = (TextView) findViewById(R.id.txt_date);
        spin_FAC_OP = (Spinner) findViewById(R.id.spin_Fac_oper);
        spin_UNITS = (Spinner) findViewById(R.id.spin_Unit);
        spin_EQ_OP = (Spinner) findViewById(R.id.spin_Eq_oper);
        txt_elev_code2 = (TextView) findViewById(R.id.lbl_elev_code_desc);
        spin_elev_code = (Spinner) findViewById(R.id.spin_elev_code);
        edit_depth = (EditText) findViewById(R.id.text_depth);
        edit_depth.setEnabled(false);
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


        btnUpdate = (Button) findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(" btnUpdate.setOnClickListener " + bAcceptWarning);
                Reading.VALIDATION iChecked = saveForms(bAcceptWarning);

                if (iChecked == Reading.VALIDATION.WARNING)
                    bAcceptWarning = true;
                else if (iChecked == Reading.VALIDATION.VALID ||
                        (bAcceptWarning && iChecked == Reading.VALIDATION.WARNING) ) {
                    dbHelper.getUpdateReading(db, input_reading);

                    AlertDialog.Builder alert = new AlertDialog.Builder(ct);
                    alert.setTitle("Success!");
                    alert.setMessage("The Record got Updated for location " + input_reading.getStrD_Loc_ID());
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    alert.show();
                }
                System.out.println(bAcceptWarning);
                System.out.println(iChecked);
            }
        });

        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
                if (elev_code_value != null && elev_code_value[1] != null)
                    edit_depth.setText(elev_code_value[1]);
            }


            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        int[] toL = new int[]{android.R.id.text1};
        String[] fromFO = new String[]{Stdet_Fac_Oper_Def.strFO_StatusID};
        String[] fromEO = new String[]{Stdet_Equip_Oper_Def.strEqO_StatusID};
        String[] fromU = new String[]{Stdet_Unit_Def.strUnitsID};
        String[] fromEl = new String[]{Stdet_Elevation_Codes.elev_code};

      txt_Loc_id.setText(current_loc);
      txt_COL_ID.setText(current_collector);
      txt_DateTime.setText(input_reading.getDatIR_Date());
      txt_Reading.setText(input_reading.getDblIR_Value());
      txt_comment.setText(current_comment);

        SimpleCursorAdapter adFO =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, Fac_Oper_Status, fromFO, toL, 0);
        adFO.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_FAC_OP.setAdapter(adFO);

        SimpleCursorAdapter adEO =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, Eq_Oper_Status, fromEO, toL, 0);
        adEO.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_EQ_OP.setAdapter(adEO);

        SimpleCursorAdapter adU =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, Units, fromU, toL, 0);
        adU.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_UNITS.setAdapter(adU);

        SimpleCursorAdapter adelev =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, Elev, fromEl, toL, 0);
        adelev.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_elev_code.setAdapter(adelev);

        String[] elev_code_value = dbHelper.getElevationCodeValue(db, current_loc,curent_elevationcode);
        if (elev_code_value != null && elev_code_value[1] != null) {
            System.out.println("current_loc  " + current_loc);
            System.out.println("current_loc  " + elev_code_value[0]);
            edit_depth.setText(elev_code_value[1]);
        }

        setSpinnerValue(spin_elev_code,alElev, elev_code_value[0]);
        setSpinnerValue(spin_UNITS,alUnits, current_unit);
        setSpinnerValue(spin_EQ_OP,alEq_Oper_Status, curent_eo);
        setSpinnerValue(spin_FAC_OP,alFac_Oper_Status, curent_fo);

        // set lock the orientation
        // otherwise, the onDestory will trigger when orientation changes
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        }

    public void onDestroy() {
        super.onDestroy();
        db.close();

    }

    private void setSpinnerValue(Spinner spinner, ArrayList<String[]> strValues, String strValue){
        int index = getIndexFromArraylist(strValues, strValue, 1);
        spinner.setSelection(index);
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


    public Reading.VALIDATION saveForms(boolean bAcceptWarning) {

        TextView temp;
        temp = (TextView) spin_EQ_OP.getSelectedView();
        curent_eo = temp.getText().toString();
        input_reading.setStrEqO_StatusID(curent_eo);

        temp = (TextView) spin_FAC_OP.getSelectedView();
        curent_fo = temp.getText().toString();
        input_reading.setStrFO_StatusID(curent_fo);

        temp = (TextView) spin_UNITS.getSelectedView();
        current_unit = temp.getText().toString();
        input_reading.setStrIR_Units(current_unit);

        current_reading = txt_Reading.getText().toString();
        input_reading.setDblIR_Value(current_reading);

        current_comment = txt_comment.getText().toString();
        input_reading.setStrComment(current_comment);

        temp = (TextView) spin_elev_code.getSelectedView();
        curent_elevationcode = temp.getText().toString();
        input_reading.setElev_code(curent_elevationcode);

        String[] error_message = new String[]{""};
        Reading.VALIDATION bresult = isRecordValid(error_message);
        if (bresult == Reading.VALIDATION.ERROR) {
            AlertDialogShow(error_message[0],"ERROR");
        }
        else if (bresult == Reading.VALIDATION.WARNING && !bAcceptWarning ) {
            AlertDialogShow("Please check\n" + error_message[0] +"\nPress 'Save' one more time to confirm the data as VALID or update the input data.","Warning");
        }
        else if (bresult == Reading.VALIDATION.VALID|| (bresult == Reading.VALIDATION.WARNING && bAcceptWarning) ) {
            System.out.println(error_message[0]);
            bresult = Reading.VALIDATION.VALID;
         }
        System.out.println( " RESULT " + bresult);
        return bresult;
    }


    public Reading.VALIDATION isRecordValid(String[] error_message) {
        String message = "";
        String[] focus = new String[]{""};
        Reading.VALIDATION isValid = Reading.VALIDATION.VALID;
        double reading;
        try {
            reading = Double.parseDouble(current_reading);
        } catch (Exception ex) {
            reading = 0.0;
        }

        isValid= input_reading.isRecordValid(error_message,focus);

        if (isValid!= Reading.VALIDATION.VALID && focus[0] != null) {
            if (focus[0].startsWith("R"))
                txt_Reading.requestFocus();
            else if (focus[0].startsWith("E"))
                spin_elev_code.requestFocus();
        }


        return isValid;

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


}
