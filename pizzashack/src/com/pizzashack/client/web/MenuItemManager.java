package com.pizzashack.client.web;


import com.pizzashack.client.dto.Pizza;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MenuItemManager {
	
    private HTTPClient httpClient;
    private String serverURL;
    private final String PIZZA_LIST_URL = "/menu";

    public MenuItemManager() throws Exception {
        httpClient = new HTTPClient();
        serverURL = PizzaShackWebConfiguration.getInstance().getServerURL();
    }

    public ArrayList<Pizza> getAvailablePizzaList(String token) {
        InputStream is = null;
        HttpResponse httpResponse = null;
        try {
        	//System.out.println("getAvailablePizzaList : serverURL : PIZZA_LIST_URL :: " + serverURL + PIZZA_LIST_URL);
        	//System.out.println("Token..." + token);
        	
            httpResponse = httpClient.doGet(serverURL + PIZZA_LIST_URL, "Bearer " + token);
        	//httpResponse = httpClient.doGet(serverURL + PIZZA_LIST_URL, "Bearer " + "876057e6f22172209c9a3ed5ff9344");
            //System.out.println("getAvailablePizzaList : httpResponse :: " + httpResponse);
            //System.out.println("httpResponse... " + httpResponse);
            Header[] menuHeader = httpResponse.getAllHeaders();
            String response = httpClient.getResponsePayload(httpResponse);
            //System.out.println("Response :: " + response);
            return JSONClient.getAvailablePizzaList(response);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
