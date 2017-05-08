package com.pizzashack.client.web;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HTTPClient {

	private ThreadSafeClientConnManager connManager;
	private DefaultHttpClient client;
	private DefaultHttpClient httpClient;

	public HTTPClient() throws Exception {
		connManager = getHTTPConnectionManager();
		client = configureHTTPClient(connManager);
		
		httpClient = new DefaultHttpClient();
		initCookiePolicy();
		initSSLPolicy();
		
	}

	public HttpResponse doPost(String url, String token, final String payload, String contentType) throws IOException {
		HttpUriRequest request = new HttpPost(url);
		addSecurityHeaders(request, token);

		HttpEntityEnclosingRequest entityEncReq = (HttpEntityEnclosingRequest) request;
		EntityTemplate ent = new EntityTemplate(new ContentProducer() {
			public void writeTo(OutputStream outputStream) throws IOException {
				outputStream.write(payload.getBytes());
				outputStream.flush();
			}
		});
		ent.setContentType(contentType);
		entityEncReq.setEntity(ent);
		System.out.println("HTTPClient -> doPost -> request -> " + request);
		return httpClient.execute(request);		
	}
	
	public HttpResponse doGet(String url, String token) throws IOException {
		HttpUriRequest request = new HttpGet(url);
		addSecurityHeaders(request, token);
		System.out.println("request :: " + request.getURI());
		return httpClient.execute(request);
	}
	
	public String getResponsePayload(HttpResponse response) throws IOException {
		StringBuffer buffer = new StringBuffer();
		InputStream in = null;
		try {
			if (response.getEntity() != null) {
			    in = response.getEntity().getContent();
			    int length;
			    byte[] tmp = new byte[2048];
			    while ((length = in.read(tmp)) != -1) {
			        buffer.append(new String(tmp, 0, length));
			    }
			}
		} catch (IllegalStateException e) {			
			e.printStackTrace();
		} finally {
			if (in != null) {
				in.close();
			}
		}

        return buffer.toString();
	}

	public HttpResponse doPut(String url, String token, final String payload, String contentType) throws IOException {
		HttpUriRequest request = new HttpPut(url);
		addSecurityHeaders(request, token);

		HttpEntityEnclosingRequest entityEncReq = (HttpEntityEnclosingRequest) request;
		EntityTemplate ent = new EntityTemplate(new ContentProducer() {
			public void writeTo(OutputStream outputStream) throws IOException {
				outputStream.write(payload.getBytes());
				outputStream.flush();
			}
		});
		ent.setContentType(contentType);
		entityEncReq.setEntity(ent);
		return httpClient.execute(request);
	}

	public HttpResponse doDelete(String url, String token) throws IOException {
		HttpUriRequest request = new HttpDelete(url);
		addSecurityHeaders(request, token);
		return httpClient.execute(request);
	}

	private DefaultHttpClient configureHTTPClient(
			ThreadSafeClientConnManager connManager) {
		connManager.setDefaultMaxPerRoute(1000);
		DefaultHttpClient client = new DefaultHttpClient(connManager);
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 30000);
		HttpConnectionParams.setSoTimeout(params, 30000);
		client.setHttpRequestRetryHandler(new HttpRequestRetryHandler() {
			public boolean retryRequest(IOException e, int i,
					HttpContext httpContext) {
				return false;
			}
		});
		return client;
	}

	private ThreadSafeClientConnManager getHTTPConnectionManager() {
		SchemeRegistry supportedSchemes = new SchemeRegistry();
		SocketFactory sf = PlainSocketFactory.getSocketFactory();
		supportedSchemes.register(new Scheme("http", sf, 80));
		supportedSchemes.register(new Scheme("https", sf, 9452));
		supportedSchemes.register(new Scheme("https", sf, 9443));

		return new ThreadSafeClientConnManager(supportedSchemes);
	}
	
	private void addSecurityHeaders(HttpRequest request, String token) {
		if (token != null) {
			request.setHeader(HttpHeaders.AUTHORIZATION, token);
		}
	}
	
	// ************************************************************
	
	void initCookiePolicy() {
		httpClient.setCookieStore(new BasicCookieStore());
	}
	
	void initSSLPolicy() throws Exception {
		Scheme http1 = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
		Scheme http2 = new Scheme("http", 8080, PlainSocketFactory.getSocketFactory());
		Scheme http3 = new Scheme("http", 8082, PlainSocketFactory.getSocketFactory());
		Scheme http4 = new Scheme("http", 9080, PlainSocketFactory.getSocketFactory());
		Scheme http5 = new Scheme("http", 9081, PlainSocketFactory.getSocketFactory());
		Scheme http6 = new Scheme("http", 9765, PlainSocketFactory.getSocketFactory());
		Scheme http7 = new Scheme("http", 8289, PlainSocketFactory.getSocketFactory());
		Scheme http8 = new Scheme("http", 8280, PlainSocketFactory.getSocketFactory());
		
		SSLSocketFactory sf = null;
	
		try {
			sf = getEasySSLSocketFactory();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to initialize SSL HttpClient");
		} 
	
		Scheme https1 = new Scheme("https", 443, sf);
		Scheme https2 = new Scheme("https", 8443, sf);
		Scheme https3 = new Scheme("https", 9443, sf);
		Scheme https4 = new Scheme("https", 9444, sf);
	
		SchemeRegistry schemeRegistry = httpClient.getConnectionManager().getSchemeRegistry();
	
		schemeRegistry.register(http1);
		schemeRegistry.register(http2);
		schemeRegistry.register(http3);
		schemeRegistry.register(http4);
		schemeRegistry.register(http5);
		schemeRegistry.register(http6);
		schemeRegistry.register(http7);
		schemeRegistry.register(http8);
		schemeRegistry.register(https1);
		schemeRegistry.register(https2);
		schemeRegistry.register(https3);
		schemeRegistry.register(https4);
	}
	
	private SSLSocketFactory getEasySSLSocketFactory() 
	throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException 
	{
		System.out.println("getEasySSLSocketFactory :: ");
		TrustStrategy trustStrategy = new TrustStrategy() {
			@Override
			public boolean isTrusted(X509Certificate[] x509Certificates,
			String s) throws CertificateException {
				System.out.println("in true loop :: ");
				return true; // Accept Self-Signed Certs			
			}
		};
		
		SSLSocketFactory sslSocketFactory = null;
		System.out.println("sslSocketFactory before :: "+sslSocketFactory);
		//Bypass check for hostname verification
		sslSocketFactory = 
		new SSLSocketFactory(trustStrategy, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		System.out.println("sslSocketFactory :: "+sslSocketFactory);
		return sslSocketFactory;
	}
	
	public DefaultHttpClient getClient() {
		return this.httpClient;
	}
	
	
	//**************************************************************
	
	
}
