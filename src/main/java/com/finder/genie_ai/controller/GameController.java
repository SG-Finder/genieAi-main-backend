package com.finder.genie_ai.controller;

import com.finder.genie_ai.controller.command.GameResultCommand;
import com.finder.genie_ai.dao.HistoryRepository;
import com.finder.genie_ai.dao.PlayerRepository;
import com.finder.genie_ai.dao.WeaponRelationRepository;
import com.finder.genie_ai.enumdata.Tier;
import com.finder.genie_ai.model.game.history.HistoryModel;
import com.finder.genie_ai.model.game.item_relation.WeaponRelation;
import com.finder.genie_ai.model.game.player.PlayerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping(value = "/finder")
public class GameController {

    private PlayerRepository playerRepository;
    private WeaponRelationRepository weaponRelationRepository;
    private HistoryRepository historyRepository;

    @Autowired
    public GameController(PlayerRepository playerRepository,
                          WeaponRelationRepository weaponRelationRepository,
                          HistoryRepository historyRepository) {
        this.playerRepository = playerRepository;
        this.weaponRelationRepository = weaponRelationRepository;
        this.historyRepository = historyRepository;
    }

    @RequestMapping(value = "/result", method = RequestMethod.POST, consumes = "application/json")
    @Transactional
    public void getResultOfGame(@RequestBody GameResultCommand command,
                                HttpServletResponse response) {

        // update winner score & point
        PlayerModel winner = playerRepository.findByNickname(command.getWinner()).get();
        winner.setScore(winner.getScore() + 20);
        winner.setPoint(winner.getPoint() + 20);
        if (winner.getTier() == Tier.BRONZE && winner.getScore() > 200) {
            winner.setTier(Tier.SILVER);
        }
        else if (winner.getTier() == Tier.SILVER && winner.getScore() > 400) {
            winner.setTier(Tier.GOLD);
        }
        playerRepository.updatePlayerInfo(winner.getPoint(),
                winner.getScore(),
                winner.getTier().toString(),
                winner.getId());

        // update winner history
        HistoryModel winnerHistory = historyRepository.findByPlayerId(winner).get();
        if (command.isFinder()) {
            winnerHistory.setFinder(winnerHistory.getFinder() + 1);
        }
        if (command.isOneShot()) {
            winnerHistory.setOneShot(winnerHistory.getOneShot() + 1);
        }
        historyRepository.updateWinnerHistory(winnerHistory.getFinder(),
                winnerHistory.getOneShot(),
                winnerHistory.getWin() + 1,
                winner.getId());

        // update winner weaponRelation
        List<WeaponRelation> winnerWeaponRelation = weaponRelationRepository.findByPlayerId(winner);
        for (WeaponRelation data : winnerWeaponRelation) {
            if (data.getUsableCount() > 0) {
                weaponRelationRepository.updateWeaponRelation(data.getUsableCount() - 1,
                        winner.getId(),
                        data.getWeaponId().getId());
            }
        }

         // update loser score & point
        PlayerModel loser = playerRepository.findByNickname(command.getLoser()).get();
        if (loser.getScore() > 15) {
            loser.setScore(loser.getScore() - 15);
        }
        else {
            loser.setScore(0);
        }
        loser.setPoint(loser.getPoint() + 10);

        if (loser.getTier() == Tier.GOLD && loser.getScore() < 400) {
            loser.setTier(Tier.SILVER);
        }
        else if (loser.getTier() == Tier.SILVER && loser.getScore() < 200) {
            loser.setTier(Tier.BRONZE);
        }
        playerRepository.updatePlayerInfo(loser.getPoint(),
                loser.getScore(),
                loser.getTier().toString(),
                loser.getId());

        // update loser hisotry
        HistoryModel loserHistory = historyRepository.findByPlayerId(loser).get();
        historyRepository.updateLoserHistory(loserHistory.getLose() + 1, loser.getId());

        // update loser weaponRelation
        List<WeaponRelation> loserWeaponRelation = weaponRelationRepository.findByPlayerId(loser);
        for (WeaponRelation data : loserWeaponRelation) {
            if (data.getUsableCount() > 0) {
                weaponRelationRepository.updateWeaponRelation(data.getUsableCount() - 1,
                        loser.getId(),
                        data.getWeaponId().getId());
            }
        }
        response.setStatus(HttpStatus.NO_CONTENT.value());

    }

}
