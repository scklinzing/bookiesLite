/**********************************************************************************************
 * Copyright 2009 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file 
 * except in compliance with the License. A copy of the License is located at
 *
 *       http://aws.amazon.com/apache2.0/
 *
 * or in the "LICENSE.txt" file accompanying this file. This file is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under the License. 
 *
 * ********************************************************************************************
 *
 *  Amazon Product Advertising API
 *  Signed Requests Sample Code
 *
 *  API Version: 2009-03-31
 *
 */

package com.amazon.advertising.api.sample;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.html.parser.DocumentParser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/*
 * This class shows how to make a simple authenticated ItemLookup call to the
 * Amazon Product Advertising API.
 * 
 * See the README.html that came with this sample for instructions on
 * configuring and running the sample.
 */
public class ItemLookupSample {
    /*
     * Your AWS Access Key ID, as taken from the AWS Your Account page.
     */
    //private static final String AWS_ACCESS_KEY_ID = "YOUR_ACCESS_KEY_ID_HERE";

    /*
     * Your AWS Secret Key corresponding to the above ID, as taken from the AWS
     * Your Account page.
     */
    //private static final String AWS_SECRET_KEY = "YOUR_SECRET_KEY_HERE";
    
	private static final String AWS_ACCESS_KEY_ID = "AKIAIIEUTNPJT3QV53HA";
    private static final String AWS_SECRET_KEY = "eL6GnfREbekc/UpCGW+/WMGUsLy7RxPcYWB771k/";

    /*
     * Use one of the following end-points, according to the region you are
     * interested in:
     * 
     *      US: ecs.amazonaws.com 
     *      CA: ecs.amazonaws.ca 
     *      UK: ecs.amazonaws.co.uk 
     *      DE: ecs.amazonaws.de 
     *      FR: ecs.amazonaws.fr 
     *      JP: ecs.amazonaws.jp
     * 
     */
    private static final String ENDPOINT = "ecs.amazonaws.com";

    /*
     * The Item ID to lookup. The value below was selected for the US locale.
     * You can choose a different value if this value does not work in the
     * locale of your choice.
     */
    //private static final String ITEM_ID = "0545010225";
    private static final String ITEM_ID = "0345337662";

    public static void main(String[] args) {
        /*
         * Set up the signed requests helper 
         */
        SignedRequestsHelper helper;
        try {
            helper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        String requestUrl = null;
        String title = null;

        /* Here is an example with string form, where the requests parameters have already been concatenated
         * into a query string. */
        System.out.println("String form example:");
        String queryString = "Service=AWSECommerceService&Version=2009-03-31&Operation=ItemLookup&ResponseGroup=Offers&AssociateTag=splint-20&SearchIndex=Books&IdType=ISBN&ItemId="
                + ITEM_ID;
        requestUrl = helper.sign(queryString);
        System.out.println("Request is \"" + requestUrl + "\"");

        try {
			fetchTitle(requestUrl, ITEM_ID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("Title is \"" + title + "\"");
        System.out.println();

    }

    /*
     * Utility function to fetch the response from the service and extract the
     * title from the XML.
     */
    private static String fetchTitle(String requestUrl, String isbn10) throws IOException, Exception {
        String title = null;
        
        URL url = new URL(requestUrl);
        URLConnection connection = url.openConnection();

        Document doc = parseXML(connection.getInputStream());
        //NodeList descNodes = doc.getElementsByTagName("Price");
        NodeList items = doc.getElementsByTagName("Item");
        
        for( int i = 0 ; i < items.getLength() ; i++ )
        { 	
        	if( items.item(i).getFirstChild().getNodeName().equals("ASIN") && items.item(i).getFirstChild().getTextContent().equalsIgnoreCase(isbn10))
        	{
        		System.out.println("found");
        		NodeList childList = items.item(i).getChildNodes();
        		for( int j = 0 ; j < childList.getLength() ; j++ )
        		{
        			if( childList.item(j).getNodeName().equalsIgnoreCase("offersummary") )
        			{
        				NodeList cat = childList.item(j).getChildNodes();
        				for( int c = 0; c < cat.getLength() ; c++ )
        				{
        					if ( cat.item(c).getNodeName().equalsIgnoreCase("lowestnewprice"))
        					{
        						NodeList l = cat.item(c).getChildNodes();
        						for( int v = 0 ; v < l.getLength() ; v++ )
        						{
        							if ( l.item(v).getNodeName().equalsIgnoreCase("formattedprice") )
        							{
        								System.out.println(l.item(v).getTextContent());
        							}
        						}
        					}
        					else if ( cat.item(c).getNodeName().equalsIgnoreCase("lowestusedprice"))
        					{
        						NodeList l = cat.item(c).getChildNodes();
        						for( int v = 0 ; v < l.getLength() ; v++ )
        						{
        							if ( l.item(v).getNodeName().equalsIgnoreCase("formattedprice") )
        							{
        								System.out.println(l.item(v).getTextContent());
        							}
        						}
        					}
        					else if ( cat.item(c).getNodeName().equalsIgnoreCase("totalNew"))
        					{
        						System.out.println(cat.item(c).getTextContent());
        					}
        					else if ( cat.item(c).getNodeName().equalsIgnoreCase("totalused"))
        					{
        						System.out.println(cat.item(c).getTextContent());
        					}
        				}
        			}
        		}
        	}
        }

//        for(int i=0; i<descNodes.getLength();i++)
//        {
//            System.out.println(descNodes.item(i).getTextContent());
//        }
        return title;
    }
    
    private static Document parseXML(InputStream stream)
	    throws Exception
	    {
	        DocumentBuilderFactory objDocumentBuilderFactory = null;
	        DocumentBuilder objDocumentBuilder = null;
	        Document doc = null;
	        try
	        {
	            objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
	            objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();

	            doc = objDocumentBuilder.parse(stream);
	        }
	        catch(Exception ex)
	        {
	            throw ex;
	        }       

	        return doc;
	    }

}
