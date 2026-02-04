package com.sales.management.controller;

import com.sales.management.model.entity.AuditLog;
import com.sales.management.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    /**
     * Get audit trail for a specific entity
     * GET /api/audit-logs/entity/{entityType}/{entityId}
     */
    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<AuditLog>> getEntityAuditTrail(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        List<AuditLog> auditTrail = auditLogService.getEntityAuditTrail(entityType, entityId);
        return ResponseEntity.ok(auditTrail);
    }

    /**
     * Search audit logs by entity type
     * GET /api/audit-logs/search/entity?entityType=SALE
     */
    @GetMapping("/search/entity")
    public ResponseEntity<Page<AuditLog>> searchByEntityType(
            @RequestParam String entityType,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AuditLog> logs = auditLogService.findByEntityType(entityType, pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * Search audit logs by entity type and entity ID
     * GET /api/audit-logs/search/entity?entityType=SALE&entityId=123
     */
    @GetMapping("/search/entity-details")
    public ResponseEntity<Page<AuditLog>> searchByEntityTypeAndId(
            @RequestParam String entityType,
            @RequestParam Long entityId,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AuditLog> logs = auditLogService.findByEntityTypeAndEntityId(entityType, entityId, pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * Search audit logs by action type
     * GET /api/audit-logs/search/action?action=CREATE
     */
    @GetMapping("/search/action")
    public ResponseEntity<Page<AuditLog>> searchByAction(
            @RequestParam String action,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AuditLog> logs = auditLogService.findByAction(action, pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * Search audit logs by user ID
     * GET /api/audit-logs/search/user?userId=1
     */
    @GetMapping("/search/user")
    public ResponseEntity<Page<AuditLog>> searchByUser(
            @RequestParam Long userId,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AuditLog> logs = auditLogService.findByUser(userId, pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * Search audit logs by user ID and action
     * GET /api/audit-logs/search/user-action?userId=1&action=CREATE
     */
    @GetMapping("/search/user-action")
    public ResponseEntity<Page<AuditLog>> searchByUserAndAction(
            @RequestParam Long userId,
            @RequestParam String action,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AuditLog> logs = auditLogService.findByUserAndAction(userId, action, pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * Search audit logs by time period
     * GET /api/audit-logs/search/time-period?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59
     */
    @GetMapping("/search/time-period")
    public ResponseEntity<Page<AuditLog>> searchByTimePeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AuditLog> logs = auditLogService.findByTimePeriod(startDate, endDate, pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * Search audit logs by entity type and time period
     * GET /api/audit-logs/search/entity-time?entityType=SALE&startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59
     */
    @GetMapping("/search/entity-time")
    public ResponseEntity<Page<AuditLog>> searchByEntityTypeAndTimePeriod(
            @RequestParam String entityType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AuditLog> logs = auditLogService.findByEntityTypeAndTimePeriod(entityType, startDate, endDate, pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * Search audit logs by user and time period
     * GET /api/audit-logs/search/user-time?userId=1&startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59
     */
    @GetMapping("/search/user-time")
    public ResponseEntity<Page<AuditLog>> searchByUserAndTimePeriod(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AuditLog> logs = auditLogService.findByUserAndTimePeriod(userId, startDate, endDate, pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * Search audit logs by action and time period
     * GET /api/audit-logs/search/action-time?action=CREATE&startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59
     */
    @GetMapping("/search/action-time")
    public ResponseEntity<Page<AuditLog>> searchByActionAndTimePeriod(
            @RequestParam String action,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AuditLog> logs = auditLogService.findByActionAndTimePeriod(action, startDate, endDate, pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * Complex search with multiple criteria
     * GET /api/audit-logs/search/advanced?entityType=SALE&action=CREATE&userId=1&startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59
     * All parameters except dates are optional
     */
    @GetMapping("/search/advanced")
    public ResponseEntity<Page<AuditLog>> advancedSearch(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AuditLog> logs = auditLogService.searchAuditLogs(entityType, action, userId, startDate, endDate, pageable);
        return ResponseEntity.ok(logs);
    }
}
