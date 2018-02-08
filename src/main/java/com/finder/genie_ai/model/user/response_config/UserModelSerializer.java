package com.finder.genie_ai.model.user.response_config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.finder.genie_ai.model.user.UserModel;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class UserModelSerializer extends JsonSerializer<UserModel> {

    @Override
    public void serialize(UserModel userModel,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("userId", userModel.getUserId());
        jsonGenerator.writeStringField("userName", userModel.getUserName());
        jsonGenerator.writeStringField("email", userModel.getEmail());
        jsonGenerator.writeStringField("birth", userModel.getBirth().toString());
        jsonGenerator.writeStringField("introduce", userModel.getIntroduce());
        jsonGenerator.writeEndObject();
    }
}
