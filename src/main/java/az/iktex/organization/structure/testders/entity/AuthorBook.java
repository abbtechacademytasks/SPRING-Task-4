package az.iktex.organization.structure.testders.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Ayrı entity əlaqəyə əlavə sahələr artırmağa imkan verir.
// Sadə JoinTable ilə müqayisədə daha çox kod və ayrıca entity tələb edir.
@Entity
@Table(
        name = "author_book",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_author_book",
                columnNames = {"author_id", "book_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Əlaqə silinəndə müəllif silinməməlidir, cascade yoxdur.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "author_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_ab_author")
    )
    private Author author;

    // Əlaqə silinəndə kitab silinməməlidir, cascade yoxdur.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "book_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_ab_book")
    )
    private Book book;
}
