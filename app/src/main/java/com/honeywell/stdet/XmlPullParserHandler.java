package com.honeywell.stdet;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;



public class XmlPullParserHandler {

    String text;
    StringBuilder builder = new StringBuilder();

    public String parse(InputStream is) {

        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("YOUR_TAG1")) {
                            builder.append("TAG1 = " + text);
                        } else if (tagname.equalsIgnoreCase("YOUR_TAG2")) {
                            builder.append("\nTAG2 = " + text);
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }


}