package com.finder.genie_ai.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PlayerRankModel {

    @ApiModelProperty(notes = "player's nickname", required = true)
    private String nickname;
    @ApiModelProperty(notes = "player's score", required = true)
    private double score;
    @ApiModelProperty(notes = "player's rank", required = true)
    private long rank;
}
