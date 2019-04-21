import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

public class ApiTest extends APITestCase {

    private static final String AUTHOR = "Robert C. Martin";
    private static final String TITLE = "Clean Code";

    @BeforeClass
    public static void apiMustStartWithAnEmptyStore() {

        given().
                when().
                get(API_ROOT).
                then().
                assertThat().
                statusCode(HttpStatus.SC_OK).
                body("$", empty());

    }

    @Test
    public void titleIsRequiredWhenPuttingABook() {
        Book bookWithoutTitle = new Book(AUTHOR, null);
        putAndVerifyErrorMessage(bookWithoutTitle, getFieldRequiredError("title"));
    }

    @Test
    public void authorIsRequiredWhenPuttingABook() {
        Book bookWithoutAuthor = new Book(null, TITLE);
        putAndVerifyErrorMessage(bookWithoutAuthor, getFieldRequiredError("author"));
    }

    @Test
    public void titleCannotBeEmptyWhenPuttingABook() {
        Book bookWithoutTitle = new Book(AUTHOR, "");
        putAndVerifyErrorMessage(bookWithoutTitle, getFieldCanNotBeEmptyError("title"));
    }

    @Test
    public void authorCannotBeEmptyWhenPuttingABook() {
        Book bookWithoutAuthor = new Book("", TITLE);
        putAndVerifyErrorMessage(bookWithoutAuthor, getFieldCanNotBeEmptyError("author"));
    }

    @Test
    public void idFieldCannotBeSent() {
        Book bookWithId = new Book(AUTHOR, TITLE);
        bookWithId.setId(1);
        given().
                contentType(ContentType.JSON).
                body(bookWithId).
                when().
                put(API_ROOT).
                then().
                assertThat().
                statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void puttingANewBook() {
        Book book = new Book("Martin Fowler", "Refactoring");
        Book responseBook = putBook(book);
        Assert.assertTrue("Different book has returned", book.equals(responseBook));
        Book bookHasBeenPut = getBook(responseBook.getId());
        Assert.assertTrue("The book has been put couldn't found", book.equals(bookHasBeenPut));
    }

    @Test
    public void puttingDuplicateBookShouldFail() throws Exception {
        Book book = new Book(AUTHOR, TITLE);
        putBook(book);
        given().
                contentType(ContentType.JSON).
                body(book).
                when().
                put(API_ROOT).
                then().
                assertThat().
                statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("", contains("Another book with similar title and author already exists."));
    }

    private void putAndVerifyErrorMessage(Book book, String errorMessage) {
        given().
                contentType(ContentType.JSON).
                body(book).
                when().
                put(API_ROOT).
                then().
                assertThat().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body("$", contains(errorMessage));
    }

    private String getFieldRequiredError(String field) {
        return "Field '" + field + "' is required.";
    }

    private String getFieldCanNotBeEmptyError(String field) {
        return "Field '" + field + "' cannot be empty.";
    }


}
