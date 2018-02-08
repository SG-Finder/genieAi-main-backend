package com.finder.genie_ai.controller.command;

import lombok.Data;

@Data
public class GameResultCommand {

    private String winner;
    private boolean oneShot;
    private boolean finder;
    private String loser;
    private String gameLog;

}
