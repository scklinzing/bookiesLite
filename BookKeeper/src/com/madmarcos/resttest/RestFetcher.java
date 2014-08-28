package com.madmarcos.resttest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import android.util.Log;

/*
 * This class is responsible for the actual sending/receiving with the web service
 * Supports http and https connections based on protocol used in passed URL
 */

public class RestFetcher {	
	public static final String TAG = "RestFetcher";
	private KeyStore keyStore = null;
	private TrustManagerFactory tmf = null;
	private SSLContext context = null;
	
	public RestFetcher() {
	}
	
	byte [] getUrlBytes(String urlSpec) throws IOException {
		URL url = new URL(urlSpec);
				
		HttpURLConnection connection = null;
		if(url.getProtocol().equalsIgnoreCase("https") && context != null) {
			try {
				// Create an SSLContext that uses our TrustManager
				connection = (HttpsURLConnection) url.openConnection();
				((HttpsURLConnection) connection).setSSLSocketFactory(context.getSocketFactory());
			} catch(Exception e) {
				Log.e(TAG, "Error connecting using https: " + e.getMessage());
			}
		} else {
			connection = (HttpURLConnection) url.openConnection();
		}
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = connection.getInputStream();
			if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return null;
			}
			int bytesRead = 0;
			byte [] buffer = new byte[1024];
			while((bytesRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, bytesRead);
			}
			out.close();
			return out.toByteArray();
		} finally {
			connection.disconnect();
		}
	}
	
	public String getUrl(String urlSpec) throws IOException {
		return new String(getUrlBytes(urlSpec));
	}

	public boolean initKeyStore(InputStream caInput) {
		//caInput is the cert file to trust
		CertificateFactory cf;
		try {
			cf = CertificateFactory.getInstance("X.509");
			Certificate ca;
			ca = cf.generateCertificate(caInput);

			// Create a KeyStore containing our trusted CAs
			String keyStoreType =KeyStore.getDefaultType();
			KeyStore keyStore =KeyStore.getInstance(keyStoreType);
			keyStore.load(null,null);
			keyStore.setCertificateEntry("ca", ca);

			// Create a TrustManager that trusts the CAs in our KeyStore
			String tmfAlgorithm =TrustManagerFactory.getDefaultAlgorithm();
			tmf =TrustManagerFactory.getInstance(tmfAlgorithm);
			tmf.init(keyStore);
			context =SSLContext.getInstance("TLS");
			context.init(null, tmf.getTrustManagers(),null);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
