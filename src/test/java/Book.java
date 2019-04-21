import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Book {
    private Integer id;
    private String author;
    private String title;

    public Book(String author, String title) {
        this.author = author;
        this.title = title;
    }

    public Boolean equals(Book book) {
        return this.author.equals(book.getAuthor()) && this.title.equals(book.getTitle());
    }

}
