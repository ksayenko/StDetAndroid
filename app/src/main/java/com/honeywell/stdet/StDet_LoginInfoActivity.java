package com.honeywell.stdet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StDet_LoginInfoActivity extends Activity {
    private EditText txt_UserName;
    private EditText txt_Password;
    String name ;
    String pwd ;
    String encryptedPassword;
    Button btnDone;
    Button btnCheck;
    CallSoapWS soap;

    Stdet_LoginInfo loginInfo = new Stdet_LoginInfo();
    public  HandHeld_SQLiteOpenHelper dbHelper;
    public SQLiteDatabase db;
    Context ct = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i("------------onCreate StDet_LoginInfoActivity", "");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        //---------------
        StdetDataTables tables= new StdetDataTables();
        tables.SetStdetTablesStructure();

        dbHelper =  new HandHeld_SQLiteOpenHelper(ct,tables);
        db = dbHelper.getReadableDatabase();
        btnDone= (Button) findViewById(R.id.btnSaveLogin);
        txt_Password= (EditText) findViewById(R.id.editTextPassword);
        txt_UserName= (EditText) findViewById(R.id.editName);


        String[] credentials = dbHelper.getLoginInfo(db);
        if (credentials[0] !="")        {
            name = credentials[0];
            encryptedPassword = credentials[1];
            try {
                pwd = StDEtEncrypt.decrypt(encryptedPassword);

                System.out.println(pwd);
            } catch (Exception e) {
                e.printStackTrace();
                pwd ="";
            }
        }
        else {
            name = "Fill the username";
            pwd = "";
        }
        txt_UserName.setText(name);
        txt_Password.setText(pwd);

        soap = new CallSoapWS(null);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean bSave = false;
                name = txt_UserName.getText().toString();
                pwd = txt_Password.getText().toString();
                bSaveLoginInfo();

            }
        });

        btnDone = (Button) findViewById(R.id.btnCheckConnectivity);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              boolean bCheck =bCheckLogin();
              if (!bCheck)
                  txt_Password.setText("");
            }
        });

    }

    private Boolean updateDBWithNewInformation() {
        Boolean rv = false;
        try {
            encryptedPassword = StDEtEncrypt.encrypt(pwd);
            System.out.println("encripted " + encryptedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error in encryption");
            encryptedPassword = "";
        }

        if (name != "" && encryptedPassword != ""){
            dbHelper.updateLoginInformationInDB(db,name,encryptedPassword);
            db.close();
            rv = true;
        }
        return rv;
    }

    private void bSaveLoginInfo() {
        AlertDialog ad = new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle("Do You Want To Save Login Info?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {

                        Boolean b = updateDBWithNewInformation();
                        if (b) {
                            Toast.makeText(ct, "Updated ", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private boolean bCheckLogin() {
        Boolean bReturnValue = false;
        name = txt_UserName.getText().toString();
        pwd = txt_Password.getText().toString();
        bReturnValue = soap.WS_GetLogin(name, pwd);
        if (bReturnValue) {
            AlertDialog ad = new AlertDialog.Builder(this)
                    .setTitle("Success!")
                    .setMessage("Connection tested")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
        } else {
            AlertDialog ad = new AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage("Please try one more time.")
                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
        }

        return bReturnValue;
    }

    public void ShowHidePass(View view) {
        //if(view.getId()==R.id.show_pass_btn){

            if(txt_Password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                //((ImageView(view)).setImageResource(R.drawable.icons_showhide_password);

                //Show Password
                txt_Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
               // ((ImageView)(view)).setImageResource(R.drawable.icons_showhide_password);

                //Hide Password
                txt_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        //}
    }
}