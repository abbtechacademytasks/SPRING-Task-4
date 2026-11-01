package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.AuditLog;
import az.iktex.organization.structure.testders.enums.AuditStatus;
import az.iktex.organization.structure.testders.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(
            String action,
            String entityName,
            Long entityId,
            AuditStatus status,
            String message
    ) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setEntityName(entityName);
        auditLog.setEntityId(entityId);
        auditLog.setStatus(status);
        auditLog.setMessage(message);

        auditLogRepository.save(auditLog);
    }
}