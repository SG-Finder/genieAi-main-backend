package com.finder.genie_ai.dto;

import com.finder.genie_ai.enumdata.Weapon;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class ShopDealDTO {

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("weapons")
    private List<PlayerWeaponDTO> weapons;

    @SerializedName("point")
    private int point;

}
