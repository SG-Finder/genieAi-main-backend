package com.finder.genie_ai.controller.genie_command;

import org.springframework.web.client.RestTemplate;

public class GenieHost {

    private static final String genieChatURL = "http://192.168.0.8:5000/api/genie_answer?user_message=";
    private static final String genieActionURL = "http://192.168.0.8:5000/api/genie_action?order=";
    private static final String questionToGenieURL = "http://192.168.0.8:5000/api/genie_search?search_order=";

    public static String sendMessageToGenie(String message) {
        RestTemplate restTemplate = new RestTemplate();
        return  restTemplate.getForObject(genieChatURL + message, String.class);
    }

    public static String orderActionToGenie(String action) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(genieActionURL, String.class);
    }

    public static String questionToGenie(String question) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(questionToGenieURL, String.class);
    }


}
