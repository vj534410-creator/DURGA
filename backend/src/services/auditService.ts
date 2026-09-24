import { v4 as uuidv4 } from 'uuid';
import { db, AuditLogRecord } from '../db';

export class AuditService {
  static record(
    eventType: string,
    entityType: string,
    entityId: string,
    details: any,
    performedBy?: string,
    badgeId?: string,
    ipAddress?: string
  ): AuditLogRecord {
    const log: AuditLogRecord = {
      id: uuidv4(),
      event_type: eventType,
      entity_type: entityType,
      entity_id: entityId,
      performed_by: performedBy,
      badge_id: badgeId,
      ip_address: ipAddress || '127.0.0.1',
      action_details: details,
      timestamp: new Date().toISOString()
    };
    db.auditLogs.unshift(log);
    return log;
  }

  static getLogs(limit: number = 50): AuditLogRecord[] {
    return db.auditLogs.slice(0, limit);
  }
}
