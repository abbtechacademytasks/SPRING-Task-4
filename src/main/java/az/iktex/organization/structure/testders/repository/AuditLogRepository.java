package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
