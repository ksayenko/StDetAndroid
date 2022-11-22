package com.honeywell.stdet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.database.Cursor;

import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class StDetEditListActivity extends Activity {

    private TableLayout tableLayout;
    private TableLayout tableLayoutHeader;
    public HandHeld_SQLiteOpenHelper dbHelper;
    public SQLiteDatabase db;
    Context ct = this;
    private TableRow rowData;
    ColorDrawable color1;
    ColorDrawable color2;
    ColorDrawable colorSel;
    Integer currentRowSelected = -1;
    String selectedLngID = "";
    Button btnEdit;
    Button btnDelete;
    Button btnDone;

    @Override
    public void onBackPressed() {
        // do something on back.
        super.onBackPressed();
       this.finish();
    }
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentRowSelected = -1;
        selectedLngID = "";
        System.out.println("in StDetEditListActivity");
        setContentView(R.layout.activity_stdet_editlist);
        tableLayoutHeader = findViewById(R.id.table_layout_header);
        tableLayout = findViewById(R.id.table_layout);

        btnEdit =findViewById(R.id.btn_edit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("------------onClick btnEdit", "12");
               System.out.println("In StDetEditListActivity btnEdit.setOnClickListener " + selectedLngID);
            }
        });

        btnDelete=findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("------------onClick btnDelete", "12");
                System.out.println("In StDetEditListActivity btnDelete.setOnClickListener " + selectedLngID);
            }
        });
        btnDone =findViewById(R.id.btn_done2);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("------------onClick btnDone", "12");
                System.out.println("In StDetEditListActivity btnDelete.btnDone " + selectedLngID);
                onBackPressed();
            }
        });


        color1 = new ColorDrawable(ContextCompat.getColor(this, R.color.greenblue1));
        color2 = new ColorDrawable(ContextCompat.getColor(this, R.color.lightblue));
        colorSel = new ColorDrawable(ContextCompat.getColor(this, R.color.brightblue));


        StdetDataTables tables = new StdetDataTables();
        tables.SetStdetTablesStructure();

        dbHelper = new HandHeld_SQLiteOpenHelper(ct, tables);
        db = dbHelper.getReadableDatabase();
        Cursor cursor_list = dbHelper.getIRRecordsShortList(db);

        createColumns();
        fillData(cursor_list);


    }

    private void createColumns() {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        row.setBackground(color1);

        //ID
        TextView textViewID = new TextView(this);
        textViewID.setText("R#");
        textViewID.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewID.setPadding(5, 5, 5, 0);
        row.addView(textViewID);

        //Loc id
        TextView textViewLocID = new TextView(this);
        textViewLocID.setText("Loc Id");
        textViewLocID.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewLocID.setPadding(5, 5, 5, 0);

        row.addView(textViewLocID);

        //Loc id
        TextView textViewDT = new TextView(this);
        textViewDT.setText("Date Time");
        textViewDT.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewDT.setPadding(5, 5, 5, 0);

        row.addView(textViewDT);

        //Reading
        TextView textViewReading = new TextView(this);
        textViewReading.setText("Reading");
        textViewReading.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewReading.setPadding(5, 5, 5, 0);

        row.addView(textViewReading);

        //lngId hidden
        TextView textViewlngId = new TextView(this);
        textViewlngId.setText("lngid");
        textViewlngId.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewlngId.setPadding(5, 5, 5, 0);
        textViewlngId.setVisibility(View.INVISIBLE);

        row.addView(textViewlngId);

        tableLayoutHeader.addView(row, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }

    private void fillData(Cursor list) {
        int i = 0;

        for (list.moveToFirst(); !list.isAfterLast(); list.moveToNext()) {
            {
                i++;
                rowData = new TableRow(this);
                rowData.setLayoutParams(new TableLayout.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                rowData.setBackground(color2);
                rowData.setSelected(true);
                final Integer finalI = i;
                rowData.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TableRow row = getRowById(finalI);
                        String lngId = getSelectedLngID(finalI);
                        if (row != null)
                            row.setBackground(colorSel);
                        //unselect
                        TableRow rowUnselect = getRowById(currentRowSelected);
                        if (rowUnselect !=null)
                            rowUnselect.setBackground(color2);
                        currentRowSelected = finalI;
                        selectedLngID = lngId;
                         Toast.makeText(ct, "Selected " + selectedLngID, Toast.LENGTH_SHORT).show();
                    }
                });


                //ID
                TextView textViewID = new TextView(this);
                textViewID.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                textViewID.setPadding(5, 5, 5, 0);
                rowData.addView(textViewID);


                //Loc id
                TextView textViewLocID = new TextView(this);
                textViewLocID.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                textViewLocID.setPadding(5, 5, 5, 0);

                rowData.addView(textViewLocID);

                //Loc id
                TextView textViewDT = new TextView(this);

                textViewDT.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                textViewDT.setPadding(5, 5, 5, 0);

                rowData.addView(textViewDT);

                //Reading
                TextView textViewReading = new TextView(this);
                textViewReading.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                textViewReading.setPadding(5, 5, 5, 0);
                rowData.addView(textViewReading);

                //lngId hidden
                TextView textViewlngId = new TextView(this);
                textViewlngId.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                textViewlngId.setPadding(5, 5, 5, 0);
                textViewlngId.setVisibility(View.INVISIBLE);
                rowData.addView(textViewlngId);

                if (list.getString(0) != null) {
                    //rowid
                    textViewID.setText(String.valueOf(i));//list.getString(0));
                }

                if (list.getString(1) != null) {
                    //locid
                    textViewLocID.setText(list.getString(1));
                }
                if (list.getString(2) != null) {
                    //datetime
                    textViewDT.setText(list.getString(2));
                }
                if (list.getString(3) != null) {
                    //readign
                    textViewReading.setText(list.getString(3));
                }
                if (list.getString(4) != null) {
                    //lng id
                    textViewlngId.setText(list.getString(4));
                }


                tableLayout.addView(rowData, new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));
            }
        }

    }


    private TableRow getRowById(int j) {
        TableRow row = null;
       if (j > 0 && j < tableLayout.getChildCount())
            row = (TableRow) tableLayout.getChildAt(j);
        return row;
    }

    private String getSelectedLngID(int j) {
        TableRow row = null;
        String lngId = "";
        String one = "";
       if (j > 0 && j < tableLayout.getChildCount()) {
            row = (TableRow) tableLayout.getChildAt(j);
            one = ((TextView) row.getChildAt(1)).getText().toString();
            System.out.println("Loc_id " + one);
            TextView textLngId = (TextView) row.getChildAt(4);
            lngId = (String) textLngId.getText();
        }
        return lngId;
    }
}