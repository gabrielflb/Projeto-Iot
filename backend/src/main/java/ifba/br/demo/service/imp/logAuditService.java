package ifba.br.demo.service.imp;

import ifba.br.demo.domain.entity.LogAudit;
import ifba.br.demo.repository.logAuditoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class logAuditService {

    @Autowired
    private logAuditoRepository log;

    @Transactional
    public LogAudit logAction(String username, String action, Long resourceId, String description) {
        LogAudit auditLog = new LogAudit();
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setUsername(username);
        auditLog.setAction(action);
        auditLog.setResourceId(resourceId);
        auditLog.setDescription(description);
        auditLog.setResult("SUCCESS");

        return log.save(auditLog);
    }

    @Transactional
    public LogAudit logAction(String username, String action, String description) {
        return logAction(username, action, null, description);
    }

    @Transactional
    public LogAudit logActionWithResult(String username, String action, Long resourceId,
                                        String description, String result) {
        LogAudit auditLog = new LogAudit();
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setUsername(username);
        auditLog.setAction(action);
        auditLog.setResourceId(resourceId);
        auditLog.setDescription(description);
        auditLog.setResult(result);

        return log.save(auditLog);
    }
    @Transactional
    public void logFailedAttempt(String username, String action, Long resourceId, String description) {
        logActionWithResult(username, action, resourceId, description, "FAILED");
    }
}
