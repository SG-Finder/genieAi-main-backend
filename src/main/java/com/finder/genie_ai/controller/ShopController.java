package com.finder.genie_ai.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finder.genie_ai.controller.command.DealCommand;
import com.finder.genie_ai.dao.PlayerRepository;
import com.finder.genie_ai.dao.WeaponRelationRepository;
import com.finder.genie_ai.dao.WeaponRepository;
import com.finder.genie_ai.dto.PlayerWeaponDTO;
import com.finder.genie_ai.dto.ShopDealDTO;
import com.finder.genie_ai.exception.BadRequestException;
import com.finder.genie_ai.exception.NotFoundException;
import com.finder.genie_ai.exception.UnauthorizedException;
import com.finder.genie_ai.model.BaseListItemModel;
import com.finder.genie_ai.model.game.BaseItemModel;
import com.finder.genie_ai.model.game.item_relation.WeaponRelation;
import com.finder.genie_ai.model.game.player.PlayerModel;
import com.finder.genie_ai.model.game.weapon.WeaponModel;
import com.finder.genie_ai.model.session.SessionModel;
import com.finder.genie_ai.redis_dao.SessionTokenRedisRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/finder/shop")
@Api(value = "genieAi Shop", description = "Operations pertaining to shop rest api")
public class ShopController {

    private SessionTokenRedisRepository sessionTokenRedisRepository;
    private PlayerRepository playerRepository;
    private WeaponRepository weaponRepository;
    private WeaponRelationRepository weaponRelationRepository;
    private ObjectMapper mapper;
    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public ShopController(SessionTokenRedisRepository sessionTokenRedisRepository,
                          PlayerRepository playerRepository,
                          WeaponRepository weaponRepository,
                          WeaponRelationRepository weaponRelationRepository,
                          ObjectMapper mapper) {
        this.sessionTokenRedisRepository = sessionTokenRedisRepository;
        this.playerRepository = playerRepository;
        this.weaponRepository = weaponRepository;
        this.weaponRelationRepository = weaponRelationRepository;
        this.mapper = mapper;
    }

    @ApiOperation(value = "Buy Item in game shop", response = ShopDealDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully Buy Item"),
            @ApiResponse(code = 400, message = "Invalid parameter form"),
            @ApiResponse(code = 401, message = "Invalid or expired session-token or Not enough money"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @Transactional
    @RequestMapping(value = "", method = RequestMethod.POST)
     public ShopDealDTO activeShop(@RequestHeader("session-token") String token,
                                   @RequestBody @Valid DealCommand command,
                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid parameter form");
        }
        //TODO minimize send query to MySQL database server by making join query on players table with other tables
        Optional<PlayerModel> player = playerRepository.findByNickname(command.getNickname());
        Optional<WeaponModel> weapon = weaponRepository.findByName(command.getItem());
        Optional<WeaponRelation> weaponRelation = weaponRelationRepository.findByPlayerIdAndWeaponId(player.get(), weapon.get());

        int playerPoint = player.get().getPoint();
        int totalPrice = command.getCount() * weapon.get().getPrice();

        if (playerPoint < totalPrice) {
            throw new UnauthorizedException("Not enough money");
        }
        //TODO check exist nickname & weapon name

        playerRepository.updatePlayerPoint(playerPoint - totalPrice, player.get().getId());

        if (weaponRelation.isPresent()) {
            weaponRelationRepository.updateWeaponRelation(weaponRelation.get().getUsableCount() + command.getCount(),
                    player.get().getId(),
                    weapon.get().getId());
        }
        else {
            WeaponRelation newWeaponRelation = new WeaponRelation();
            newWeaponRelation.setPlayerId(player.get());
            newWeaponRelation.setWeaponId(weapon.get());
            newWeaponRelation.setUsableCount(command.getCount());
            weaponRelationRepository.save(newWeaponRelation);
        }

        ShopDealDTO shopDealDTO = new ShopDealDTO();
        shopDealDTO.setNickname(player.get().getNickname());
        shopDealDTO.setPoint(playerPoint - totalPrice);

        List<WeaponRelation> listWeaponRelation = weaponRelationRepository.findByPlayerId(player.get());
        List<PlayerWeaponDTO> playerWeaponDTOs = new ArrayList<>(listWeaponRelation.size());

        for (WeaponRelation data : listWeaponRelation) {
            PlayerWeaponDTO dto = modelMapper.map(data.getWeaponId(), PlayerWeaponDTO.class);
            dto.setUsableCount(data.getUsableCount());
            playerWeaponDTOs.add(dto);
        }

        shopDealDTO.setWeapons(playerWeaponDTOs);

        return shopDealDTO;
    }

    @ApiOperation(value = "Get item list by choosing item type", response = BaseListItemModel.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully Buy Item"),
            @ApiResponse(code = 400, message = "Required Integer parameter 'count or cursor' is not present"),
            @ApiResponse(code = 401, message = "Invalid or expired session-token"),
            @ApiResponse(code = 404, message = "Please check item type"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/{itemType}", method = RequestMethod.GET, produces = "application/json")
    public BaseListItemModel getItemList(@RequestHeader("session-token") String token,
                                         @PathVariable("itemType") String itemType,
                                         @RequestParam("count") String count,
                                         @RequestParam("cursor") String cursor) {
        int wantingCount, presentCursor;

        try {
            wantingCount = Integer.parseInt(count);
            presentCursor = Integer.parseInt(cursor);
        }
        catch (NumberFormatException e) {
            System.out.println(Integer.parseInt(count));
            throw new BadRequestException("Required Integer parameter 'count or cursor' is not present");
        }

        //Todo paging 처리
        if (itemType.equals("weapon")) {
            BaseListItemModel<WeaponModel> itemDatas = new BaseListItemModel();
            List<WeaponModel> list = weaponRepository.findAll();
            itemDatas.setDatas(list);
            itemDatas.setCount(list.size());
            itemDatas.setCursor(presentCursor + 1);

            return itemDatas;
        }

        throw new NotFoundException("Please check item type");
    }
}
