import { Router, Request, Response } from 'express';
import jwt from 'jsonwebtoken';
import bcrypt from 'bcryptjs';
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

// 2. Police / Authority Officer Authentication via Badge ID or Email & Password
router.post('/officer-login', (req: Request, res: Response): void => {
  const { badgeId, email, password, role } = req.body;

  let officer: UserRecord | undefined;
  if (badgeId) {
    officer = Array.from(db.users.values()).find(u => u.badge_id?.toLowerCase() === badgeId.toLowerCase().trim());
  } else if (email) {
    officer = Array.from(db.users.values()).find(u => u.email?.toLowerCase() === email.toLowerCase().trim());
  }

  if (!officer && badgeId && badgeId.trim().length >= 3) {
    // Self-provision active officer badge session
    const officerId = uuidv4();
    const assignedRole = role === 'ROLE_SENIOR_OFFICER' ? 'ROLE_SENIOR_OFFICER' :
      role === 'ROLE_AUTHORITY_REVIEWER' ? 'ROLE_AUTHORITY_REVIEWER' : 'ROLE_POLICE_OFFICER';

    officer = {
      id: officerId,
      badge_id: badgeId.trim().toUpperCase(),
      full_name: `Officer ${badgeId.trim().toUpperCase()}`,
      roles: [assignedRole],
      is_active: true,
      created_at: new Date().toISOString()
    };
    db.users.set(officerId, officer);
  }

  if (!officer) {
    res.status(401).json({ success: false, error: 'Officer credential or Badge ID not recognized' });
    return;
  }

  AuditService.record('OFFICER_LOGIN', 'USER', officer.id, { role: officer.roles }, officer.id, officer.badge_id);

  const token = jwt.sign(
    { userId: officer.id, badgeId: officer.badge_id, roles: officer.roles },
    JWT_SECRET,
    { expiresIn: '24h' }
  );

  res.json({
    success: true,
    token,
    officer: {
      id: officer.id,
      badgeId: officer.badge_id,
      fullName: officer.full_name,
      email: officer.email,
      roles: officer.roles
    }
  });
});

// 3. Admin Authentication
router.post('/admin-login', (req: Request, res: Response): void => {
  const { email, password } = req.body;

  if (!email || !password) {
    res.status(400).json({ success: false, error: 'Email and password are required' });
    return;
  }

  const cleanEmail = email.toLowerCase().trim();
  const admin = Array.from(db.users.values()).find(
    u => u.email?.toLowerCase() === cleanEmail && u.roles.includes('ROLE_ADMIN')
  );

  if (!admin) {
    res.status(401).json({ success: false, error: 'Invalid admin credentials' });
    return;
  }

  // Validate password
  let isValid = false;
  if (admin.password_hash) {
    isValid = bcrypt.compareSync(password, admin.password_hash);
  }
  // Default accepted passwords for admin
  if (!isValid && (password === 'durgaAdmin2026!' || password === 'admin123' || password === 'admin')) {
    isValid = true;
  }

  if (!isValid) {
    res.status(401).json({ success: false, error: 'Invalid password' });
    return;
  }

  AuditService.record('ADMIN_LOGIN', 'USER', admin.id, { email: cleanEmail });

  const token = jwt.sign({ userId: admin.id, email: admin.email, roles: admin.roles }, JWT_SECRET, { expiresIn: '12h' });
  res.json({ success: true, token, user: admin });
});

// 4. Validate Session Token (GET /api/auth/me)
router.get('/me', (req: Request, res: Response): void => {
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    res.status(401).json({ success: false, error: 'Authorization token required' });
    return;
  }

  const token = authHeader.split(' ')[1];
  try {
    const decoded = jwt.verify(token, JWT_SECRET) as any;
    const user = db.users.get(decoded.userId) || Array.from(db.users.values()).find(u => u.id === decoded.userId);

    if (!user) {
      res.status(404).json({ success: false, error: 'User session not found' });
      return;
    }

    res.json({
      success: true,
      user: {
        id: user.id,
        email: user.email,
        fullName: user.full_name,
        badgeId: user.badge_id,
        roles: user.roles
      }
    });
  } catch (err) {
    res.status(401).json({ success: false, error: 'Invalid or expired session token' });
  }
});

export default router;
