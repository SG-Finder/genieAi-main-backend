package com.finder.genie_ai.dto;

import com.finder.genie_ai.enumdata.Tier;
import com.finder.genie_ai.enumdata.Weapon;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerDTO {

    @ApiModelProperty(notes = "Player's nickname", required = true)
    @SerializedName("nickname")
    private String nickname;

    @ApiModelProperty(notes = "Player's tier", required = true)
    @SerializedName("tier")
    private Tier tier;

    @ApiModelProperty(notes = "Player's score", required = true)
    @SerializedName("score")
    private int score;

    @ApiModelProperty(notes = "Player's history", required = true)
    @SerializedName("history")
    private HistoryDTO history;

    @ApiModelProperty(notes = "Player's owning weapon list", required = true)
    @SerializedName("weapon")
    private List<PlayerWeaponDTO> weapons;

    @ApiModelProperty(notes = "Player's point", required = true)
    @SerializedName("point")
    private int point;

}
