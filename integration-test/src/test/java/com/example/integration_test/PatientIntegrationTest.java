package com.example.integration_test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class PatientIntegrationTest {
    @BeforeAll
    static void setUp(){
        RestAssured.baseURI = "http://localhost:4004";
    }

    @Test
    public void shouldReturnPatientWithValidToken(){
        String loginPayload = """
                {
                "email":"testuser@test.com",
                "password":"password123"
                }
                """;


        String token = given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                        .extract().jsonPath().get("jwt_token");


       given()
               .header("Authorization","Bearer"+ token)
               .when()
               .get("/api/patient")
               .then()
               .log().all()
               .statusCode(200)
               .body("patient",notNullValue());
    }
}
