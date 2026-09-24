import { Router, Request, Response } from 'express';
import { v4 as uuidv4 } from 'uuid';
import { db, ComplaintRecord } from '../db';
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
      evidenceCount
    } = req.body;

    const generatedId = caseId || `DURGA-${new Date().getFullYear()}-${Math.floor(10000 + Math.random() * 90000)}`;

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
      current_status: 'SUBMITTED_TO_DURGA',
      evidence_count: Number(evidenceCount) || 0,
      created_at: new Date().toISOString(),
      updated_at: new Date().toISOString()
    };

    db.complaints.set(complaint.id, complaint);

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

// GET /cases - List all complaints/cases (with optional status or search query)
router.get('/', (req: Request, res: Response): void => {
  const { status, category, limit } = req.query;
  let items = Array.from(db.complaints.values());

  if (status) {
    items = items.filter(c => c.current_status.toLowerCase() === String(status).toLowerCase());
  }

  if (category) {
    items = items.filter(c => c.category_id.toLowerCase() === String(category).toLowerCase());
  }

  // Sort descending by creation date
  items.sort((a, b) => new Date(b.created_at).getTime() - new Date(a.created_at).getTime());

  if (limit) {
    items = items.slice(0, Number(limit));
  }

  res.json({
    success: true,
    count: items.length,
    cases: items
  });
});

// GET /cases/:id - Get specific case details
router.get('/:id', (req: Request, res: Response): void => {
  const { id } = req.params;
  const complaint = Array.from(db.complaints.values()).find(
    c => c.id === id || c.complaint_ref_no === id
  );

  if (!complaint) {
    res.status(404).json({ success: false, error: 'Case not found' });
    return;
  }

  const notes = db.caseNotes.filter(n => n.case_id === complaint.id || n.case_id === complaint.complaint_ref_no);

  res.json({
    success: true,
    case: complaint,
    notes
  });
});

// PATCH /cases/:id/status - Update case status with notes & audit trail
router.patch('/:id/status', (req: Request, res: Response): void => {
  const { id } = req.params;
  const { status, remarks, officerBadge } = req.body;

  const complaint = Array.from(db.complaints.values()).find(
    c => c.id === id || c.complaint_ref_no === id
  );

  if (!complaint) {
    res.status(404).json({ success: false, error: 'Case not found' });
    return;
  }

  const oldStatus = complaint.current_status;
  complaint.current_status = status;
  complaint.updated_at = new Date().toISOString();

  if (remarks) {
    db.caseNotes.push({
      id: uuidv4(),
      case_id: complaint.id,
      badge_id: officerBadge || 'ADMIN',
      note_type: 'STATUS_UPDATE',
      content: `Status updated from ${oldStatus} to ${status}. Remarks: ${remarks}`,
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
    message: 'Case status updated successfully',
    case: complaint
  });
});

// POST /cases/:id/notes - Add official case note
router.post('/:id/notes', (req: Request, res: Response): void => {
  const { id } = req.params;
  const { content, noteType, officerBadge } = req.body;

  const complaint = Array.from(db.complaints.values()).find(
    c => c.id === id || c.complaint_ref_no === id
  );

  if (!complaint) {
    res.status(404).json({ success: false, error: 'Case not found' });
    return;
  }

  const note = {
    id: uuidv4(),
    case_id: complaint.id,
    badge_id: officerBadge || 'DUTY_OFFICER',
    note_type: noteType || 'OFFICIAL_NOTE',
    content,
    created_at: new Date().toISOString()
  };

  db.caseNotes.push(note);
  AuditService.record('CASE_NOTE_ADDED', 'COMPLAINT', complaint.id, { noteType }, undefined, officerBadge);

  res.status(201).json({ success: true, note });
});

export default router;
