package com.pizzashack.client.web;


import com.pizzashack.client.dto.Order;
import org.apache.http.HttpResponse;

import java.io.IOException;

public class OrderManager {
    private HTTPClient httpClient;
    private String serverURL;
    //private final String PIZZA_ORDER_URL = "/order/1.0.0";
    //private final String PIZZA_DELIVERY_URL = "/api/delivery";
    private final String PIZZA_ORDER_URL = "/order/";
    //private final String PIZZA_FINDORDER_URL = "/findorder/1.0.0";
    private final String PIZZA_FINDORDER_URL = "/delivery";


    public OrderManager() throws Exception {
        httpClient = new HTTPClient();
        serverURL = PizzaShackWebConfiguration.getInstance().getServerURL();
    }

    public Order saveOrder(String address, String pizzaType, String quantity
            , String customerName, String creditCardNumber, String token) {
        Order order = new Order(address,pizzaType,customerName,quantity,creditCardNumber);
        String jsonString = JSONClient.generateSaveOrderMessage(order);

        String submitUrl = PizzaShackWebConfiguration.getInstance().getServerURL() + PIZZA_ORDER_URL;
        
        System.out.println("Order URL :: " + submitUrl);
        try {
            HttpResponse httpResponse = httpClient.doPost(submitUrl,"Bearer " + token,jsonString,"application/json");
            String response = httpClient.getResponsePayload(httpResponse);
            System.out.println("OrderManager :: saveOrder :: response :: " + response);
            //new instance with orderId
            order = JSONClient.getOrder(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return order;
    }

    public Order getOrder(String orderId, String token){
    	System.out.println("orderId..." + orderId + "   token... " + token);
    	if (orderId == null || orderId.isEmpty()) {
    		orderId = "0";
    	}
        String submitUrl = PizzaShackWebConfiguration.getInstance().getServerURL()
        		+ PIZZA_ORDER_URL + orderId;
                //+ PIZZA_ORDER_URL + "/order/" + orderId;
        		//+ PIZZA_ORDER_URL + "/" + orderId;

        Order order = null;
        try {
        	System.out.println("getOrder :: submitUrl :: " + submitUrl);
            HttpResponse httpResponse = httpClient.doGet(submitUrl, "Bearer " + token);
            System.out.println("getOrder :: httpResponse :: " + httpResponse);
            //if (!httpResponse.toString().contains("404")) {
	            String response = httpClient.getResponsePayload(httpResponse);
	            System.out.println("getOrder :: response :: " + response);
	            boolean isLimitExceeded = false;
	            if (response.contains("900800")) {
	            	isLimitExceeded = true;
	            	System.out.println("getOrder :: Message Throttled Out :: " + isLimitExceeded);
	            	order = new Order();
	            	order.setMessage("Message Throttled Out");
	            	//order.setLimitExceeded(isLimitExceeded);
	            } else {
	            	order = JSONClient.getOrder(response);
	            }
            //}
            return order;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return order;
    }

}
