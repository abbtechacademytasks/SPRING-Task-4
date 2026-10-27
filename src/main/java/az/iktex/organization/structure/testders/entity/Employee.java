package az.iktex.organization.structure.testders.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;

@Entity
@Table(name = "employees")
@Check(name = "chk_employee_salary", constraints = "salary > 0")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "surname", nullable = false, length = 50)
    private String surname;

    // Pul məbləğini dəqiq saxlamaq üçün BigDecimal istifadə olunur.
    @Column(name = "salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal salary;

    // İşçi silinəndə şöbə silinməməlidir, ona görə cascade yoxdur.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "department_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_employee_department")
    )
    private Department department;
}