package com.alco.searchapp.serializer;

import com.alco.searchapp.constants.CharacteristicTypeEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;


public class CharacteristicTypeEnumSerializer extends JsonSerializer<CharacteristicTypeEnum> {
    @Override
    public void serialize(CharacteristicTypeEnum object, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(object.getName());
    }
}
