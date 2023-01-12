package com.honeywell.stdet;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;

public class HandHeldDomParser {


    public static StdetDataTable XMLParse(String fullfilename, String filename) {
        StdetDataTable data;
        String sName = "";
        String sValue = "";
        data = null;
        if (filename.equalsIgnoreCase(CallSoapWS.ELEVATIONS + ".xml")) {
            data = new Stdet_Elevations();
        } else if (filename.equalsIgnoreCase(CallSoapWS.FACILITY + ".xml")) {
            data = new Stdet_Facility();
        } else if (filename.equalsIgnoreCase(CallSoapWS.DATA_COL_IDENT + ".xml")) {
            data = new Stdet_Data_Col_Ident();
        } else if (filename.equalsIgnoreCase(CallSoapWS.DCP_LOC_CHAR + ".xml")) {
            data = new Stdet_DCP_Loc_Char();
        } else if (filename.equalsIgnoreCase(CallSoapWS.DCP_LOC_DEF + ".xml")) {
            data = new Stdet_DCP_Loc_Def();
        } else if (filename.equalsIgnoreCase(CallSoapWS.ELEVATIONCODES + ".xml")) {
            data = new Stdet_Elevation_Codes();
        } else if (filename.equalsIgnoreCase(CallSoapWS.EQUIP_OPER_DEF + ".xml")) {
            data = new Stdet_Equip_Oper_Def();
        } else if (filename.equalsIgnoreCase(CallSoapWS.TABLEVERS + ".xml")) {
            data = new Stdet_TableVers();
        } else if (filename.equalsIgnoreCase(CallSoapWS.FAC_OPER_DEF + ".xml")) {
            data = new Stdet_Fac_Oper_Def();
        } else if (filename.equalsIgnoreCase(CallSoapWS.UNIT_DEF + ".xml")) {
            data = new Stdet_Unit_Def();
        } else
            data = new StdetDataTable();

        int nRows = 0;
        int nCols = 0;

        if (data != null) {
            try {
                File inputFile = new File(fullfilename);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                System.out.println("Before dBuilder.parse(inputFile)"+ fullfilename);
                Document doc = dBuilder.parse(inputFile);
                doc.getDocumentElement().normalize();

                NodeList nListTable = doc.getElementsByTagName("Table");

                Element el = doc.getDocumentElement();
                NamedNodeMap nListMap = el.getAttributes();
                nRows = nListTable.getLength();

                for (Integer i = 0; i < nRows; i++) {
                    try {
                        Node nNode1 = nListTable.item(i);
                        //System.out.println("\nCurrent Element :" + i.toString() + " " + nNode1.getNodeName());
                        NamedNodeMap map = nNode1.getAttributes();
                        NodeList nList = nNode1.getChildNodes();

                        ArrayList<String> dRow = data.getEmptyDataRow();

                        nCols = nList.getLength();
                        for (Integer k = 0; k < nCols; k++) {

                            Node nNode = nList.item(k);
                            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element element = (Element) nNode;

                                String sk = k.toString();

                                sName = nList.item(k).getNodeName();
                                NodeList nlisttemp = element.getChildNodes();
                                sValue = "";
                                if (nlisttemp != null && nlisttemp.item(0) != null) {
                                    sValue = nlisttemp.item(0).getTextContent();
                                    sValue = sValue.trim();

                                    int iIndex = data.GetElementIndex(sName);
                                    if (iIndex > -1) {
                                        dRow.set(iIndex, sValue);
                                    }
                                } else
                                    continue;

                            }
                        }

                        data.AddRowToData(dRow);
                        dRow = data.getEmptyDataRow();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Error: System.out.println(e.toString());or inside the Row : " + filename + " " + sName + sValue);

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.toString());
            }
        }
        return data;
    }
}
