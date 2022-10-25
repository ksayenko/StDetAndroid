package com.honeywell.stdet;


import android.app.Activity;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.util.Xml;
import android.view.View;

public class StdetFiles  {

    private File directoryApp;
    //View view;
    public File GetDirectory(){
        return directoryApp;
    }
    public final String NAMESPACE = "http://api.limstor.com/stdet/wsstdet.asmx";


    public StdetFiles(File dir){
        //view =v;
        directoryApp = dir;
    }

    public boolean WriteServerDateAsXML(String data, String filename, String opentag, String closetag){
        File newXml = null;

        try {

            if (!directoryApp.exists())
                directoryApp.mkdir();

            newXml = new File(directoryApp + "/" + filename);

            FileOutputStream fos;
            String fullfilename = newXml.getAbsolutePath().toString();
            fos = new FileOutputStream(fullfilename);

            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "UTF-8");
            serializer.startDocument("UTF-8", null);
            serializer.startTag(NAMESPACE,opentag);

            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            serializer.endDocument();
            serializer.text(data);
            serializer.endTag(NAMESPACE,closetag);

            serializer.flush();
            fos.close();
        } catch (IOException exception) {
            System.out.println(exception.toString());

            return false;
        } catch (Exception exception) {
            System.out.println(exception.toString());
            return false;
        }
        return true;
    }

    public StdetDataTable WriteXMLDataAndCreateSTDETable(String data, String filename) {
        File newXml = null;
        StdetDataTable table = null;

        try {

            if (!directoryApp.exists())
                directoryApp.mkdir();

            newXml = new File(directoryApp + "/" + filename);

            FileOutputStream fos;
            String fullfilename = newXml.getAbsolutePath().toString();
            data = "<?xml version='1.0' encoding='UTF-8' ?>" + data;
            data = data.replace("</soap:Body>","");
            data = data.replace("<soap:Body>","");
            data = data.replace("</soap:Envelope>","");
            data = data.replace("<xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">","");
            XmlPullParserHandler parser = new XmlPullParserHandler();



            fos = new FileOutputStream(fullfilename);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fos);
            myOutWriter.append(data);

            myOutWriter.close();

            fos.flush();
            fos.close();
            FileInputStream input = new FileInputStream(fullfilename);
            table = HandHeldDomParser.XMLParse(fullfilename, filename);
            input.close();

          /*
            XmlSerializer serializer = Xml.newSerializer();

            serializer.setOutput(fos, "UTF-8");
            serializer.startDocument("UTF-8", null);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.endDocument();
            serializer.text(handleEscapeCharacter(data));
            serializer.flush();
           */

        } catch (IOException exception) {
            exception.printStackTrace();
            System.out.println(exception.toString());

            return null;
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println(exception.toString());
            return null;
        }
        return table;
    }

    public void WriteXMLData(String data, String filename) {
        File newXml = null;
        StdetDataTable table = null;

        try {

            if (!directoryApp.exists())
                directoryApp.mkdir();

            newXml = new File(directoryApp + "/" + filename);

            FileOutputStream fos;
            String fullfilename = newXml.getAbsolutePath().toString();
            data = "<?xml version='1.0' encoding='UTF-8' ?>" + data;
            data = data.replace("</soap:Body>","");
            data = data.replace("<soap:Body>","");
            data = data.replace("</soap:Envelope>","");
            data = data.replace("<xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">","");
            XmlPullParserHandler parser = new XmlPullParserHandler();



            fos = new FileOutputStream(fullfilename);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fos);
            myOutWriter.append(data);

            myOutWriter.close();

            fos.flush();
            fos.close();
            FileInputStream input = new FileInputStream(fullfilename);



          /*
            XmlSerializer serializer = Xml.newSerializer();

            serializer.setOutput(fos, "UTF-8");
            serializer.startDocument("UTF-8", null);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.endDocument();
            serializer.text(handleEscapeCharacter(data));
            serializer.flush();
           */

        } catch (IOException exception) {
            exception.printStackTrace();
            System.out.println(exception.toString());

        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println(exception.toString());

        }

    }

    public StdetDataTable ReadXMLToSTDETable(String filename) {
        File newXml = null;
        StdetDataTable table = null;

        try {

            if (!directoryApp.exists())
                directoryApp.mkdir();

            newXml = new File(directoryApp + "/" + filename);


            String fullfilename = newXml.getAbsolutePath().toString();

            FileInputStream input = new FileInputStream(fullfilename);
            table = HandHeldDomParser.XMLParse(fullfilename, filename);
            input.close();


        } catch (IOException exception) {
            exception.printStackTrace();
            System.out.println(exception.toString());

            return null;
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println(exception.toString());
            return null;
        }
        return table;
    }

    public StdetDataTables ReadXMLToSTDETables() {
        File newXml = null;
        StdetDataTables tables = new StdetDataTables();

        try {

            if (!directoryApp.exists())
                directoryApp.mkdir();


            tables.AddStdetDataTable(new Stdet_Inst_Readings());
            tables.AddStdetDataTable(ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.UNIT_DEF + ".xml"));
            tables.AddStdetDataTable(ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.FACILITY + ".xml"));
            tables.AddStdetDataTable(ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.DATA_COL_IDENT + ".xml"));
            tables.AddStdetDataTable(ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.ELEVATIONS + ".xml"));
            tables.AddStdetDataTable(ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.DCP_LOC_CHAR + ".xml"));
            tables.AddStdetDataTable(ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.DCP_LOC_DEF + ".xml"));
            tables.AddStdetDataTable(ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.EQUIP_OPER_DEF + ".xml"));
            tables.AddStdetDataTable(ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.FAC_OPER_DEF + ".xml"));
            tables.AddStdetDataTable(ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.TABLEVERS + ".xml"));
            tables.AddStdetDataTable(ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.ELEVATIONCODES + ".xml"));
            tables.AddStdetDataTable(ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.EQUIP_OPER_DEF + ".xml"));
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println(exception.toString());
            return null;
        }
        return tables;
    }


    public String handleEscapeCharacter( String strOrig ) {
        String[] escapeCharacters = {
                "&gt;", "&lt;", "&amp;", "&quot;", "&apos;" };
        String[] onReadableCharacter = {">", "<", "&", "\"\"", "'"};
        String str = strOrig;
        String stringFixed = str;
        for (int i = 0; i < escapeCharacters.length; i++) {
            str = str.replace(escapeCharacters[i], onReadableCharacter[i]);
        }
        int a = 2;
        return str;


    }
}
