package az.iktex.organization.structure.testders.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses", uniqueConstraints = @UniqueConstraint(name = "uk_course_title", columnNames = "title"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "credit", nullable = false)
    private Integer credit;

    @Column(name = "teacher", nullable = false, length = 50)
    private String teacher;

    // Kurs silinəndə tələbələr silinməməlidir, ona görə cascade yoxdur.
    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    private Set<Student> students = new HashSet<>();
}
