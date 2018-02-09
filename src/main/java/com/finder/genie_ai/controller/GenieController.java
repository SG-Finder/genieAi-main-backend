package com.finder.genie_ai.controller;

import com.finder.genie_ai.controller.genie_command.GenieHost;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/finder")
public class GenieController {

    @RequestMapping(value = "/genie/message" ,method = RequestMethod.GET, produces = "application/json")
    public JsonObject sendMessageToGenie(@RequestHeader("session-token") String token,
                                         @RequestParam("user_message") String userMessage) {
        JsonParser parser = new JsonParser();
        return parser.parse(GenieHost.sendMessageToGenie(userMessage)).getAsJsonObject();
    }

    @RequestMapping(value = "/genie/action", method = RequestMethod.GET, produces = "application/json")
    public JsonObject orderActionToGenie(@RequestHeader("session-token") String token,
                                         @RequestParam("order") String order) {
        JsonParser parser = new JsonParser();
        return parser.parse(GenieHost.orderActionToGenie(order)).getAsJsonObject();
    }

    @RequestMapping(value = "/genie/question", method = RequestMethod.GET, produces = "application/json")
    public JsonObject questionToGenie(@RequestHeader("session-token") String token,
                                      @RequestParam("question") String question) {
        JsonParser parser = new JsonParser();
        return parser.parse(GenieHost.questionToGenie(question)).getAsJsonObject();
    }
    
}
