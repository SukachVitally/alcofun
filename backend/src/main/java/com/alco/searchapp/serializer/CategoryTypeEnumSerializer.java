package com.alco.searchapp.serializer;

import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class CategoryTypeEnumSerializer extends JsonSerializer<CategoryTypeEnum> {
    @Override
    public void serialize(CategoryTypeEnum object, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(object.getName());
    }
}
