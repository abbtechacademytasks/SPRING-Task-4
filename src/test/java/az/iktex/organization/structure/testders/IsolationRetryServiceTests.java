package az.iktex.organization.structure.testders;

import az.iktex.organization.structure.testders.response.SalaryChangeResponse;
import az.iktex.organization.structure.testders.service.IsolationDemoService;
import az.iktex.organization.structure.testders.service.IsolationRetryService;
import org.junit.jupiter.api.Test;
import org.springframework.dao.CannotAcquireLockException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IsolationRetryServiceTests {
    @Test
    void stopsAfterThreeFailedAttempts() {
        var transactionService = mock(IsolationDemoService.class);
        var conflict = new CannotAcquireLockException("conflict");
        when(transactionService.raiseRepeatableRead(1L, BigDecimal.TEN, 0)).thenThrow(conflict);
        var retry = new IsolationRetryService(transactionService);
        assertSame(conflict, assertThrows(CannotAcquireLockException.class,
                () -> retry.raise(1L, BigDecimal.TEN, 0)));
        verify(transactionService, times(3)).raiseRepeatableRead(1L, BigDecimal.TEN, 0);
    }

    @Test
    void doesNotRetryBusinessConflict() {
        var transactionService = mock(IsolationDemoService.class);
        when(transactionService.enrollSerializable(1L, 2L, 0)).thenThrow(new IllegalStateException("Course is full"));
        var retry = new IsolationRetryService(transactionService);
        assertThrows(IllegalStateException.class, () -> retry.enroll(1L, 2L, 0));
        verify(transactionService).enrollSerializable(1L, 2L, 0);
    }

    @Test
    void returnsTheSuccessfulAttemptWithoutFurtherCalls() {
        var transactionService = mock(IsolationDemoService.class);
        var response = new SalaryChangeResponse(1L, BigDecimal.TEN, new BigDecimal("20"), "REPEATABLE_READ");
        when(transactionService.raiseRepeatableRead(1L, BigDecimal.TEN, 0))
                .thenThrow(new CannotAcquireLockException("conflict")).thenReturn(response);
        assertSame(response, new IsolationRetryService(transactionService).raise(1L, BigDecimal.TEN, 0));
        verify(transactionService, times(2)).raiseRepeatableRead(1L, BigDecimal.TEN, 0);
    }
}
