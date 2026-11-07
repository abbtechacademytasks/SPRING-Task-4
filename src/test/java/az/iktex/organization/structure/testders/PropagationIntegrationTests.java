package az.iktex.organization.structure.testders;

import az.iktex.organization.structure.testders.request.EmployeeRequest;
import az.iktex.organization.structure.testders.request.TransferRequest;
import az.iktex.organization.structure.testders.service.AuditLogService;
import az.iktex.organization.structure.testders.service.EmployeeService;
import az.iktex.organization.structure.testders.enums.AuditStatus;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5432/school_task21_cache_checks",
        "spring.jpa.properties.hibernate.session.events.log=false"
})
class PropagationIntegrationTests {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void failedTransferRollsBackEmployeeAndChecksAuditTransaction() throws Exception {
        boolean independentAudit = !"REQUIRED".equals(System.getenv("C2_AUDIT_MODE"));
        Propagation expected = independentAudit ? Propagation.REQUIRES_NEW : Propagation.REQUIRED;
        assertEquals(expected, AuditLogService.class.getMethod("log", String.class, String.class,
                Long.class, AuditStatus.class, String.class).getAnnotation(Transactional.class).propagation());

        Long id = createEmployee("9000");
        try {
            BusinessException error = assertThrows(BusinessException.class,
                    () -> employeeService.transfer(id, new TransferRequest(2L, new BigDecimal("15"))));
            assertEquals(ErrorCode.EMPLOYEE_SALARY_LIMIT, error.getErrorCode());
            assertEquals(1L, jdbcTemplate.queryForObject(
                    "select department_id from employees where id = ?", Long.class, id));
            assertEquals(0, new BigDecimal("9000").compareTo(jdbcTemplate.queryForObject(
                    "select salary from employees where id = ?", BigDecimal.class, id)));

            Integer auditRows = jdbcTemplate.queryForObject(
                    "select count(*) from audit_logs where entity_name = 'Employee' and entity_id = ? and status = 'FAILED'",
                    Integer.class, id);
            assertEquals(independentAudit ? 1 : 0, auditRows);
            System.out.println("C2 CHECK: audit=" + expected + "; failed transfer: department1 salary9000; FAILED rows=" + auditRows);
        } finally {
            cleanUp(id);
        }
    }

    @Test
    void successfulTransferCommitsEmployeeAndSuccessAudit() {
        Long id = createEmployee("1000");
        try {
            employeeService.transfer(id, new TransferRequest(2L, new BigDecimal("15")));
            assertEquals(2L, jdbcTemplate.queryForObject(
                    "select department_id from employees where id = ?", Long.class, id));
            assertEquals(0, new BigDecimal("1150").compareTo(jdbcTemplate.queryForObject(
                    "select salary from employees where id = ?", BigDecimal.class, id)));
            assertEquals(1, jdbcTemplate.queryForObject(
                    "select count(*) from audit_logs where entity_name = 'Employee' and entity_id = ? and status = 'SUCCESS'",
                    Integer.class, id));
            System.out.println("C2 CHECK: success transfer department2 salary1150; SUCCESS rows=1");
        } finally {
            cleanUp(id);
        }
    }

    private Long createEmployee(String salary) {
        return employeeService.create(new EmployeeRequest(
                "C2Check", UUID.randomUUID().toString(), new BigDecimal(salary), 1L)).id();
    }

    private void cleanUp(Long id) {
        jdbcTemplate.update("delete from audit_logs where entity_name = 'Employee' and entity_id = ?", id);
        employeeService.delete(id);
    }
}
