import { Router, Request, Response } from 'express';
import { v4 as uuidv4 } from 'uuid';
import { db, ComplaintRecord, TimelineEventRecord } from '../db';
import { AuditService } from '../services/auditService';

const router = Router();

// POST /cases - Used by Android App and Web Portals to file a complaint/case
router.post('/', (req: Request, res: Response): void => {
  try {
    const {
      caseId,
      anonymousDeviceId,
      category,
      title,
      description,
      originalLanguage,
      incidentDate,
      incidentTime,
      incidentLocation,
      latitude,
      longitude,
      isWitnessReport,
      evidenceCount,
      complainant,
      sharedItemsSummary,
      secureToken,
      priority
    } = req.body;

    const generatedId = caseId || `DURGA-${new Date().getFullYear()}-${Math.floor(100000 + Math.random() * 900000)}`;

    const complaint: ComplaintRecord = {
      id: uuidv4(),
      complaint_ref_no: generatedId,
      category_id: category || 'GENERAL_SAFETY',
      title: title || 'Safety Incident Complaint',
      original_description: description || '',
      structured_description: description || '',
      original_language: originalLanguage || 'en',
      incident_date: incidentDate || new Date().toISOString().split('T')[0],
      incident_time: incidentTime || '12:00',
      incident_location: incidentLocation || 'Gujarat, India',
      latitude: Number(latitude) || 0.0,
      longitude: Number(longitude) || 0.0,
      is_witness_report: Boolean(isWitnessReport),
      current_status: 'REPORT_SUBMITTED',
      priority: priority || 'NORMAL',
      evidence_count: Number(evidenceCount) || 0,
      complainant: complainant || {},
      shared_items_summary: sharedItemsSummary || 'Complaint Draft, Location',
      secure_token: secureToken || `sec-${uuidv4().substring(0, 8)}`,
      locked: true,
      lock_timestamp: new Date().toISOString(),
      created_at: new Date().toISOString(),
      updated_at: new Date().toISOString()
    };

    db.complaints.set(complaint.id, complaint);

    // Initial timeline record
    const timelineEvent: TimelineEventRecord = {
      id: uuidv4(),
      case_id: complaint.id,
      event_type: 'CASE_SUBMITTED',
      title: 'Incident Report Submitted',
      description: `Citizen filed complaint [${complaint.complaint_ref_no}] via mobile safety client.`,
      actor_name: complainant?.name ? 'Verified Complainant' : 'Citizen (Mobile App)',
      actor_role: 'CITIZEN',
      timestamp: complaint.created_at
    };
    db.timeline.push(timelineEvent);

    AuditService.record(
      'CASE_SUBMITTED',
      'COMPLAINT',
      complaint.id,
      {
        ref: complaint.complaint_ref_no,
        category: complaint.category_id,
        evidenceCount: complaint.evidence_count
      },
      undefined,
      undefined,
      req.ip
    );

    res.status(201).json({
      success: true,
      caseId: complaint.complaint_ref_no,
      complaintId: complaint.id,
      status: complaint.current_status,
      message: 'Complaint successfully registered in DURGA central vault'
    });
  } catch (error: any) {
    res.status(500).json({ success: false, error: error.message });
  }
});

// GET /cases - Search, filter, and paginate cases
router.get('/', (req: Request, res: Response): void => {
  const { status, category, priority, search, officerId, page = 1, limit = 50 } = req.query;
  let items = Array.from(db.complaints.values());

  // Search by ref, title, location, category
  if (search && String(search).trim() !== '') {
    const q = String(search).toLowerCase().trim();
    items = items.filter(c =>
      c.complaint_ref_no.toLowerCase().includes(q) ||
      c.title.toLowerCase().includes(q) ||
      c.incident_location.toLowerCase().includes(q) ||
      c.category_id.toLowerCase().includes(q) ||
      (c.assigned_officer_name && c.assigned_officer_name.toLowerCase().includes(q))
    );
  }

  if (status && status !== 'ALL') {
    items = items.filter(c => c.current_status.toUpperCase() === String(status).toUpperCase());
  }

  if (category && category !== 'ALL') {
    items = items.filter(c => c.category_id.toUpperCase() === String(category).toUpperCase());
  }

  if (priority && priority !== 'ALL') {
    items = items.filter(c => (c.priority || 'NORMAL').toUpperCase() === String(priority).toUpperCase());
  }

  if (officerId) {
    items = items.filter(c => c.assigned_officer_id === officerId || (c.assigned_officer_name && c.assigned_officer_name.includes(String(officerId))));
  }

  // Sort descending by creation date
  items.sort((a, b) => new Date(b.created_at).getTime() - new Date(a.created_at).getTime());

  const total = items.length;
  const p = Math.max(1, Number(page));
  const l = Math.max(1, Number(limit));
  const paginated = items.slice((p - 1) * l, p * l);

  res.json({
    success: true,
    total,
    page: p,
    limit: l,
    cases: paginated
  });
});

// GET /cases/lookup/qr - Fast QR Code / Secure Code Case Verification
router.get('/lookup/qr', (req: Request, res: Response): void => {
  handleQrLookup(req, res, String(req.query.code || ''));
});

// GET /cases/secure/:token - Route matching /case/secure/<token>
router.get('/secure/:token', (req: Request, res: Response): void => {
  handleQrLookup(req, res, String(req.params.token || ''));
});

// POST /cases/revoke-qr - Revoke / Deactivate a Case QR token
router.post('/revoke-qr', (req: Request, res: Response): void => {
  const { code } = req.body;
  if (!code) {
    res.status(400).json({ success: false, error: 'Token or Case ID is required' });
    return;
  }
  const cleanCode = String(code).trim();
  const complaint = Array.from(db.complaints.values()).find(
    c => c.complaint_ref_no.equalsIgnoreCase(cleanCode) ||
         c.secure_token === cleanCode ||
         c.id === cleanCode
  );
  if (!complaint) {
    res.status(404).json({ success: false, error: 'Case record not found.' });
    return;
  }
  complaint.qr_active = false;
  AuditService.record('CASE_QR_REVOKED', 'COMPLAINT', complaint.id, { code: cleanCode }, undefined, undefined, req.ip);
  res.json({ success: true, message: 'Case QR has been deactivated successfully', caseId: complaint.complaint_ref_no });
});

function handleQrLookup(req: Request, res: Response, rawCode: string): void {
  if (!rawCode || rawCode.trim() === '') {
    res.status(400).json({ success: false, error: 'Invalid DURGA Case QR', code: 'INVALID_QR' });
    return;
  }

  // Strip standard prefixes if QR encoded full URL or path
  let cleanCode = rawCode.trim();
  const prefixes = ['https://durga.gov.in/case/secure/', 'durga://case/secure/', '/case/secure/'];
  for (const prefix of prefixes) {
    if (cleanCode.startsWith(prefix)) {
      cleanCode = cleanCode.substring(prefix.length).trim();
      break;
    }
  }

  const complaint = Array.from(db.complaints.values()).find(
    c => c.complaint_ref_no.equalsIgnoreCase(cleanCode) ||
         c.secure_token === cleanCode ||
         c.id === cleanCode
  );

  if (!complaint) {
    res.status(404).json({ success: false, error: 'Case record not found.', code: 'CASE_NOT_FOUND' });
    return;
  }

  if (complaint.qr_active === false) {
    res.status(403).json({ success: false, error: 'This Case QR is no longer active.', code: 'QR_DEACTIVATED' });
    return;
  }

  const timelineEvents = db.timeline
    .filter(t => t.case_id === complaint.id)
    .sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime());

  const caseEvidence = Array.from(db.evidence.values())
    .filter(e => e.complaint_id === complaint.id || e.case_ref === complaint.complaint_ref_no);

  const photoCount = caseEvidence.filter(e => e.file_type === 'PHOTO').length;
  const videoCount = caseEvidence.filter(e => e.file_type === 'VIDEO').length;
  const audioCount = caseEvidence.filter(e => e.file_type === 'AUDIO').length;
  const docCount = caseEvidence.filter(e => e.file_type === 'DOCUMENT' || e.file_type === 'SCREENSHOT').length;

  AuditService.record('CASE_QR_VERIFIED', 'COMPLAINT', complaint.id, { code: cleanCode }, undefined, undefined, req.ip);

  res.json({
    success: true,
    caseId: complaint.complaint_ref_no,
    status: complaint.current_status,
    category: complaint.category_id,
    title: complaint.title,
    summary: complaint.structured_description || complaint.original_description || complaint.title,
    incidentDate: complaint.incident_date,
    incidentTime: complaint.incident_time,
    incidentLocation: complaint.incident_location || 'Not provided',
    createdAt: complaint.created_at,
    submittedAt: complaint.created_at,
    policeStation: complaint.police_station || (complaint.assigned_officer_name ? 'Central Mahila Police Station' : null),
    district: complaint.police_district || (complaint.assigned_officer_name ? 'Gandhinagar' : null),
    assignedInspector: complaint.assigned_inspector || (complaint.assigned_officer_name ? complaint.assigned_officer_name : null),
    investigatingOfficer: complaint.investigating_officer || null,
    assignedAt: complaint.assigned_at || null,
    evidenceSummary: {
      photoCount,
      videoCount,
      audioCount,
      documentCount: docCount,
      totalCount: complaint.evidence_count || caseEvidence.length
    },
    timeline: timelineEvents,
    secureToken: complaint.secure_token,
    isQrActive: true
  });
}

// Helper for case-insensitive matching
declare global {
  interface String {
    equalsIgnoreCase(other: string): boolean;
  }
}
String.prototype.equalsIgnoreCase = function (other: string) {
  return this.toLowerCase() === (other || '').toLowerCase();
};

// GET /cases/:id - Get complete case dossier (complaint, evidence, drafts, timeline, notes)
router.get('/:id', (req: Request, res: Response): void => {
  const { id } = req.params;
  const complaint = Array.from(db.complaints.values()).find(
    c => c.id === id || c.complaint_ref_no.equalsIgnoreCase(id)
  );

  if (!complaint) {
    res.status(404).json({ success: false, error: 'Case not found' });
    return;
  }

  const notes = db.caseNotes.filter(n => n.case_id === complaint.id || n.case_id === complaint.complaint_ref_no);
  const evidenceList = Array.from(db.evidence.values()).filter(e => e.complaint_id === complaint.id || e.case_ref === complaint.complaint_ref_no);
  const drafts = Array.from(db.drafts.values()).filter(d => d.case_id === complaint.id);
  const timeline = db.timeline.filter(t => t.case_id === complaint.id).sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime());

  AuditService.record('CASE_VIEWED', 'COMPLAINT', complaint.id, { ref: complaint.complaint_ref_no }, undefined, undefined, req.ip);

  res.json({
    success: true,
    case: complaint,
    evidence: evidenceList,
    drafts,
    timeline,
    notes
  });
});

// PATCH /cases/:id/status - Update case status with official remarks & timeline update
router.patch('/:id/status', (req: Request, res: Response): void => {
  const { id } = req.params;
  const { status, remarks, officerBadge, officerName } = req.body;

  const complaint = Array.from(db.complaints.values()).find(
    c => c.id === id || c.complaint_ref_no.equalsIgnoreCase(id)
  );

  if (!complaint) {
    res.status(404).json({ success: false, error: 'Case not found' });
    return;
  }

  const oldStatus = complaint.current_status;
  complaint.current_status = status;
  complaint.updated_at = new Date().toISOString();

  // Add official timeline record
  db.timeline.push({
    id: uuidv4(),
    case_id: complaint.id,
    event_type: 'STATUS_UPDATED',
    title: `Status Changed to ${status.replace(/_/g, ' ')}`,
    description: remarks || `Investigation status advanced from ${oldStatus} to ${status}.`,
    actor_name: officerName || officerBadge || 'Duty Officer',
    actor_role: officerBadge ? 'POLICE_OFFICER' : 'ADMIN',
    timestamp: complaint.updated_at
  });

  if (remarks) {
    db.caseNotes.push({
      id: uuidv4(),
      case_id: complaint.id,
      author_name: officerName || 'Duty Officer',
      badge_id: officerBadge || 'ADMIN',
      role: officerBadge ? 'POLICE_OFFICER' : 'ADMIN',
      note_type: 'STATUS_UPDATE',
      content: `[${status}] ${remarks}`,
      created_at: new Date().toISOString()
    });
  }

  AuditService.record(
    'CASE_STATUS_UPDATED',
    'COMPLAINT',
    complaint.id,
    { oldStatus, newStatus: status, remarks },
    undefined,
    officerBadge,
    req.ip
  );

  res.json({
    success: true,
    message: 'Case status updated successfully and recorded in timeline',
    case: complaint
  });
});

// POST /cases/:id/notes - Add official case note with strict role identification
router.post('/:id/notes', (req: Request, res: Response): void => {
  const { id } = req.params;
  const { content, noteType, officerBadge, officerName, role } = req.body;

  const complaint = Array.from(db.complaints.values()).find(
    c => c.id === id || c.complaint_ref_no.equalsIgnoreCase(id)
  );

  if (!complaint) {
    res.status(404).json({ success: false, error: 'Case not found' });
    return;
  }

  if (!content || !content.trim()) {
    res.status(400).json({ success: false, error: 'Note content cannot be empty' });
    return;
  }

  const note = {
    id: uuidv4(),
    case_id: complaint.id,
    author_name: officerName || 'Authorized Officer',
    badge_id: officerBadge || 'STATION_DESK',
    role: role || (officerBadge ? 'POLICE_OFFICER' : 'ADMIN'),
    note_type: noteType || 'OFFICIAL_DISPATCH_NOTE',
    content: content.trim(),
    created_at: new Date().toISOString()
  };

  db.caseNotes.push(note);

  // Record timeline event
  db.timeline.push({
    id: uuidv4(),
    case_id: complaint.id,
    event_type: 'NOTE_ADDED',
    title: 'Official Note Deposited',
    description: `Note added by ${note.author_name} (${note.badge_id})`,
    actor_name: note.author_name,
    actor_role: note.role,
    timestamp: note.created_at
  });

  AuditService.record('CASE_NOTE_ADDED', 'COMPLAINT', complaint.id, { noteType: note.note_type }, undefined, officerBadge, req.ip);

  res.status(201).json({ success: true, note });
});

export default router;

