package com.finder.genie_ai.redis_dao;

import com.finder.genie_ai.dto.PlayerRankModel;
import com.finder.genie_ai.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Repository
public class LeaderBoardRedisRepository {

    public static final String KEY = "leaderBoard";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private ZSetOperations<String, String> zSetOps;

    @PostConstruct
    public void init() {
        zSetOps = redisTemplate.opsForZSet();
    }

    public List<String> getPlayersRankOfRange(int startIndex, int endIndex) {
        Set<String> rankReverseSet = zSetOps.reverseRange(KEY, startIndex, endIndex);
        Iterator<String> iter = rankReverseSet.iterator();
        List<String> list = new ArrayList<>(rankReverseSet.size());

        while (iter.hasNext()) {
            list.add(iter.next());
        }
        return list;
    }

    public PlayerRankModel getOnePlayerRank(String nickname) {

        Long playerRank = zSetOps.reverseRank(KEY, nickname);
        if (playerRank == null) {
            throw new NotFoundException("Please check nickname");
        }

        PlayerRankModel playerRankModel = new PlayerRankModel();
        playerRankModel.setNickname(nickname);
        playerRankModel.setRank(playerRank.longValue() + 1);
        playerRankModel.setScore(zSetOps.score(KEY, nickname));

        return playerRankModel;
    }

    public void updateLeaderBoard(String winner, double winnerScore, String loser, double loserScore) {
        zSetOps.add(KEY, winner, winnerScore);
        zSetOps.add(KEY, loser, loserScore);
    }
}
