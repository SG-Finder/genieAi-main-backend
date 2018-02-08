package com.finder.genie_ai.model.session.json_config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.finder.genie_ai.model.session.SessionModel;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

@JsonComponent
public class SessionUserInfoSerializer extends JsonSerializer<SessionModel> {

    @Override
    public void serialize(SessionModel sessionModel,
            JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("ip", sessionModel.getIp());
                jsonGenerator.writeStringField("signin_at", sessionModel.getSigninAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss")));
                jsonGenerator.writeStringField("last_updated_at", sessionModel.getLastUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss")));
                jsonGenerator.writeEndObject();
            }

}
