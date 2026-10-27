package az.iktex.organization.structure.testders.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "surname", nullable = false, length = 50)
    private String surname;

    @Column(name = "country", length = 50)
    private String country;

    // Müəllif silinəndə yalnız əlaqələr silinir, kitablar saxlanılır.
    // Siyahıdan çıxarılan əlaqə orphanRemoval ilə silinir.
    @OneToMany(
            mappedBy = "author",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AuthorBook> authorBooks = new ArrayList<>();

    public void addBook(Book book) {
        AuthorBook authorBook = new AuthorBook();
        authorBook.setAuthor(this);
        authorBook.setBook(book);

        authorBooks.add(authorBook);
        book.getAuthorBooks().add(authorBook);
    }

    public void removeAuthorBook(AuthorBook authorBook) {
        if (authorBooks.remove(authorBook)) {
            authorBook.getBook().getAuthorBooks().remove(authorBook);
            authorBook.setAuthor(null);
            authorBook.setBook(null);
        }
    }
}