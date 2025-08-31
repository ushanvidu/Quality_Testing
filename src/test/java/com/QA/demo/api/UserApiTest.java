package com.QA.demo.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserApiTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api";
    }

    @Test
    void testCreateUser() {
        String userJson = """
            {
                "name": "API Test User",
                "email": "apitest@example.com",
                "age": 28
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .when()
                .post("/users")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .body("name", equalTo("API Test User"))
                .body("email", equalTo("apitest@example.com"))
                .body("age", equalTo(28));
    }

    @Test
    void testCreateUserWithDuplicateEmail() {
        // First create a user
        String userJson = """
            {
                "name": "First User",
                "email": "duplicate@example.com",
                "age": 25
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .when()
                .post("/users");

        // Try to create another user with same email
        String duplicateUserJson = """
            {
                "name": "Second User",
                "email": "duplicate@example.com",
                "age": 30
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(duplicateUserJson)
                .when()
                .post("/users")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("Email already exists"));
    }

    @Test
    void testGetUserById() {
        // First create a user
        String userJson = """
            {
                "name": "Get Test User",
                "email": "gettest@example.com",
                "age": 32
            }
            """;

        Response response = given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .when()
                .post("/users");

        Long userId = response.jsonPath().getLong("id");

        // Get the user by ID
        given()
                .when()
                .get("/users/" + userId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(userId.intValue()))
                .body("name", equalTo("Get Test User"))
                .body("email", equalTo("gettest@example.com"))
                .body("age", equalTo(32));
    }

    @Test
    void testGetUserByIdNotFound() {
        given()
                .when()
                .get("/users/9999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testGetAllUsers() {
        given()
                .when()
                .get("/users")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(greaterThanOrEqualTo(0)));
    }

    @Test
    void testDeleteUser() {
        // First create a user
        String userJson = """
            {
                "name": "Delete Test User",
                "email": "deletetest@example.com",
                "age": 35
            }
            """;

        Response response = given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .when()
                .post("/users");

        Long userId = response.jsonPath().getLong("id");

        // Delete the user
        given()
                .when()
                .delete("/users/" + userId)
                .then()
                .statusCode(HttpStatus.OK.value());

        // Verify user is deleted
        given()
                .when()
                .get("/users/" + userId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testUpdateUser() {
        // First create a user
        String userJson = """
            {
                "name": "Original User",
                "email": "original@example.com",
                "age": 25
            }
            """;

        Response response = given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .when()
                .post("/users");

        Long userId = response.jsonPath().getLong("id");

        // Update the user
        String updateJson = """
            {
                "name": "Updated User",
                "email": "updated@example.com",
                "age": 30
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(updateJson)
                .when()
                .put("/users/" + userId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo("Updated User"))
                .body("email", equalTo("updated@example.com"))
                .body("age", equalTo(30));
    }
}