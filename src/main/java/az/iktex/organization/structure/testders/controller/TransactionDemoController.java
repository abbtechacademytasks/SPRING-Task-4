package az.iktex.organization.structure.testders.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import az.iktex.organization.structure.testders.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Course", description = "Course management")
@RestController
@RequestMapping("/api/tx-demo")
@RequiredArgsConstructor
public class TransactionDemoController {

    private final EnrollmentService enrollmentService;

    @Operation(
            summary = "Demonstrate mandatory transaction",
            description = "Calling without an existing transaction returns 409 (ERR-1009)."
    )
    @PostMapping("/reserve-seat/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reserveSeat(
            @PathVariable("courseId") Long courseId
    ) {
        enrollmentService.reserveSeat(courseId);
    }
}