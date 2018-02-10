package com.finder.genie_ai.dto;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class HistoryDTO {

    @ApiModelProperty(notes = "Player's win count", required = true)
    @SerializedName("win")
    private int win;

    @ApiModelProperty(notes = "Player's lose count", required = true)
    @SerializedName("lose")
    private int lose;

    @ApiModelProperty(notes = "Player's oneShot count", required = true)
    @SerializedName("oneShot")
    private int oneShot;

    @ApiModelProperty(notes = "Player's finder count", required = true)
    @SerializedName("finder")
    private int finder;

    @ApiModelProperty(notes = "Player's lastWeekRank", required = true)
    @SerializedName("lastWeekRank")
    private int lastWeekRank;

}
