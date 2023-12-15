//package com.example.GraplerEnhancemet.dto;
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.databind.JsonSerializer;
//import com.fasterxml.jackson.databind.SerializerProvider;
//import org.apache.tomcat.util.codec.binary.Base64;
//
//import java.io.IOException;
//
//
//public class CompanyDTOSerializer extends JsonSerializer<CompanyDTO> {
//    @Override
//    public void serialize(CompanyDTO companyDto, JsonGenerator jsonGenerator, SerializerProvider serializers)
//            throws IOException {
//
//        jsonGenerator.writeStartObject();
//        jsonGenerator.writeNumberField("id", companyDto.getId());
//        jsonGenerator.writeStringField("name", companyDto.getName());
//        jsonGenerator.writeStringField("email", companyDto.getEmail());
//
//        byte[] logoBytes = companyDto.getLogo();
//        String logoBase64 = Base64.encodeBase64String(logoBytes);
//        jsonGenerator.writeStringField("logo", logoBase64);
//
//       }
//}
