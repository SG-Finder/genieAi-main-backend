package com.finder.genie_ai.dao;

import com.finder.genie_ai.model.game.history.HistoryModel;
import com.finder.genie_ai.model.game.player.PlayerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<HistoryModel, Integer> {

    Optional<HistoryModel> findByPlayerId(PlayerModel playerId);

}
