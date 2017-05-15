package com.qianbao.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.security.cert.CertificateException;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.log4j.Logger;

public class HttpClentSSL {
	private static Logger logger = Logger.getLogger(HttpClentSSL.class);

	
	private PoolingHttpClientConnectionManager poolConnManager;
	private final int maxTotalPool = 200;
	private final int maxConPerRoute = 20;
	private final int socketTimeout = 2000;
	private final int connectionRequestTimeout = 3000;
	private final int connectTimeout = 1000;

	public void init(){  
	 try {  
	    SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(null,  
	                    new TrustSelfSignedStrategy())  
	            .build();  
	    HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;  
	    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(  
	            sslcontext,(X509HostnameVerifier) hostnameVerifier);  
	    Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()  
	            .register("http", PlainConnectionSocketFactory.getSocketFactory())  
	            .register("https", sslsf)  
	            .build();  
	    poolConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);  
	    // Increase max total connection to 200  
	    poolConnManager.setMaxTotal(maxTotalPool);  
	    // Increase default max connection per route to 20  
	    poolConnManager.setDefaultMaxPerRoute(maxConPerRoute);  
	    SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(socketTimeout).build();  
	    poolConnManager.setDefaultSocketConfig(socketConfig);  
	} catch (Exception e) {  

	}  
	}
	
	public CloseableHttpClient getConnection() {
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout)
				.setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(poolConnManager)
				.setDefaultRequestConfig(requestConfig).build();
		if (poolConnManager != null && poolConnManager.getTotalStats() != null) {

			System.out.println("now client pool " + poolConnManager.getTotalStats().toString());
		}
		return httpClient;
	}

	
	public static CloseableHttpClient createHttpclient() {

		FileInputStream instream = null;
		CloseableHttpClient httpclient = null;
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			instream = new FileInputStream(new File("D:/cpdg/my.truststore"));
			trustStore.load(instream, "123123".toCharArray());
			SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(trustStore).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
					SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
			httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

		} catch (KeyStoreException | NoSuchAlgorithmException | java.security.cert.CertificateException | IOException
				| KeyManagementException e) {
			logger.error("²Ù×÷Òì³£ " + e.getMessage());
		} finally {
			try {
				if (instream != null) {
					instream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return httpclient;
	}
}
