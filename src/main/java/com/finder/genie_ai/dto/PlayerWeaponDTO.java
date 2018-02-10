package com.finder.genie_ai.dto;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PlayerWeaponDTO {

    @ApiModelProperty(notes = "Weapon's name", required = true)
    @SerializedName("name")
    private String name;

    @ApiModelProperty(notes = "Weapon's damage", required = true)
    @SerializedName("damage")
    private int damage;

    @ApiModelProperty(notes = "Weapon's price", required = true)
    @SerializedName("price")
    private int price;

    @ApiModelProperty(notes = "Weapon's amount", required = true)
    @SerializedName("usableCount")
    private int usableCount = 0;

}
