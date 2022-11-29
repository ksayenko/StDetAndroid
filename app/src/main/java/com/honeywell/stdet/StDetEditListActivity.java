package com.honeywell.stdet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Objects;

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
    HorizontalScrollView scrView;
    HorizontalScrollView scrViewHeader;
    boolean bDataExists = false;

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

        StdetDataTables tables = new StdetDataTables();
        tables.SetStdetTablesStructure();

        dbHelper = new HandHeld_SQLiteOpenHelper(ct, tables);
        db = dbHelper.getReadableDatabase();

        int rowsInDB = dbHelper.getRowsInLookupTables(db);
        if (rowsInDB < 1) {
            AlertDialogShow("The Lookup Tables aren't populated, go to Menu | Download and Populate Lookup DB","ERROR!");
        }


        currentRowSelected = -1;
        selectedLngID = "";
        System.out.println("in StDetEditListActivity");
        setContentView(R.layout.activity_stdet_editlist);
        tableLayoutHeader = findViewById(R.id.table_layout_header);
        tableLayout = findViewById(R.id.table_layout);
        scrView = findViewById(R.id.hor_scroll_view);
        scrViewHeader = findViewById(R.id.hor_scroll_view_header);

        scrView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                final int newScrollX = scrollX;
                if(scrollX != oldScrollX) {
                    scrViewHeader.post(  new Runnable() {
                        public void run() {
                            scrViewHeader.scrollTo(newScrollX, 0);
                        }
                    });
                }
            }});

        btnEdit =findViewById(R.id.btn_edit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("------------onClick btnEdit", "12");
                Reading r = null;
                if (!Objects.equals(selectedLngID, "")) {
                    r = dbHelper.getReading(db, selectedLngID);
                }
                if (r == null) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(ct);
                    alert.setTitle("Select a record to edit entry");

                    alert.setMessage("Please Select a Record to edit ");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    alert.show();
                }
                else {
                    Log.i("------------onClick StDetInputActivity", "12");
                    // get the intent action string from AndroidManifest.xml
                    Intent barcodeIntent = new Intent("android.intent.action.STDETEDITACTIVITY");
                    barcodeIntent.putExtra("IR", r);
                    startActivity(barcodeIntent);

                }
               System.out.println("In StDetEditListActivity btnEdit.setOnClickListener " + selectedLngID);
            }
        });

        btnDelete=findViewById(R.id.btn_update);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("------------onClick btnDelete", "s");
                System.out.println("In StDetEditListActivity btnDelete.setOnClickListener " + selectedLngID);

                AlertDialog.Builder alert = new AlertDialog.Builder(ct);
                alert.setTitle("Delete entry");

                alert.setMessage("Are you sure you want to delete a record "
                        + String.valueOf(currentRowSelected+1) +"? ");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        dbHelper.deleteRecords(db, selectedLngID);
                        Cursor cursor_list = dbHelper.getIRRecordsShortList(db);
                        fillData(cursor_list);
                        finish();
                        startActivity(getIntent());
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });
                alert.show();
            }

        });
        btnDone =findViewById(R.id.btn_cancel);
        btnDone.setOnClickListener(new View.OnClickListener() {
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


        Cursor cursor_list = dbHelper.getIRRecordsShortList(db);

        createColumns();
        fillData(cursor_list);
        tableLayout.post(new Runnable() {
            @Override
            public void run() {
                makeHeaderTableColumnsEven();
            }
        });


    }

    private void makeHeaderTableColumnsEven() {
        if (bDataExists) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(0);
            TableRow headerRow = (TableRow) tableLayoutHeader.getChildAt(0);
            for (int i = 0; i < headerRow.getChildCount(); i++) {
                int wHeaderRow = headerRow.getChildAt(i).getMeasuredWidth();
                int wTableRow = tableRow.getChildAt(i).getMeasuredWidth();
                int w = (wHeaderRow > wTableRow) ? wHeaderRow : wTableRow;
                if (wHeaderRow < wTableRow) {
                    headerRow.getChildAt(i).setLayoutParams(new TableRow.LayoutParams(w,
                            headerRow.getChildAt(i).getMeasuredHeight()));
                } else {
                    tableRow.getChildAt(i).setLayoutParams(new TableRow.LayoutParams(w,
                            tableRow.getChildAt(i).getMeasuredHeight()));
                }

            }
        }
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
        bDataExists = false;
        if (list.getCount()>0)
            bDataExists = true;
        for (list.moveToFirst(); !list.isAfterLast(); list.moveToNext()) {
            {
                rowData = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT);

                lp.setMargins(1,1,1,10);
                rowData.setLayoutParams(lp);
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
                        TableRow rowUnselect = null;
                        if (currentRowSelected > -1)
                            rowUnselect = getRowById(currentRowSelected);
                        if (rowUnselect != null && !Objects.equals(currentRowSelected, finalI))
                            rowUnselect.setBackground(color2);
                        currentRowSelected = finalI;
                        selectedLngID = lngId;
                        //Toast.makeText(ct, "Selected " + selectedLngID, Toast.LENGTH_SHORT).show();
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
                //the max digits after 55.9897384643555
                DecimalFormat df = new DecimalFormat("#.################");
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
                    textViewID.setText(String.valueOf(i+1));//list.getString(0));
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
                    //reading
                    double dreading = list.getDouble(3);
                    textViewReading.setText(df.format(dreading));
                }
                if (list.getString(4) != null) {
                    //lng id
                    textViewlngId.setText(list.getString(4));
                }


                tableLayout.addView(rowData, new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));
                i++;

            }
        }

    }


    private TableRow getRowById(int j) {
        TableRow row = null;
       if (j >= 0 && j < tableLayout.getChildCount())
            row = (TableRow) tableLayout.getChildAt(j);
        return row;
    }

    private String getSelectedLngID(int j) {
        TableRow row = null;
        String lngId = "";
        String one = "";
       if (j >=0 && j < tableLayout.getChildCount()) {
            row = (TableRow) tableLayout.getChildAt(j);
            one = ((TextView) row.getChildAt(1)).getText().toString();
            System.out.println("Loc_id " + one);
            TextView textLngId = (TextView) row.getChildAt(4);
            lngId = (String) textLngId.getText();
        }
        return lngId;
    }

    public void onDestroy() {
        super.onDestroy();
            db.close();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
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
}