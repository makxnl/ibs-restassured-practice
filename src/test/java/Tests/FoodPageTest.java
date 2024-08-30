package Tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.*;

public class FoodPageTest {

    private static RequestSpecification requestSpec;
    private static ResponseSpecification responseSpec;

    @BeforeAll
    static void init() {
        requestSpec = RestAssured.given()
                .baseUri("http://localhost:8080")
                .contentType(ContentType.JSON);
        responseSpec = RestAssured
                .expect()
                .statusCode(200);
    }

    @Test
    @DisplayName("Добавление неэкзотического овощя в список товаров")
    void addNotExoticVegetable() {

         requestSpec
                .body("{\n" +
                        "  \"name\": \"Лук\",\n" +
                        "  \"type\": \"VEGETABLE\",\n" +
                        "  \"exotic\": false\n" +
                        "}")
                .when()
                .log().all()
                .post("/api/food")
                .then()
                .spec(responseSpec);

         requestSpec
                 .when()
                 .get("/food")
                 .then()
                 .log().all()
                 .spec(responseSpec);
    }
}
