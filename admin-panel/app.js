// =========================================================
// DURGA CENTRAL ADMIN & POLICE OPERATIONS JAVASCRIPT
// =========================================================

const API_BASE = window.location.origin.includes('localhost') || window.location.origin.includes('127.0.0.1')
  ? 'http://127.0.0.1:3000'
  : window.location.origin;

let currentUser = null;
let currentToken = null;
let currentLoginRole = 'ADMIN'; // 'ADMIN' or 'OFFICER'

let cachedCases = [];
let cachedOfficers = [];
let cachedAuditLogs = [];
let activeCaseForAssignment = null;
let activeCaseDossier = null;

// =========================================================
// 1. AUTHENTICATION & SESSION MANAGEMENT
// =========================================================

function setLoginRole(role) {
  currentLoginRole = role;
  const adminBtn = document.getElementById('tab-btn-admin');
  const officerBtn = document.getElementById('tab-btn-officer');
  const identityLabel = document.getElementById('identity-label');
  const identityInput = document.getElementById('login-identity');
  const demoLabel = document.getElementById('demo-role-label');
  const demoText = document.getElementById('demo-text');

  if (role === 'ADMIN') {
    adminBtn.classList.add('active');
    officerBtn.classList.remove('active');
    identityLabel.textContent = 'Official Administrator Email:';
    identityInput.placeholder = 'e.g. admin@durga.gov.in';
    demoLabel.textContent = '🔑 Admin Credentials';
    demoText.innerHTML = 'Email: <code>admin@durga.gov.in</code> &bull; Password: <code>durgaAdmin2026!</code>';
  } else {
    officerBtn.classList.add('active');
    adminBtn.classList.remove('active');
    identityLabel.textContent = 'Police Badge ID or Email:';
    identityInput.placeholder = 'e.g. POL-GJ-4029';
    demoLabel.textContent = '👮 Officer Credentials';
    demoText.innerHTML = 'Badge: <code>POL-GJ-4029</code> &bull; Password: <code>officer123</code>';
  }
}

function autoFillDemoCredentials() {
  const identityInput = document.getElementById('login-identity');
  const passwordInput = document.getElementById('login-password');
  if (currentLoginRole === 'ADMIN') {
    identityInput.value = 'admin@durga.gov.in';
    passwordInput.value = 'durgaAdmin2026!';
  } else {
    identityInput.value = 'POL-GJ-4029';
    passwordInput.value = 'officer123';
  }
}

function togglePasswordVisibility() {
  const pwd = document.getElementById('login-password');
  pwd.type = pwd.type === 'password' ? 'text' : 'password';
}

async function handlePortalLogin(e) {
  e.preventDefault();
  const identity = document.getElementById('login-identity').value.trim();
  const password = document.getElementById('login-password').value;
  const errorAlert = document.getElementById('login-error-alert');
  const submitBtn = document.getElementById('btn-submit-login');
  const spinner = document.getElementById('login-btn-spinner');
  const btnText = document.getElementById('login-btn-text');

  errorAlert.style.display = 'none';
  submitBtn.disabled = true;
  spinner.style.display = 'inline';
  btnText.textContent = 'Verifying Credentials...';

  try {
    let endpoint = `${API_BASE}/api/auth/admin-login`;
    let bodyData = { email: identity, password };

    if (currentLoginRole === 'OFFICER') {
      endpoint = `${API_BASE}/api/auth/officer-login`;
      bodyData = identity.includes('@')
        ? { email: identity, password }
        : { badgeId: identity, password };
    }

    const res = await fetch(endpoint, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(bodyData)
    });

    const data = await res.json();

    if (!res.ok || !data.success) {
      throw new Error(data.error || 'Authentication failed. Please verify credentials.');
    }

    // Success
    currentToken = data.token;
    currentUser = data.user || data.officer;
    sessionStorage.setItem('durga_token', currentToken);
    sessionStorage.setItem('durga_user', JSON.stringify(currentUser));

    applyUserSessionUI();
    showDashboardView();
    refreshData();
  } catch (err) {
    errorAlert.textContent = err.message || 'Login failed. Network or server error.';
    errorAlert.style.display = 'block';
  } finally {
    submitBtn.disabled = false;
    spinner.style.display = 'none';
    btnText.textContent = 'Authenticate & Enter Portal';
  }
}

function applyUserSessionUI() {
  if (!currentUser) return;
  const nameEl = document.getElementById('sidebar-user-name');
  const roleEl = document.getElementById('sidebar-user-role');
  const avatarEl = document.getElementById('sidebar-user-avatar');

  const displayName = currentUser.fullName || currentUser.full_name || currentUser.badgeId || 'Admin User';
  const roleName = (currentUser.roles && currentUser.roles.includes('ROLE_ADMIN'))
    ? 'Central Admin Desk'
    : `Officer (${currentUser.badgeId || currentUser.badge_id || 'On Duty'})`;

  nameEl.textContent = displayName;
  roleEl.textContent = roleName;
  avatarEl.textContent = displayName.substring(0, 2).toUpperCase();
}

function handlePortalLogout() {
  currentToken = null;
  currentUser = null;
  sessionStorage.removeItem('durga_token');
  sessionStorage.removeItem('durga_user');

  document.getElementById('dashboard-view').style.display = 'none';
  document.getElementById('login-view').style.display = 'flex';
}

function showDashboardView() {
  document.getElementById('login-view').style.display = 'none';
  document.getElementById('dashboard-view').style.display = 'flex';
}

async function verifySavedSession() {
  const savedToken = sessionStorage.getItem('durga_token');
  const savedUser = sessionStorage.getItem('durga_user');

  if (savedToken && savedUser) {
    try {
      currentToken = savedToken;
      currentUser = JSON.parse(savedUser);
      applyUserSessionUI();
      showDashboardView();
      refreshData();
      return;
    } catch (e) {
      sessionStorage.clear();
    }
  }

  // Not logged in: show login view
  document.getElementById('login-view').style.display = 'flex';
  document.getElementById('dashboard-view').style.display = 'none';
}

// =========================================================
// 2. TAB SWITCHING & TITLES
// =========================================================

function switchTab(tabId) {
  document.querySelectorAll('.tab-pane').forEach(el => el.classList.remove('active'));
  document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));

  const targetPane = document.getElementById(`tab-${tabId}`);
  if (targetPane) targetPane.classList.add('active');

  const navItems = document.querySelectorAll('.nav-links .nav-item');
  const tabIndexMap = { dashboard: 0, cases: 1, officers: 2, audit: 3, helplines: 4 };
  if (tabIndexMap[tabId] !== undefined && navItems[tabIndexMap[tabId]]) {
    navItems[tabIndexMap[tabId]].classList.add('active');
  }

  const titles = {
    dashboard: ['Executive Dashboard', 'Real-time incident reports, case progression, and statutory compliance.'],
    cases: ['Cases Repository & Triage Vault', 'Complete repository of incoming reports, triage, and officer assignment.'],
    officers: ['Officers & Personnel Roster', 'Authorized law enforcement officers and statutory nodal reviewers.'],
    audit: ['Statutory Audit Trail', 'Immutable cryptographic SHA-256 log of every access, triage, and update.'],
    helplines: ['Emergency Helplines', 'Verified official national and state emergency directories in India.']
  };

  if (titles[tabId]) {
    document.getElementById('page-title').textContent = titles[tabId][0];
    document.getElementById('page-subtitle').textContent = titles[tabId][1];
  }
}

// =========================================================
// 3. DATA REFRESH & BACKEND SYNC
// =========================================================

async function refreshData() {
  checkSystemHealth();

  try {
    // 1. Fetch Stats
    const statsRes = await fetch(`${API_BASE}/api/admin/stats`);
    if (statsRes.ok) {
      const data = await statsRes.json();
      if (data.stats) {
        document.getElementById('kpi-total').textContent = data.stats.totalCases;
        document.getElementById('kpi-pending').textContent = data.stats.pendingReview;
        document.getElementById('kpi-investigating').textContent = data.stats.underInvestigation;
        document.getElementById('kpi-audit').textContent = data.stats.totalAuditLogs;
        document.getElementById('kpi-resolved').textContent = data.stats.resolved;
      }
    }

    // 2. Fetch Cases
    const casesRes = await fetch(`${API_BASE}/api/cases`);
    if (casesRes.ok) {
      const data = await casesRes.json();
      if (data.cases) {
        cachedCases = data.cases;
        // Count assigned
        const assignedCount = cachedCases.filter(c => c.current_status === 'ASSIGNED' || c.current_status === 'UNDER_REVIEW').length;
        document.getElementById('kpi-assigned').textContent = assignedCount;
      }
    }

    // 3. Fetch Officers
    const officersRes = await fetch(`${API_BASE}/api/admin/officers`);
    if (officersRes.ok) {
      const data = await officersRes.json();
      if (data.officers) {
        cachedOfficers = data.officers;
        populateOfficerSelects();
      }
    }

    // 4. Fetch Audit Logs
    const auditRes = await fetch(`${API_BASE}/api/admin/audit-logs?limit=150`);
    if (auditRes.ok) {
      const data = await auditRes.json();
      if (data.logs) cachedAuditLogs = data.logs;
    }
  } catch (err) {
    console.warn('Backend sync failed, using cached memory:', err);
  }

  renderDashboard();
  renderCases();
  renderOfficers();
  renderAuditLogs();
}

async function checkSystemHealth() {
  const badge = document.getElementById('system-health-badge');
  try {
    const res = await fetch(`${API_BASE}/health`);
    if (res.ok) {
      badge.textContent = '● API Online';
      badge.className = 'system-badge online';
    } else {
      badge.textContent = '▲ Degraded';
      badge.className = 'system-badge offline';
    }
  } catch (e) {
    badge.textContent = '✕ Offline';
    badge.className = 'system-badge offline';
  }
}

// =========================================================
// 4. RENDER DASHBOARD & CASES
// =========================================================

function renderDashboard() {
  const tbody = document.getElementById('dashboard-recent-cases');
  if (!tbody) return;

  if (cachedCases.length === 0) {
    tbody.innerHTML = `<tr><td colspan="8" style="text-align:center;padding:24px;color:#94A3B8;">No incident cases recorded in vault yet.</td></tr>`;
    return;
  }

  tbody.innerHTML = cachedCases.slice(0, 6).map(c => `
    <tr>
      <td><strong>${c.complaint_ref_no || c.caseId}</strong></td>
      <td><span class="badge-tag">${formatCategory(c.category_id || c.category)}</span></td>
      <td><strong>${c.title}</strong></td>
      <td>${c.incident_location || c.location || 'Gujarat'}</td>
      <td><span class="priority-badge priority-${c.priority || 'NORMAL'}">${c.priority || 'NORMAL'}</span></td>
      <td><span class="status-badge status-${c.current_status || c.status}">${formatStatus(c.current_status || c.status)}</span></td>
      <td>${c.assigned_officer_name || c.assigned_officer_id || 'Unassigned'}</td>
      <td>
        <button class="btn btn-sm btn-primary" onclick="openCaseDossier('${c.id || c.complaint_ref_no}')">Review Dossier</button>
      </td>
    </tr>
  `).join('');
}

function renderCases() {
  const tbody = document.getElementById('full-cases-list');
  if (!tbody) return;

  const query = (document.getElementById('case-search')?.value || '').toLowerCase().trim();
  const statusFilter = document.getElementById('case-status-filter')?.value || 'ALL';
  const priorityFilter = document.getElementById('case-priority-filter')?.value || 'ALL';
  const categoryFilter = document.getElementById('case-category-filter')?.value || 'ALL';

  const filtered = cachedCases.filter(c => {
    const matchesQuery =
      (c.complaint_ref_no || '').toLowerCase().includes(query) ||
      (c.title || '').toLowerCase().includes(query) ||
      (c.incident_location || '').toLowerCase().includes(query) ||
      (c.assigned_officer_name || '').toLowerCase().includes(query);

    const matchesStatus = statusFilter === 'ALL' || c.current_status === statusFilter;
    const matchesPriority = priorityFilter === 'ALL' || (c.priority || 'NORMAL') === priorityFilter;
    const matchesCategory = categoryFilter === 'ALL' || (c.category_id || c.category) === categoryFilter;

    return matchesQuery && matchesStatus && matchesPriority && matchesCategory;
  });

  if (filtered.length === 0) {
    tbody.innerHTML = `<tr><td colspan="9" style="text-align:center;padding:24px;color:#94A3B8;">No matching cases found for the selected filters.</td></tr>`;
    return;
  }

  tbody.innerHTML = filtered.map(c => `
    <tr>
      <td>
        <strong>${c.complaint_ref_no || c.caseId}</strong>
        <div style="font-size:10px;color:#94A3B8;">${new Date(c.created_at || Date.now()).toLocaleDateString()}</div>
      </td>
      <td><span class="badge-tag">${formatCategory(c.category_id || c.category)}</span></td>
      <td>
        <strong>${c.title}</strong>
        <div style="font-size:11px;color:#64748B;max-width:240px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">
          ${c.structured_description || c.original_description || ''}
        </div>
      </td>
      <td>${c.incident_location || 'Gujarat'}</td>
      <td><span class="priority-badge priority-${c.priority || 'NORMAL'}">${c.priority || 'NORMAL'}</span></td>
      <td>
        <span class="badge-tag">🔒 ${c.evidence_count || 0} Files</span>
      </td>
      <td><span class="status-badge status-${c.current_status || c.status}">${formatStatus(c.current_status || c.status)}</span></td>
      <td>${c.assigned_officer_name || c.assigned_officer_id || '<span style="color:#DC2626;">Unassigned</span>'}</td>
      <td style="white-space:nowrap;">
        <button class="btn btn-sm btn-primary" onclick="openCaseDossier('${c.id || c.complaint_ref_no}')">👁️ Dossier</button>
        <button class="btn btn-sm btn-outline" onclick="openAssignModal('${c.id || c.complaint_ref_no}')">👮 Assign</button>
      </td>
    </tr>
  `).join('');
}

function filterCases() {
  renderCases();
}

function formatStatus(status) {
  if (!status) return 'SUBMITTED';
  return status.replace(/_/g, ' ');
}

function formatCategory(cat) {
  if (!cat) return 'GENERAL SAFETY';
  return cat.replace(/_/g, ' ');
}

// =========================================================
// 5. CASE DOSSIER & INVESTIGATION DETAILS
// =========================================================

async function openCaseDossier(caseIdentifier) {
  try {
    const res = await fetch(`${API_BASE}/api/cases/${caseIdentifier}`);
    if (!res.ok) throw new Error('Could not load case details');
    const data = await res.json();
    activeCaseDossier = data;
    populateCaseDossierModal(data);
    document.getElementById('case-dossier-modal').style.display = 'flex';
  } catch (err) {
    // Fallback to local cache
    const c = cachedCases.find(item => item.id === caseIdentifier || item.complaint_ref_no === caseIdentifier);
    if (c) {
      activeCaseDossier = { case: c, evidence: [], timeline: [], notes: [] };
      populateCaseDossierModal(activeCaseDossier);
      document.getElementById('case-dossier-modal').style.display = 'flex';
    } else {
      alert('Case details could not be found.');
    }
  }
}

function closeCaseDossier() {
  activeCaseDossier = null;
  document.getElementById('case-dossier-modal').style.display = 'none';
}

function populateCaseDossierModal(data) {
  const c = data.case;
  document.getElementById('modal-case-heading').textContent = `Case Dossier: ${c.complaint_ref_no}`;
  document.getElementById('modal-ref-no').textContent = c.complaint_ref_no;
  document.getElementById('modal-category').textContent = formatCategory(c.category_id);
  document.getElementById('modal-incident-datetime').textContent = `${c.incident_date || 'N/A'} • ${c.incident_time || 'N/A'}`;
  document.getElementById('modal-location').textContent = c.incident_location || 'Gujarat, India';
  document.getElementById('modal-officer').textContent = c.assigned_officer_name || c.assigned_officer_id || 'Not Assigned';

  const statusBadge = document.getElementById('modal-current-status');
  statusBadge.textContent = formatStatus(c.current_status);
  statusBadge.className = `status-badge status-${c.current_status}`;

  const priorityBadge = document.getElementById('modal-priority-badge');
  priorityBadge.textContent = c.priority || 'NORMAL';
  priorityBadge.className = `priority-badge priority-${c.priority || 'NORMAL'}`;

  // Complainant Information (Strict Privacy Guard)
  const compGrid = document.getElementById('modal-complainant-grid');
  if (c.complainant && c.complainant.name) {
    compGrid.innerHTML = `
      <div class="info-item"><label>Complainant Name</label><span>${c.complainant.name}</span></div>
      <div class="info-item"><label>Age & Gender</label><span>${c.complainant.age || 'N/A'} yrs &bull; ${c.complainant.gender || 'N/A'}</span></div>
      <div class="info-item"><label>Mobile Contact</label><span>${c.complainant.mobile || 'Confidential'}</span></div>
      <div class="info-item"><label>State & District</label><span>${c.complainant.district || ''}, ${c.complainant.state || 'Gujarat'}</span></div>
      <div class="info-item"><label>Identity Status</label><span style="color:#16A34A;">● Voluntary Verified Submission</span></div>
    `;
  } else {
    compGrid.innerHTML = `
      <div class="info-item" style="grid-column: 1 / -1;">
        <span style="color:#475569; font-weight: 500;">
          🛡️ <strong>Citizen Protection Active:</strong> Anonymous or Witness report. Personal complainant details are withheld under statutory confidentiality guidelines.
        </span>
      </div>
    `;
  }

  // Statements
  document.getElementById('modal-original-description').textContent = c.original_description || 'No statement submitted.';
  const structuredWrapper = document.getElementById('modal-structured-wrapper');
  if (c.structured_description && c.structured_description !== c.original_description) {
    structuredWrapper.style.display = 'block';
    document.getElementById('modal-structured-description').textContent = c.structured_description;
  } else {
    structuredWrapper.style.display = 'none';
  }

  // Evidence
  const evList = document.getElementById('modal-evidence-list');
  const evidenceItems = data.evidence || [];
  if (evidenceItems.length === 0) {
    evList.innerHTML = `<div style="font-size:12px;color:#94A3B8;padding:8px;">No external files deposited or files encrypted in local device vault.</div>`;
  } else {
    evList.innerHTML = evidenceItems.map((e, idx) => `
      <div class="evidence-chip">
        <div>
          <strong>📎 File #${idx + 1}: ${e.file_name || e.evidence_ref}</strong>
          <div style="font-size:10px;color:#64748B;">Type: ${e.file_type} &bull; Timestamp: ${new Date(e.created_at).toLocaleString()}</div>
        </div>
        <div class="hash-badge" title="Cryptographic SHA-256 Hash">
          SHA-256: ${(e.sha256_checksum || '').substring(0, 16)}...
        </div>
      </div>
    `).join('');
  }

  // Timeline
  const tlList = document.getElementById('modal-timeline-list');
  const events = data.timeline || [];
  if (events.length === 0) {
    tlList.innerHTML = `<div style="font-size:12px;color:#94A3B8;">Case registered in vault.</div>`;
  } else {
    tlList.innerHTML = events.map(ev => `
      <div class="timeline-item">
        <div class="time">${new Date(ev.timestamp).toLocaleString()} &bull; <strong>${ev.actor_name} (${ev.actor_role})</strong></div>
        <div class="title">${ev.title}</div>
        <div class="desc">${ev.description}</div>
      </div>
    `).join('');
  }

  // Notes
  const notesList = document.getElementById('modal-notes-list');
  const notes = data.notes || [];
  if (notes.length === 0) {
    notesList.innerHTML = `<div style="font-size:12px;color:#94A3B8;">No official station notes recorded yet.</div>`;
  } else {
    notesList.innerHTML = notes.map(n => `
      <div style="background:#F1F5F9; border-left:3px solid #0284C7; padding:8px 12px; border-radius:4px; margin-bottom:8px; font-size:12px;">
        <div style="display:flex; justify-content:space-between; margin-bottom:4px; font-size:11px; color:#64748B;">
          <strong>${n.author_name} (${n.badge_id || n.role})</strong>
          <span>${new Date(n.created_at).toLocaleTimeString()}</span>
        </div>
        <div>${n.content}</div>
      </div>
    `).join('');
  }

  // Status select init
  document.getElementById('modal-update-status-select').value = c.current_status || 'UNDER_INVESTIGATION';
}

async function submitCaseStatusUpdate() {
  if (!activeCaseDossier || !activeCaseDossier.case) return;
  const c = activeCaseDossier.case;
  const newStatus = document.getElementById('modal-update-status-select').value;
  const remarks = document.getElementById('modal-action-remarks').value.trim();
  const officerSelect = document.getElementById('modal-update-officer-select');
  const selectedOfficerId = officerSelect.value;
  const selectedOfficerText = officerSelect.options[officerSelect.selectedIndex]?.text || '';

  const officerBadge = currentUser?.badgeId || currentUser?.badge_id || 'ADMIN-DESK';
  const officerName = currentUser?.fullName || currentUser?.full_name || 'Authorized Official';

  try {
    // 1. Update status
    await fetch(`${API_BASE}/api/cases/${c.id || c.complaint_ref_no}/status`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        status: newStatus,
        remarks: remarks || `Status updated to ${newStatus}`,
        officerBadge,
        officerName
      })
    });

    // 2. Assign officer if changed
    if (selectedOfficerId) {
      await fetch(`${API_BASE}/api/admin/assign-officer`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          caseId: c.id,
          officerId: selectedOfficerId,
          adminNotes: remarks,
          adminName: officerName
        })
      });
    }

    document.getElementById('modal-action-remarks').value = '';
    alert('Case status and official remarks updated successfully!');
    closeCaseDossier();
    refreshData();
  } catch (err) {
    alert('Failed to update status on server: ' + err.message);
  }
}

function printOfficialDocket() {
  window.print();
}

// =========================================================
// 6. QUICK CASE QR / CODE LOOKUP
// =========================================================

async function quickLookupCase() {
  const input = document.getElementById('quick-case-input');
  const code = input.value.trim();
  if (!code) {
    alert('Please enter a Case ID or Secure Token');
    return;
  }

  try {
    const res = await fetch(`${API_BASE}/api/cases/lookup/qr?code=${encodeURIComponent(code)}`);
    const data = await res.json();
    if (!res.ok || !data.success) {
      throw new Error(data.error || 'Case not found');
    }

    input.value = '';
    openCaseDossier(data.caseId);
  } catch (err) {
    alert(err.message || 'Invalid Case ID or Code');
  }
}

// =========================================================
// 7. ASSIGN OFFICER MODAL
// =========================================================

function openAssignModal(caseId) {
  activeCaseForAssignment = caseId;
  const c = cachedCases.find(item => item.id === caseId || item.complaint_ref_no === caseId);
  document.getElementById('modal-assign-title').textContent = `Assigning Officer for Case: ${c ? (c.complaint_ref_no || c.title) : caseId}`;
  populateOfficerSelects();
  document.getElementById('reassign-modal').style.display = 'flex';
}

function closeAssignModal() {
  activeCaseForAssignment = null;
  document.getElementById('reassign-modal').style.display = 'none';
}

async function confirmAssignOfficer() {
  const officerSelect = document.getElementById('modal-officer-select');
  const officerId = officerSelect.value;
  const remarks = document.getElementById('modal-assign-remarks').value;

  if (!activeCaseForAssignment) return;

  try {
    await fetch(`${API_BASE}/api/admin/assign-officer`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        caseId: activeCaseForAssignment,
        officerId,
        adminNotes: remarks,
        adminName: currentUser?.fullName || 'Admin Desk'
      })
    });
  } catch (e) {
    console.error('Error assigning officer:', e);
  }

  closeAssignModal();
  refreshData();
}

function populateOfficerSelects() {
  const modalSelect = document.getElementById('modal-officer-select');
  const updateSelect = document.getElementById('modal-update-officer-select');
  if (!modalSelect || !updateSelect) return;

  const optionsHtml = cachedOfficers.map(o => `
    <option value="${o.id || o.badge_id}">${o.full_name || o.fullName} (Badge: ${o.badge_id || 'POLICE'})</option>
  `).join('');

  modalSelect.innerHTML = optionsHtml;
  updateSelect.innerHTML = `<option value="">-- Keep Current Assignment --</option>` + optionsHtml;
}

// =========================================================
// 8. OFFICERS ROSTER & REGISTRATION
// =========================================================

function renderOfficers() {
  const tbody = document.getElementById('officers-list');
  if (!tbody) return;

  if (cachedOfficers.length === 0) {
    tbody.innerHTML = `<tr><td colspan="6" style="text-align:center;padding:20px;color:#94A3B8;">No officers registered.</td></tr>`;
    return;
  }

  tbody.innerHTML = cachedOfficers.map(o => `
    <tr>
      <td><strong>${o.badge_id || 'POL-GJ-XXXX'}</strong></td>
      <td>${o.full_name || o.fullName}</td>
      <td>${o.email || 'officer@police.gov.in'}</td>
      <td><span class="badge-tag highlight">${(o.roles || []).join(', ').replace(/ROLE_/g, '')}</span></td>
      <td>Gandhinagar / Gujarat State</td>
      <td><span class="status-badge status-RESOLVED">Active On-Duty</span></td>
    </tr>
  `).join('');
}

function openNewOfficerModal() {
  document.getElementById('register-officer-modal').style.display = 'flex';
}

function closeNewOfficerModal() {
  document.getElementById('register-officer-modal').style.display = 'none';
}

async function confirmRegisterOfficer() {
  const badgeId = document.getElementById('new-officer-badge').value.trim();
  const name = document.getElementById('new-officer-name').value.trim();
  const email = document.getElementById('new-officer-email').value.trim();
  const role = document.getElementById('new-officer-role').value;

  if (!badgeId || !name) {
    alert('Badge Number and Officer Name are required');
    return;
  }

  try {
    await fetch(`${API_BASE}/api/auth/officer-login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ badgeId, role })
    });

    closeNewOfficerModal();
    refreshData();
    alert(`Officer ${name} (${badgeId}) registered successfully!`);
  } catch (err) {
    alert('Failed to register officer: ' + err.message);
  }
}

// =========================================================
// 9. AUDIT LOGS
// =========================================================

function renderAuditLogs() {
  const tbody = document.getElementById('audit-logs-list');
  if (!tbody) return;

  if (cachedAuditLogs.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;padding:24px;color:#94A3B8;">No audit events recorded yet.</td></tr>`;
    return;
  }

  tbody.innerHTML = cachedAuditLogs.map(l => `
    <tr>
      <td>${new Date(l.timestamp).toLocaleTimeString()}</td>
      <td><strong>${l.event_type}</strong></td>
      <td><span class="badge-tag">${l.entity_type}</span></td>
      <td>${l.performed_by || 'System'}</td>
      <td>${l.badge_id || 'N/A'}</td>
      <td><code>${l.ip_address || '127.0.0.1'}</code></td>
      <td style="max-width:260px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;">
        <code>${JSON.stringify(l.action_details || {})}</code>
      </td>
    </tr>
  `).join('');
}

// =========================================================
// 10. INITIALIZATION
// =========================================================

document.addEventListener('DOMContentLoaded', () => {
  verifySavedSession();
});
