package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // ORM
    boolean existsByIdAndCoursesId(Long id, Long courseId);
    List<Student> findAllBySurname(String surname);
    List<Student> findAllByAgeGreaterThanEqual(Integer age);

    // JPQL
    @Query("""
            select s from Student s
            left join fetch s.courses
            where s.id = :id
            """)
    Optional<Student> findByIdWithCourses(@Param("id") Long id);

    @Query("""
            select s from Student s
            where s.courses is empty
            """)
    List<Student> findAllWithoutCourses();
}
