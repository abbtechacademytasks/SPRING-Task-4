package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    // ORM
    boolean existsByNameAndSurname(String name, String surname);
    boolean existsByNameAndSurnameAndIdNot(String name, String surname, Long id);
    List<Author> findAllByCountry(String country);

    // JPQL
    @Query("""
            select a from Author a
            where a.authorBooks is empty
            """)
    List<Author> findAllWithoutBooks();

    @Query("""
            select a from Author a
            left join fetch a.authorBooks ab
            left join fetch ab.book
            where a.id = :id
            """)
    Optional<Author> findByIdWithBooks(@Param("id") Long id);
}
