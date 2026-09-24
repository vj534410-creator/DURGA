import { v4 as uuidv4 } from 'uuid';

export interface UserRecord {
  id: string;
  email?: string;
  phone_number?: string;
  password_hash?: string;
  full_name?: string;
  badge_id?: string;
  roles: string[];
  is_active: boolean;
  created_at: string;
}

export interface ComplaintRecord {
  id: string;
  complaint_ref_no: string;
  user_id?: string;
  category_id: string;
  title: string;
  original_description: string;
  structured_description?: string;
  original_language: string;
  incident_date: string;
  incident_time: string;
  incident_location: string;
  latitude?: number;
  longitude?: number;
  is_witness_report: boolean;
  current_status: string;
  assigned_officer_id?: string;
  evidence_count: number;
  created_at: string;
  updated_at: string;
}

export interface AuditLogRecord {
  id: string;
  event_type: string;
  entity_type: string;
  entity_id?: string;
  performed_by?: string;
  badge_id?: string;
  ip_address?: string;
  action_details: any;
  timestamp: string;
}

export interface CaseNoteRecord {
  id: string;
  case_id: string;
  author_id?: string;
  badge_id?: string;
  note_type: string;
  content: string;
  created_at: string;
}

// In-Memory resilient database for development / zero-setup mode, seamlessly adaptable to PostgreSQL
class DatabaseStore {
  users: Map<string, UserRecord> = new Map();
  complaints: Map<string, ComplaintRecord> = new Map();
  auditLogs: AuditLogRecord[] = [];
  caseNotes: CaseNoteRecord[] = [];

  constructor() {
    this.seedDefaultData();
  }

  private seedDefaultData() {
    // Seed default admin
    const adminId = 'admin-user-001';
    this.users.set(adminId, {
      id: adminId,
      email: 'admin@durga.gov.in',
      full_name: 'DURGA Central Administrator',
      roles: ['ROLE_ADMIN'],
      is_active: true,
      created_at: new Date().toISOString()
    });

    // Seed default police officer
    const officerId = 'officer-user-001';
    this.users.set(officerId, {
      id: officerId,
      email: 'officer.sharma@police.gov.in',
      full_name: 'Inspector R. Sharma',
      badge_id: 'POL-GJ-4029',
      roles: ['ROLE_POLICE_OFFICER'],
      is_active: true,
      created_at: new Date().toISOString()
    });

    // Seed initial demo complaints for immediate testing & portal verification
    const complaint1: ComplaintRecord = {
      id: uuidv4(),
      complaint_ref_no: 'DURGA-2026-08192',
      category_id: 'DOMESTIC_VIOLENCE',
      title: 'Emergency Domestic Assistance Request',
      original_description: 'Immediate counseling and protective intervention requested in residential area.',
      structured_description: 'Victim reports repeated threats and verbal abuse. Safe shelter and legal protection order required urgently.',
      original_language: 'gu',
      incident_date: '2026-09-24',
      incident_time: '14:30',
      incident_location: 'Sector 7, Gandhinagar, Gujarat',
      latitude: 23.2156,
      longitude: 72.6369,
      is_witness_report: false,
      current_status: 'UNDER_INVESTIGATION',
      assigned_officer_id: officerId,
      evidence_count: 2,
      created_at: new Date().toISOString(),
      updated_at: new Date().toISOString()
    };
    this.complaints.set(complaint1.id, complaint1);

    const complaint2: ComplaintRecord = {
      id: uuidv4(),
      complaint_ref_no: 'DURGA-2026-08193',
      category_id: 'CYBER_CRIME',
      title: 'Online Defamation & Impersonation on Social Media',
      original_description: 'Fake profile created using personal phone and photos without consent.',
      structured_description: 'Perpetrator using unverified phone to send threatening messages across platforms.',
      original_language: 'en',
      incident_date: '2026-09-23',
      incident_time: '18:15',
      incident_location: 'Ahmedabad, Gujarat',
      latitude: 23.0225,
      longitude: 72.5714,
      is_witness_report: false,
      current_status: 'SUBMITTED_TO_DURGA',
      evidence_count: 1,
      created_at: new Date(Date.now() - 3600000).toISOString(),
      updated_at: new Date().toISOString()
    };
    this.complaints.set(complaint2.id, complaint2);
  }
}

export const db = new DatabaseStore();
