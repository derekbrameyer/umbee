package com.doomonafireball.umbee.util;

import com.doomonafireball.umbee.model.NoaaByDay;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Pair;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * User: derek Date: 6/4/12 Time: 5:16 PM
 */
public class XmlParser {

    public static NoaaByDay parseNoaaByDay(String xml) {
        NoaaByDay nbd = new NoaaByDay();
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();

            XMLReader xr = sp.getXMLReader();
            NoaaByDayHandler nbdh = new NoaaByDayHandler();
            xr.setContentHandler(nbdh);

            InputSource is = new InputSource();
            is.setEncoding("UTF-16");
            is.setCharacterStream(new StringReader(xml));

            xr.parse(is);

            return nbdh.getParsedData();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static class NoaaByDayHandler extends DefaultHandler {

        private boolean inPrecipTag = false;
        private boolean inNameTag = false;
        private boolean inValueTag = false;
        private boolean hasGottenValue1 = false;
        private int value1 = 0;

        private NoaaByDay myNbd = new NoaaByDay();

        public NoaaByDay getParsedData() {
            return this.myNbd;
        }

        @Override
        public void startDocument() throws SAXException {
            this.myNbd = new NoaaByDay();
            this.myNbd.mPop = new NoaaByDay.NoaaProbabilityOfPrecipitation();
            this.myNbd.mPop.probabilities = new ArrayList<Pair<Integer, Integer>>();
        }

        @Override
        public void endDocument() throws SAXException {
            // Nothing to do
        }

        @Override
        public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes atts) throws SAXException {
            super.startElement(uri, localName, qName, atts);
            if (localName.equals("probability-of-precipitation")) {
                this.inPrecipTag = true;
                String attrValue = atts.getValue("type");
                this.myNbd.mPop.type = attrValue;
                attrValue = atts.getValue("units");
                this.myNbd.mPop.units = attrValue;
                attrValue = atts.getValue("time-layout");
                this.myNbd.mPop.timeLayout = attrValue;
            } else if (localName.equals("name") && this.inPrecipTag) {
                this.inNameTag = true;
            } else if (localName.equals("value") && this.inPrecipTag) {
                this.inValueTag = true;
            }
        }

        @Override
        public void endElement(String namespaceURI, String localName, String qName)
                throws SAXException {
            if (localName.equals("probability-of-precipitation")) {
                this.inPrecipTag = false;
            } else if (localName.equals("name") && this.inPrecipTag) {
                this.inNameTag = false;
            } else if (localName.equals("value") && this.inPrecipTag) {
                this.inValueTag = false;
            }
        }

        @Override
        public void characters(char ch[], int start, int length) {
            if (this.inNameTag) {
                this.myNbd.mPop.name = new String(ch).substring(start, length);
            } else if (this.inValueTag) {
                if (!this.hasGottenValue1) {
                    this.value1 = Integer.parseInt(new String(ch).substring(start, length));
                    this.hasGottenValue1 = true;
                } else {
                    //this.myNbd.mPop.eveningProbability = Integer.parseInt(new String(ch).substring(start, length));
                    Pair<Integer, Integer> pair = new Pair<Integer, Integer>(value1,
                            Integer.parseInt(new String(ch).substring(start, length)));
                    this.myNbd.mPop.probabilities.add(pair);
                    this.hasGottenValue1 = false;
                }
            }
        }
    }
}
