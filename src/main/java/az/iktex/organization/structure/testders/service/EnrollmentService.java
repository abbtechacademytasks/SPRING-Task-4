package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.Course;
import az.iktex.organization.structure.testders.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final CourseRepository courseRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void reserveSeat(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Course not found: " + courseId
                        ));

        int studentCount =
                courseRepository.countStudentsByCourseId(courseId);

        if (studentCount >= course.getCapacity()) {
            throw new IllegalStateException("Course is full");
        }
    }
}