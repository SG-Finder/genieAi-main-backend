package com.finder.genie_ai.model.user.command;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@ToString
public class UserSignUpCommand {

    @NotNull
    private String userId;
    @NotNull
    private String passwd;
    @NotNull
    private String userName;
    @NotNull
    private String email;
    @NotNull
    private String birth;
    @NotNull
    private String introduce;

}
