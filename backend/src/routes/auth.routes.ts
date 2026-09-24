import { Router, Request, Response } from 'express';
import jwt from 'jsonwebtoken';
import { v4 as uuidv4 } from 'uuid';
import { db, UserRecord } from '../db';
import { AuditService } from '../services/auditService';

const router = Router();
const JWT_SECRET = process.env.JWT_SECRET || 'durga_prod_jwt_secret_statutory_2026';

// 1. User Registration / Guest creation
router.post('/register', (req: Request, res: Response): void => {
  const { email, phone_number, full_name, preferred_language } = req.body;
  const userId = uuidv4();

  const user: UserRecord = {
    id: userId,
    email,
    phone_number,
    full_name: full_name || 'Anonymous User',
    roles: ['ROLE_USER'],
    is_active: true,
    created_at: new Date().toISOString()
  };

  db.users.set(userId, user);
  AuditService.record('USER_REGISTERED', 'USER', userId, { phone_number });

  const token = jwt.sign({ userId, email, roles: user.roles }, JWT_SECRET, { expiresIn: '7d' });
  res.status(201).json({ success: true, token, user });
});

// 2. Police / Authority Officer Authentication via Badge ID
router.post('/officer-login', (req: Request, res: Response): void => {
  const { badgeId, role } = req.body;

  if (!badgeId || badgeId.trim().length < 3) {
    res.status(400).json({ success: false, error: 'Valid Service Badge ID required' });
    return;
  }

  // Find or create active officer session
  let officer = Array.from(db.users.values()).find(u => u.badge_id === badgeId);

  const assignedRole = role === 'ROLE_SENIOR_OFFICER' ? 'ROLE_SENIOR_OFFICER' :
    role === 'ROLE_AUTHORITY_REVIEWER' ? 'ROLE_AUTHORITY_REVIEWER' : 'ROLE_POLICE_OFFICER';

  if (!officer) {
    const officerId = uuidv4();
    officer = {
      id: officerId,
      badge_id: badgeId,
      full_name: `Officer ${badgeId}`,
      roles: [assignedRole],
      is_active: true,
      created_at: new Date().toISOString()
    };
    db.users.set(officerId, officer);
  } else {
    if (!officer.roles.includes(assignedRole)) {
      officer.roles.push(assignedRole);
    }
  }

  AuditService.record('OFFICER_LOGIN', 'USER', officer.id, { role: assignedRole }, officer.id, badgeId);

  const token = jwt.sign(
    { userId: officer.id, badgeId, roles: officer.roles },
    JWT_SECRET,
    { expiresIn: '24h' }
  );

  res.json({
    success: true,
    token,
    officer: {
      id: officer.id,
      badgeId,
      fullName: officer.full_name,
      roles: officer.roles
    }
  });
});

// 3. Admin Authentication
router.post('/admin-login', (req: Request, res: Response): void => {
  const { email, password } = req.body;
  const admin = Array.from(db.users.values()).find(u => u.email === email && u.roles.includes('ROLE_ADMIN'));

  if (!admin) {
    res.status(401).json({ success: false, error: 'Invalid admin credentials' });
    return;
  }

  AuditService.record('ADMIN_LOGIN', 'USER', admin.id, { email });

  const token = jwt.sign({ userId: admin.id, email: admin.email, roles: admin.roles }, JWT_SECRET, { expiresIn: '12h' });
  res.json({ success: true, token, user: admin });
});

export default router;
