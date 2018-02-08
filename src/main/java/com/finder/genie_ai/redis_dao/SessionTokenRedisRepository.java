package com.finder.genie_ai.redis_dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Repository
@Transactional
public class SessionTokenRedisRepository {

    private static final String SESSION = "session";
    private static final String USER = "user";
    private static final long EXPIRED_TIME = 30L;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private ValueOperations<String, String> valueOps;

    @PostConstruct
    public void init() {
        this.valueOps = redisTemplate.opsForValue();
    }

    public void saveSessionToken(String token, String userId, String data) {
        String tokenKey = SESSION + ":"  + token;
        String userKey = USER + ":" + userId;
        valueOps.set(tokenKey, data, EXPIRED_TIME, TimeUnit.MINUTES);
        valueOps.set(userKey, token, EXPIRED_TIME, TimeUnit.MINUTES);
    }

    public void updateSessionTokenOnDupLogin(String newToken, String oldToken, String userId, String data) {
        String newTokenKey = SESSION + ":" + newToken;
        String oldTokenKey = SESSION + ":" + oldToken;
        String userKey = USER + ":" + userId;

        valueOps.getOperations().expire(oldTokenKey, 0L, TimeUnit.NANOSECONDS);
        valueOps.set(newTokenKey, data);
        valueOps.set(userKey, newToken);
    }

    public void updateSessionToken(String token, String data) {
        String tokenKey = SESSION + ":" + token;
        valueOps.set(tokenKey, data, EXPIRED_TIME, TimeUnit.MINUTES);
    }

    public String findSessionToken(String token) {
        String tokenKey = SESSION + ":" + token;
        return valueOps.get(tokenKey);
    }

    @Deprecated
    public boolean isSessionValid(String token, String userId) {
        String key = SESSION + ":"  + token + ":" + userId;
        if (valueOps.get(key) == null) {
            return false;
        }
        else {
            return true;
        }
    }

    public boolean isSessionValid(String token) {
        String tokenKey = SESSION + ":"  + token;
        if (valueOps.get(tokenKey) == null) {
            return false;
        }
        else {
            return true;
        }
    }

    public String findSessionUserId(String userId) {
        return valueOps.get(USER + ":" + userId);
    }

    @Deprecated
    public boolean deleteSession(String token, String userId) {
        String tokenKey = SESSION + ":" + token + ":" + userId;
        return valueOps.getOperations().expire(tokenKey, 0L, TimeUnit.NANOSECONDS);
    }

    public boolean expireSession(String token, String userId) {
        if (deleteSessionToken(token) && deleteSessionUser(userId)) {
            return true;
        }
        else {
            return false;
        }
    }

    private boolean deleteSessionToken(String token) {
        String tokenKey = SESSION + ":" + token;
        return valueOps.getOperations().expire(tokenKey, 0L, TimeUnit.NANOSECONDS);
    }

    private boolean deleteSessionUser(String userId) {
        String userKey = USER + ":" + userId;
        return valueOps.getOperations().expire(userKey, 0L, TimeUnit.NANOSECONDS);
    }

}
