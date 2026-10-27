package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // ORM
    boolean existsByNameAndSurnameAndDepartmentId(String name, String surname, Long departmentId);
    boolean existsByNameAndSurnameAndDepartmentIdAndIdNot(
            String name, String surname, Long departmentId, Long id);
    @EntityGraph(attributePaths = "department")
    List<Employee> findAllBySalaryBetween(BigDecimal min, BigDecimal max);

    // JPQL
    @Query("""
            select e from Employee e
            join fetch e.department
            where e.id = :id
            """)
    Optional<Employee> findByIdWithDepartment(@Param("id") Long id);

    @Query("""
            select e from Employee e
            join fetch e.department
            where e.department.id = :departmentId
            """)
    List<Employee> findAllByDepartmentIdWithDepartment(@Param("departmentId") Long departmentId);

    @Query("""
        select avg(e.salary)
        from Employee e
        where e.department.id = :departmentId
        """)
    Double findAverageSalaryByDepartmentId(
            @Param("departmentId") Long departmentId
    );
}
