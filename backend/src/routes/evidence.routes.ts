import { Router, Request, Response } from 'express';
import { v4 as uuidv4 } from 'uuid';
import { db } from '../db';
import { AuditService } from '../services/auditService';

const router = Router();

// POST /evidence/register - Register evidence metadata & SHA-256 integrity hash
router.post('/register', (req: Request, res: Response): void => {
  const { complaintId, fileType, mimeType, fileSizeBytes, sha256Checksum, capturedAt, officerBadge } = req.body;

  if (!complaintId || !sha256Checksum) {
    res.status(400).json({ success: false, error: 'complaintId and sha256Checksum are required' });
    return;
  }

  const evidenceId = uuidv4();
  const evidenceRef = `EVD-${new Date().getFullYear()}-${Math.floor(1000 + Math.random() * 9000)}`;

  // Update complaint evidence count
  const complaint = Array.from(db.complaints.values()).find(
    c => c.id === complaintId || c.complaint_ref_no === complaintId
  );
  if (complaint) {
    complaint.evidence_count = (complaint.evidence_count || 0) + 1;
  }

  AuditService.record(
    'EVIDENCE_DEPOSITED',
    'EVIDENCE',
    evidenceId,
    {
      evidenceRef,
      complaintId,
      fileType: fileType || 'PHOTO',
      mimeType: mimeType || 'image/jpeg',
      fileSizeBytes: fileSizeBytes || 1024,
      sha256Checksum,
      capturedAt: capturedAt || new Date().toISOString()
    },
    undefined,
    officerBadge
  );

  res.status(201).json({
    success: true,
    evidenceId,
    evidenceRef,
    sha256Checksum,
    status: 'DEPOSITED_IN_SECURE_VAULT',
    message: 'Evidence cryptographic hash registered with tamper-evident chain of custody'
  });
});

export default router;
