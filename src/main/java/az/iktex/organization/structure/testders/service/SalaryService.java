package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.exception.InvalidRequestException;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.exception.BusinessException;
import az.iktex.organization.structure.testders.entity.Employee;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class SalaryService {

    @Transactional(propagation = Propagation.REQUIRED)
    public void raise(Employee employee, BigDecimal percent) {
        if (percent == null || percent.signum() <= 0) {
            throw new InvalidRequestException(ErrorCode.INVALID_RAISE_PERCENT);
        }

        BigDecimal multiplier = BigDecimal.ONE.add(
                percent.divide(new BigDecimal("100"))
        );

        BigDecimal newSalary = employee.getSalary()
                .multiply(multiplier)
                .setScale(2, RoundingMode.HALF_UP);

        if (newSalary.compareTo(new BigDecimal("10000")) > 0) {
            throw new BusinessException(ErrorCode.EMPLOYEE_SALARY_LIMIT);
        }

        employee.setSalary(newSalary);
    }
}