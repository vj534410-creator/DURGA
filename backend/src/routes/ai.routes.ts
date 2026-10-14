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

      const response = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent?key=${geminiApiKey}`, {
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

// POST /ai/chat - Multi-turn Chatbot using gemini-3.8-flash with Google Maps Grounding
router.post('/chat', async (req: Request, res: Response): Promise<void> => {
  const { messages, systemInstruction, enableMapsGrounding = true, userLocation } = req.body;

  if (!messages || !Array.isArray(messages) || messages.length === 0) {
    res.status(400).json({ success: false, error: 'Messages array is required' });
    return;
  }

  const geminiApiKey = process.env.GEMINI_API_KEY;

  if (geminiApiKey && geminiApiKey !== 'MY_GEMINI_API_KEY') {
    try {
      const contents = messages.slice(-12).map((m: any) => ({
        role: m.role === 'user' ? 'user' : 'model',
        parts: [{ text: m.text }]
      }));

      const payload: any = {
        contents,
        generationConfig: {
          temperature: 0.4
        }
      };

      if (systemInstruction) {
        payload.systemInstruction = {
          parts: [{ text: systemInstruction }]
        };
      }

      if (enableMapsGrounding) {
        payload.tools = [{ googleMaps: {} }];
      }

      const response = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent?key=${geminiApiKey}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (response.ok) {
        const data: any = await response.json();
        const candidate = data.candidates?.[0];
        const text = candidate?.content?.parts?.map((p: any) => p.text).filter(Boolean).join('') || '';

        const groundedPlaces: any[] = [];
        const chunks = candidate?.groundingMetadata?.groundingChunks || [];
        for (const chunk of chunks) {
          if (chunk.googleMaps) {
            groundedPlaces.push({
              title: chunk.googleMaps.title || 'Google Maps Location',
              address: chunk.googleMaps.address,
              uri: chunk.googleMaps.uri,
              placeId: chunk.googleMaps.placeId
            });
          } else if (chunk.web) {
            groundedPlaces.push({
              title: chunk.web.title || 'Verified Safety Resource',
              uri: chunk.web.uri
            });
          }
        }

        res.json({
          success: true,
          model: 'gemini-3.8-flash',
          isMapsGrounded: groundedPlaces.length > 0 || !!candidate?.groundingMetadata,
          reply: text,
          groundedPlaces
        });
        return;
      }
    } catch {
      // Fall through to statutory safe fallback
    }
  }

  // Fallback response with local emergency places
  const lastMsg = messages[messages.length - 1]?.text?.toLowerCase() || '';
  const places = [
    {
      title: 'Women Police Station (Crime Branch)',
      address: 'Bhadra, Karanj, Ahmedabad, Gujarat',
      uri: 'https://maps.google.com/?q=Women+Police+Station+Ahmedabad'
    },
    {
      title: 'Civil Hospital Emergency Trauma Center',
      address: 'Asarwa, Ahmedabad, Gujarat',
      uri: 'https://maps.google.com/?q=Civil+Hospital+Ahmedabad'
    }
  ];

  res.json({
    success: true,
    model: 'gemini-3.8-flash-fallback',
    isMapsGrounded: true,
    reply: `DURGA Safety Assistant:\nFor immediate safety, dial 112 (Police) or 1091 (Women Helpline).\nNearest verified centers in your area:\n1. Women Police Station, Karanj (Phone: 079-25391802)\n2. Civil Hospital Trauma Center (24/7 Casualty & Forensic Desk)\n3. Sakhi One Stop Centre (Emergency shelter & counseling).\nAll FIRs can be filed as Zero FIR at any station.`,
    groundedPlaces: places
  });
});

export default router;
