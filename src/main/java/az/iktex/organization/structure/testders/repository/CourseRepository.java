package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // ORM
    boolean existsByTitle(String title);
    boolean existsByTitleAndIdNot(String title, Long id);
    List<Course> findAllByTeacher(String teacher);

    // JPQL
    @Query("""
            select c from Course c
            left join fetch c.students
            where c.id = :id
            """)
    Optional<Course> findByIdWithStudents(@Param("id") Long id);

    @Query("""
            select c from Student s
            join s.courses c
            where s.id = :studentId
            """)
    List<Course> findAllByStudentId(@Param("studentId") Long studentId);

    @Query("""
            select size(c.students) from Course c
            where c.id = :id
            """)
    Integer countStudentsByCourseId(@Param("id") Long id);
}
