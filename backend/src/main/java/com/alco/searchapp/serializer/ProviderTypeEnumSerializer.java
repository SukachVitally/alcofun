package com.alco.searchapp.serializer;

import com.alco.searchapp.constants.ProviderTypeEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class ProviderTypeEnumSerializer extends JsonSerializer<ProviderTypeEnum> {
    @Override
    public void serialize(ProviderTypeEnum object, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(object.getName());
    }
}
