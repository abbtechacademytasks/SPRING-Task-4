package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    // ORM
    boolean existsByTitle(String title);
    boolean existsByTitleAndIdNot(String title, Long id);
    List<Book> findAllByTitleContainingIgnoreCase(String title);
    List<Book> findAllByPriceLessThanEqualOrderByPriceAsc(BigDecimal price);

    // JPQL
    @Query("""
            select b from Book b
            left join fetch b.authorBooks ab
            left join fetch ab.author
            where b.id = :id
            """)
    Optional<Book> findByIdWithAuthors(@Param("id") Long id);

    @Query("""
            select ab.book from AuthorBook ab
            where ab.author.id = :authorId
            """)
    List<Book> findAllByAuthorId(@Param("authorId") Long authorId);
}
