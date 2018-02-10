package com.finder.genie_ai.dto;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ShopDealDTO {

    @ApiModelProperty(notes = "Player's nickname", required = true)
    @SerializedName("nickname")
    private String nickname;

    @ApiModelProperty(notes = "Player's weapon list", required = true)
    @SerializedName("weapons")
    private List<PlayerWeaponDTO> weapons;

    @ApiModelProperty(notes = "Player's point", required = true)
    @SerializedName("point")
    private int point;

}
