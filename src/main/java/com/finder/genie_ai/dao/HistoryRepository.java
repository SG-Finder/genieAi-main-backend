package com.finder.genie_ai.dao;

import com.finder.genie_ai.model.game.history.HistoryModel;
import com.finder.genie_ai.model.game.player.PlayerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<HistoryModel, Integer> {

    Optional<HistoryModel> findByPlayerId(PlayerModel playerId);

    @Modifying
    @Query(value = "UPDATE history SET finder = :finder, one_shot = :oneShot, win = win WHERE player_id = :playerId", nativeQuery = true)
    int updateWinnerHistory(@Param("finder") int finder,
                            @Param("oneShot") int oneShot,
                            @Param("win") int win,
                            @Param("playerId") int playerId);

    @Modifying
    @Query(value = "UPDATE hisotry SET lose = :lose WHERE player_id = :playerId", nativeQuery = true)
    int updateLoserHistory(@Param("lose") int lose,
                           @Param("playerId") int playerId);

}
