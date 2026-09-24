import { Router, Request, Response } from 'express';
import { AuditService } from '../services/auditService';

const router = Router();

// POST /ai/draft-complaint - Format and structure raw facts into formal legal complaint draft
router.post('/draft-complaint', async (req: Request, res: Response): Promise<void> => {
  const { incidentType, rawFacts, incidentDate, incidentLocation, language } = req.body;

  if (!rawFacts || rawFacts.trim().length === 0) {
    res.status(400).json({ success: false, error: 'Raw incident facts are required' });
    return;
  }

  // Check if GEMINI_API_KEY is configured on server-side
  const geminiApiKey = process.env.GEMINI_API_KEY;

  let structuredDraft = '';
  let mode = 'DEVELOPMENT_COMPLIANT_DRAFT';

  if (geminiApiKey) {
    // Real Gemini API server-side call
    try {
      const prompt = `You are a formal legal and incident documentation assistant for the DURGA safety platform in India.
Convert these raw facts into a clear, chronological, objective incident narrative for statutory complaint filing.
Do NOT invent new facts. Do NOT make criminal determinations. Preserve all dates, times, and descriptions provided.
Category: ${incidentType || 'General'}
Date: ${incidentDate || 'Not specified'}
Location: ${incidentLocation || 'Not specified'}
Language: ${language || 'English'}
User Facts:
${rawFacts}`;

      const response = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${geminiApiKey}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          contents: [{ parts: [{ text: prompt }] }]
        })
      });

      if (response.ok) {
        const data: any = await response.json();
        const candidate = data.candidates?.[0]?.content?.parts?.[0]?.text;
        if (candidate) {
          structuredDraft = candidate.trim();
          mode = 'GEMINI_SERVER_SYNTHESIZED';
        }
      }
    } catch {
      // Fall through to statutory fallback format
    }
  }

  // Statutory template formatter fallback
  if (!structuredDraft) {
    structuredDraft = `INCIDENT REPORT SUMMARY (DURGA Platform)
----------------------------------------
Category: ${incidentType || 'General Incident'}
Date/Time of Occurrence: ${incidentDate || 'As reported'}
Location: ${incidentLocation || 'As reported'}

CHRONOLOGICAL NARRATIVE:
${rawFacts}

REQUESTED ACTION:
Statutory registration under relevant sections for official review by jurisdictional nodal officer.

[DISCLAIMER: Generated as a drafting aid only. Review and confirm facts prior to submission.]`;
  }

  AuditService.record('AI_DRAFT_GENERATED', 'AI_REQUEST', 'draft', {
    incidentType,
    length: rawFacts.length,
    mode
  });

  res.json({
    success: true,
    mode,
    draft: structuredDraft,
    disclaimer: 'This draft is generated strictly from the facts you provided. Please review before official filing.'
  });
});

export default router;
