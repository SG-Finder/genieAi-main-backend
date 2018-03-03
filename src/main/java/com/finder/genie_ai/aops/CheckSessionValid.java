package com.finder.genie_ai.aops;

import com.finder.genie_ai.exception.UnauthorizedException;
import com.finder.genie_ai.redis_dao.SessionTokenRedisRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class CheckSessionValid {

    private SessionTokenRedisRepository sessionTokenRedisRepository;

    @Autowired
    public CheckSessionValid(SessionTokenRedisRepository sessionTokenRedisRepository) {
        this.sessionTokenRedisRepository = sessionTokenRedisRepository;
    }

    @Pointcut("execution(public * com.finder.genie_ai.controller.ShopController.*(..))")
    public void controllerClassMethods() {}

    @Before(value = "controllerClassMethods()")
    public void checkSessionValid(JoinPoint joinPoint) {
        String token = (String)joinPoint.getArgs()[0];
        System.out.println(token);
        if (!sessionTokenRedisRepository.isSessionValid(token)) {
            throw new UnauthorizedException();
        }
        JsonElement element = new JsonParser().parse(sessionTokenRedisRepository.findSessionToken(token));
        System.out.println(element.getAsJsonObject());
    }

}
