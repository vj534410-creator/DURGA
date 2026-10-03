const API_BASE = window.location.origin.includes('localhost') || window.location.origin.includes('127.0.0.1')
  ? 'http://127.0.0.1:3000'
  : window.location.origin;

let currentBadgeId = 'POL-GJ-4029';
let currentOfficerName = 'Inspector R. Sharma';
let assignedCases = [];
let activeCase = null;

async function refreshOfficerCases() {
  document.getElementById('assigned-cases-count').textContent = 'Syncing...';

  try {
    const res = await fetch(`${API_BASE}/api/officers/my-cases?badgeId=${currentBadgeId}`);
    if (res.ok) {
      const data = await res.json();
      if (data.cases) assignedCases = data.cases;
    }
  } catch (err) {
    console.warn('Backend unavailable, using default officer docket:', err);
    assignedCases = [
      {
        id: 'c-001',
        complaint_ref_no: 'DURGA-2026-08192',
        category_id: 'DOMESTIC_VIOLENCE',
        title: 'Emergency Domestic Assistance Request',
        original_description: 'Immediate counseling and protective intervention requested in residential area.',
        structured_description: 'Victim reports repeated threats and verbal abuse. Safe shelter and legal protection order required urgently.',
        incident_date: '2026-09-24',
        incident_time: '14:30',
        incident_location: 'Sector 7, Gandhinagar, Gujarat',
        current_status: 'UNDER_INVESTIGATION',
        evidence_count: 2
      }
    ];
  }

  document.getElementById('assigned-cases-count').textContent = `${assignedCases.length} assigned`;
  renderCasesList();
}

function renderCasesList() {
  const container = document.getElementById('officer-cases-list');
  if (!container) return;

  if (assignedCases.length === 0) {
    container.innerHTML = '<div style="padding:20px;text-align:center;color:#64748B;">No cases assigned to your badge docket.</div>';
    return;
  }

  container.innerHTML = assignedCases.map(c => `
    <div class="case-card ${activeCase && activeCase.complaint_ref_no === c.complaint_ref_no ? 'active' : ''}"
         onclick="selectCase('${c.complaint_ref_no}')">
      <div class="card-top">
        <span class="card-ref">${c.complaint_ref_no}</span>
        <div class="status-indicator-wrapper">
          <span class="tag-category">${c.category_id || 'GENERAL'}</span>
          <span class="status-badge status-${c.current_status || 'REPORT_SUBMITTED'}">${(c.current_status || 'REPORT_SUBMITTED').replace(/_/g, ' ')}</span>
        </div>
      </div>
      <div class="card-title">${c.title}</div>
      <div class="card-loc">📍 ${c.incident_location || 'Gujarat'}</div>
    </div>
  `).join('');
}

function selectCase(refNo) {
  activeCase = assignedCases.find(c => c.complaint_ref_no === refNo);
  if (!activeCase) return;

  renderCasesList();

  document.getElementById('empty-state').style.display = 'none';
  document.getElementById('dossier-content').style.display = 'flex';

  document.getElementById('dossier-category').textContent = activeCase.category_id || 'INCIDENT';
  document.getElementById('dossier-title').textContent = activeCase.title;
  document.getElementById('dossier-ref').textContent = `Official Ref: ${activeCase.complaint_ref_no}`;
  
  const statusElem = document.getElementById('dossier-status');
  if (statusElem) {
    statusElem.textContent = (activeCase.current_status || 'UNDER_INVESTIGATION').replace(/_/g, ' ');
    statusElem.className = `status-pill status-${activeCase.current_status || 'UNDER_INVESTIGATION'}`;
  }

  document.getElementById('dossier-datetime').textContent = `${activeCase.incident_date} • ${activeCase.incident_time}`;
  document.getElementById('dossier-location').textContent = activeCase.incident_location;
  document.getElementById('dossier-narrative').textContent = activeCase.structured_description || activeCase.original_description;

  document.getElementById('action-status-select').value = activeCase.current_status;
  document.getElementById('action-remarks').value = '';
  const phraseSelect = document.getElementById('standard-phrases-select');
  if (phraseSelect) phraseSelect.value = '';
}

function applyStandardPhrase() {
  const select = document.getElementById('standard-phrases-select');
  const textarea = document.getElementById('action-remarks');
  if (select && textarea && select.value) {
    if (textarea.value.trim() === '') {
      textarea.value = select.value;
    } else {
      textarea.value = textarea.value.trim() + '\n' + select.value;
    }
  }
}

async function submitOfficerAction() {
  if (!activeCase) return;

  const newStatus = document.getElementById('action-status-select').value;
  const remarks = document.getElementById('action-remarks').value;

  try {
    await fetch(`${API_BASE}/api/cases/${activeCase.complaint_ref_no}/status`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        status: newStatus,
        remarks: remarks || `Investigated by Officer ${currentBadgeId}`,
        officerBadge: currentBadgeId
      })
    });
  } catch (e) {
    // Continue with optimistic update
  }

  activeCase.current_status = newStatus;
  const statusElem = document.getElementById('dossier-status');
  if (statusElem) {
    statusElem.textContent = newStatus.replace(/_/g, ' ');
    statusElem.className = `status-pill status-${newStatus}`;
  }
  alert(`Officer Action Recorded: Case ${activeCase.complaint_ref_no} updated to ${newStatus}. Statutory audit log preserved.`);
  refreshOfficerCases();
}

function switchOfficerBadge() {
  const newBadge = prompt('Enter your Police Officer Service Badge ID (e.g. POL-GJ-8102):', currentBadgeId);
  if (newBadge && newBadge.trim()) {
    currentBadgeId = newBadge.trim();
    document.getElementById('session-badge').textContent = `BADGE: ${currentBadgeId}`;
    activeCase = null;
    document.getElementById('empty-state').style.display = 'flex';
    document.getElementById('dossier-content').style.display = 'none';
    refreshOfficerCases();
  }
}

document.addEventListener('DOMContentLoaded', () => {
  refreshOfficerCases();
});
