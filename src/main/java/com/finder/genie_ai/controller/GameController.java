package com.finder.genie_ai.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finder.genie_ai.controller.command.GameResultCommand;
import com.finder.genie_ai.dao.HistoryRepository;
import com.finder.genie_ai.dao.PlayerRepository;
import com.finder.genie_ai.dao.WeaponRelationRepository;
import com.finder.genie_ai.dto.LeaderBoardDTO;
import com.finder.genie_ai.dto.PlayerRankModel;
import com.finder.genie_ai.enumdata.Tier;
import com.finder.genie_ai.exception.BadRequestException;
import com.finder.genie_ai.exception.NotFoundException;
import com.finder.genie_ai.exception.UnauthorizedException;
import com.finder.genie_ai.model.game.history.HistoryModel;
import com.finder.genie_ai.model.game.item_relation.WeaponRelation;
import com.finder.genie_ai.model.game.player.PlayerModel;
import com.finder.genie_ai.model.session.SessionModel;
import com.finder.genie_ai.redis_dao.LeaderBoardRedisRepository;
import com.finder.genie_ai.redis_dao.SessionTokenRedisRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/finder")
@Api(value = "GenieAi result game", description = "Operations pertaining to game's result register")
public class GameController {

    private PlayerRepository playerRepository;
    private WeaponRelationRepository weaponRelationRepository;
    private HistoryRepository historyRepository;
    private SessionTokenRedisRepository sessionTokenRedisRepository;
    private LeaderBoardRedisRepository leaderBoardRedisRepository;
    private ObjectMapper mapper;

    @Autowired
    public GameController(PlayerRepository playerRepository,
                          WeaponRelationRepository weaponRelationRepository,
                          HistoryRepository historyRepository,
                          SessionTokenRedisRepository sessionTokenRedisRepository,
                          LeaderBoardRedisRepository leaderBoardRedisRepository,
                          ObjectMapper mapper) {
        this.playerRepository = playerRepository;
        this.weaponRelationRepository = weaponRelationRepository;
        this.historyRepository = historyRepository;
        this.sessionTokenRedisRepository = sessionTokenRedisRepository;
        this.leaderBoardRedisRepository = leaderBoardRedisRepository;
        this.mapper = mapper;
    }


    //TODO authorization about game server, have to set some token connect with game server
    //TODO NOT_FOUND error handler
    @ApiOperation(value = "Throw data that is result of any round of game")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully register result of game"),
            @ApiResponse(code = 400, message = "Invalid parameter form"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/result", method = RequestMethod.POST, consumes = "application/json")
    @Transactional
    public void getResultOfGame(@RequestBody @Valid GameResultCommand command,
                                BindingResult bindingResult,
                                HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("invalid parameter form");
        }

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

        // insert or update leaderboard in redis
        leaderBoardRedisRepository.updateLeaderBoard(winner.getNickname(),
                winner.getScore(),
                loser.getNickname(),
                loser.getScore());


        response.setStatus(HttpStatus.NO_CONTENT.value());

    }

    @ApiOperation(value = "Inquire leaderboard between 1 and 100", response = LeaderBoardDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully get leaderboard information"),
            @ApiResponse(code = 401, message = "Invalid or expired session-token"),
            @ApiResponse(code = 404, message = "Doesn't have any rank information"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/rank/leaderBoard", method = RequestMethod.GET, produces = "application/json")
    @Transactional
    public LeaderBoardDTO showRealTimerRankLeaderBoard(@RequestHeader("session-token") String token,
                                                       HttpServletRequest request) throws JsonProcessingException {
        if (!sessionTokenRedisRepository.isSessionValid(token)) {
            throw new UnauthorizedException();
        }
        JsonElement element = new JsonParser().parse(sessionTokenRedisRepository.findSessionToken(token));
        SessionModel sessionModel = new SessionModel(request.getRemoteAddr(), LocalDateTime.parse(element.getAsJsonObject().get("signin_at").getAsString()), LocalDateTime.now());
        sessionTokenRedisRepository.updateSessionToken(token, mapper.writeValueAsString(sessionModel));

        LeaderBoardDTO leaderBoardDTO = new LeaderBoardDTO();
        leaderBoardDTO.setLeaderBoard(leaderBoardRedisRepository.getPlayersRankOfRange(0, 99));

        if (leaderBoardDTO.getLeaderBoard().isEmpty()) {
            throw new NotFoundException("Doesn't have any rank information");
        }

        leaderBoardDTO.setDate(LocalDate.now());
        return leaderBoardDTO;
    }

    @ApiOperation(value = "Inquire player's rank & score", response = PlayerRankModel.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully get player's rank & score"),
            @ApiResponse(code = 401, message = "Invalid or expired session-token"),
            @ApiResponse(code = 404, message = "Please check nickname"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/rank/player/{nickname}", method = RequestMethod.GET, produces = "application/json")
    @Transactional
    public PlayerRankModel getPlayerRankWithScore(@RequestHeader("session-token") String token,
                                                  @PathVariable("nickname") String nickname,
                                                  HttpServletRequest request) throws JsonProcessingException {
        if (!sessionTokenRedisRepository.isSessionValid(token)) {
            throw new UnauthorizedException();
        }
        JsonElement element = new JsonParser().parse(sessionTokenRedisRepository.findSessionToken(token));
        SessionModel sessionModel = new SessionModel(request.getRemoteAddr(), LocalDateTime.parse(element.getAsJsonObject().get("signin_at").getAsString()), LocalDateTime.now());
        sessionTokenRedisRepository.updateSessionToken(token, mapper.writeValueAsString(sessionModel));

        return leaderBoardRedisRepository.getOnePlayerRank(nickname);
    }

}
