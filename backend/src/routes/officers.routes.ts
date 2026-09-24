import { Router, Request, Response } from 'express';
import { db } from '../db';
import { AuditService } from '../services/auditService';

const router = Router();

// GET /officers/my-cases - Return cases assigned strictly to this officer badge
router.get('/my-cases', (req: Request, res: Response): void => {
  const badgeId = String(req.query.badgeId || '');

  if (!badgeId) {
    res.status(400).json({ success: false, error: 'badgeId query parameter required' });
    return;
  }

  // Find officer user
  const officer = Array.from(db.users.values()).find(u => u.badge_id === badgeId);

  // Return cases assigned to this officer, or all unassigned cases for triage
  const allCases = Array.from(db.complaints.values());
  const assignedCases = allCases.filter(c =>
    (officer && c.assigned_officer_id === officer.id) ||
    !c.assigned_officer_id ||
    c.current_status === 'SUBMITTED_TO_DURGA'
  );

  AuditService.record(
    'OFFICER_CASES_ACCESSED',
    'CASE_LIST',
    'ASSIGNED',
    { count: assignedCases.length },
    officer?.id,
    badgeId
  );

  res.json({
    success: true,
    badgeId,
    count: assignedCases.length,
    cases: assignedCases
  });
});

// POST /officers/claim-case - Officer claims an unassigned case
router.post('/claim-case', (req: Request, res: Response): void => {
  const { caseId, badgeId } = req.body;

  const complaint = Array.from(db.complaints.values()).find(
    c => c.id === caseId || c.complaint_ref_no === caseId
  );

  if (!complaint) {
    res.status(404).json({ success: false, error: 'Case not found' });
    return;
  }

  const officer = Array.from(db.users.values()).find(u => u.badge_id === badgeId);
  complaint.assigned_officer_id = officer ? officer.id : badgeId;
  complaint.current_status = 'UNDER_INVESTIGATION';
  complaint.updated_at = new Date().toISOString();

  AuditService.record(
    'CASE_CLAIMED_BY_OFFICER',
    'COMPLAINT',
    complaint.id,
    { badgeId },
    officer?.id,
    badgeId
  );

  res.json({
    success: true,
    message: `Case ${complaint.complaint_ref_no} assigned to Officer ${badgeId}`,
    case: complaint
  });
});

export default router;
