package az.iktex.organization.structure.testders.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@Check(name = "chk_book_price", constraints = "price >= 0")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "price", nullable = false, precision = 8, scale = 2)
    private BigDecimal price;

    @Column(name = "page_count", nullable = false)
    private Integer pageCount;

    // Kitab silinəndə yalnız əlaqələr silinir, müəlliflər saxlanılır.
    // Siyahıdan çıxarılan əlaqə orphanRemoval ilə silinir.
    @OneToMany(
            mappedBy = "book",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AuthorBook> authorBooks = new ArrayList<>();
}