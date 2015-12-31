package com.mifos.mifosxdroid.createnewclient.data;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class AadharDAO {
    AadharDetail ad = new AadharDetail();

    public AadharDetail parseXml(String content) {
        String sStringToParse;

// put your XML value into the sStringToParse variable
        sStringToParse = new String(content);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new ByteArrayInputStream(sStringToParse.getBytes("utf-8")));
            NodeList nlRecords = doc.getElementsByTagName("PrintLetterBarcodeData");

            int num = nlRecords.getLength();

            for (int i = 0; i < num; i++) {
                Element node = (Element) nlRecords.item(i);

                // get a map containing the attributes of this node
                NamedNodeMap attributes = node.getAttributes();

                // get the number of nodes in this map
                int numAttrs = attributes.getLength();

                for (int j = 0; j < numAttrs; j++) {
                    Attr attr = (Attr) attributes.item(j);
                    String attrName = attr.getNodeName();
                    String attrValue = attr.getNodeValue();
                    setAadharDetails(attrName, attrValue);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ad;
    }

    //To set the parsed incomeValues to aadhar details
    public void setAadharDetails(String attrName, String attrValue) {
        if (attrName.equals("uid")) {
            ad.setUid(attrValue);
        }
        if (attrName.equals("name")) {
            ad.setName(attrValue);
        }
        if (attrName.equals("dob")) {
            ad.setDob(attrValue);
        }
        if (attrName.equals("gname")) {
            ad.setGname(attrValue);
        }
        if (attrName.equals("gender")) {
            ad.setGender(attrValue);

        }
        if (attrName.equals("yob")) {
            ad.setYob(attrValue);
        }
        if (attrName.equals("vtc")) {
            ad.setVtc(attrValue);
        }
        if (attrName.equals("po")) {
            ad.setPo(attrValue);
        }
        if (attrName.equals("pc")) {
            ad.setPc(attrValue);
        }
        if (attrName.equals("dist")) {
            ad.setDist(attrValue);
        }
        if (attrName.equals("state")) {
            ad.setState(attrValue);
        }
    }

}
