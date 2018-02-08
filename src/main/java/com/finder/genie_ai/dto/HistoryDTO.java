package com.finder.genie_ai.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class HistoryDTO {

    @SerializedName("win")
    private int win;

    @SerializedName("lose")
    private int lose;

    @SerializedName("oneShot")
    private int oneShot;

    @SerializedName("finder")
    private int finder;

    @SerializedName("lastWeekRank")
    private int lastWeekRank;

}
