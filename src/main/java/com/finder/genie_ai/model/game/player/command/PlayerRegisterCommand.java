package com.finder.genie_ai.model.game.player.command;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@ToString
public class PlayerRegisterCommand {

    @NotNull
    private String nickname;
    @NotNull
    private String userId;

}
