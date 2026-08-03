package com.hemanth.automation.api;
import com.hemanth.automation.config.ConfigReader;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class ApiBase {

    private static final RequestSpecification JSON_SPEC = new RequestSpecBuilder()
            .setBaseUri(ConfigReader.getProperty("api.base.url"))
            .setContentType(ContentType.JSON)
            .build();

    private static final RequestSpecification FORM_SPEC = new RequestSpecBuilder()
            .setBaseUri(ConfigReader.getProperty("api.base.url"))
            .setContentType(ContentType.URLENC)
            .build();

    public static RequestSpecification requestSpec() {
        return JSON_SPEC;
    }

    public static RequestSpecification formRequestSpec() {
        return FORM_SPEC;
    }
}
