package com.finder.genie_ai.model;

import com.finder.genie_ai.model.game.BaseItemModel;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BaseListItemModel<T extends BaseItemModel> {

    @ApiModelProperty(notes = "Item's list", required = true)
    @SerializedName("datas")
    private List<T> datas;
    @ApiModelProperty(notes = "Next Cursor position", required = true)
    @SerializedName("cursor")
    private int cursor;
    @ApiModelProperty(notes = "Item's total count", required = true)
    @SerializedName("totalCount")
    private int count;

}