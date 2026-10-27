package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.User;
import az.iktex.organization.structure.testders.enums.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // ORM
    @Override
    @EntityGraph(attributePaths = "passport")
    List<User> findAll();

    @EntityGraph(attributePaths = "passport")
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);

    @EntityGraph(attributePaths = "passport")
    List<User> findAllByGender(Gender gender);

    // JPQL
    @Query("""
        select u
        from User u
        left join fetch u.passport
        where u.id = :id
        """)
    Optional<User> findByIdWithPassport(@Param("id") Long id);

    @Query("""
        select u
        from User u
        where u.passport is null
        """)
    List<User> findAllWithoutPassport();

}
