package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tx-demo")
@RequiredArgsConstructor
public class TransactionDemoController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/reserve-seat/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reserveSeat(
            @PathVariable("courseId") Long courseId
    ) {
        enrollmentService.reserveSeat(courseId);
    }
}