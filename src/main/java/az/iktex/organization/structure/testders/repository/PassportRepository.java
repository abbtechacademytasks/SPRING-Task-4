package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.Passport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PassportRepository extends JpaRepository<Passport, Long> {
    // ORM
    Optional<Passport> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    boolean existsBySerialNumber(String serialNumber);

    // JPQL
    @Query("""
        select p
        from Passport p
        join fetch p.user
        where p.id = :id
        """)
    Optional<Passport> findByIdWithUser(@Param("id") Long id);

    @Query("""
        select p
        from Passport p
        where p.country = :country
        """)
    List<Passport> findAllByCountry(@Param("country") String country);
}
