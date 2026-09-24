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

// Health Check Endpoint (Required by Specification)
app.get('/health', (_req: Request, res: Response) => {
  res.status(200).json({
    status: 'healthy',
    timestamp: new Date().toISOString(),
    service: 'DURGA Central Safety & Support REST API',
    version: '1.0.0',
    database: 'connected'
  });
});

app.get('/', (_req: Request, res: Response) => {
  res.json({
    message: 'DURGA Safety & Support Platform — Central REST API',
    documentation: '/api-docs',
    endpoints: {
      health: '/health',
      auth: '/api/auth',
      cases: '/api/cases',
      officers: '/api/officers',
      admin: '/api/admin',
      evidence: '/api/evidence',
      government: '/api/government',
      ai: '/api/ai'
    }
  });
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
  const LISTEN_PORT = process.env.BACKEND_PORT || 3000;
  app.listen(LISTEN_PORT, () => {
    console.log(`[DURGA Backend] REST API server active on port ${LISTEN_PORT}`);
  });
}

export default app;
