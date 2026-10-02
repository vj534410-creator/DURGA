import { v4 as uuidv4 } from 'uuid';
import bcrypt from 'bcryptjs';

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

export interface ComplainantInfo {
  name?: string;
  age?: string;
  gender?: string;
  state?: string;
  district?: string;
  mobile?: string;
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
  priority?: string; // NORMAL, HIGH, URGENT, EMERGENCY
  police_station?: string;
  police_district?: string;
  assigned_inspector?: string;
  investigating_officer?: string;
  assigned_at?: string;
  qr_active?: boolean;
  assigned_officer_id?: string;
  assigned_officer_name?: string;
  evidence_count: number;
  complainant?: ComplainantInfo;
  shared_items_summary?: string;
  secure_token?: string;
  locked?: boolean;
  lock_timestamp?: string;
  created_at: string;
  updated_at: string;
}

export interface EvidenceRecord {
  id: string;
  complaint_id: string;
  case_ref: string;
  evidence_ref: string;
  file_type: string; // PHOTO, VIDEO, AUDIO, SCREENSHOT, DOCUMENT
  file_name?: string;
  mime_type: string;
  file_size_bytes: number;
  sha256_checksum: string;
  is_locked: boolean;
  is_shared_with_police: boolean;
  captured_at: string;
  created_at: string;
}

export interface CaseDraftRecord {
  id: string;
  case_id: string;
  draft_title: string;
  category: string;
  raw_notes: string;
  ai_draft_summary: string;
  ai_draft_description: string;
  missing_questions?: string[];
  version: number;
  created_at: string;
  updated_at: string;
}

export interface TimelineEventRecord {
  id: string;
  case_id: string;
  event_type: string;
  title: string;
  description: string;
  actor_name: string;
  actor_role: string;
  timestamp: string;
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
  author_name?: string;
  badge_id?: string;
  role?: string;
  note_type: string;
  content: string;
  created_at: string;
}

// In-Memory resilient database for development / zero-setup mode, seamlessly adaptable to PostgreSQL
class DatabaseStore {
  users: Map<string, UserRecord> = new Map();
  complaints: Map<string, ComplaintRecord> = new Map();
  evidence: Map<string, EvidenceRecord> = new Map();
  drafts: Map<string, CaseDraftRecord> = new Map();
  timeline: TimelineEventRecord[] = [];
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
      password_hash: bcrypt.hashSync('durgaAdmin2026!', 10),
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
      password_hash: bcrypt.hashSync('officer123', 10),
      roles: ['ROLE_POLICE_OFFICER'],
      is_active: true,
      created_at: new Date().toISOString()
    });

    const officer2Id = 'officer-user-002';
    this.users.set(officer2Id, {
      id: officer2Id,
      email: 'officer.patel@police.gov.in',
      full_name: 'Sub-Inspector P. Patel',
      badge_id: 'POL-GJ-8102',
      password_hash: bcrypt.hashSync('officer123', 10),
      roles: ['ROLE_POLICE_OFFICER'],
      is_active: true,
      created_at: new Date().toISOString()
    });

    // Seed initial cases mirroring real DURGA mobile app structure
    const complaint1Id = uuidv4();
    const caseRef1 = 'DURGA-2026-08192';
    const complaint1: ComplaintRecord = {
      id: complaint1Id,
      complaint_ref_no: caseRef1,
      category_id: 'HOME_DOMESTIC',
      title: 'Emergency Domestic Safety & Protection Request',
      original_description: 'Immediate counseling and protective intervention requested in residential area.',
      structured_description: 'Victim reports repeated threats and verbal abuse. Safe shelter and legal protection order required urgently.',
      original_language: 'gu',
      incident_date: '2026-09-24',
      incident_time: '14:30',
      incident_location: 'Sector 7, Gandhinagar, Gujarat',
      latitude: 23.2156,
      longitude: 72.6369,
      is_witness_report: false,
      current_status: 'ASSIGNED',
      priority: 'URGENT',
      assigned_officer_id: officerId,
      assigned_officer_name: 'Inspector R. Sharma (POL-GJ-4029)',
      evidence_count: 2,
      complainant: {
        name: 'Protected Complainant',
        age: '29',
        gender: 'Female',
        state: 'Gujarat',
        district: 'Gandhinagar',
        mobile: '+919876543210'
      },
      shared_items_summary: 'Complaint, Location, Evidence Photos',
      secure_token: 'sec-tok-08192-durga',
      locked: true,
      lock_timestamp: new Date().toISOString(),
      created_at: new Date(Date.now() - 7200000).toISOString(),
      updated_at: new Date().toISOString()
    };
    this.complaints.set(complaint1.id, complaint1);

    // Timeline for complaint 1
    this.timeline.push(
      {
        id: uuidv4(),
        case_id: complaint1.id,
        event_type: 'CASE_CREATED',
        title: 'Case Submitted from DURGA Mobile App',
        description: 'Citizen filed request via DURGA Android client with encrypted payload.',
        actor_name: 'Mobile Citizen User',
        actor_role: 'CITIZEN',
        timestamp: new Date(Date.now() - 7200000).toISOString()
      },
      {
        id: uuidv4(),
        case_id: complaint1.id,
        event_type: 'EVIDENCE_LOCKED',
        title: 'One-Tap Evidence Lock Activated',
        description: 'Cryptographic SHA-256 integrity seal applied to 2 shared photos.',
        actor_name: 'Mobile Safety Vault',
        actor_role: 'SYSTEM',
        timestamp: new Date(Date.now() - 7100000).toISOString()
      },
      {
        id: uuidv4(),
        case_id: complaint1.id,
        event_type: 'OFFICER_ASSIGNED',
        title: 'Assigned to Investigating Officer',
        description: 'Assigned to Inspector R. Sharma (Badge: POL-GJ-4029) by Admin Desk.',
        actor_name: 'Admin Desk',
        actor_role: 'ADMIN',
        timestamp: new Date(Date.now() - 3600000).toISOString()
      }
    );

    // Seed Evidence for complaint 1
    const ev1Id = uuidv4();
    this.evidence.set(ev1Id, {
      id: ev1Id,
      complaint_id: complaint1.id,
      case_ref: caseRef1,
      evidence_ref: 'EVD-2026-4821',
      file_type: 'PHOTO',
      file_name: 'incident_scene_door_damage.jpg',
      mime_type: 'image/jpeg',
      file_size_bytes: 204850,
      sha256_checksum: 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855',
      is_locked: true,
      is_shared_with_police: true,
      captured_at: '2026-09-24T14:35:00Z',
      created_at: new Date(Date.now() - 7100000).toISOString()
    });

    const ev2Id = uuidv4();
    this.evidence.set(ev2Id, {
      id: ev2Id,
      complaint_id: complaint1.id,
      case_ref: caseRef1,
      evidence_ref: 'EVD-2026-4822',
      file_type: 'DOCUMENT',
      file_name: 'protective_affidavit.pdf',
      mime_type: 'application/pdf',
      file_size_bytes: 412000,
      sha256_checksum: 'a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e',
      is_locked: true,
      is_shared_with_police: true,
      captured_at: '2026-09-24T14:40:00Z',
      created_at: new Date(Date.now() - 7000000).toISOString()
    });

    // Seed Draft for complaint 1
    const draft1Id = uuidv4();
    this.drafts.set(draft1Id, {
      id: draft1Id,
      case_id: complaint1.id,
      draft_title: 'Formal Domestic Intervention Petition',
      category: 'HOME_DOMESTIC',
      raw_notes: 'Urgent protection needed. Suspect visited home repeatedly despite restraining warning.',
      ai_draft_summary: 'Complainant seeks urgent intervention under Section 12 of PWDVA against repeated unauthorized premises entry.',
      ai_draft_description: 'On 24-09-2026 at 14:30 hrs, applicant was subjected to intimidation at Sector 7 residence. Evidence deposited.',
      version: 1,
      created_at: new Date(Date.now() - 7200000).toISOString(),
      updated_at: new Date(Date.now() - 7200000).toISOString()
    });

    // Seed Complaint 2
    const complaint2Id = uuidv4();
    const caseRef2 = 'DURGA-2026-08193';
    const complaint2: ComplaintRecord = {
      id: complaint2Id,
      complaint_ref_no: caseRef2,
      category_id: 'CYBER_ONLINE',
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
      current_status: 'REPORT_SUBMITTED',
      priority: 'NORMAL',
      evidence_count: 1,
      complainant: {
        name: 'Citizen Anonymous',
        age: '24',
        gender: 'Female',
        state: 'Gujarat',
        district: 'Ahmedabad',
        mobile: '+919811223344'
      },
      shared_items_summary: 'Complaint, Screenshot Evidence',
      secure_token: 'sec-tok-08193-durga',
      locked: true,
      lock_timestamp: new Date().toISOString(),
      created_at: new Date(Date.now() - 3600000).toISOString(),
      updated_at: new Date().toISOString()
    };
    this.complaints.set(complaint2.id, complaint2);

    this.timeline.push({
      id: uuidv4(),
      case_id: complaint2.id,
      event_type: 'CASE_CREATED',
      title: 'Report Received by Support Desk',
      description: 'Cyber harassment incident report entered into central triage vault.',
      actor_name: 'DURGA App',
      actor_role: 'SYSTEM',
      timestamp: new Date(Date.now() - 3600000).toISOString()
    });

    const ev3Id = uuidv4();
    this.evidence.set(ev3Id, {
      id: ev3Id,
      complaint_id: complaint2.id,
      case_ref: caseRef2,
      evidence_ref: 'EVD-2026-4830',
      file_type: 'SCREENSHOT',
      file_name: 'fake_profile_screenshot.png',
      mime_type: 'image/png',
      file_size_bytes: 524100,
      sha256_checksum: '8f434346648f6b96df89dda901c5176b10f607629f7660cc6349d6f705b4a524',
      is_locked: true,
      is_shared_with_police: true,
      captured_at: '2026-09-23T18:20:00Z',
      created_at: new Date(Date.now() - 3500000).toISOString()
    });
  }
}

export const db = new DatabaseStore();

