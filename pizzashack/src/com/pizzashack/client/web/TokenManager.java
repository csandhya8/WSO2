package com.pizzashack.client.web;


import com.pizzashack.client.dto.Token;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;

import sun.misc.BASE64Encoder;

import java.io.IOException;

public class TokenManager {
	
    private HTTPClient httpClient;

    public TokenManager() {
    	try {
    		httpClient = new HTTPClient();
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    public Token getToken(String username, String password){
    	Token token = null;
        String submitUrl = PizzaShackWebConfiguration.getInstance().getLoginURL();
        String consumerKey = PizzaShackWebConfiguration.getInstance().getConsumerKey();
        String consumerSecret = PizzaShackWebConfiguration.getInstance().getConsumerSecret();
        try {
            String applicationToken = consumerKey + ":" + consumerSecret;
            BASE64Encoder base64Encoder = new BASE64Encoder();
            applicationToken = "Basic " + base64Encoder.encode(applicationToken.getBytes()).trim();
            String payload = "grant_type=password&username="+username+"&password="+password;
            System.out.println("TokenManager -> getToken -> payload -> " + payload);
            HttpResponse httpResponse = httpClient.doPost(submitUrl,applicationToken,
            		payload,"application/x-www-form-urlencoded");
            System.out.println("TokenManager -> getToken -> httpResponse -> " + httpResponse);
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
            	return null;
            }
            String response = httpClient.getResponsePayload(httpResponse);
            System.out.println("TokenManager -> getToken -> response -> " + response);
            token = JSONClient.getAccessToken(response);
            System.out.println("TokenManager -> getToken -> token -> " + token);		
            return token;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
