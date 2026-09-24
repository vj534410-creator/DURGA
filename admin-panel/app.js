const API_BASE = window.location.origin.includes('localhost') || window.location.origin.includes('127.0.0.1')
  ? 'http://127.0.0.1:3000'
  : window.location.origin;

let cachedCases = [
  {
    id: 'c-001',
    complaint_ref_no: 'DURGA-2026-08192',
    category_id: 'DOMESTIC_VIOLENCE',
    title: 'Emergency Domestic Assistance Request',
    original_description: 'Immediate counseling and protective intervention requested in residential area.',
    incident_location: 'Gandhinagar, Gujarat',
    current_status: 'UNDER_INVESTIGATION',
    assigned_officer_id: 'Inspector R. Sharma (POL-GJ-4029)',
    evidence_count: 2
  },
  {
    id: 'c-002',
    complaint_ref_no: 'DURGA-2026-08193',
    category_id: 'CYBER_CRIME',
    title: 'Online Defamation & Impersonation on Social Media',
    original_description: 'Fake profile created using personal phone and photos without consent.',
    incident_location: 'Ahmedabad, Gujarat',
    current_status: 'SUBMITTED_TO_DURGA',
    assigned_officer_id: 'Unassigned',
    evidence_count: 1
  }
];

let cachedOfficers = [
  {
    badge_id: 'POL-GJ-4029',
    full_name: 'Inspector R. Sharma',
    email: 'officer.sharma@police.gov.in',
    roles: ['ROLE_POLICE_OFFICER'],
    is_active: true
  },
  {
    badge_id: 'POL-GJ-8102',
    full_name: 'Sub-Inspector P. Patel',
    email: 'officer.patel@police.gov.in',
    roles: ['ROLE_POLICE_OFFICER'],
    is_active: true
  }
];

let cachedAuditLogs = [
  {
    timestamp: new Date().toISOString(),
    event_type: 'CASE_STATUS_UPDATED',
    entity_type: 'COMPLAINT',
    performed_by: 'Inspector R. Sharma',
    ip_address: '127.0.0.1',
    action_details: { oldStatus: 'SUBMITTED_TO_DURGA', newStatus: 'UNDER_INVESTIGATION' }
  },
  {
    timestamp: new Date(Date.now() - 3600000).toISOString(),
    event_type: 'CASE_SUBMITTED',
    entity_type: 'COMPLAINT',
    performed_by: 'Citizen Mobile App',
    ip_address: '127.0.0.1',
    action_details: { ref: 'DURGA-2026-08192' }
  }
];

let activeCaseForAssignment = null;

// Tab Switching
function switchTab(tabId) {
  document.querySelectorAll('.tab-pane').forEach(el => el.classList.remove('active'));
  document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));

  const targetPane = document.getElementById(`tab-${tabId}`);
  if (targetPane) targetPane.classList.add('active');

  const titles = {
    dashboard: ['Executive Dashboard', 'Real-time emergency reports, case progression, and statutory compliance.'],
    cases: ['Complaints & Cases Vault', 'Complete repository of incoming reports, triage, and assignment.'],
    officers: ['Officer & Personnel Roster', 'Authorized police nodal officers and statutory auditors.'],
    audit: ['Statutory Audit Trail', 'Immutable cryptographic log of every access, review, and status update.'],
    helplines: ['Emergency Helplines', 'Verified official national and state emergency helplines in India.']
  };

  if (titles[tabId]) {
    document.getElementById('page-title').textContent = titles[tabId][0];
    document.getElementById('page-subtitle').textContent = titles[tabId][1];
  }
}

// Fetch and Refresh Data
async function refreshData() {
  try {
    const statsRes = await fetch(`${API_BASE}/api/admin/stats`);
    if (statsRes.ok) {
      const data = await statsRes.json();
      if (data.stats) {
        document.getElementById('kpi-total').textContent = data.stats.totalCases;
        document.getElementById('kpi-pending').textContent = data.stats.pendingReview;
        document.getElementById('kpi-investigating').textContent = data.stats.underInvestigation;
        document.getElementById('kpi-audit').textContent = data.stats.totalAuditLogs;
      }
    }

    const casesRes = await fetch(`${API_BASE}/api/cases`);
    if (casesRes.ok) {
      const data = await casesRes.json();
      if (data.cases) cachedCases = data.cases;
    }

    const officersRes = await fetch(`${API_BASE}/api/admin/officers`);
    if (officersRes.ok) {
      const data = await officersRes.json();
      if (data.officers) cachedOfficers = data.officers;
    }

    const auditRes = await fetch(`${API_BASE}/api/admin/audit-logs`);
    if (auditRes.ok) {
      const data = await auditRes.json();
      if (data.logs) cachedAuditLogs = data.logs;
    }
  } catch (err) {
    console.warn('Backend API offline or unreachable, rendering local cache:', err);
  }

  renderDashboard();
  renderCases();
  renderOfficers();
  renderAuditLogs();
}

function renderDashboard() {
  const tbody = document.getElementById('dashboard-recent-cases');
  if (!tbody) return;
  tbody.innerHTML = cachedCases.slice(0, 5).map(c => `
    <tr>
      <td><strong>${c.complaint_ref_no || c.caseId}</strong></td>
      <td><span class="badge-tag">${c.category_id || c.category}</span></td>
      <td>${c.title}</td>
      <td>${c.incident_location || c.location || 'Gujarat'}</td>
      <td><span class="status-badge status-${c.current_status || c.status}">${(c.current_status || c.status).replace(/_/g, ' ')}</span></td>
      <td><button class="btn btn-sm btn-outline" onclick="openAssignModal('${c.id || c.complaint_ref_no}')">Assign</button></td>
    </tr>
  `).join('');
}

function renderCases() {
  const tbody = document.getElementById('full-cases-list');
  if (!tbody) return;

  const query = (document.getElementById('case-search')?.value || '').toLowerCase();
  const statusFilter = document.getElementById('case-status-filter')?.value || 'ALL';

  const filtered = cachedCases.filter(c => {
    const matchesQuery = (c.complaint_ref_no || '').toLowerCase().includes(query) ||
                         (c.title || '').toLowerCase().includes(query) ||
                         (c.incident_location || '').toLowerCase().includes(query);
    const matchesStatus = statusFilter === 'ALL' || c.current_status === statusFilter;
    return matchesQuery && matchesStatus;
  });

  tbody.innerHTML = filtered.map(c => `
    <tr>
      <td><strong>${c.complaint_ref_no || c.caseId}</strong></td>
      <td><span class="badge-tag">${c.category_id || c.category}</span></td>
      <td>
        <strong>${c.title}</strong>
        <div style="font-size:11px;color:#64748B;">${c.original_description || ''}</div>
      </td>
      <td>${c.incident_location || c.location || 'Gujarat'}</td>
      <td>${c.evidence_count || 0} Files</td>
      <td><span class="status-badge status-${c.current_status || c.status}">${(c.current_status || c.status).replace(/_/g, ' ')}</span></td>
      <td>${c.assigned_officer_id || 'Unassigned'}</td>
      <td>
        <button class="btn btn-sm btn-primary" onclick="openAssignModal('${c.id || c.complaint_ref_no}')">Assign</button>
      </td>
    </tr>
  `).join('');
}

function filterCases() {
  renderCases();
}

function renderOfficers() {
  const tbody = document.getElementById('officers-list');
  if (!tbody) return;

  tbody.innerHTML = cachedOfficers.map(o => `
    <tr>
      <td><strong>${o.badge_id || 'N/A'}</strong></td>
      <td>${o.full_name || o.fullName}</td>
      <td>${o.email || 'N/A'}</td>
      <td><span class="badge-tag highlight">${(o.roles || []).join(', ')}</span></td>
      <td><span class="status-badge status-RESOLVED">Active On-Duty</span></td>
    </tr>
  `).join('');
}

function renderAuditLogs() {
  const tbody = document.getElementById('audit-logs-list');
  if (!tbody) return;

  tbody.innerHTML = cachedAuditLogs.map(l => `
    <tr>
      <td>${new Date(l.timestamp).toLocaleTimeString()}</td>
      <td><strong>${l.event_type}</strong></td>
      <td>${l.entity_type}</td>
      <td>${l.performed_by || l.badge_id || 'System'}</td>
      <td>${l.ip_address || '127.0.0.1'}</td>
      <td><code>${JSON.stringify(l.action_details || {})}</code></td>
    </tr>
  `).join('');
}

// Assignment Modal
function openAssignModal(caseId) {
  activeCaseForAssignment = caseId;
  const c = cachedCases.find(item => item.id === caseId || item.complaint_ref_no === caseId);
  document.getElementById('modal-case-title').textContent = `Assign Case: ${c ? (c.complaint_ref_no || c.title) : caseId}`;
  document.getElementById('reassign-modal').style.display = 'flex';
}

function closeAssignModal() {
  activeCaseForAssignment = null;
  document.getElementById('reassign-modal').style.display = 'none';
}

async function confirmAssignOfficer() {
  const officerSelect = document.getElementById('modal-officer-select');
  const officerId = officerSelect.value;
  const officerName = officerSelect.options[officerSelect.selectedIndex].text;

  if (!activeCaseForAssignment) return;

  try {
    await fetch(`${API_BASE}/api/admin/assign-officer`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ caseId: activeCaseForAssignment, officerId })
    });
  } catch (e) {
    // Continue with local update
  }

  const c = cachedCases.find(item => item.id === activeCaseForAssignment || item.complaint_ref_no === activeCaseForAssignment);
  if (c) {
    c.assigned_officer_id = officerName;
    c.current_status = 'UNDER_INVESTIGATION';
  }

  closeAssignModal();
  refreshData();
}

// Initial Load
document.addEventListener('DOMContentLoaded', () => {
  refreshData();
});
