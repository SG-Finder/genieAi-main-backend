package com.finder.genie_ai.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finder.genie_ai.GenieAiApplication;
import com.finder.genie_ai.controller.genie_command.GenieHost;
import com.finder.genie_ai.dto.PlayerDTO;
import com.finder.genie_ai.exception.UnauthorizedException;
import com.finder.genie_ai.model.session.SessionModel;
import com.finder.genie_ai.redis_dao.SessionTokenRedisRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/finder")
@Api(value = "GenieAi Chat bot", description = "Operations pertaining to Genie(chat bot) rest api")
public class GenieController {

    private SessionTokenRedisRepository sessionTokenRedisRepository;
    private ObjectMapper mapper;
    private static final Logger logger = LogManager.getLogger(GenieAiApplication.class);

    @Autowired
    public GenieController(SessionTokenRedisRepository sessionTokenRedisRepository,
                           ObjectMapper mapper) {
        this.sessionTokenRedisRepository = sessionTokenRedisRepository;
        this.mapper = mapper;
    }

    @ApiOperation(value = "User register nickname for playing game")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully send message"),
            @ApiResponse(code = 400, message = "Required String parameter 'user_message' is not present"),
            @ApiResponse(code = 401, message = "Invalid or expired session-token"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/genie/message" ,method = RequestMethod.GET, produces = "application/json")
    public JsonObject sendMessageToGenie(@RequestHeader("session-token") String token,
                                   @RequestParam("user_message") String userMessage,
                                   HttpServletRequest request) throws JsonProcessingException {
        if (!sessionTokenRedisRepository.isSessionValid(token)) {
            throw new UnauthorizedException();
        }
        //TODO modularization this part
        //TODO left log
        JsonElement element = new JsonParser().parse(sessionTokenRedisRepository.findSessionToken(token));
        SessionModel sessionModel = new SessionModel(request.getRemoteAddr(), LocalDateTime.parse(element.getAsJsonObject().get("signin_at").getAsString()), LocalDateTime.now());
        sessionTokenRedisRepository.updateSessionToken(token, mapper.writeValueAsString(sessionModel));

        String messageTest = GenieHost.sendMessageToGenie(userMessage);
        logger.info(messageTest);
        JsonParser parser = new JsonParser();
        return parser.parse(messageTest).getAsJsonObject();
    }

    @ApiOperation(value = "User register nickname for playing game")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully order action"),
            @ApiResponse(code = 400, message = "Required String parameter 'order' is not present"),
            @ApiResponse(code = 401, message = "Invalid or expired session-token"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
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

    @ApiOperation(value = "User register nickname for playing game")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully send question"),
            @ApiResponse(code = 400, message = "Required String parameter 'question' is not present"),
            @ApiResponse(code = 401, message = "Invalid or expired session-token"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
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
