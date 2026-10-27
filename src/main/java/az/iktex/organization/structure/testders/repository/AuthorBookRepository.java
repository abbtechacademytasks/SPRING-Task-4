package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.AuthorBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuthorBookRepository extends JpaRepository<AuthorBook, Long> {

    // ORM
    boolean existsByAuthorIdAndBookId(Long authorId, Long bookId);
    Optional<AuthorBook> findByAuthorIdAndBookId(Long authorId, Long bookId);
    List<AuthorBook> findAllByBookId(Long bookId);

    // JPQL
    @Query("""
            select ab from AuthorBook ab
            join fetch ab.author
            join fetch ab.book
            where ab.author.id = :authorId and ab.book.id = :bookId
            """)
    Optional<AuthorBook> findLinkWithAuthorAndBook(
            @Param("authorId") Long authorId, @Param("bookId") Long bookId);

    @Modifying
    @Query("""
            delete from AuthorBook ab
            where ab.author.id = :authorId
            """)
    int deleteAllByAuthorId(@Param("authorId") Long authorId);

    @Modifying
    @Query("""
            delete from AuthorBook ab
            where ab.book.id = :bookId
            """)
    int deleteAllByBookId(@Param("bookId") Long bookId);
}
