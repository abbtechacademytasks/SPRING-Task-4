package az.iktex.organization.structure.testders;

import az.iktex.organization.structure.testders.entity.Course;
import az.iktex.organization.structure.testders.service.BulkEnrollmentService;
import az.iktex.organization.structure.testders.service.IsolationRetryService;
import az.iktex.organization.structure.testders.service.StudentService;
import jakarta.persistence.EntityManagerFactory;
import az.iktex.organization.structure.testders.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5432/school_task21_cache_checks",
        "spring.jpa.properties.hibernate.session.events.log=false"
})
@ExtendWith(OutputCaptureExtension.class)
class BonusIntegrationTests {
    @Autowired private BulkEnrollmentService bulkService;
    @Autowired private IsolationRetryService retryService;
    @Autowired private StudentService studentService;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private EntityManagerFactory entityManagerFactory;

    @Test
    void fullCourseInsertIsRolledBackToSavepointAndOtherCoursesCommit(CapturedOutput output) {
        Long student = createStudent();
        Long occupant = createStudent();
        List<Long> courses = new ArrayList<>();
        try {
            Long first = createCourse(); courses.add(first);
            Long full = createCourse(); courses.add(full);
            Long last = createCourse(); courses.add(last);
            jdbc.update("insert into student_course values (?, ?)", occupant, full);

            var response = bulkService.enroll(student, List.of(last, full, first));

            assertEquals(List.of(first, last), response.enrolledCourseIds());
            assertEquals(List.of(full), response.fullCourseIds());
            assertEquals(2, countLinks(student));
            assertEquals(0, jdbc.queryForObject(
                    "select count(*) from student_course where student_id = ? and course_id = ?",
                    Integer.class, student, full));
            assertEquals(1, countLinks(occupant));
            assertEquals(2, studentService.getById(student).courses().size());
            assertTrue(output.getOut().contains("Rolling back transaction to savepoint"));
            System.out.println("BONUS NESTED: successful links=2; full-course tentative insert rolled back; savepoint confirmed");
        } finally {
            cleanup(student, occupant, courses);
        }
    }

    @Test
    void unexpectedFailureRollsBackEvenSuccessfulNestedEnrollment() {
        Long student = createStudent();
        Long course = createCourse();
        try {
            assertThrows(NotFoundException.class,
                    () -> bulkService.enroll(student, List.of(course, Long.MAX_VALUE)));
            assertEquals(0, countLinks(student));
            System.out.println("BONUS NESTED: later missing course rolls back the whole outer transaction");
        } finally {
            cleanup(student, null, List.of(course));
        }
    }

    @Test
    void concurrentSalaryConflictRetriesAfterRollbackAndPreservesBothIncrements(CapturedOutput output) throws Exception {
        Long employee = jdbc.queryForObject(
                "insert into employees(name,surname,salary,department_id) values ('Retry',?,1000,1) returning id",
                Long.class, UUID.randomUUID().toString());
        try (var executor = Executors.newFixedThreadPool(2)) {
            CountDownLatch start = new CountDownLatch(1);
            var first = executor.submit(() -> {
                start.await(); return retryService.raise(employee, new BigDecimal("100"), 3000);
            });
            var second = executor.submit(() -> {
                start.await(); return retryService.raise(employee, new BigDecimal("100"), 3000);
            });
            start.countDown();
            boolean simultaneousReads = false;
            for (int attempt = 0; attempt < 100; attempt++) {
                int readers = jdbc.queryForObject("""
                        select count(*) from pg_stat_activity
                        where datname = current_database() and state = 'idle in transaction'
                        and query ilike '%from%employees%'
                        """, Integer.class);
                if (readers >= 2) { simultaneousReads = true; break; }
                Thread.sleep(20);
            }
            assertTrue(simultaneousReads, "Both initial salary reads must occur before either write");
            first.get(20, TimeUnit.SECONDS);
            second.get(20, TimeUnit.SECONDS);
            assertEquals(0, new BigDecimal("1200").compareTo(jdbc.queryForObject(
                    "select salary from employees where id = ?", BigDecimal.class, employee)));
            assertTrue(output.getOut().contains("Retry salary: attempt=2"));
            assertTrue(output.getOut().contains("conflict, starting a new transaction"));
            System.out.println("BONUS RETRY: two initial reads confirmed; serialization conflict retried; salary=1200");
        } finally {
            jdbc.update("delete from employees where id = ?", employee);
        }
    }

    private Long createStudent() {
        return jdbc.queryForObject("insert into students(name,surname,age) values ('Bonus',?,20) returning id",
                Long.class, UUID.randomUUID().toString());
    }

    private Long createCourse() {
        return jdbc.queryForObject("insert into courses(title,teacher,credit,capacity) values (?,'Bonus',3,1) returning id",
                Long.class, "Bonus-" + UUID.randomUUID());
    }

    private int countLinks(Long student) {
        return jdbc.queryForObject("select count(*) from student_course where student_id = ?", Integer.class, student);
    }

    private void cleanup(Long student, Long occupant, List<Long> courses) {
        jdbc.update("delete from student_course where student_id = ?", student);
        jdbc.update("delete from students where id = ?", student);
        if (occupant != null) {
            jdbc.update("delete from student_course where student_id = ?", occupant);
            jdbc.update("delete from students where id = ?", occupant);
        }
        for (Long course : courses) {
            jdbc.update("delete from courses where id = ?", course);
            entityManagerFactory.getCache().evict(Course.class, course);
        }
    }
}
