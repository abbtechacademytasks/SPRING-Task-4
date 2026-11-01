package az.iktex.organization.structure.testders.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcEnrollmentRepository {
    private final JdbcTemplate jdbcTemplate;

    public boolean studentExists(Long studentId) {
        return jdbcTemplate.queryForObject("select exists(select 1 from students where id = ?)",
                Boolean.class, studentId);
    }

    public Optional<Integer> lockCourse(Long courseId) {
        return jdbcTemplate.queryForList("select capacity from courses where id = ? for update",
                        Integer.class, courseId).stream().findFirst();
    }

    public boolean isEnrolled(Long studentId, Long courseId) {
        return jdbcTemplate.queryForObject(
                "select exists(select 1 from student_course where student_id = ? and course_id = ?)",
                Boolean.class, studentId, courseId);
    }

    public void enroll(Long studentId, Long courseId) {
        jdbcTemplate.update("insert into student_course(student_id, course_id) values (?, ?)", studentId, courseId);
    }

    public int countStudents(Long courseId) {
        return jdbcTemplate.queryForObject("select count(*) from student_course where course_id = ?",
                Integer.class, courseId);
    }
}
