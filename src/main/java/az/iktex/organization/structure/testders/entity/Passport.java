package az.iktex.organization.structure.testders.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "passports", uniqueConstraints = {
        @UniqueConstraint(name = "uk_passport_serial", columnNames = "serial_number"),
        @UniqueConstraint(name = "uk_passport_user", columnNames = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Passport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "serial_number", nullable = false, length = 20)
    private String serialNumber;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "country", nullable = false, length = 50)
    private String country;

    // Pasport silinəndə istifadəçi silinməməlidir, ona görə cascade yoxdur.
    // user_id unikaldır: bir istifadəçiyə yalnız bir pasport bağlana bilər.
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_passport_user")
    )
    private User user;
}
