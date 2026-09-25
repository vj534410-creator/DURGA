import path from 'path';
import express, { Request, Response } from 'express';
import cors from 'cors';
import dotenv from 'dotenv';
import authRoutes from './routes/auth.routes';
import casesRoutes from './routes/cases.routes';
import officersRoutes from './routes/officers.routes';
import adminRoutes from './routes/admin.routes';
import evidenceRoutes from './routes/evidence.routes';
import governmentRoutes from './routes/government.routes';
import aiRoutes from './routes/ai.routes';

dotenv.config();

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json({ limit: '20mb' }));
app.use(express.urlencoded({ extended: true }));

// Serve Admin Panel and Officer Portal directly through the main web domain
const adminPath = path.resolve(__dirname, '../../admin-panel');
const officerPath = path.resolve(__dirname, '../../officer-portal');
app.use('/admin', express.static(adminPath));
app.use('/officer', express.static(officerPath));

app.get('/admin', (_req: Request, res: Response) => {
  res.sendFile(path.join(adminPath, 'index.html'));
});
app.get('/officer', (_req: Request, res: Response) => {
  res.sendFile(path.join(officerPath, 'index.html'));
});

// Health Check Endpoint
app.get('/health', (_req: Request, res: Response) => {
  res.status(200).json({
    status: 'healthy',
    timestamp: new Date().toISOString(),
    service: 'DURGA Central Safety & Support REST API',
    version: '1.0.0',
    database: 'connected'
  });
});

// Root Landing & Portal Hub
app.get('/', (_req: Request, res: Response) => {
  res.send(`<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>DURGA — Central Platform & Portals Hub</title>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Inter', system-ui, sans-serif; }
    body { background: #0B0F19; color: #F1F5F9; min-height: 100vh; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 24px; }
    .container { max-width: 900px; width: 100%; text-align: center; }
    .badge { display: inline-flex; align-items: center; gap: 8px; background: rgba(225, 29, 72, 0.15); border: 1px solid rgba(225, 29, 72, 0.4); color: #FB7185; padding: 6px 16px; border-radius: 9999px; font-size: 13px; font-weight: 600; margin-bottom: 20px; }
    h1 { font-size: 38px; font-weight: 800; letter-spacing: -0.5px; margin-bottom: 12px; }
    h1 span { color: #E11D48; }
    p.subtitle { color: #94A3B8; font-size: 16px; max-width: 650px; margin: 0 auto 36px; line-height: 1.6; }
    .cards-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 20px; text-align: left; }
    .portal-card { background: #111827; border: 1px solid #1F2937; border-radius: 14px; padding: 24px; text-decoration: none; color: inherit; transition: all 0.25s ease; display: flex; flex-direction: column; justify-content: space-between; }
    .portal-card:hover { border-color: #E11D48; transform: translateY(-4px); box-shadow: 0 12px 30px rgba(225, 29, 72, 0.2); }
    .card-icon { font-size: 32px; margin-bottom: 14px; }
    .card-title { font-size: 18px; font-weight: 700; margin-bottom: 8px; color: #FFFFFF; }
    .card-desc { font-size: 13px; color: #9CA3AF; line-height: 1.5; margin-bottom: 20px; flex: 1; }
    .card-action { font-size: 13px; font-weight: 600; color: #FB7185; display: flex; align-items: center; gap: 6px; }
    .status-bar { margin-top: 40px; display: inline-flex; align-items: center; gap: 12px; background: #1E293B; border: 1px solid #334155; padding: 8px 18px; border-radius: 10px; font-size: 12px; color: #CBD5E1; }
    .dot { width: 8px; height: 8px; background: #10B981; border-radius: 50%; box-shadow: 0 0 8px #10B981; }
  </style>
</head>
<body>
  <div class="container">
    <div class="badge">🛡️ GOVERNMENT OF GUJARAT & INDIA STATUTORY COMPLIANT</div>
    <h1><span>DURGA</span> Safety & Support Platform</h1>
    <p class="subtitle">Digital Unified Response & Guidance for All — Unified Central Cloud System for Citizens, Law Enforcement Officers, and Administrators.</p>

    <div class="cards-grid">
      <a href="/admin" class="portal-card">
        <div>
          <div class="card-icon">📊</div>
          <div class="card-title">Central Admin Panel</div>
          <div class="card-desc">System executive dashboard, complaints triage, officer roster, and statutory immutable audit logs.</div>
        </div>
        <div class="card-action">Launch Admin Panel &rarr;</div>
      </a>

      <a href="/officer" class="portal-card">
        <div>
          <div class="card-icon">👮</div>
          <div class="card-title">Police Officer Portal</div>
          <div class="card-desc">Authorized law enforcement portal for badge verification, evidence review, and official police dispatch actions.</div>
        </div>
        <div class="card-action">Launch Officer Portal &rarr;</div>
      </a>

      <a href="/health" class="portal-card">
        <div>
          <div class="card-icon">⚡</div>
          <div class="card-title">REST API & Health</div>
          <div class="card-desc">Production REST API server, cryptographic SHA-256 evidence validation, and government dispatch gateway.</div>
        </div>
        <div class="card-action">View Health Status &rarr;</div>
      </a>
    </div>

    <div class="status-bar">
      <span class="dot"></span>
      <span>REST API Online • Cloud Run Gateway Active • Portals Ready</span>
    </div>
  </div>
</body>
</html>`);
});

// API Routes
app.use('/api/auth', authRoutes);
app.use('/api/cases', casesRoutes);
app.use('/api/officers', officersRoutes);
app.use('/api/admin', adminRoutes);
app.use('/api/evidence', evidenceRoutes);
app.use('/api/government', governmentRoutes);
app.use('/api/ai', aiRoutes);

// Global 404
app.use((_req: Request, res: Response) => {
  res.status(404).json({ success: false, error: 'Endpoint not found' });
});

if (process.env.NODE_ENV !== 'test' && require.main === module) {
  const LISTEN_PORT = Number(process.env.DEFAULT_APP_PORT || process.env.BACKEND_PORT || 3000);
  app.listen(LISTEN_PORT, '0.0.0.0', () => {
    console.log(`[DURGA Backend] REST API server active on 0.0.0.0:${LISTEN_PORT}`);
  });
}

export default app;
