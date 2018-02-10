package com.finder.genie_ai.controller.command;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GameResultCommand {

    @NotNull
    private String winner;
    @NotNull
    private boolean oneShot;
    @NotNull
    private boolean finder;
    @NotNull
    private String loser;
    @NotNull
    private String gameLog;

}
