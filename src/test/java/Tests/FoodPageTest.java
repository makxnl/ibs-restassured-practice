package Tests;

import io.restassured.RestAssured;
import io.restassured.filter.cookie.CookieFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.*;


public class FoodPageTest {

    private static RequestSpecification requestSpec;
    private static ResponseSpecification responseSpec;
    private static CookieFilter cookieFilter = new CookieFilter();

    @BeforeAll
    static void init() {
        requestSpec = RestAssured.given()
                .baseUri("http://localhost:8080")
                .contentType(ContentType.JSON);
        responseSpec = RestAssured
                .expect()
                .statusCode(200);
    }

    @AfterEach
    void dataReset() {
        RestAssured.given()
                .baseUri("http://localhost:8080")
                .header("accept", "*/*")
                .filter(cookieFilter)
                .body("")
                .post("/api/data/reset")
                .then()
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
                .filter(cookieFilter)
                .when()
                .post("/api/food")
                .then()
                .spec(responseSpec);

         String response = requestSpec
                 .when()
                 .filter(cookieFilter)
                 .get("/food")
                 .then()
                 .spec(responseSpec)
                 .extract().asString();

        Document doc = Jsoup.parse(response);
        Elements rows = doc.select("table.table tbody tr");
        Element lastRow = rows.get(rows.size() - 1);
        Elements cellId = lastRow.select("th");
        Elements cells = lastRow.select("td");

        Assertions.assertAll("Проверка добавления товара",
                () -> Assertions.assertEquals("5", cellId.get(0).text(),
                        "Id неверно"),
                () -> Assertions.assertEquals("Лук", cells.get(0).text(), "Имя неверно"),
                () -> Assertions.assertEquals("Овощ", cells.get(1).text(), "Тип неверно"),
                () -> Assertions.assertEquals("false", cells.get(2).text(),
                        "Неверное поле экзотический")
                );
    }

    @Test
    @DisplayName("Добавление экзотического фрукта в список товаров")
    void addExoticFruit() {

         requestSpec
                .body("{\n" +
                        "  \"name\": \"Чили\",\n" +
                        "  \"type\": \"FRUIT\",\n" +
                        "  \"exotic\": true\n" +
                        "}")
                .filter(cookieFilter)
                .when()
                .post("/api/food")
                .then()
                .spec(responseSpec);

         String response = requestSpec
                 .when()
                 .filter(cookieFilter)
                 .get("/food")
                 .then()
                 .spec(responseSpec)
                 .extract().asString();

        Document doc = Jsoup.parse(response);
        Elements rows = doc.select("table.table tbody tr");
        Element lastRow = rows.get(rows.size() - 1);
        Elements cellId = lastRow.select("th");
        Elements cells = lastRow.select("td");

        Assertions.assertAll("Проверка добавления товара",
                () -> Assertions.assertEquals("5", cellId.get(0).text(),
                        "Id неверно"),
                () -> Assertions.assertEquals("Чили", cells.get(0).text(), "Имя неверно"),
                () -> Assertions.assertEquals("Фрукт", cells.get(1).text(), "Тип неверно"),
                () -> Assertions.assertEquals("true", cells.get(2).text(),
                        "Неверное поле экзотический")
                );
    }
}
