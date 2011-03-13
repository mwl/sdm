package com.trifork.sdm.replication.dgws;


import java.io.InputStream;
import java.net.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Node;


public class SoapHelper
{
	public static String send(String urlString, Node node) throws Exception
	{
		URL url = new URL(urlString);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// Prepare for SOAP

		connection.setRequestMethod("POST");
		connection.setRequestProperty("SOAPAction", "\"\"");
		connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8;");

		// Send the request XML.

		connection.setDoOutput(true);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new DOMSource(node), new StreamResult(connection.getOutputStream()));

		// Read the response.

		InputStream inputStream;

		if (connection.getResponseCode() < 400)
		{
			inputStream = connection.getInputStream();
		}
		else
		{
			inputStream = connection.getErrorStream();
		}

		String response = IOUtils.toString(inputStream);

		return response;
	}
}