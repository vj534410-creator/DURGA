import { Router, Request, Response } from 'express';
import { GovernmentIntegrationService } from '../services/governmentIntegrationService';
import { AuditService } from '../services/auditService';

const router = Router();

// POST /government/submit - Statutory forwarding to official nodal/police system
router.post('/submit', async (req: Request, res: Response): Promise<void> => {
  const { complaintId, payload } = req.body;

  if (!complaintId) {
    res.status(400).json({ success: false, error: 'complaintId is required' });
    return;
  }

  const integration = GovernmentIntegrationService.getIntegration();
  const result = await integration.submitComplaint(complaintId, payload);

  AuditService.record(
    'GOVERNMENT_FORWARDING_QUEUED',
    'GOVERNMENT_INTEGRATION',
    complaintId,
    result
  );

  res.json({
    success: true,
    result
  });
});

// GET /government/status/:ref - Check status of statutory integration
router.get('/status/:ref', async (req: Request, res: Response): Promise<void> => {
  const { ref } = req.params;
  const integration = GovernmentIntegrationService.getIntegration();
  const status = await integration.getSubmissionStatus(ref);
  res.json({ success: true, status });
});

export default router;
