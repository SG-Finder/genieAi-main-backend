package com.finder.genie_ai.model;

import com.finder.genie_ai.model.game.BaseItemModel;
import lombok.Data;

import java.util.List;

@Data
public class BaseListItemModel<T extends BaseItemModel> {

    private List<T> datas;
    private int cursor;
    private int count;

}