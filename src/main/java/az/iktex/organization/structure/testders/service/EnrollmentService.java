package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.exception.NotFoundException;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.exception.CourseFullException;
import az.iktex.organization.structure.testders.entity.Course;
import az.iktex.organization.structure.testders.repository.CourseRepository;
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
                        new NotFoundException(ErrorCode.COURSE_NOT_FOUND, courseId));

        int studentCount =
                courseRepository.countStudentsByCourseId(courseId);

        if (studentCount >= course.getCapacity()) {
            throw new CourseFullException(courseId);
        }
    }
}