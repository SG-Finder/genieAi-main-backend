package com.finder.genie_ai.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finder.genie_ai.dao.*;
import com.finder.genie_ai.dto.HistoryDTO;
import com.finder.genie_ai.dto.PlayerDTO;
import com.finder.genie_ai.dto.PlayerWeaponDTO;
import com.finder.genie_ai.dto.ShopDealDTO;
import com.finder.genie_ai.exception.BadRequestException;
import com.finder.genie_ai.exception.DuplicateException;
import com.finder.genie_ai.exception.NotFoundException;
import com.finder.genie_ai.exception.UnauthorizedException;
import com.finder.genie_ai.model.game.history.HistoryModel;
import com.finder.genie_ai.model.game.item_relation.WeaponRelation;
import com.finder.genie_ai.model.game.player.PlayerModel;
import com.finder.genie_ai.model.game.player.command.PlayerRegisterCommand;
import com.finder.genie_ai.model.session.SessionModel;
import com.finder.genie_ai.redis_dao.SessionTokenRedisRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "finder/player")
@Api(value = "genieAi Player", description = "Operations pertaining to player rest api")
public class PlayerController {

    private PlayerRepository playerRepository;
    private UserRepository userRepository;
    private WeaponRelationRepository weaponRelationRepository;
    private WeaponRepository weaponRepository;
    private HistoryRepository historyRepository;
    private SessionTokenRedisRepository sessionTokenRedisRepository;
    private ObjectMapper mapper;
    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public PlayerController(PlayerRepository playerRepository,
                            UserRepository userRepository,
                            WeaponRelationRepository weaponRelationRepository,
                            WeaponRepository weaponRepository,
                            HistoryRepository historyRepository,
                            SessionTokenRedisRepository sessionTokenRedisRepository,
                            ObjectMapper mapper) {
        this.playerRepository = playerRepository;
        this.userRepository = userRepository;
        this.weaponRelationRepository = weaponRelationRepository;
        this.weaponRepository = weaponRepository;
        this.historyRepository = historyRepository;
        this.sessionTokenRedisRepository = sessionTokenRedisRepository;
        this.mapper = mapper;
    }

    @ApiOperation(value = "User register nickname for playing game", response = PlayerDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully register nickname"),
            @ApiResponse(code = 400, message = "Invalid parameter form"),
            @ApiResponse(code = 401, message = "Invalid or expired session-token"),
            @ApiResponse(code = 409, message = "Duplicated nickname"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @Transactional
    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody PlayerDTO registerPlayer(@RequestHeader(name = "session-token") String token,
                                                  @RequestBody @Valid PlayerRegisterCommand command,
                                                  BindingResult bindingResult,
                                                  HttpServletRequest request) throws JsonProcessingException {
        if (!sessionTokenRedisRepository.isSessionValid(token)) {
            throw new UnauthorizedException();
        }

        JsonElement element = new JsonParser().parse(sessionTokenRedisRepository.findSessionToken(token));
        SessionModel sessionModel = new SessionModel(request.getRemoteAddr(), LocalDateTime.parse(element.getAsJsonObject().get("signin_at").getAsString()), LocalDateTime.now());
        System.out.println(mapper.writeValueAsString(sessionModel));
        sessionTokenRedisRepository.updateSessionToken(token, mapper.writeValueAsString(sessionModel));

        if (bindingResult.hasErrors()) {
            throw new BadRequestException("invalid parameter form");
        }

        // check duplicated nickname
        if (playerRepository.findByNickname(command.getNickname()).isPresent()) {
            throw new DuplicateException("Duplicated nickname");
        }

        PlayerModel player = new PlayerModel();
        player.setNickname(command.getNickname());
        player.setUserId(userRepository.findByUserId(command.getUserId()).get());
        player = playerRepository.save(player);

        HistoryModel history = new HistoryModel();
        history.setPlayerId(player);
        history = historyRepository.save(history);

        WeaponRelation weaponRelation = new WeaponRelation();
        weaponRelation.setPlayerId(player);
        weaponRelation.setUsableCount(Integer.MAX_VALUE);
        weaponRelation.setWeaponId(weaponRepository.findOne(1));
        weaponRelation = weaponRelationRepository.save(weaponRelation);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        HistoryDTO historyDTO = modelMapper.map(history, HistoryDTO.class);

        PlayerWeaponDTO playerWeaponDTO = modelMapper.map(weaponRelation.getWeaponId(), PlayerWeaponDTO.class);
        playerWeaponDTO.setUsableCount(weaponRelation.getUsableCount());

        List<PlayerWeaponDTO> weaponList = new ArrayList<>(1);
        weaponList.add(playerWeaponDTO);

        return new PlayerDTO(player.getNickname(),
                player.getTier(),
                player.getScore(),
                historyDTO,
                weaponList,
                player.getPoint());
    }

    @ApiOperation(value = "Get player information", response = PlayerDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully get player information"),
            @ApiResponse(code = 400, message = "Invalid parameter form"),
            @ApiResponse(code = 401, message = "Invalid or expired session-token or Not enough money"),
            @ApiResponse(code = 404, message = "Please check player nickname"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/{nickname}", method = RequestMethod.GET, produces = "application/json")
    public PlayerDTO getPlayerInfo(@PathVariable("nickname") String nickname,
                                   @RequestHeader("session-token") String token,
                                   HttpServletRequest request) throws JsonProcessingException {
        if (!sessionTokenRedisRepository.isSessionValid(token)) {
            throw new UnauthorizedException();
        }
        JsonElement element = new JsonParser().parse(sessionTokenRedisRepository.findSessionToken(token));
        SessionModel sessionModel = new SessionModel(request.getRemoteAddr(), LocalDateTime.parse(element.getAsJsonObject().get("signin_at").getAsString()), LocalDateTime.now());
        sessionTokenRedisRepository.updateSessionToken(token, mapper.writeValueAsString(sessionModel));

        PlayerModel playerModel = playerRepository
                .findByNickname(nickname)
                .orElseThrow(() -> new NotFoundException("Please check player nickname"));

        List<WeaponRelation> listWeaponRelation = weaponRelationRepository.findByPlayerId(playerModel);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);

        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setNickname(playerModel.getNickname());

        HistoryModel historyModel = historyRepository.findByPlayerId(playerModel).get();

        playerDTO.setHistory(modelMapper.map(historyModel, HistoryDTO.class));
        playerDTO.setPoint(playerModel.getPoint());
        playerDTO.setTier(playerModel.getTier());

        if (listWeaponRelation.size() != 0) {
            List<PlayerWeaponDTO> playerWeaponDTOs = new ArrayList<>(listWeaponRelation.size());

            for (WeaponRelation data : listWeaponRelation) {
                PlayerWeaponDTO dto = modelMapper.map(data.getWeaponId(), PlayerWeaponDTO.class);
                dto.setUsableCount(data.getUsableCount());
                playerWeaponDTOs.add(dto);
            }
            playerDTO.setWeapons(playerWeaponDTOs);
        }

        return playerDTO;
    }

}
