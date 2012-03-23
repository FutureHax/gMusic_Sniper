
package com.t3hh4xx0r.cloudsniper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Utility class for working with an XmlPullParser.
 */
public final class XmlUtils {
	   @SuppressWarnings("static-access")
	public static final void nextElement(XmlPullParser parser) throws XmlPullParserException, IOException
	    {
	        int type;
	        while ((type=parser.next()) != parser.START_TAG
	                   && type != parser.END_DOCUMENT) {
	            ;
	        }
	    }
	   @SuppressWarnings("static-access")
	    public static final void beginDocument(XmlPullParser parser, String firstElementName) throws XmlPullParserException, IOException {
	        int type;
	        while ((type=parser.next()) != parser.START_TAG
	                   && type != parser.END_DOCUMENT) {
	            ;
	        }

	        if (type != parser.START_TAG) {
	            throw new XmlPullParserException("No start tag found");
	        }

	        if (!parser.getName().equals(firstElementName)) {
	            throw new XmlPullParserException("Unexpected start tag: found " + parser.getName() +
	                    ", expected " + firstElementName);
	        }
	    }
}