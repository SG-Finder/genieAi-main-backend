package com.finder.genie_ai.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finder.genie_ai.controller.genie_command.GenieHost;
import com.finder.genie_ai.exception.UnauthorizedException;
import com.finder.genie_ai.model.session.SessionModel;
import com.finder.genie_ai.redis_dao.SessionTokenRedisRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/finder")
public class GenieController {

    private SessionTokenRedisRepository sessionTokenRedisRepository;
    private ObjectMapper mapper;

    @Autowired
    public GenieController(SessionTokenRedisRepository sessionTokenRedisRepository,
                           ObjectMapper mapper) {
        this.sessionTokenRedisRepository = sessionTokenRedisRepository;
        this.mapper = mapper;
    }


    @RequestMapping(value = "/genie/message" ,method = RequestMethod.GET, produces = "application/json")
    public JsonObject sendMessageToGenie(@RequestHeader("session-token") String token,
                                         @RequestParam("user_message") String userMessage,
                                         HttpServletRequest request) throws JsonProcessingException {
        if (!sessionTokenRedisRepository.isSessionValid(token)) {
            throw new UnauthorizedException();
        }
        JsonElement element = new JsonParser().parse(sessionTokenRedisRepository.findSessionToken(token));
        SessionModel sessionModel = new SessionModel(request.getRemoteAddr(), LocalDateTime.parse(element.getAsJsonObject().get("signin_at").getAsString()), LocalDateTime.now());
        sessionTokenRedisRepository.updateSessionToken(token, mapper.writeValueAsString(sessionModel));

        JsonParser parser = new JsonParser();
        return parser.parse(GenieHost.sendMessageToGenie(userMessage)).getAsJsonObject();
    }

    @RequestMapping(value = "/genie/action", method = RequestMethod.GET, produces = "application/json")
    public JsonObject orderActionToGenie(@RequestHeader("session-token") String token,
                                         @RequestParam("order") String order,
                                         HttpServletRequest request) throws JsonProcessingException {
        if (!sessionTokenRedisRepository.isSessionValid(token)) {
            throw new UnauthorizedException();
        }
        JsonElement element = new JsonParser().parse(sessionTokenRedisRepository.findSessionToken(token));
        SessionModel sessionModel = new SessionModel(request.getRemoteAddr(), LocalDateTime.parse(element.getAsJsonObject().get("signin_at").getAsString()), LocalDateTime.now());
        sessionTokenRedisRepository.updateSessionToken(token, mapper.writeValueAsString(sessionModel));


        JsonParser parser = new JsonParser();
        return parser.parse(GenieHost.orderActionToGenie(order)).getAsJsonObject();
    }

    @RequestMapping(value = "/genie/question", method = RequestMethod.GET, produces = "application/json")
    public JsonObject questionToGenie(@RequestHeader("session-token") String token,
                                      @RequestParam("question") String question,
                                      HttpServletRequest request) throws JsonProcessingException {
        if (!sessionTokenRedisRepository.isSessionValid(token)) {
            throw new UnauthorizedException();
        }
        JsonElement element = new JsonParser().parse(sessionTokenRedisRepository.findSessionToken(token));
        SessionModel sessionModel = new SessionModel(request.getRemoteAddr(), LocalDateTime.parse(element.getAsJsonObject().get("signin_at").getAsString()), LocalDateTime.now());
        sessionTokenRedisRepository.updateSessionToken(token, mapper.writeValueAsString(sessionModel));


        JsonParser parser = new JsonParser();
        return parser.parse(GenieHost.questionToGenie(question)).getAsJsonObject();
    }

}
