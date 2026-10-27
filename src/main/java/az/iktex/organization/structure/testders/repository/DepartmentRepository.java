package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // ORM
    Optional<Department> findByName(String name);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);

    // JPQL
    @Query("""
            select d from Department d
            left join fetch d.employees
            where d.id = :id
            """)
    Optional<Department> findByIdWithEmployees(@Param("id") Long id);

    @Query("""
            select d from Department d
            where d.employees is empty
            """)
    List<Department> findAllWithoutEmployees();

    @Query("""
            select count(e) > 0 from Employee e
            where e.department.id = :departmentId
            """)
    boolean hasEmployees(@Param("departmentId") Long departmentId);
}
