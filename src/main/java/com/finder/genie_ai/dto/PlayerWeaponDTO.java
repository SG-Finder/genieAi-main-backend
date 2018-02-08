package com.finder.genie_ai.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class PlayerWeaponDTO {

    @SerializedName("name")
    private String name;

    @SerializedName("damage")
    private int damage;

    @SerializedName("price")
    private int price;

    @SerializedName("usableCount")
    private int usableCount = 0;

}
