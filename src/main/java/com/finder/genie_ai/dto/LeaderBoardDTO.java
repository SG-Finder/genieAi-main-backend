package com.finder.genie_ai.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class LeaderBoardDTO {

    @ApiModelProperty(notes = "date", required = true)
    private LocalDate date;
    @ApiModelProperty(notes = "leaderboard list", required = true)
    private List<String> leaderBoard;

}
