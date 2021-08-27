package com.doug.rest;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.util.Random;

import io.restassured.RestAssured;


/*
 * Restassured information found here:
 * https://github.com/jayway/rest-assured
 */

@SuppressWarnings("deprecation")
public final class LoadTestRestEndpoint {

    public static void main(String[] args) throws IOException {
        new LoadTestRestEndpoint().doMain(args);
    }

    public void doMain(String[] args) throws IOException {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.baseURI = "https://localhost";
        RestAssured.port = 8888;
        performGET();

    }

    public void performGET() {
        try {
            Random rand = new Random();
            String userName = "";

            while (true) {
                int randInt = rand.nextInt(1000000);
                userName = "user." + randInt;
                given().auth()
                        .preemptive()
                        .basic(userName, "password")
                        .when()
                        .get("/directory/v1")
                        .then()
                        .assertThat()
                        .statusCode(200);
            }
        } catch (AssertionError ae) {
            System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + ae.getMessage());
        }

    }

}
