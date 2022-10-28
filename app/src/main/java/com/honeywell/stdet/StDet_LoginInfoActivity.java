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
import android.widget.TextView;

public class StDet_LoginInfoActivity extends Activity {
    private EditText txt_UserName;
    private EditText txt_Password;
    String name ;
    String pwd ;
    String encryptedPassword;
    Button btnDone;
    Button btnCheck;
    CallSoapWS soap;
    static int result =0;


    Stdet_LoginInfo loginInfo = new Stdet_LoginInfo();
    public  HandHeld_SQLiteOpenHelper dbHelper;
    public SQLiteDatabase db;
    Context ct = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i("------------onCreate StDet_LoginInfoActivity", "10");
        super.onCreate(savedInstanceState);
        Log.i("------------onCreate StDet_LoginInfoActivity", "1");
        setContentView(R.layout.activity_login);
        //---------------
        StdetDataTables tables= new StdetDataTables();
        tables.SetStdetTablesStructure();

        dbHelper =  new HandHeld_SQLiteOpenHelper(ct,tables);
        db = dbHelper.getReadableDatabase();
        btnDone= (Button) findViewById(R.id.btnSaveLogin);
        txt_Password= (EditText) findViewById(R.id.editTextPassword);
        txt_UserName= (EditText) findViewById(R.id.editName);
        String[] credentials = dbHelper.GetLogin(db);
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
                if (name != "" && pwd != "")
                    bSave = bSaveLoginInfo();
                System.out.println("bsave"+ String.valueOf(bSave));
                if (bSave) {
                    System.out.println("encrypting and saving to db");
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
                    }
                }

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

    private boolean bSaveLoginInfo() {

         Boolean bReturnValue = false;

        System.out.print("BEFORE ok result :: "+ String.valueOf(result));

        final EditText answer = new EditText(this);
        AlertDialog ad = new AlertDialog.Builder(this,R.style.AlertDialogTheme)
                .setTitle("Do You Want To Save Login Info?")
                //.setMessage("Do You Want To Save Login Info?!")
                //.setView(answer)
                .setPositiveButton("Yes"  , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {

                        StDet_LoginInfoActivity.result =1;
                        System.out.print("AlertDialog ok result :: "+ String.valueOf(result));

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        result =0;
                        dialog.cancel();
                    }
                })
                .show();
        System.out.print("after AlertDialog ok : "+ String.valueOf(result));
        if (StDet_LoginInfoActivity.result>0){
            bReturnValue = true;
            StDet_LoginInfoActivity.result = 0;
        }

        return bReturnValue;

    }

    private boolean bCheckLogin()
    {
        Boolean bReturnValue = false;
        name = txt_UserName.getText().toString();
        pwd = txt_Password.getText().toString();
        bReturnValue = soap.WS_GetLogin(name,pwd);
        if (bReturnValue) {
            AlertDialog ad = new AlertDialog.Builder(this)
                    .setTitle("Success!")
                    .setMessage("Connection tested")
                    //.setView(answer)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //String url = answer.getText().toString();

                        }
                    })
                    .show();
        }
        else
        {
            AlertDialog ad = new AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage("Please try one more time.")
                    //.setView(answer)
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