import { Router, Request, Response } from 'express';
import { v4 as uuidv4 } from 'uuid';
import { db } from '../db';
import { AuditService } from '../services/auditService';

const router = Router();

// GET /admin/stats - System-wide health & case statistics
router.get('/stats', (_req: Request, res: Response): void => {
  const complaints = Array.from(db.complaints.values());
  const users = Array.from(db.users.values());

  const stats = {
    totalCases: complaints.length,
    pendingReview: complaints.filter(c => c.current_status === 'SUBMITTED_TO_DURGA').length,
    underInvestigation: complaints.filter(c => c.current_status === 'UNDER_INVESTIGATION').length,
    resolved: complaints.filter(c => c.current_status === 'RESOLVED').length,
    activeOfficers: users.filter(u => u.roles.includes('ROLE_POLICE_OFFICER') || u.badge_id).length,
    totalAuditLogs: db.auditLogs.length,
    evidenceItemsRecorded: complaints.reduce((acc, c) => acc + (c.evidence_count || 0), 0)
  };

  res.json({ success: true, stats });
});

// GET /admin/audit-logs - View tamper-evident audit logs
router.get('/audit-logs', (req: Request, res: Response): void => {
  const limit = Number(req.query.limit) || 100;
  res.json({
    success: true,
    total: db.auditLogs.length,
    logs: AuditService.getLogs(limit)
  });
});

// GET /admin/officers - List verified police & nodal officers
router.get('/officers', (_req: Request, res: Response): void => {
  const officers = Array.from(db.users.values()).filter(
    u => u.roles.includes('ROLE_POLICE_OFFICER') ||
         u.roles.includes('ROLE_SENIOR_OFFICER') ||
         u.badge_id
  );

  res.json({ success: true, count: officers.length, officers });
});

// POST /admin/assign-officer - Assign case to designated police officer
router.post('/assign-officer', (req: Request, res: Response): void => {
  const { caseId, officerId, adminNotes, adminName } = req.body;

  const complaint = Array.from(db.complaints.values()).find(
    c => c.id === caseId || c.complaint_ref_no === caseId
  );

  if (!complaint) {
    res.status(404).json({ success: false, error: 'Case not found' });
    return;
  }

  const officer = db.users.get(officerId) || Array.from(db.users.values()).find(u => u.badge_id === officerId);

  complaint.assigned_officer_id = officer ? officer.id : officerId;
  complaint.assigned_officer_name = officer ? `${officer.full_name} (${officer.badge_id})` : officerId;
  complaint.current_status = 'ASSIGNED';
  complaint.updated_at = new Date().toISOString();

  // Add timeline entry
  db.timeline.push({
    id: uuidv4(),
    case_id: complaint.id,
    event_type: 'OFFICER_ASSIGNED',
    title: 'Case Assigned to Officer',
    description: `Assigned to ${complaint.assigned_officer_name}. Admin remarks: ${adminNotes || 'None'}`,
    actor_name: adminName || 'Admin Desk',
    actor_role: 'ADMIN',
    timestamp: complaint.updated_at
  });

  AuditService.record(
    'CASE_ASSIGNED_BY_ADMIN',
    'COMPLAINT',
    complaint.id,
    { officerId, officerName: complaint.assigned_officer_name, notes: adminNotes }
  );

  res.json({ success: true, message: 'Case assigned successfully', case: complaint });
});

export default router;
