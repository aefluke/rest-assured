import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

class APITestCase {

    static final String API_ROOT = "http://www.books.com/api/books/";

    Book putBook(Book book) {
        return given().
                contentType(ContentType.JSON).
                body(book).
                when().
                put(API_ROOT).
                getBody().
                as(Book.class);
    }

    Book getBook(Integer id) {
        return given().
                contentType(ContentType.JSON).
                when().
                get(API_ROOT + id).
                getBody().
                as(Book.class);
    }
}
