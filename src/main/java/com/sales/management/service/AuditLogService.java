package com.sales.management.service;

import com.sales.management.model.entity.AuditLog;
import com.sales.management.model.entity.User;
import com.sales.management.repository.AuditLogRepository;
import com.sales.management.repository.UserRepository;
import com.sales.management.util.Constants;
import com.sales.management.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    /**
     * Create an audit log entry
     */
    @Transactional
    public AuditLog createAuditLog(String entityType, Long entityId, String action, 
                                   String oldValue, String newValue) {
        User user = getCurrentUser();
        
        AuditLog auditLog = AuditLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .oldValue(oldValue)
                .newValue(newValue)
                .user(user)
                .ipAddress(getClientIpAddress())
                .userAgent(getUserAgent())
                .timestamp(LocalDateTime.now())
                .build();

        AuditLog saved = auditLogRepository.save(auditLog);
        log.info("Audit log created: {} - {} - {} by user {}", entityType, entityId, action, user.getId());
        return saved;
    }

    /**
     * Create an audit log entry with only action (for simple operations like DELETE)
     */
    @Transactional
    public AuditLog createAuditLog(String entityType, Long entityId, String action) {
        return createAuditLog(entityType, entityId, action, null, null);
    }

    /**
     * Get all audit logs for a specific entity (audit trail)
     */
    public List<AuditLog> getEntityAuditTrail(String entityType, Long entityId) {
        return auditLogRepository.findEntityAuditTrail(entityType, entityId);
    }

    /**
     * Search by entity type only
     */
    public Page<AuditLog> findByEntityType(String entityType, Pageable pageable) {
        return auditLogRepository.findByEntityType(entityType, pageable);
    }

    /**
     * Search by entity type and entity ID
     */
    public Page<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId, Pageable pageable) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId, pageable);
    }

    /**
     * Search by action type (CREATE, UPDATE, DELETE)
     */
    public Page<AuditLog> findByAction(String action, Pageable pageable) {
        return auditLogRepository.findByAction(action, pageable);
    }

    /**
     * Search by user ID
     */
    public Page<AuditLog> findByUser(Long userId, Pageable pageable) {
        return auditLogRepository.findByUserId(userId, pageable);
    }

    /**
     * Search by user ID and action
     */
    public Page<AuditLog> findByUserAndAction(Long userId, String action, Pageable pageable) {
        return auditLogRepository.findByUserIdAndAction(userId, action, pageable);
    }

    /**
     * Search by time period
     */
    public Page<AuditLog> findByTimePeriod(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return auditLogRepository.findByTimestampBetween(startDate, endDate, pageable);
    }

    /**
     * Search by entity type and time period
     */
    public Page<AuditLog> findByEntityTypeAndTimePeriod(String entityType, LocalDateTime startDate, 
                                                        LocalDateTime endDate, Pageable pageable) {
        return auditLogRepository.findByEntityTypeAndTimestampBetween(entityType, startDate, endDate, pageable);
    }

    /**
     * Search by user and time period
     */
    public Page<AuditLog> findByUserAndTimePeriod(Long userId, LocalDateTime startDate, 
                                                  LocalDateTime endDate, Pageable pageable) {
        return auditLogRepository.findByUserIdAndTimestampBetween(userId, startDate, endDate, pageable);
    }

    /**
     * Search by action and time period
     */
    public Page<AuditLog> findByActionAndTimePeriod(String action, LocalDateTime startDate, 
                                                    LocalDateTime endDate, Pageable pageable) {
        return auditLogRepository.findByActionAndTimestampBetween(action, startDate, endDate, pageable);
    }

    /**
     * Complex search with multiple criteria
     * All parameters are optional (null filters are ignored)
     */
    public Page<AuditLog> searchAuditLogs(String entityType, String action, Long userId, 
                                          LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        // Set default time period if not provided
        LocalDateTime start = startDate != null ? startDate : LocalDateTime.now().minusMonths(1);
        LocalDateTime end = endDate != null ? endDate : LocalDateTime.now();

        return auditLogRepository.searchAuditLogs(entityType, action, userId, start, end, pageable);
    }

    /**
     * Get current authenticated user
     */
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException(Constants.UNAUTHORIZED_ACCESS));
    }

    /**
     * Get client IP address from request
     */
    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                String xForwardedFor = attributes.getRequest().getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                return attributes.getRequest().getRemoteAddr();
            }
        } catch (Exception e) {
            log.debug("Could not retrieve client IP address", e);
        }
        return "UNKNOWN";
    }

    /**
     * Get user agent from request
     */
    private String getUserAgent() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                return attributes.getRequest().getHeader("User-Agent");
            }
        } catch (Exception e) {
            log.debug("Could not retrieve user agent", e);
        }
        return "UNKNOWN";
    }
}
