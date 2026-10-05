const API_BASE = window.location.origin.includes('localhost') || window.location.origin.includes('127.0.0.1')
  ? 'http://127.0.0.1:3000'
  : window.location.origin;

let currentBadgeId = 'POL-GJ-4029';
let currentOfficerName = 'Inspector R. Sharma';
let assignedCases = [];
let activeCase = null;

function getCasePriorityLevel(c) {
  if (!c) return 'NORMAL';
  const priority = (c.priority || '').toUpperCase();
  if (priority === 'EMERGENCY') return 'EMERGENCY';
  if (priority === 'URGENT') return 'URGENT';

  const category = (c.category_id || '').toUpperCase();
  if (category.includes('EMERGENCY') || category.includes('SOS')) return 'EMERGENCY';
  if (category.includes('URGENT') || category.includes('CRITICAL')) return 'URGENT';

  if (c.is_emergency === true) return 'EMERGENCY';

  const title = (c.title || '').toUpperCase();
  if (title.includes('EMERGENCY') || title.includes('DISTRESS') || title.startsWith('CRITICAL')) return 'EMERGENCY';
  if (title.includes('URGENT')) return 'URGENT';

  return 'NORMAL';
}

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
        id: 'c-003',
        complaint_ref_no: 'DURGA-2026-08194',
        category_id: 'SOS_EMERGENCY',
        title: 'CRITICAL: Physical Distress Alert & Stalking in Transit',
        original_description: 'Citizen triggered live emergency SOS alert from bus terminal. Immediate officer patrol dispatch requested.',
        structured_description: 'Active stalker pursuing victim near Gandhinagar Central Bus Terminal. Immediate field intervention required.',
        incident_date: '2026-09-25',
        incident_time: '19:45',
        incident_location: 'Central Bus Terminal, Sector 11, Gandhinagar',
        latitude: 23.2212,
        longitude: 72.6481,
        priority: 'EMERGENCY',
        current_status: 'UNDER_INVESTIGATION',
        evidence_count: 3
      },
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
        latitude: 23.2156,
        longitude: 72.6369,
        priority: 'URGENT',
        current_status: 'UNDER_INVESTIGATION',
        evidence_count: 2
      },
      {
        id: 'c-002',
        complaint_ref_no: 'DURGA-2026-08193',
        category_id: 'CYBER_ONLINE',
        title: 'Online Defamation & Impersonation on Social Media',
        original_description: 'Fake profile created using personal phone and photos without consent.',
        structured_description: 'Perpetrator using unverified phone to send threatening messages across platforms.',
        incident_date: '2026-09-23',
        incident_time: '18:15',
        incident_location: 'Navrangpura, Ahmedabad, Gujarat',
        latitude: 23.0225,
        longitude: 72.5714,
        priority: 'NORMAL',
        current_status: 'REPORT_SUBMITTED',
        evidence_count: 1
      }
    ];
  }

  document.getElementById('assigned-cases-count').textContent = `${assignedCases.length} assigned`;
  renderCasesList();

  // Automatically select first case if none active
  if (assignedCases.length > 0 && !activeCase) {
    selectCase(assignedCases[0].complaint_ref_no);
  }
}

function renderCasesList() {
  const container = document.getElementById('officer-cases-list');
  if (!container) return;

  if (assignedCases.length === 0) {
    container.innerHTML = '<div style="padding:20px;text-align:center;color:#64748B;">No cases assigned to your badge docket.</div>';
    return;
  }

  container.innerHTML = assignedCases.map(c => {
    const priority = getCasePriorityLevel(c);
    const isEmergency = priority === 'EMERGENCY';
    const isUrgent = priority === 'URGENT';

    let cardModifierClass = '';
    let notificationBanner = '';
    let priorityBadge = '';

    if (isEmergency) {
      cardModifierClass = 'card-flagged-emergency';
      notificationBanner = `
        <div class="card-notification-banner banner-emergency">
          <span class="beacon-dot beacon-emergency"></span>
          <span class="banner-title">🚨 EMERGENCY CASE</span>
          <span class="banner-action-flag">IMMEDIATE ATTENTION</span>
        </div>
      `;
      priorityBadge = `<span class="priority-badge badge-emergency">EMERGENCY</span>`;
    } else if (isUrgent) {
      cardModifierClass = 'card-flagged-urgent';
      notificationBanner = `
        <div class="card-notification-banner banner-urgent">
          <span class="beacon-dot beacon-urgent"></span>
          <span class="banner-title">⚠️ URGENT CASE</span>
          <span class="banner-action-flag">HIGH PRIORITY</span>
        </div>
      `;
      priorityBadge = `<span class="priority-badge badge-urgent">URGENT</span>`;
    }

    const isActive = activeCase && activeCase.complaint_ref_no === c.complaint_ref_no;
    const lat = c.latitude ? Number(c.latitude).toFixed(4) : null;
    const lng = c.longitude ? Number(c.longitude).toFixed(4) : null;

    return `
      <div class="case-card ${cardModifierClass} ${isActive ? 'active' : ''}"
           onclick="selectCase('${c.complaint_ref_no}')"
           tabindex="0"
           role="button"
           aria-label="Case ${c.complaint_ref_no} ${priority}">
        ${notificationBanner}
        <div class="card-top">
          <span class="card-ref">${c.complaint_ref_no}</span>
          <div class="status-indicator-wrapper">
            ${priorityBadge}
            <span class="tag-category">${c.category_id || 'GENERAL'}</span>
            <span class="status-badge status-${c.current_status || 'REPORT_SUBMITTED'}">${(c.current_status || 'REPORT_SUBMITTED').replace(/_/g, ' ')}</span>
          </div>
        </div>
        <div class="card-title">${c.title}</div>
        <div class="card-loc">📍 ${c.incident_location || 'Gujarat'}</div>
        ${lat && lng ? `<div class="card-coords-pill">🛰️ ${lat}° N, ${lng}° E</div>` : ''}
      </div>
    `;
  }).join('');
}

function selectCase(refNo) {
  activeCase = assignedCases.find(c => c.complaint_ref_no === refNo);
  if (!activeCase) return;

  renderCasesList();

  document.getElementById('empty-state').style.display = 'none';
  document.getElementById('dossier-content').style.display = 'flex';

  // Priority Notification Banner in Dossier Review Pane
  const priority = getCasePriorityLevel(activeCase);
  const dossierAlertElem = document.getElementById('dossier-priority-alert');
  if (dossierAlertElem) {
    if (priority === 'EMERGENCY') {
      dossierAlertElem.style.display = 'flex';
      dossierAlertElem.className = 'dossier-priority-alert alert-emergency';
      dossierAlertElem.innerHTML = `
        <span class="beacon-dot beacon-emergency"></span>
        <div class="alert-content">
          <strong>🚨 CRITICAL EMERGENCY CASE FLAGGED:</strong>
          <span>This complaint has been flagged as an EMERGENCY requiring immediate officer response and priority field patrol dispatch.</span>
        </div>
      `;
    } else if (priority === 'URGENT') {
      dossierAlertElem.style.display = 'flex';
      dossierAlertElem.className = 'dossier-priority-alert alert-urgent';
      dossierAlertElem.innerHTML = `
        <span class="beacon-dot beacon-urgent"></span>
        <div class="alert-content">
          <strong>⚠️ URGENT CASE ATTENTION REQUIRED:</strong>
          <span>This complaint requires priority investigation and rapid statutory review under officer protocol.</span>
        </div>
      `;
    } else {
      dossierAlertElem.style.display = 'none';
    }
  }

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

  // Reset time-scrubber to live fix and stop playback on case switch
  currentSnapshotIndex = 4;
  stopScrubberPlayback();

  // Render Static Map Preview Component with exact incident coordinates
  renderStaticMap(activeCase);

  document.getElementById('action-status-select').value = activeCase.current_status;
  document.getElementById('action-remarks').value = '';
  const phraseSelect = document.getElementById('standard-phrases-select');
  if (phraseSelect) phraseSelect.value = '';
}

// =========================================================
// STATIC MAP PREVIEW COMPONENT & CONTROLS
// =========================================================

let currentMapLayer = 'street'; // 'street' or 'satellite'
let currentMapZoom = 15;        // 12 (wide area) to 18 (building level)
let currentMapHeading = 0;      // 0, 90, 180, 270 degrees
let isPinTooltipOpen = false;
let currentExportFormat = 'PNG'; // 'PNG', 'JPEG', 'PDF'
let showNearbyLandmarks = false; // Toggle state for Nearby Landmarks overlay
let isLegendOpen = false;        // Toggle state for Incident Types Legend
let currentSnapshotIndex = 4;    // Time scrubber active snapshot (0: T-45m to 4: LIVE)
let isScrubberPlaying = false;   // Play/Pause animation state
let scrubberPlayTimer = null;    // Auto-advance timer

// Panning State
let isMapDragging = false;
let dragStartX = 0;
let dragStartY = 0;
let currentPanX = 0;
let currentPanY = 0;
let baseIncidentLat = 23.2156;
let baseIncidentLng = 72.6369;
let currentCenterLat = 23.2156;
let currentCenterLng = 72.6369;
let dragDistanceMoved = 0;

function onMapMouseDown(e) {
  if (e.button !== 0) return;
  if (e.target.closest('.map-zoom-controls') || 
      e.target.closest('.map-compass-widget') || 
      e.target.closest('.incident-pin-tooltip') ||
      e.target.closest('#btn-recenter-map') ||
      e.target.closest('#btn-toggle-legend') ||
      e.target.closest('#map-incident-legend') ||
      e.target.closest('.btn-legend-toggle') ||
      e.target.closest('#map-time-scrubber-bar') ||
      e.target.closest('#map-time-scrubber') ||
      e.target.closest('.scrubber-nav-btn') ||
      e.target.closest('.scrubber-tick') ||
      e.target.closest('#map-viewport-capture-group') ||
      e.target.closest('#btn-viewport-capture-map') ||
      e.target.closest('#btn-capture-dropdown-toggle') ||
      e.target.closest('#map-format-dropdown-menu') ||
      e.target.closest('#map-export-format-select')) {
    return;
  }

  e.preventDefault();
  isMapDragging = true;
  dragStartX = e.clientX;
  dragStartY = e.clientY;
  dragDistanceMoved = 0;

  const viewport = document.getElementById('static-map-viewport');
  if (viewport) {
    viewport.classList.add('is-dragging');
  }

  window.addEventListener('mousemove', onMapMouseMove);
  window.addEventListener('mouseup', onMapMouseUp);
}

function onMapMouseMove(e) {
  if (!isMapDragging) return;

  const deltaX = e.clientX - dragStartX;
  const deltaY = e.clientY - dragStartY;
  dragDistanceMoved += Math.hypot(deltaX, deltaY);

  dragStartX = e.clientX;
  dragStartY = e.clientY;

  // Coordinate adjustments based on rotation heading
  let adjDeltaX = deltaX;
  let adjDeltaY = deltaY;
  if (currentMapHeading === 90) {
    adjDeltaX = -deltaY;
    adjDeltaY = deltaX;
  } else if (currentMapHeading === 180) {
    adjDeltaX = -deltaX;
    adjDeltaY = -deltaY;
  } else if (currentMapHeading === 270) {
    adjDeltaX = deltaY;
    adjDeltaY = -deltaX;
  }

  currentPanX += adjDeltaX;
  currentPanY += adjDeltaY;

  applyMapPan();
  updateCoordinatesFromPan();
}

function onMapMouseUp(e) {
  if (!isMapDragging) return;
  isMapDragging = false;

  window.removeEventListener('mousemove', onMapMouseMove);
  window.removeEventListener('mouseup', onMapMouseUp);

  const viewport = document.getElementById('static-map-viewport');
  if (viewport) {
    viewport.classList.remove('is-dragging');
  }

  updateRecenterButtonVisibility();
}

// Touch Event Handlers for Mobile & Tablet Gesture Panning
function onMapTouchStart(e) {
  if (!e.touches || e.touches.length !== 1) return;
  const touch = e.touches[0];
  if (e.target.closest('.map-zoom-controls') || 
      e.target.closest('.map-compass-widget') || 
      e.target.closest('.incident-pin-tooltip') ||
      e.target.closest('#btn-recenter-map') ||
      e.target.closest('#btn-toggle-legend') ||
      e.target.closest('#map-incident-legend') ||
      e.target.closest('.btn-legend-toggle') ||
      e.target.closest('#map-time-scrubber-bar') ||
      e.target.closest('#map-time-scrubber') ||
      e.target.closest('.scrubber-nav-btn') ||
      e.target.closest('.scrubber-tick') ||
      e.target.closest('#map-viewport-capture-group') ||
      e.target.closest('#btn-viewport-capture-map') ||
      e.target.closest('#btn-capture-dropdown-toggle') ||
      e.target.closest('#map-format-dropdown-menu') ||
      e.target.closest('#map-export-format-select')) {
    return;
  }

  isMapDragging = true;
  dragStartX = touch.clientX;
  dragStartY = touch.clientY;
  dragDistanceMoved = 0;

  const viewport = document.getElementById('static-map-viewport');
  if (viewport) {
    viewport.classList.add('is-dragging');
  }

  window.addEventListener('touchmove', onMapTouchMove, { passive: false });
  window.addEventListener('touchend', onMapTouchEnd);
  window.addEventListener('touchcancel', onMapTouchEnd);
}

function onMapTouchMove(e) {
  if (!isMapDragging || !e.touches || e.touches.length !== 1) return;
  e.preventDefault();
  const touch = e.touches[0];
  const deltaX = touch.clientX - dragStartX;
  const deltaY = touch.clientY - dragStartY;
  dragDistanceMoved += Math.hypot(deltaX, deltaY);

  dragStartX = touch.clientX;
  dragStartY = touch.clientY;

  let adjDeltaX = deltaX;
  let adjDeltaY = deltaY;
  if (currentMapHeading === 90) {
    adjDeltaX = -deltaY;
    adjDeltaY = deltaX;
  } else if (currentMapHeading === 180) {
    adjDeltaX = -deltaX;
    adjDeltaY = -deltaY;
  } else if (currentMapHeading === 270) {
    adjDeltaX = deltaY;
    adjDeltaY = -deltaX;
  }

  currentPanX += adjDeltaX;
  currentPanY += adjDeltaY;

  applyMapPan();
  updateCoordinatesFromPan();
}

function onMapTouchEnd(e) {
  if (!isMapDragging) return;
  isMapDragging = false;

  window.removeEventListener('touchmove', onMapTouchMove);
  window.removeEventListener('touchend', onMapTouchEnd);
  window.removeEventListener('touchcancel', onMapTouchEnd);

  const viewport = document.getElementById('static-map-viewport');
  if (viewport) {
    viewport.classList.remove('is-dragging');
  }

  updateRecenterButtonVisibility();
}

function applyMapPan() {
  const panLayer = document.getElementById('map-pan-layer');
  if (panLayer) {
    panLayer.setAttribute('transform', `translate(${currentPanX}, ${currentPanY})`);
  }
  const tooltip = document.getElementById('incident-pin-tooltip');
  if (tooltip && isPinTooltipOpen) {
    tooltip.style.transform = `translate(calc(-50% + ${currentPanX}px), ${currentPanY}px)`;
  }
}

function updateCoordinatesFromPan() {
  // Degrees per pixel scaled by zoom level
  const degPerPx = 0.000035 * Math.pow(2, 15 - currentMapZoom);

  currentCenterLat = baseIncidentLat + (currentPanY * degPerPx);
  currentCenterLng = baseIncidentLng - (currentPanX * degPerPx);

  const latFormatted = `${Math.abs(currentCenterLat).toFixed(4)}° ${currentCenterLat >= 0 ? 'N' : 'S'}`;
  const lngFormatted = `${Math.abs(currentCenterLng).toFixed(4)}° ${currentCenterLng >= 0 ? 'E' : 'W'}`;

  const coordsVal = document.getElementById('map-coords-val');
  if (coordsVal) {
    const isPanned = Math.hypot(currentPanX, currentPanY) > 6;
    coordsVal.textContent = `${latFormatted}, ${lngFormatted}${isPanned ? ' (Panned)' : ''}`;
  }

  const panHudText = document.getElementById('map-pan-hud-text');
  if (panHudText) {
    panHudText.textContent = `Center: ${latFormatted}, ${lngFormatted}`;
  }

  const extLink = document.getElementById('map-external-link');
  if (extLink) {
    extLink.href = `https://maps.google.com/?q=${currentCenterLat},${currentCenterLng}`;
  }

  const svgStamp = document.getElementById('svg-latlng-stamp');
  if (svgStamp) {
    svgStamp.textContent = `[${currentCenterLat.toFixed(4)}, ${currentCenterLng.toFixed(4)}]`;
  }
}

function resetMapPan(e) {
  if (e) e.stopPropagation();
  currentPanX = 0;
  currentPanY = 0;
  applyMapPan();
  updateCoordinatesFromPan();
  updateRecenterButtonVisibility();
}

function updateRecenterButtonVisibility() {
  const btn = document.getElementById('btn-recenter-map');
  if (btn) {
    btn.style.display = (Math.hypot(currentPanX, currentPanY) > 8) ? 'flex' : 'none';
  }
}

// =========================================================
// MAP EXPORT FORMAT SELECTION & CAPTURE (PNG, JPEG, PDF)
// =========================================================

function selectExportFormat(fmt, event) {
  if (event) {
    event.stopPropagation();
  }
  fmt = (fmt || 'PNG').toUpperCase();
  if (!['PNG', 'JPEG', 'PDF'].includes(fmt)) fmt = 'PNG';
  currentExportFormat = fmt;

  // Update active format tag
  const formatTag = document.getElementById('active-format-tag');
  if (formatTag) formatTag.textContent = fmt;

  // Sync native and header selects
  const selects = [
    document.getElementById('map-export-format-select'),
    document.getElementById('header-export-format-select')
  ];
  selects.forEach(s => {
    if (s) s.value = fmt;
  });

  // Update dropdown menu items
  ['PNG', 'JPEG', 'PDF'].forEach(f => {
    const item = document.getElementById(`format-opt-${f.toLowerCase()}`);
    const check = document.getElementById(`fmt-check-${f.toLowerCase()}`);
    if (item) {
      if (f === fmt) {
        item.classList.add('active');
        if (check) check.textContent = '✓';
      } else {
        item.classList.remove('active');
        if (check) check.textContent = '';
      }
    }
  });

  closeCaptureFormatMenu();
}

function toggleCaptureFormatMenu(event) {
  if (event) {
    event.stopPropagation();
    event.preventDefault();
  }
  const menu = document.getElementById('map-format-dropdown-menu');
  const btn = document.getElementById('btn-capture-dropdown-toggle');
  if (!menu) return;
  const isOpening = (menu.style.display === 'none' || !menu.style.display);
  menu.style.display = isOpening ? 'flex' : 'none';
  if (btn) btn.setAttribute('aria-expanded', isOpening ? 'true' : 'false');

  if (isOpening) {
    setTimeout(() => {
      window.addEventListener('click', closeCaptureFormatMenu, { once: true });
    }, 10);
  }
}

function closeCaptureFormatMenu() {
  const menu = document.getElementById('map-format-dropdown-menu');
  const btn = document.getElementById('btn-capture-dropdown-toggle');
  if (menu) menu.style.display = 'none';
  if (btn) btn.setAttribute('aria-expanded', 'false');
}

async function exportMapCurrentFormat(event) {
  if (event) {
    event.stopPropagation();
    event.preventDefault();
  }
  await captureMapWithFormat(currentExportFormat, event);
}

// Backwards compatibility alias
async function captureMapToPng(event) {
  await captureMapWithFormat('PNG', event);
}

// High-Resolution Map Export for Police Reports (PNG, JPEG, PDF)
async function captureMapWithFormat(format = 'PNG', event) {
  if (event) {
    event.stopPropagation();
    event.preventDefault();
  }

  const exportFormat = (format || currentExportFormat || 'PNG').toUpperCase();

  const captureBtns = [
    document.getElementById('btn-capture-map'),
    document.getElementById('btn-viewport-capture-map')
  ].filter(Boolean);

  const btnIcon = document.getElementById('btn-capture-icon');
  const btnText = document.getElementById('btn-capture-text');

  captureBtns.forEach(b => {
    b.classList.add('is-capturing');
    const label = b.querySelector('.btn-capture-label') || btnText;
    if (label) label.textContent = exportFormat === 'PDF' ? 'Building PDF...' : 'Rendering...';
  });
  if (btnIcon) btnIcon.textContent = '⏳';

  try {
    const svgElem = document.querySelector('#static-map-render-target svg') || document.querySelector('#static-map-viewport svg');
    if (!svgElem) {
      alert('Map viewport is not ready to capture.');
      captureBtns.forEach(b => {
        b.classList.remove('is-capturing');
        const label = b.querySelector('.btn-capture-label') || btnText;
        if (label) label.textContent = 'Capture Map';
      });
      if (btnIcon) btnIcon.textContent = '📸';
      return;
    }

    // High resolution canvas (1400 x 720) for print quality in physical case dockets
    const scaleFactor = 2;
    const mapW = 700 * scaleFactor;
    const mapH = 260 * scaleFactor;
    const headerH = 60 * scaleFactor;
    const footerH = 40 * scaleFactor;
    const totalW = mapW;
    const totalH = headerH + mapH + footerH;

    const canvas = document.createElement('canvas');
    canvas.width = totalW;
    canvas.height = totalH;
    const ctx = canvas.getContext('2d');

    // Clone and serialize SVG with namespace
    const serializer = new XMLSerializer();
    let svgString = serializer.serializeToString(svgElem);
    if (!svgString.includes('xmlns="http://www.w3.org/2000/svg"')) {
      svgString = svgString.replace('<svg', '<svg xmlns="http://www.w3.org/2000/svg"');
    }

    const svgBlob = new Blob([svgString], { type: 'image/svg+xml;charset=utf-8' });
    const blobUrl = URL.createObjectURL(svgBlob);

    const img = new Image();
    img.crossOrigin = 'anonymous';

    await new Promise((resolve, reject) => {
      img.onload = () => resolve();
      img.onerror = () => {
        // Fallback to data URI if blob URL fails
        img.onload = () => resolve();
        img.onerror = (e) => reject(e);
        img.src = 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(svgString);
      };
      img.src = blobUrl;
    });

    // 1. Draw High-Res Police Report Background & Header
    ctx.fillStyle = '#0F172A';
    ctx.fillRect(0, 0, totalW, headerH);

    ctx.fillStyle = '#FFFFFF';
    ctx.font = `bold ${12 * scaleFactor}px 'Inter', sans-serif`;
    ctx.fillText('GOVERNMENT OF GUJARAT • POLICE DEPARTMENT', 20 * scaleFactor, 22 * scaleFactor);

    ctx.fillStyle = '#38BDF8';
    ctx.font = `bold ${9.5 * scaleFactor}px 'Inter', sans-serif`;
    ctx.fillText('CRIME INVESTIGATION & TACTICAL INCIDENT GEOLOCATION MAP DOCKET', 20 * scaleFactor, 40 * scaleFactor);

    const caseId = activeCase ? activeCase.complaint_ref_no : 'DURGA-2026';
    const priority = activeCase ? getCasePriorityLevel(activeCase) : 'NORMAL';
    const locText = activeCase ? (activeCase.incident_location || 'Gujarat Jurisdiction') : 'Gandhinagar';

    // Right header badge
    ctx.fillStyle = (priority === 'EMERGENCY') ? '#DC2626' : ((priority === 'URGENT') ? '#EA580C' : '#0284C7');
    ctx.fillRect(totalW - (175 * scaleFactor), 12 * scaleFactor, 155 * scaleFactor, 36 * scaleFactor);
    ctx.fillStyle = '#FFFFFF';
    ctx.font = `bold ${9.5 * scaleFactor}px 'Inter', sans-serif`;
    ctx.textAlign = 'center';
    ctx.fillText(`PRIORITY: ${priority}`, totalW - (97 * scaleFactor), 27 * scaleFactor);
    ctx.font = `normal ${8 * scaleFactor}px 'Inter', sans-serif`;
    ctx.fillText(`CASE: ${caseId} [${exportFormat}]`, totalW - (97 * scaleFactor), 40 * scaleFactor);
    ctx.textAlign = 'left';

    // 2. Draw Map Image
    ctx.drawImage(img, 0, headerH, mapW, mapH);
    try { URL.revokeObjectURL(blobUrl); } catch(e) {}

    // 3. Draw Bottom Report Metadata Footer
    const footerY = headerH + mapH;
    ctx.fillStyle = '#FFFFFF';
    ctx.fillRect(0, footerY, totalW, footerH);
    ctx.strokeStyle = '#CBD5E1';
    ctx.lineWidth = 1 * scaleFactor;
    ctx.beginPath();
    ctx.moveTo(0, footerY);
    ctx.lineTo(totalW, footerY);
    ctx.stroke();

    ctx.fillStyle = '#1E293B';
    ctx.font = `bold ${8.5 * scaleFactor}px 'Inter', sans-serif`;
    ctx.fillText(`Incident Location: ${locText} ${showNearbyLandmarks ? '• [Nearby Landmarks Active]' : ''}`, 20 * scaleFactor, footerY + (16 * scaleFactor));
    ctx.font = `bold ${8.5 * scaleFactor}px 'Inter', sans-serif`;
    ctx.fillText(`GPS Fix: ${Math.abs(currentCenterLat).toFixed(4)}° N, ${Math.abs(currentCenterLng).toFixed(4)}° E | Layer: ${currentMapLayer.toUpperCase()} (${currentMapZoom}x) | Format: ${exportFormat}`, 20 * scaleFactor, footerY + (30 * scaleFactor));

    ctx.fillStyle = '#64748B';
    ctx.font = `normal ${8 * scaleFactor}px 'Inter', sans-serif`;
    ctx.textAlign = 'right';
    ctx.fillText(`Investigating Officer: ${currentOfficerName} (${currentBadgeId}) • Date: ${new Date().toLocaleDateString('en-GB')}`, totalW - (20 * scaleFactor), footerY + (23 * scaleFactor));
    ctx.textAlign = 'left';

    // 4. Trigger File Download based on Selected Format
    const timestamp = Date.now();
    const downloadLink = document.createElement('a');

    if (exportFormat === 'PDF') {
      const jpegData = canvas.toDataURL('image/jpeg', 0.95);
      const pdfBlob = generatePdfFromJpeg(jpegData);
      const pdfUrl = URL.createObjectURL(pdfBlob);
      downloadLink.download = `POLICE_INCIDENT_MAP_${caseId}_${timestamp}.pdf`;
      downloadLink.href = pdfUrl;
      document.body.appendChild(downloadLink);
      downloadLink.click();
      document.body.removeChild(downloadLink);
      setTimeout(() => {
        try { URL.revokeObjectURL(pdfUrl); } catch(e) {}
      }, 5000);
    } else if (exportFormat === 'JPEG') {
      const dataUrl = canvas.toDataURL('image/jpeg', 0.92);
      downloadLink.download = `POLICE_INCIDENT_MAP_${caseId}_${timestamp}.jpg`;
      downloadLink.href = dataUrl;
      document.body.appendChild(downloadLink);
      downloadLink.click();
      document.body.removeChild(downloadLink);
    } else {
      // Default PNG
      const dataUrl = canvas.toDataURL('image/png');
      downloadLink.download = `POLICE_INCIDENT_MAP_${caseId}_${timestamp}.png`;
      downloadLink.href = dataUrl;
      document.body.appendChild(downloadLink);
      downloadLink.click();
      document.body.removeChild(downloadLink);
    }

    captureBtns.forEach(b => {
      const label = b.querySelector('.btn-capture-label') || btnText;
      if (label) label.textContent = `✓ Saved ${exportFormat}!`;
    });
    if (btnIcon) btnIcon.textContent = '✅';
    setTimeout(() => {
      captureBtns.forEach(b => {
        b.classList.remove('is-capturing');
        const label = b.querySelector('.btn-capture-label') || btnText;
        if (label) label.textContent = 'Capture Map';
      });
      if (btnIcon) btnIcon.textContent = '📸';
    }, 2200);

  } catch (err) {
    console.error('Failed to export map:', err);
    captureBtns.forEach(b => {
      b.classList.remove('is-capturing');
      const label = b.querySelector('.btn-capture-label') || btnText;
      if (label) label.textContent = 'Capture Map';
    });
    if (btnIcon) btnIcon.textContent = '📸';
  }
}

// Generate Standard Compliant PDF-1.4 file embedding the tactical map image
function generatePdfFromJpeg(jpegDataUrl, widthPt = 612, heightPt = 792) {
  const base64Data = jpegDataUrl.split(',')[1];
  const binaryJpeg = atob(base64Data);
  const jpegLength = binaryJpeg.length;

  const margin = 36;
  const contentW = widthPt - (margin * 2);
  const contentH = contentW * (640 / 1400);
  const posX = margin;
  const posY = heightPt - margin - contentH;

  let pdf = '%PDF-1.4\n%\xE2\xE3\xCF\xD3\n';
  let offsets = [];

  // Obj 1: Catalog
  offsets.push(pdf.length);
  pdf += '1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n';

  // Obj 2: Pages
  offsets.push(pdf.length);
  pdf += '2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n';

  // Obj 3: Page
  offsets.push(pdf.length);
  pdf += `3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 ${widthPt} ${heightPt}] /Resources << /XObject << /Im1 4 0 R >> /Font << /F1 6 0 R >> >> /Contents 5 0 R >>\nendobj\n`;

  // Obj 4: Image XObject with JPEG stream
  offsets.push(pdf.length);
  pdf += `4 0 obj\n<< /Type /XObject /Subtype /Image /Width 1400 /Height 640 /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /DCTDecode /Length ${jpegLength} >>\nstream\n${binaryJpeg}\nendstream\nendobj\n`;

  // Obj 5: Page Content stream
  const contentStream = 
    `q\n` +
    `${contentW} 0 0 ${contentH} ${posX} ${posY} cm\n` +
    `/Im1 Do\n` +
    `Q\n` +
    `BT\n` +
    `/F1 11 Tf\n` +
    `36 460 Td\n` +
    `(STATUTORY CRIME INVESTIGATION GEOSPATIAL DOSSIER) Tj\n` +
    `0 -16 Td\n` +
    `/F1 9 Tf\n` +
    `(Government of Gujarat - Police Department - Durga Incident Management Protocol) Tj\n` +
    `0 -14 Td\n` +
    `(Authenticated Forensic Map Docket for Judicial and Inter-Agency Field Submission) Tj\n` +
    `ET\n`;

  offsets.push(pdf.length);
  pdf += `5 0 obj\n<< /Length ${contentStream.length} >>\nstream\n${contentStream}endstream\nendobj\n`;

  // Obj 6: Font
  offsets.push(pdf.length);
  pdf += '6 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>\nendobj\n';

  // Cross-reference table
  const xrefOffset = pdf.length;
  pdf += 'xref\n0 7\n';
  pdf += '0000000000 65535 f \r\n';
  for (let i = 0; i < offsets.length; i++) {
    pdf += String(offsets[i]).padStart(10, '0') + ' 00000 n \r\n';
  }

  // Trailer
  pdf += `trailer\n<< /Size 7 /Root 1 0 R >>\nstartxref\n${xrefOffset}\n%%EOF\n`;

  const uint8 = new Uint8Array(pdf.length);
  for (let i = 0; i < pdf.length; i++) {
    uint8[i] = pdf.charCodeAt(i) & 0xff;
  }

  return new Blob([uint8], { type: 'application/pdf' });
}

// =========================================================
// NEARBY LANDMARKS OVERLAY TOGGLE FOR SITUATIONAL AWARENESS
// =========================================================

function toggleNearbyLandmarks(event) {
  if (event) {
    event.stopPropagation();
    event.preventDefault();
  }

  showNearbyLandmarks = !showNearbyLandmarks;
  updateNearbyLandmarksControls();

  const landmarksLayer = document.getElementById('nearby-landmarks-layer');
  if (landmarksLayer) {
    landmarksLayer.style.display = showNearbyLandmarks ? 'block' : 'none';
  } else if (activeCase) {
    renderStaticMap(activeCase);
  }
}

function updateNearbyLandmarksControls() {
  const btn = document.getElementById('btn-toggle-landmarks');
  const hudPill = document.getElementById('landmarks-hud-pill');

  if (btn) {
    if (showNearbyLandmarks) {
      btn.classList.add('is-active');
      btn.setAttribute('title', 'Nearby Landmarks: ON (7 Locations Overlayed - Click to Hide)');
      btn.style.background = '#0284C7';
      btn.style.color = '#FFFFFF';
    } else {
      btn.classList.remove('is-active');
      btn.setAttribute('title', 'Overlay Nearby Landmarks on map view for situational awareness');
      btn.style.background = '';
      btn.style.color = '';
    }
  }

  if (hudPill) {
    hudPill.style.display = showNearbyLandmarks ? 'inline-flex' : 'none';
  }
}

function rotateMapHeading() {
  currentMapHeading = (currentMapHeading + 90) % 360;
  updateCompassWidget();
  applyMapOrientation();
}

function updateCompassWidget() {
  const dial = document.getElementById('compass-dial');
  const readout = document.getElementById('compass-bearing-readout');

  if (dial) {
    dial.style.transform = `rotate(${-currentMapHeading}deg)`;
  }
  if (readout) {
    const headings = { 0: '0° N', 90: '90° E', 180: '180° S', 270: '270° W' };
    readout.textContent = headings[currentMapHeading] || `${currentMapHeading}°`;
  }
}

function applyMapOrientation() {
  const renderTarget = document.getElementById('static-map-render-target');
  if (renderTarget) {
    renderTarget.style.transition = 'transform 0.4s cubic-bezier(0.4, 0, 0.2, 1)';
    renderTarget.style.transformOrigin = 'center center';
    renderTarget.style.transform = `rotate(${currentMapHeading}deg)`;
  }
}

function toggleIncidentPinTooltip(event) {
  if (event) {
    event.stopPropagation();
    event.preventDefault();
  }
  // Ignore click if mouse moved during drag
  if (dragDistanceMoved > 5) return;
  isPinTooltipOpen = !isPinTooltipOpen;
  updatePinTooltip();
}

function closeIncidentPinTooltip(event) {
  if (event) {
    event.stopPropagation();
  }
  isPinTooltipOpen = false;
  updatePinTooltip();
}

function updatePinTooltip() {
  const tooltip = document.getElementById('incident-pin-tooltip');
  if (!tooltip) return;

  if (isPinTooltipOpen && activeCase) {
    const snapshots = getCaseSnapshots(activeCase);
    const snap = snapshots[currentSnapshotIndex] || snapshots[4];
    const priority = getCasePriorityLevel(activeCase);
    const idElem = document.getElementById('tooltip-case-id');
    const titleElem = document.getElementById('tooltip-case-title');
    const priorityElem = document.getElementById('tooltip-case-priority');
    const coordsElem = document.getElementById('tooltip-case-coords');

    if (idElem) {
      idElem.textContent = (currentSnapshotIndex === 4)
        ? activeCase.complaint_ref_no
        : `${activeCase.complaint_ref_no} [${snap.relativeTag}]`;
    }
    if (titleElem) {
      titleElem.textContent = (currentSnapshotIndex === 4)
        ? (activeCase.title || 'Incident Case Docket')
        : `${snap.statusTitle} (${snap.time})`;
    }
    if (priorityElem) {
      if (currentSnapshotIndex === 4) {
        priorityElem.textContent = priority;
        priorityElem.className = `tooltip-badge badge-${priority.toLowerCase()}`;
        priorityElem.style.background = '';
      } else {
        priorityElem.textContent = snap.badgeText;
        priorityElem.className = 'tooltip-badge';
        priorityElem.style.background = snap.badgeColor;
      }
    }
    if (coordsElem) {
      coordsElem.textContent = `📍 ${snap.lat}° N, ${snap.lng}° E`;
    }

    tooltip.style.display = 'block';
  } else {
    tooltip.style.display = 'none';
  }
}

// Floating Incident Types Legend Toggle
function toggleIncidentLegend(event) {
  if (event) {
    event.stopPropagation();
    event.preventDefault();
  }
  isLegendOpen = !isLegendOpen;
  updateLegendControls();
}

function updateLegendControls() {
  const legendElem = document.getElementById('map-incident-legend');
  const toggleBtn = document.getElementById('btn-toggle-legend');
  if (legendElem) {
    legendElem.style.display = isLegendOpen ? 'block' : 'none';
  }
  if (toggleBtn) {
    toggleBtn.classList.toggle('active', isLegendOpen);
    toggleBtn.setAttribute('aria-expanded', isLegendOpen ? 'true' : 'false');
  }
}

// Time-Scrubber Snapshots & Navigation
function getCaseSnapshots(caseData) {
  if (!caseData) return [];
  const baseLat = parseFloat(caseData.latitude) || 23.2156;
  const baseLng = parseFloat(caseData.longitude) || 72.6369;
  const priority = getCasePriorityLevel(caseData);
  const isEmergency = priority === 'EMERGENCY';
  const isUrgent = priority === 'URGENT';
  const timeStr = caseData.incident_time || '19:45';

  const parseTimeMinutes = (tStr) => {
    const parts = (tStr || '19:45').split(':');
    return (parseInt(parts[0], 10) || 19) * 60 + (parseInt(parts[1], 10) || 45);
  };
  const formatMinutes = (m) => {
    let normalized = ((m % 1440) + 1440) % 1440;
    const hh = String(Math.floor(normalized / 60)).padStart(2, '0');
    const mm = String(normalized % 60).padStart(2, '0');
    return `${hh}:${mm}`;
  };
  const baseMins = parseTimeMinutes(timeStr);

  return [
    {
      index: 0,
      relativeTag: 'T-45m',
      time: formatMinutes(baseMins - 45),
      stage: 'ALERT_TRIGGERED',
      statusTitle: isEmergency ? '🚨 SOS Beacon Triggered' : (isUrgent ? '⚠️ Initial Distress Call' : '📋 Incident Report Filed'),
      badgeColor: '#DC2626',
      badgeBg: '#FEE2E2',
      badgeText: 'INITIAL ALERT',
      dx: -110,
      dy: 40,
      lat: (baseLat - 0.0055).toFixed(4),
      lng: (baseLng - 0.0068).toFixed(4),
      description: isEmergency 
        ? 'Citizen triggered panic SOS transmission near transit portal perimeter.'
        : 'Initial distress dispatch registered by Gandhinagar Police Control Room.',
      pinColor: '#DC2626'
    },
    {
      index: 1,
      relativeTag: 'T-30m',
      time: formatMinutes(baseMins - 30),
      stage: 'TRANSIT_TRACKING',
      statusTitle: '📍 Motion / Transit Vector Tracked',
      badgeColor: '#EA580C',
      badgeBg: '#FFEDD5',
      badgeText: 'IN TRANSIT',
      dx: -72,
      dy: 22,
      lat: (baseLat - 0.0036).toFixed(4),
      lng: (baseLng - 0.0042).toFixed(4),
      description: 'Cellular triangulation and CCTV feed locate victim movement along main arterial road.',
      pinColor: '#EA580C'
    },
    {
      index: 2,
      relativeTag: 'T-15m',
      time: formatMinutes(baseMins - 15),
      stage: 'PATROL_DISPATCHED',
      statusTitle: '🚔 Patrol Unit Dispatched',
      badgeColor: '#0284C7',
      badgeBg: '#E0F2FE',
      badgeText: 'PATROL EN ROUTE',
      dx: -38,
      dy: -14,
      lat: (baseLat - 0.0019).toFixed(4),
      lng: (baseLng - 0.0021).toFixed(4),
      description: 'PCR Van 04 and Cheetah Mobile patrol directed to intercept coordinate grid.',
      pinColor: '#0284C7'
    },
    {
      index: 3,
      relativeTag: 'T-5m',
      time: formatMinutes(baseMins - 5),
      stage: 'INTERCEPT_PERIMETER',
      statusTitle: '🛡️ Visual Contact & Perimeter Formed',
      badgeColor: '#16A34A',
      badgeBg: '#DCFCE7',
      badgeText: 'PERIMETER ON SCENE',
      dx: -14,
      dy: -5,
      lat: (baseLat - 0.0007).toFixed(4),
      lng: (baseLng - 0.0008).toFixed(4),
      description: 'First responders establish visual contact; immediate protective perimeter cordoned off.',
      pinColor: '#16A34A'
    },
    {
      index: 4,
      relativeTag: 'LIVE FIX',
      time: timeStr,
      stage: 'ACTIVE_INVESTIGATION',
      statusTitle: isEmergency ? '🚨 Active Emergency Scene' : '🔍 Active Investigation Docket',
      badgeColor: isEmergency ? '#DC2626' : (isUrgent ? '#EA580C' : '#2563EB'),
      badgeBg: isEmergency ? '#FEE2E2' : '#EFF6FF',
      badgeText: 'CURRENT LIVE FIX',
      dx: 0,
      dy: 0,
      lat: baseLat.toFixed(4),
      lng: baseLng.toFixed(4),
      description: `Present status: ${(caseData.current_status || 'Under Active Investigation').replace(/_/g, ' ')}; evidence secured at incident coordinates.`,
      pinColor: isEmergency ? '#DC2626' : (isUrgent ? '#EA580C' : '#2563EB')
    }
  ];
}

function onTimeScrubberInput(val, event) {
  if (event) event.stopPropagation();
  onTimeScrubberChange(val, event);
}

function onTimeScrubberChange(val, event) {
  if (event) event.stopPropagation();
  currentSnapshotIndex = Math.max(0, Math.min(4, parseInt(val, 10)));
  updateScrubberUI();
  if (activeCase) {
    const snapshots = getCaseSnapshots(activeCase);
    const snap = snapshots[currentSnapshotIndex];
    if (snap) {
      updateCoordinateDisplay(snap.lat, snap.lng);
      updatePinTooltip();
    }
    renderStaticMap(activeCase);
  }
}

function scrubberRewind(event) {
  if (event) {
    event.stopPropagation();
    event.preventDefault();
  }
  stopScrubberPlayback();
  onTimeScrubberChange(0, event);
}

function scrubberFastForward(event) {
  if (event) {
    event.stopPropagation();
    event.preventDefault();
  }
  stopScrubberPlayback();
  onTimeScrubberChange(4, event);
}

function scrubberPrev(event) {
  if (event) {
    event.stopPropagation();
    event.preventDefault();
  }
  stopScrubberPlayback();
  if (currentSnapshotIndex > 0) {
    onTimeScrubberChange(currentSnapshotIndex - 1, event);
  }
}

function scrubberNext(event) {
  if (event) {
    event.stopPropagation();
    event.preventDefault();
  }
  stopScrubberPlayback();
  if (currentSnapshotIndex < 4) {
    onTimeScrubberChange(currentSnapshotIndex + 1, event);
  }
}

function scrubberTogglePlay(event) {
  if (event) {
    event.stopPropagation();
    event.preventDefault();
  }
  if (isScrubberPlaying) {
    stopScrubberPlayback();
  } else {
    startScrubberPlayback();
  }
}

function startScrubberPlayback() {
  isScrubberPlaying = true;
  const playBtn = document.getElementById('btn-scrubber-play');
  if (playBtn) {
    playBtn.textContent = '⏸';
    playBtn.classList.add('is-playing');
    playBtn.title = 'Pause incident movement timeline';
  }
  if (currentSnapshotIndex >= 4) {
    currentSnapshotIndex = 0;
    onTimeScrubberChange(0);
  }
  clearInterval(scrubberPlayTimer);
  scrubberPlayTimer = setInterval(() => {
    let nextIdx = currentSnapshotIndex + 1;
    if (nextIdx > 4) {
      nextIdx = 0;
    }
    onTimeScrubberChange(nextIdx);
  }, 1500);
}

function stopScrubberPlayback() {
  isScrubberPlaying = false;
  clearInterval(scrubberPlayTimer);
  scrubberPlayTimer = null;
  const playBtn = document.getElementById('btn-scrubber-play');
  if (playBtn) {
    playBtn.textContent = '▶';
    playBtn.classList.remove('is-playing');
    playBtn.title = 'Play incident movement timeline';
  }
}

function updateScrubberUI() {
  const slider = document.getElementById('map-time-scrubber');
  if (slider && parseInt(slider.value, 10) !== currentSnapshotIndex) {
    slider.value = currentSnapshotIndex;
  }

  // Update ticks
  for (let i = 0; i <= 4; i++) {
    const tick = document.getElementById(`scrubber-tick-${i}`);
    if (tick) {
      tick.classList.toggle('active', i === currentSnapshotIndex);
    }
  }

  if (!activeCase) return;
  const snapshots = getCaseSnapshots(activeCase);
  const snap = snapshots[currentSnapshotIndex] || snapshots[4];

  const statusText = document.getElementById('scrubber-status-text');
  const statusDot = document.getElementById('scrubber-status-dot');
  const stepBadge = document.getElementById('scrubber-step-badge');
  const detailTime = document.getElementById('scrubber-detail-time');
  const detailDesc = document.getElementById('scrubber-detail-desc');

  if (statusText) {
    statusText.textContent = (currentSnapshotIndex === 4) 
      ? `LIVE FIX (${snap.time})`
      : `${snap.relativeTag} (${snap.time})`;
  }

  if (statusDot) {
    if (currentSnapshotIndex === 4) {
      statusDot.className = 'scrubber-status-indicator';
      statusDot.textContent = '●';
    } else {
      statusDot.className = 'scrubber-status-indicator rewound';
      statusDot.textContent = '⏱';
    }
  }

  if (stepBadge) {
    stepBadge.textContent = `SNAPSHOT ${currentSnapshotIndex + 1}/5`;
    stepBadge.style.background = snap.badgeColor;
  }
  if (detailTime) {
    detailTime.textContent = snap.time;
  }
  if (detailDesc) {
    detailDesc.textContent = `${snap.statusTitle}: ${snap.description}`;
  }
}

function generateMovementTrailSvg(snapshots, activeIdx, isSatellite) {
  if (!snapshots || snapshots.length === 0) return '';
  const trailColor = isSatellite ? '#A855F7' : '#9333EA';

  let pathD = '';
  const points = [];
  for (let i = 0; i <= activeIdx; i++) {
    const s = snapshots[i];
    points.push({ x: 350 + s.dx, y: 115 + s.dy, snap: s, idx: i });
  }

  if (points.length > 1) {
    pathD = `M ${points.map(p => `${p.x},${p.y}`).join(' L ')}`;
  }

  let waypointsHtml = '';
  for (let i = 0; i < activeIdx; i++) {
    const pt = points[i];
    const s = pt.snap;
    waypointsHtml += `
      <g class="snapshot-waypoint" transform="translate(${pt.x}, ${pt.y})" onclick="onTimeScrubberChange(${i}, event)" role="button" tabindex="0" aria-label="Snapshot ${i + 1} at ${s.time}">
        <circle cx="0" cy="0" r="7" fill="${s.badgeColor}" stroke="#FFFFFF" stroke-width="1.5"/>
        <text x="0" y="2.5" font-size="6.5" font-weight="900" fill="#FFFFFF" text-anchor="middle">${i + 1}</text>
        <rect x="-24" y="9" width="48" height="13" rx="2" fill="${isSatellite ? '#020617' : '#FFFFFF'}" stroke="${s.badgeColor}" stroke-width="1" opacity="0.95"/>
        <text x="0" y="18" font-size="6.5" font-weight="700" fill="${isSatellite ? '#E2E8F0' : '#1E293B'}" text-anchor="middle">${s.time}</text>
      </g>
    `;
  }

  return `
    <g class="incident-movement-trail">
      ${pathD ? `
        <!-- Outer glow trail -->
        <path d="${pathD}" stroke="${trailColor}" stroke-width="5" opacity="0.3" fill="none" stroke-linecap="round"/>
        <!-- Animated dashed flow line -->
        <path d="${pathD}" class="incident-trail-path incident-trail-flow" stroke="${trailColor}" stroke-width="2.5" stroke-dasharray="4,4" fill="none" stroke-linecap="round"/>
      ` : ''}
      ${waypointsHtml}
    </g>
  `;
}

function toggleMapBaseLayer() {
  currentMapLayer = (currentMapLayer === 'street') ? 'satellite' : 'street';
  updateMapLayerControls();
  if (activeCase) {
    renderStaticMap(activeCase);
  }
}

function updateMapLayerControls() {
  const btnText = document.getElementById('map-layer-text');
  const btnIcon = document.getElementById('map-layer-icon');
  const pillLabel = document.getElementById('layer-pill-label');
  const pillDot = document.getElementById('layer-pill-dot');
  const pillContainer = document.getElementById('map-layer-indicator-pill');
  const toggleBtn = document.getElementById('btn-toggle-map-layer');

  if (currentMapLayer === 'satellite') {
    if (btnText) btnText.textContent = 'Switch to Street View';
    if (btnIcon) btnIcon.textContent = '🗺️';
    if (pillLabel) pillLabel.textContent = 'SATELLITE VIEW';
    if (pillDot) pillDot.style.color = '#38BDF8';
    if (pillContainer) {
      pillContainer.className = 'map-layer-indicator-pill layer-satellite';
    }
    if (toggleBtn) {
      toggleBtn.classList.add('active-satellite');
    }
  } else {
    if (btnText) btnText.textContent = 'Switch to Satellite';
    if (btnIcon) btnIcon.textContent = '🛰️';
    if (pillLabel) pillLabel.textContent = 'STREET VIEW';
    if (pillDot) pillDot.style.color = '#22C55E';
    if (pillContainer) {
      pillContainer.className = 'map-layer-indicator-pill layer-street';
    }
    if (toggleBtn) {
      toggleBtn.classList.remove('active-satellite');
    }
  }
}

function zoomInMap(event) {
  if (event) {
    event.stopPropagation();
    event.preventDefault();
  }
  if (currentMapZoom < 18) {
    currentMapZoom++;
    updateZoomControls();
    if (activeCase) renderStaticMap(activeCase);
  }
}

function zoomOutMap(event) {
  if (event) {
    event.stopPropagation();
    event.preventDefault();
  }
  if (currentMapZoom > 12) {
    currentMapZoom--;
    updateZoomControls();
    if (activeCase) renderStaticMap(activeCase);
  }
}

function updateZoomControls() {
  const indicator = document.getElementById('map-zoom-indicator');
  const inBtn = document.getElementById('map-zoom-in');
  const outBtn = document.getElementById('map-zoom-out');

  if (indicator) indicator.textContent = `${currentMapZoom}x`;
  if (inBtn) inBtn.disabled = currentMapZoom >= 18;
  if (outBtn) outBtn.disabled = currentMapZoom <= 12;
}

function renderStaticMap(caseData) {
  const target = document.getElementById('static-map-render-target');
  const container = target || document.getElementById('static-map-viewport');
  const coordsVal = document.getElementById('map-coords-val');
  const locAddress = document.getElementById('map-location-address');
  const extLink = document.getElementById('map-external-link');
  const stationChip = document.getElementById('map-station-chip');

  if (!container || !caseData) return;

  // Extract or calculate coordinates
  let lat = Number(caseData.latitude);
  let lng = Number(caseData.longitude);
  let isEstimated = false;

  if (isNaN(lat) || isNaN(lng) || (lat === 0 && lng === 0)) {
    const loc = (caseData.incident_location || '').toLowerCase();
    if (loc.includes('ahmedabad')) {
      lat = 23.0225; lng = 72.5714;
    } else if (loc.includes('sector 11') || loc.includes('terminal')) {
      lat = 23.2212; lng = 72.6481;
    } else {
      lat = 23.2156; lng = 72.6369; // Gandhinagar default
    }
    isEstimated = true;
  }

  baseIncidentLat = lat;
  baseIncidentLng = lng;
  if (!currentPanX && !currentPanY) {
    currentCenterLat = lat;
    currentCenterLng = lng;
  }

  const latFormatted = `${Math.abs(currentCenterLat).toFixed(4)}° ${currentCenterLat >= 0 ? 'N' : 'S'}`;
  const lngFormatted = `${Math.abs(currentCenterLng).toFixed(4)}° ${currentCenterLng >= 0 ? 'E' : 'W'}`;

  if (coordsVal) {
    const isPanned = Math.hypot(currentPanX, currentPanY) > 6;
    coordsVal.textContent = `${latFormatted}, ${lngFormatted}${isPanned ? ' (Panned)' : (isEstimated ? ' (Estimated)' : '')}`;
  }
  if (locAddress) {
    locAddress.textContent = caseData.incident_location || 'Jurisdiction Incident Zone';
  }
  if (extLink) {
    extLink.href = `https://maps.google.com/?q=${currentCenterLat},${currentCenterLng}`;
  }
  if (stationChip) {
    const station = caseData.police_station || (caseData.incident_location && caseData.incident_location.includes('Ahmedabad') ? 'Ahmedabad Central Police Station' : 'Gandhinagar Sector 7 Police Station');
    stationChip.textContent = `🚔 Jurisdiction: ${station}`;
  }

  updateMapLayerControls();
  updateZoomControls();
  updateCompassWidget();
  applyMapOrientation();
  updatePinTooltip();
  updateRecenterButtonVisibility();
  updateNearbyLandmarksControls();
  updateLegendControls();
  updateScrubberUI();

  // Render SVG static map with base layer and zoom level
  container.innerHTML = generateStaticMapSvg(lat, lng, caseData, currentMapZoom, currentMapLayer);
  applyMapPan();
}

function generateLandmarksSvg(isSatellite) {
  const bg = isSatellite ? '#0F172A' : '#FFFFFF';
  const textCol = isSatellite ? '#F8FAFC' : '#1E293B';

  return `
    <!-- Nearby Landmarks Layer for Situational Awareness -->
    <g id="nearby-landmarks-layer" class="nearby-landmarks-layer" style="display: ${showNearbyLandmarks ? 'block' : 'none'};">
      <!-- 1. Civil Hospital & Trauma Centre -->
      <g class="landmark-node" transform="translate(170, 52)">
        <circle cx="0" cy="0" r="10" fill="#EF4444" stroke="#FFFFFF" stroke-width="2"/>
        <text x="0" y="3.5" font-size="10" text-anchor="middle">🏥</text>
        <line x1="0" y1="10" x2="0" y2="18" stroke="#EF4444" stroke-width="1.5" stroke-dasharray="2,2"/>
        <rect x="-65" y="18" width="130" height="23" rx="4" fill="${bg}" stroke="#EF4444" stroke-width="1.2" opacity="0.95"/>
        <text x="0" y="28" font-size="7.5" font-weight="800" fill="${textCol}" text-anchor="middle">Civil Hospital &amp; Trauma</text>
        <text x="0" y="37" font-size="6.5" font-weight="700" fill="#EF4444" text-anchor="middle">EMERGENCY MEDICAL • 850m NW</text>
      </g>

      <!-- 2. Sector 7 Police Station -->
      <g class="landmark-node" transform="translate(190, 150)">
        <circle cx="0" cy="0" r="10" fill="#3B82F6" stroke="#FFFFFF" stroke-width="2"/>
        <text x="0" y="3.5" font-size="10" text-anchor="middle">🚔</text>
        <line x1="0" y1="-10" x2="0" y2="-18" stroke="#3B82F6" stroke-width="1.5" stroke-dasharray="2,2"/>
        <rect x="-65" y="-41" width="130" height="23" rx="4" fill="${bg}" stroke="#3B82F6" stroke-width="1.2" opacity="0.95"/>
        <text x="0" y="-31" font-size="7.5" font-weight="800" fill="${textCol}" text-anchor="middle">Sector 7 Police HQ</text>
        <text x="0" y="-22" font-size="6.5" font-weight="700" fill="#3B82F6" text-anchor="middle">JURISDICTION HQ • 1.2km W</text>
      </g>

      <!-- 3. Vidhan Sabha Govt Complex -->
      <g class="landmark-node" transform="translate(240, 185)">
        <circle cx="0" cy="0" r="10" fill="#8B5CF6" stroke="#FFFFFF" stroke-width="2"/>
        <text x="0" y="3.5" font-size="10" text-anchor="middle">🏛️</text>
        <rect x="-65" y="-36" width="130" height="23" rx="4" fill="${bg}" stroke="#8B5CF6" stroke-width="1.2" opacity="0.95"/>
        <text x="0" y="-26" font-size="7.5" font-weight="800" fill="${textCol}" text-anchor="middle">Gujarat Vidhan Sabha</text>
        <text x="0" y="-17" font-size="6.5" font-weight="700" fill="#8B5CF6" text-anchor="middle">STATE SECRETARIAT • 1.5km S</text>
      </g>

      <!-- 4. Sector 11 Bus Terminal -->
      <g class="landmark-node" transform="translate(520, 55)">
        <circle cx="0" cy="0" r="10" fill="#F59E0B" stroke="#FFFFFF" stroke-width="2"/>
        <text x="0" y="3.5" font-size="10" text-anchor="middle">🚌</text>
        <line x1="0" y1="10" x2="0" y2="17" stroke="#F59E0B" stroke-width="1.5" stroke-dasharray="2,2"/>
        <rect x="-65" y="17" width="130" height="23" rx="4" fill="${bg}" stroke="#F59E0B" stroke-width="1.2" opacity="0.95"/>
        <text x="0" y="27" font-size="7.5" font-weight="800" fill="${textCol}" text-anchor="middle">Sector 11 Transit Terminal</text>
        <text x="0" y="36" font-size="6.5" font-weight="700" fill="#F59E0B" text-anchor="middle">TRANSIT JUNCTION • 600m NE</text>
      </g>

      <!-- 5. Mahatma Mandir Civic Complex -->
      <g class="landmark-node" transform="translate(575, 155)">
        <circle cx="0" cy="0" r="10" fill="#10B981" stroke="#FFFFFF" stroke-width="2"/>
        <text x="0" y="3.5" font-size="10" text-anchor="middle">🏢</text>
        <rect x="-65" y="-36" width="130" height="23" rx="4" fill="${bg}" stroke="#10B981" stroke-width="1.2" opacity="0.95"/>
        <text x="0" y="-26" font-size="7.5" font-weight="800" fill="${textCol}" text-anchor="middle">Mahatma Mandir Complex</text>
        <text x="0" y="-17" font-size="6.5" font-weight="700" fill="#10B981" text-anchor="middle">CIVIC CONVENTION • 1.8km SE</text>
      </g>

      <!-- 6. Sector 16 Fire & Rescue Post -->
      <g class="landmark-node" transform="translate(100, 160)">
        <circle cx="0" cy="0" r="10" fill="#EA580C" stroke="#FFFFFF" stroke-width="2"/>
        <text x="0" y="3.5" font-size="10" text-anchor="middle">🚒</text>
        <rect x="-60" y="-36" width="120" height="23" rx="4" fill="${bg}" stroke="#EA580C" stroke-width="1.2" opacity="0.95"/>
        <text x="0" y="-26" font-size="7.5" font-weight="800" fill="${textCol}" text-anchor="middle">Sector 16 Fire &amp; Rescue</text>
        <text x="0" y="-17" font-size="6.5" font-weight="700" fill="#EA580C" text-anchor="middle">FIRST RESPONDER • 950m SW</text>
      </g>

      <!-- 7. Infocity Tech Corridor Checkpost -->
      <g class="landmark-node" transform="translate(480, 24)">
        <circle cx="0" cy="0" r="10" fill="#06B6D4" stroke="#FFFFFF" stroke-width="2"/>
        <text x="0" y="3.5" font-size="10" text-anchor="middle">🛡️</text>
        <line x1="0" y1="10" x2="0" y2="16" stroke="#06B6D4" stroke-width="1.5" stroke-dasharray="2,2"/>
        <rect x="-65" y="16" width="130" height="23" rx="4" fill="${bg}" stroke="#06B6D4" stroke-width="1.2" opacity="0.95"/>
        <text x="0" y="26" font-size="7.5" font-weight="800" fill="${textCol}" text-anchor="middle">Infocity Police Checkpost</text>
        <text x="0" y="35" font-size="6.5" font-weight="700" fill="#06B6D4" text-anchor="middle">PATROL CHECKPOINT • 1.4km N</text>
      </g>
    </g>
  `;
}

function generateStaticMapSvg(lat, lng, caseData, zoom = 15, layer = 'street') {
  const priority = getCasePriorityLevel(caseData);
  const isEmergency = priority === 'EMERGENCY';
  const isUrgent = priority === 'URGENT';
  
  const isSatellite = (layer === 'satellite');
  const pinColor = isEmergency ? '#DC2626' : (isUrgent ? '#EA580C' : (isSatellite ? '#38BDF8' : '#2563EB'));
  const priorityLabel = isEmergency ? '🚨 CRITICAL EMERGENCY PIN' : (isUrgent ? '⚠️ URGENT INCIDENT PIN' : '📍 INCIDENT PIN');

  const snapshots = getCaseSnapshots(caseData);
  const activeSnapshot = snapshots[currentSnapshotIndex] || snapshots[4];
  const isRewound = (currentSnapshotIndex < 4);
  const activePinColor = isRewound ? activeSnapshot.pinColor : pinColor;
  const pinX = 350 + activeSnapshot.dx;
  const pinY = 115 + activeSnapshot.dy;
  const pinCalloutText = isRewound 
    ? `⏱ ${activeSnapshot.badgeText} (${activeSnapshot.time}): ${activeSnapshot.statusTitle}`
    : `${priorityLabel}: ${caseData.complaint_ref_no} ℹ️ (Click)`;

  // Zoom scale configuration
  const zoomConfig = {
    12: { scale: '5 km', ring1: '1.5 km', ring2: '3.0 km', r1: 30, r2: 60, detail: 'REGIONAL JURISDICTION' },
    13: { scale: '2 km', ring1: '1.0 km', ring2: '2.0 km', r1: 35, r2: 70, detail: 'DISTRICT SECTOR' },
    14: { scale: '1 km', ring1: '500 m',  ring2: '1.0 km', r1: 40, r2: 80, detail: 'ZONE PATROL GRID' },
    15: { scale: '500 m', ring1: '300 m',  ring2: '600 m',  r1: 45, r2: 85, detail: 'TACTICAL INCIDENT' },
    16: { scale: '250 m', ring1: '150 m',  ring2: '300 m',  r1: 50, r2: 95, detail: 'NEIGHBORHOOD BLOCK' },
    17: { scale: '100 m', ring1: '50 m',   ring2: '100 m',  r1: 55, r2: 105, detail: 'STREET LEVEL' },
    18: { scale: '50 m',  ring1: '25 m',   ring2: '50 m',   r1: 60, r2: 115, detail: 'PERIMETER BLUEPRINT' }
  };
  const z = zoomConfig[zoom] || zoomConfig[15];

  const uniqueId = `${caseData.complaint_ref_no}-${zoom}-${layer}`;

  if (isSatellite) {
    // SATELLITE BASE LAYER
    return `
      <svg viewBox="0 0 700 260" width="100%" height="260" xmlns="http://www.w3.org/2000/svg" class="static-map-svg" style="display:block; width:100%; height:260px; border-radius: 6px; background: #0B132B;">
        <defs>
          <!-- Satellite Coordinate Grid -->
          <pattern id="sat-grid-${uniqueId}" width="40" height="40" patternUnits="userSpaceOnUse">
            <path d="M 40 0 L 0 0 0 40" fill="none" stroke="#1E3A8A" stroke-width="0.5" opacity="0.4"/>
            <circle cx="0" cy="0" r="1" fill="#38BDF8" opacity="0.5"/>
          </pattern>
          <!-- Satellite Terrain Noise / Texture -->
          <radialGradient id="sat-terrain-grad-${uniqueId}" cx="50%" cy="50%" r="60%">
            <stop offset="0%" stop-color="#1E293B"/>
            <stop offset="50%" stop-color="#0F172A"/>
            <stop offset="100%" stop-color="#020617"/>
          </radialGradient>
          <!-- Animated Pin Radar Pulse -->
          <radialGradient id="pulse-grad-${uniqueId}" cx="50%" cy="50%" r="50%">
            <stop offset="0%" stop-color="${pinColor}" stop-opacity="0.8"/>
            <stop offset="60%" stop-color="${pinColor}" stop-opacity="0.3"/>
            <stop offset="100%" stop-color="${pinColor}" stop-opacity="0"/>
          </radialGradient>
          <filter id="pin-shadow-${uniqueId}" x="-30%" y="-30%" width="160%" height="160%">
            <feDropShadow dx="0" dy="3" stdDeviation="4" flood-color="#000000" flood-opacity="0.8"/>
          </filter>
        </defs>

        <!-- Satellite Deep Earth Background -->
        <rect width="700" height="220" fill="url(#sat-terrain-grad-${uniqueId})"/>

        <!-- Dynamic Pannable Map Layer (translates on mouse-drag) -->
        <g id="map-pan-layer" transform="translate(${currentPanX}, ${currentPanY})">
          <!-- Satellite Vegetation Canopy Patches (Dark Forest/Agri) -->
          <path d="M 20,15 Q 80,5 140,40 L 130,85 Q 60,90 20,70 Z" fill="#14532D" opacity="0.65"/>
          <path d="M 480,120 Q 560,110 650,135 L 670,180 Q 580,195 490,175 Z" fill="#14532D" opacity="0.55"/>
          <text x="45" y="42" font-size="8.5" fill="#4ADE80" font-weight="700" opacity="0.75" letter-spacing="0.05em">VEGETATION CANOPY</text>

          <!-- Satellite Water Basin / Canal -->
          <path d="M 0,200 Q 180,180 350,195 T 700,185 L 700,220 L 0,220 Z" fill="#0C4A6E" opacity="0.85"/>
          <path d="M 0,203 Q 180,183 350,198 T 700,188" stroke="#38BDF8" stroke-width="1" fill="none" opacity="0.4"/>
          <text x="610" y="210" font-size="8" fill="#7DD3FC" font-style="italic" opacity="0.85">SABARMATI WATER CORRIDOR</text>

          <!-- Satellite Urban Structures / Roof Footprints -->
          <g fill="#334155" stroke="#1E293B" stroke-width="1" opacity="0.85">
            <rect x="30" y="30" width="40" height="30" rx="2"/>
            <rect x="80" y="25" width="45" height="35" rx="2"/>
            <rect x="190" y="30" width="50" height="35" rx="2"/>
            <rect x="250" y="35" width="40" height="30" rx="2"/>
            
            <rect x="330" y="25" width="60" height="40" rx="2"/>
            <rect x="400" y="20" width="50" height="45" rx="2"/>
            
            <rect x="520" y="25" width="55" height="35" rx="2"/>
            <rect x="590" y="30" width="65" height="40" rx="2"/>

            <!-- South structures -->
            <rect x="40" y="135" width="50" height="35" rx="2"/>
            <rect x="100" y="140" width="45" height="30" rx="2"/>
            <rect x="190" y="140" width="55" height="35" rx="2"/>
            <rect x="260" y="135" width="45" height="40" rx="2"/>

            <rect x="340" y="135" width="55" height="40" rx="2"/>
            <rect x="410" y="140" width="50" height="35" rx="2"/>

            ${zoom >= 16 ? `
              <!-- Close-Up Incident Parcel Footprint -->
              <rect x="325" y="85" width="50" height="38" rx="2" fill="#475569" stroke="${pinColor}" stroke-width="1.5" stroke-dasharray="3,2"/>
              <text x="350" y="107" font-size="7.5" fill="#F8FAFC" font-weight="700" text-anchor="middle">TARGET BLDG</text>
            ` : ''}
          </g>

          <!-- Satellite Telemetry Coordinates Grid Overlay -->
          <rect width="700" height="220" fill="url(#sat-grid-${uniqueId})"/>

          <!-- Satellite Asphalt Highway Corridors -->
          <line x1="0" y1="105" x2="700" y2="105" stroke="#0F172A" stroke-width="16"/>
          <line x1="0" y1="97" x2="700" y2="97" stroke="#0284C7" stroke-width="1.5" opacity="0.8"/>
          <line x1="0" y1="113" x2="700" y2="113" stroke="#0284C7" stroke-width="1.5" opacity="0.8"/>
          <line x1="0" y1="105" x2="700" y2="105" stroke="#38BDF8" stroke-width="1" stroke-dasharray="8,6" opacity="0.9"/>
          <text x="500" y="93" font-size="8.5" font-weight="700" fill="#38BDF8" letter-spacing="0.06em">HIGHWAY ARTERY [SATELLITE TRACE]</text>

          <!-- Vertical Connecting Boulevard -->
          <line x1="350" y1="0" x2="350" y2="220" stroke="#0F172A" stroke-width="14"/>
          <line x1="343" y1="0" x2="343" y2="220" stroke="#38BDF8" stroke-width="1" opacity="0.7"/>
          <line x1="357" y1="0" x2="357" y2="220" stroke="#38BDF8" stroke-width="1" opacity="0.7"/>

          <!-- Secondary Roads -->
          <line x1="160" y1="0" x2="160" y2="220" stroke="#1E293B" stroke-width="8"/>
          <line x1="480" y1="0" x2="480" y2="220" stroke="#1E293B" stroke-width="8"/>

          <!-- Tactical Patrol Perimeter Rings with Dynamic Zoom Radius -->
          <circle cx="350" cy="105" r="${z.r1}" fill="none" stroke="${pinColor}" stroke-width="1.2" stroke-dasharray="3,3" opacity="0.75"/>
          <text x="${350 + z.r1 + 5}" y="102" font-size="7.5" fill="${pinColor}" font-weight="700">${z.ring1}</text>
          
          <circle cx="350" cy="105" r="${z.r2}" fill="none" stroke="${pinColor}" stroke-width="1.2" stroke-dasharray="4,4" opacity="0.5"/>
          <text x="${350 + z.r2 + 5}" y="102" font-size="7.5" fill="${pinColor}" font-weight="700">${z.ring2}</text>

          <!-- Pulsing Thermal Radar Scan Circle -->
          <circle cx="350" cy="105" r="28" fill="url(#pulse-grad-${uniqueId})">
            <animate attributeName="r" values="12;${z.r1};12" dur="2.4s" repeatCount="indefinite"/>
            <animate attributeName="opacity" values="0.9;0.1;0.9" dur="2.4s" repeatCount="indefinite"/>
          </circle>

          <!-- Police Station Dispatch Post -->
          <g transform="translate(180, 50)">
            <circle cx="0" cy="0" r="14" fill="#0284C7" stroke="#FFFFFF" stroke-width="2"/>
            <text x="0" y="4" font-size="11" text-anchor="middle" fill="#FFFFFF">🚔</text>
            <line x1="10" y1="10" x2="160" y2="52" stroke="#38BDF8" stroke-width="2" stroke-dasharray="4,4"/>
            <rect x="-38" y="18" width="76" height="15" rx="3" fill="#020617" stroke="#0284C7" stroke-width="1"/>
            <text x="0" y="29" font-size="7.5" font-weight="700" fill="#38BDF8" text-anchor="middle">STATION POST (1.2 km)</text>
          </g>

          ${generateLandmarksSvg(isSatellite)}
          ${generateMovementTrailSvg(snapshots, currentSnapshotIndex, isSatellite)}

          <!-- Distinct Animated Active-Incident Pin Icon Centered on Active Snapshot Coordinates -->
          <g id="active-incident-pin" class="active-incident-pin" transform="translate(${pinX}, ${pinY})" onclick="toggleIncidentPinTooltip(event)" role="button" tabindex="0" aria-label="Incident Pin: Case ${caseData.complaint_ref_no}" filter="url(#pin-shadow-${uniqueId})">
            <!-- Animated Radar Beacon Ping Waves -->
            <circle cx="0" cy="0" r="10" fill="${activePinColor}" opacity="0.4" class="pin-pulse-wave"/>

            <!-- Target Reticle Crosshair with Pulsing Ring -->
            <circle cx="0" cy="0" r="18" fill="none" stroke="${activePinColor}" stroke-width="1.8" class="pin-reticle-ring"/>
            <line x1="-24" y1="0" x2="24" y2="0" stroke="${activePinColor}" stroke-width="1.5"/>
            <line x1="0" y1="-24" x2="0" y2="24" stroke="${activePinColor}" stroke-width="1.5"/>

            <!-- Pin Head Droplet with Animated Bounce -->
            <g class="pin-head-bounce">
              <path d="M 0,0 C -13,-13 -13,-32 0,-32 C 13,-32 13,-13 0,0 Z" fill="${activePinColor}" stroke="#FFFFFF" stroke-width="2.5" class="pin-body"/>
              <!-- Inner Pin Symbol -->
              <circle cx="0" cy="-19" r="6" fill="#FFFFFF"/>
              <text x="0" y="-16" font-size="9" font-weight="900" text-anchor="middle" fill="${activePinColor}">${isRewound ? '⏱' : '⚠'}</text>
            </g>

            <!-- Interactive Click-For-Tooltip Prompt Callout -->
            <g transform="translate(0, -44)" class="pin-label-callout">
              <rect x="-135" y="-12" width="270" height="20" rx="4" fill="#020617" stroke="${activePinColor}" stroke-width="1.5"/>
              <text x="0" y="2" font-family="'Inter', sans-serif" font-size="8.5" font-weight="800" fill="#FFFFFF" text-anchor="middle">
                ${pinCalloutText}
              </text>
            </g>
          </g>
        </g>

        ${isRewound ? `
          <!-- Rewound Snapshot Status Banner in Map HUD -->
          <g transform="translate(350, 22)">
            <rect x="-140" y="-12" width="280" height="24" rx="4" fill="#020617" stroke="#A855F7" stroke-width="1.5" opacity="0.95"/>
            <circle cx="-125" cy="0" r="4" fill="#A855F7"/>
            <text x="0" y="3.5" font-family="'Inter', sans-serif" font-size="8.5" font-weight="800" fill="#F3E8FF" text-anchor="middle" letter-spacing="0.04em">
              ⏪ REWOUND SNAPSHOT [${activeSnapshot.relativeTag} • ${activeSnapshot.time}]
            </text>
          </g>
        ` : ''}

        <!-- Top-Left: Compass Rose -->
        <g transform="translate(35, 32)">
          <circle cx="0" cy="0" r="15" fill="#0F172A" stroke="#38BDF8" stroke-width="1" opacity="0.95"/>
          <polygon points="0,-12 4,0 0,-3 -4,0" fill="#EF4444"/>
          <polygon points="0,12 4,0 0,-3 -4,0" fill="#94A3B8"/>
          <text x="0" y="-14" font-size="8" font-weight="800" fill="#EF4444" text-anchor="middle">N</text>
        </g>

        <!-- Top-Right: GIS Telemetry Status Tag -->
        <g transform="translate(560, 18)">
          <rect x="0" y="0" width="125" height="18" rx="3" fill="#020617" stroke="#38BDF8" stroke-width="1" opacity="0.95"/>
          <circle cx="10" cy="9" r="3" fill="#38BDF8"/>
          <text x="65" y="12" font-size="7.5" font-weight="700" fill="#E0F2FE" text-anchor="middle" letter-spacing="0.04em">
            ${z.detail} (${zoom}x)
          </text>
        </g>

        <!-- Bottom-Left: Map Scale Bar -->
        <g transform="translate(25, 205)">
          <rect x="0" y="-12" width="105" height="15" rx="3" fill="#020617" stroke="#38BDF8" stroke-width="1" opacity="0.9"/>
          <line x1="10" y1="-4" x2="95" y2="-4" stroke="#38BDF8" stroke-width="2"/>
          <line x1="10" y1="-7" x2="10" y2="-1" stroke="#38BDF8" stroke-width="2"/>
          <line x1="95" y1="-7" x2="95" y2="-1" stroke="#38BDF8" stroke-width="2"/>
          <text x="52" y="-5" font-size="7.5" font-weight="700" fill="#E0F2FE" text-anchor="middle">${z.scale}</text>
        </g>

        <!-- Bottom-Right: Lat/Long stamps -->
        <text id="svg-latlng-stamp" x="680" y="208" font-size="7.5" font-weight="600" fill="#94A3B8" text-anchor="end">
          SATELLITE RECON • ISRO/CARTOSAT [${currentCenterLat.toFixed(4)}, ${currentCenterLng.toFixed(4)}]
        </text>
      </svg>
    `;
  }

  // STREET VIEW BASE LAYER
  return `
    <svg viewBox="0 0 700 260" width="100%" height="260" xmlns="http://www.w3.org/2000/svg" class="static-map-svg" style="display:block; width:100%; height:260px; border-radius: 6px; background: #F8FAFC;">
      <defs>
        <!-- Map Grid Pattern -->
        <pattern id="tactical-grid-${uniqueId}" width="40" height="40" patternUnits="userSpaceOnUse">
          <path d="M 40 0 L 0 0 0 40" fill="none" stroke="#CBD5E1" stroke-width="0.7" opacity="0.6"/>
        </pattern>
        <!-- Animated Pin Radar Pulse Gradient -->
        <radialGradient id="pulse-grad-${uniqueId}" cx="50%" cy="50%" r="50%">
          <stop offset="0%" stop-color="${activePinColor}" stop-opacity="0.6"/>
          <stop offset="60%" stop-color="${activePinColor}" stop-opacity="0.2"/>
          <stop offset="100%" stop-color="${activePinColor}" stop-opacity="0"/>
        </radialGradient>
        <!-- Drop Shadow for Pin -->
        <filter id="pin-shadow-${uniqueId}" x="-30%" y="-30%" width="160%" height="160%">
          <feDropShadow dx="0" dy="3" stdDeviation="3" flood-opacity="0.35"/>
        </filter>
      </defs>

      <!-- Base Ground & Land Texture -->
      <rect width="700" height="260" fill="#F1F5F9"/>

      <!-- Dynamic Pannable Map Layer (translates on mouse-drag) -->
      <g id="map-pan-layer" transform="translate(${currentPanX}, ${currentPanY})">
        <!-- Urban Land Parcels / City Blocks -->
        <rect x="20" y="20" width="140" height="65" rx="4" fill="#E2E8F0" opacity="0.75"/>
        <rect x="180" y="22" width="120" height="63" rx="4" fill="#E2E8F0" opacity="0.75"/>
        <rect x="320" y="15" width="160" height="70" rx="4" fill="#E2E8F0" opacity="0.75"/>
        <rect x="500" y="20" width="180" height="65" rx="4" fill="#E2E8F0" opacity="0.75"/>

        <rect x="30" y="140" width="130" height="65" rx="4" fill="#E2E8F0" opacity="0.75"/>
        <rect x="180" y="145" width="130" height="60" rx="4" fill="#E2E8F0" opacity="0.75"/>
        <rect x="330" y="140" width="150" height="65" rx="4" fill="#E2E8F0" opacity="0.75"/>
        <rect x="500" y="140" width="170" height="65" rx="4" fill="#E2E8F0" opacity="0.75"/>

        <!-- Park / Green Buffer Zone -->
        <path d="M 40,20 Q 90,15 130,45 L 120,75 Q 70,80 30,65 Z" fill="#DCFCE7" opacity="0.9"/>
        <text x="50" y="42" font-size="9" fill="#166534" font-weight="600" opacity="0.85">City Park Zone</text>

        <!-- River / Water Canal -->
        <path d="M 0,200 Q 180,180 350,195 T 700,185 L 700,260 L 0,260 Z" fill="#BFDBFE" opacity="0.6"/>
        <text x="610" y="210" font-size="8.5" fill="#1D4ED8" font-style="italic" opacity="0.8">Canal Corridor</text>

        <!-- Tactical Geographic Coordinate Grid -->
        <rect width="700" height="260" fill="url(#tactical-grid-${uniqueId})"/>

        <!-- Major Arterial Roads -->
        <!-- Horizontal Boulevard -->
        <line x1="0" y1="115" x2="700" y2="115" stroke="#FFFFFF" stroke-width="16"/>
        <line x1="0" y1="115" x2="700" y2="115" stroke="#FBBF24" stroke-width="1.5" stroke-dasharray="8,6"/>
        <line x1="0" y1="107" x2="700" y2="107" stroke="#94A3B8" stroke-width="1"/>
        <line x1="0" y1="123" x2="700" y2="123" stroke="#94A3B8" stroke-width="1"/>
        <text x="490" y="103" font-size="8.5" font-weight="700" fill="#475569" letter-spacing="0.05em">STATE HIGHWAY / JURISDICTION ARTERY</text>

        <!-- Vertical Connecting Avenue -->
        <line x1="350" y1="0" x2="350" y2="260" stroke="#FFFFFF" stroke-width="14"/>
        <line x1="350" y1="0" x2="350" y2="260" stroke="#E2E8F0" stroke-width="1.5" stroke-dasharray="6,4"/>
        <line x1="343" y1="0" x2="343" y2="260" stroke="#94A3B8" stroke-width="1"/>
        <line x1="357" y1="0" x2="357" y2="260" stroke="#94A3B8" stroke-width="1"/>

        <!-- Secondary Connecting Roads -->
        <line x1="160" y1="0" x2="160" y2="260" stroke="#FFFFFF" stroke-width="8"/>
        <line x1="160" y1="0" x2="160" y2="260" stroke="#CBD5E1" stroke-width="1"/>
        <line x1="480" y1="0" x2="480" y2="260" stroke="#FFFFFF" stroke-width="8"/>
        <line x1="480" y1="0" x2="480" y2="260" stroke="#CBD5E1" stroke-width="1"/>

        <!-- Tactical Patrol Perimeter Rings with Dynamic Zoom Radius -->
        <circle cx="350" cy="115" r="${z.r1}" fill="none" stroke="${activePinColor}" stroke-width="1" stroke-dasharray="3,3" opacity="0.65"/>
        <text x="${350 + z.r1 + 5}" y="112" font-size="7.5" fill="${activePinColor}" font-weight="600">${z.ring1}</text>
        
        <circle cx="350" cy="115" r="${z.r2}" fill="none" stroke="${activePinColor}" stroke-width="1" stroke-dasharray="4,4" opacity="0.45"/>
        <text x="${350 + z.r2 + 5}" y="112" font-size="7.5" fill="${activePinColor}" font-weight="600">${z.ring2}</text>

        <!-- Pulsing Radar Animation Circle -->
        <circle cx="350" cy="115" r="30" fill="url(#pulse-grad-${uniqueId})" class="animated-radar-circle">
          <animate attributeName="r" values="15;${z.r1};15" dur="2.5s" repeatCount="indefinite"/>
          <animate attributeName="opacity" values="0.8;0.15;0.8" dur="2.5s" repeatCount="indefinite"/>
        </circle>

        <!-- Police Station Dispatch Post -->
        <g transform="translate(180, 50)">
          <circle cx="0" cy="0" r="14" fill="#1E3A8A" stroke="#FFFFFF" stroke-width="2"/>
          <text x="0" y="4" font-size="11" text-anchor="middle" fill="#FFFFFF">🚔</text>
          <line x1="10" y1="10" x2="160" y2="52" stroke="#3B82F6" stroke-width="2" stroke-dasharray="4,4"/>
          <rect x="-35" y="18" width="70" height="15" rx="3" fill="#1E293B" opacity="0.85"/>
          <text x="0" y="29" font-size="7.5" font-weight="700" fill="#FFFFFF" text-anchor="middle">STATION POST (1.2 km)</text>
        </g>

        ${generateLandmarksSvg(isSatellite)}
        ${generateMovementTrailSvg(snapshots, currentSnapshotIndex, isSatellite)}

        <!-- Distinct Animated Active-Incident Pin Icon Centered on Coordinates -->
        <g id="active-incident-pin" class="active-incident-pin" transform="translate(${pinX}, ${pinY})" onclick="toggleIncidentPinTooltip(event)" role="button" tabindex="0" aria-label="Incident Pin: Case ${caseData.complaint_ref_no}" filter="url(#pin-shadow-${uniqueId})">
          <!-- Animated Radar Beacon Ping Waves -->
          <circle cx="0" cy="0" r="10" fill="${activePinColor}" opacity="0.35" class="pin-pulse-wave"/>

          <!-- Target Reticle Crosshair with Pulsing Ring -->
          <circle cx="0" cy="0" r="18" fill="none" stroke="${activePinColor}" stroke-width="1.8" class="pin-reticle-ring"/>
          <line x1="-24" y1="0" x2="24" y2="0" stroke="${activePinColor}" stroke-width="1.5"/>
          <line x1="0" y1="-24" x2="0" y2="24" stroke="${activePinColor}" stroke-width="1.5"/>

          <!-- Pin Head Droplet with Animated Bounce -->
          <g class="pin-head-bounce">
            <path d="M 0,0 C -13,-13 -13,-32 0,-32 C 13,-32 13,-13 0,0 Z" fill="${activePinColor}" stroke="#FFFFFF" stroke-width="2.5" class="pin-body"/>
            <!-- Inner Pin Symbol -->
            <circle cx="0" cy="-19" r="6" fill="#FFFFFF"/>
            <text x="0" y="-16" font-size="9" font-weight="900" text-anchor="middle" fill="${activePinColor}">${isRewound ? '⏱' : '⚠'}</text>
          </g>

          <!-- Interactive Click-For-Tooltip Prompt Callout -->
          <g transform="translate(0, -44)" class="pin-label-callout">
            <rect x="-135" y="-12" width="270" height="20" rx="4" fill="#0F172A" stroke="${activePinColor}" stroke-width="1.5"/>
            <text x="0" y="2" font-family="'Inter', sans-serif" font-size="8.5" font-weight="800" fill="#FFFFFF" text-anchor="middle">
              ${pinCalloutText}
            </text>
          </g>
        </g>
      </g>

      ${isRewound ? `
        <!-- Rewound Snapshot Status Banner in Map HUD -->
        <g transform="translate(350, 22)">
          <rect x="-140" y="-12" width="280" height="24" rx="4" fill="#0F172A" stroke="#A855F7" stroke-width="1.5" opacity="0.95"/>
          <circle cx="-125" cy="0" r="4" fill="#A855F7"/>
          <text x="0" y="3.5" font-family="'Inter', sans-serif" font-size="8.5" font-weight="800" fill="#F3E8FF" text-anchor="middle" letter-spacing="0.04em">
            ⏪ REWOUND SNAPSHOT [${activeSnapshot.relativeTag} • ${activeSnapshot.time}]
          </text>
        </g>
      ` : ''}

      <!-- Map HUD Overlays -->
      <!-- Top-Left: Compass Rose -->
      <g transform="translate(35, 32)">
        <circle cx="0" cy="0" r="15" fill="#FFFFFF" stroke="#CBD5E1" stroke-width="1" opacity="0.95"/>
        <polygon points="0,-12 4,0 0,-3 -4,0" fill="#DC2626"/>
        <polygon points="0,12 4,0 0,-3 -4,0" fill="#64748B"/>
        <text x="0" y="-14" font-size="8" font-weight="800" fill="#DC2626" text-anchor="middle">N</text>
      </g>

      <!-- Top-Right: GIS Telemetry Status Tag -->
      <g transform="translate(560, 18)">
        <rect x="0" y="0" width="125" height="18" rx="3" fill="#0F172A" opacity="0.9"/>
        <circle cx="10" cy="9" r="3" fill="#22C55E"/>
        <text x="65" y="12" font-size="7.5" font-weight="700" fill="#F8FAFC" text-anchor="middle" letter-spacing="0.04em">
          ${z.detail} (${zoom}x)
        </text>
      </g>

      <!-- Bottom-Left: Map Scale Indicator -->
      <g transform="translate(25, 205)">
        <rect x="0" y="-12" width="105" height="15" rx="3" fill="#FFFFFF" stroke="#CBD5E1" stroke-width="1" opacity="0.9"/>
        <line x1="10" y1="-4" x2="95" y2="-4" stroke="#0F172A" stroke-width="2"/>
        <line x1="10" y1="-7" x2="10" y2="-1" stroke="#0F172A" stroke-width="2"/>
        <line x1="95" y1="-7" x2="95" y2="-1" stroke="#0F172A" stroke-width="2"/>
        <text x="52" y="-5" font-size="7.5" font-weight="700" fill="#0F172A" text-anchor="middle">${z.scale}</text>
      </g>

      <!-- Bottom-Right: Lat/Long coordinate stamps -->
      <text id="svg-latlng-stamp" x="680" y="208" font-size="7.5" font-weight="600" fill="#64748B" text-anchor="end">
        DURGA STREET GIS • WGS-84 [${currentCenterLat.toFixed(4)}, ${currentCenterLng.toFixed(4)}]
      </text>
    </svg>
  `;
}

function copyCaseCoordinates() {
  if (!activeCase) return;
  const lat = activeCase.latitude || 23.2156;
  const lng = activeCase.longitude || 72.6369;
  const coordsStr = `${lat}, ${lng}`;
  if (navigator.clipboard && navigator.clipboard.writeText) {
    navigator.clipboard.writeText(coordsStr).then(() => {
      alert(`Incident GPS Coordinates Copied: ${coordsStr}`);
    }).catch(() => {
      prompt('Copy Coordinates:', coordsStr);
    });
  } else {
    prompt('Copy Coordinates:', coordsStr);
  }
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

// =========================================================
// CAMERA QR SCANNER & CASE STATUS VERIFICATION
// =========================================================

let cameraStream = null;
let scanAnimationTimer = null;
let isVerifyingQr = false;

function openQrScannerModal() {
  const modal = document.getElementById('qr-scanner-modal');
  if (!modal) return;
  modal.style.display = 'flex';

  const overlay = document.getElementById('camera-status-overlay');
  if (overlay) {
    overlay.style.display = 'flex';
    overlay.textContent = 'Initializing officer camera feed...';
  }
  const feedback = document.getElementById('scan-feedback-message');
  if (feedback) feedback.style.display = 'none';

  startCameraFeed();
}

function closeQrScannerModal() {
  stopCameraFeed();
  const modal = document.getElementById('qr-scanner-modal');
  if (modal) modal.style.display = 'none';
}

function stopCameraFeed() {
  if (scanAnimationTimer) {
    cancelAnimationFrame(scanAnimationTimer);
    scanAnimationTimer = null;
  }
  if (cameraStream) {
    cameraStream.getTracks().forEach(track => track.stop());
    cameraStream = null;
  }
  const video = document.getElementById('qr-camera-video');
  if (video) video.srcObject = null;
}

async function startCameraFeed() {
  const overlay = document.getElementById('camera-status-overlay');
  const video = document.getElementById('qr-camera-video');

  if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
    if (overlay) overlay.textContent = 'Camera API not supported in this browser. Enter Token/Ref below.';
    return;
  }

  try {
    cameraStream = await navigator.mediaDevices.getUserMedia({
      video: { facingMode: 'environment', width: { ideal: 640 }, height: { ideal: 480 } }
    });
    if (video) {
      video.srcObject = cameraStream;
      await video.play();
      if (overlay) overlay.style.display = 'none';
      scanFrameLoop();
    }
  } catch (err) {
    if (overlay) {
      overlay.textContent = 'Camera permission required: ' + err.message + '. Please use manual entry below.';
    }
  }
}

async function scanFrameLoop() {
  if (!cameraStream) return;
  const video = document.getElementById('qr-camera-video');

  if (video && video.readyState >= 2 && !isVerifyingQr) {
    if ('BarcodeDetector' in window) {
      try {
        const barcodeDetector = new BarcodeDetector({ formats: ['qr_code'] });
        const barcodes = await barcodeDetector.detect(video);
        if (barcodes && barcodes.length > 0) {
          const rawCode = barcodes[0].rawValue;
          if (rawCode) {
            handleScannedQrResult(rawCode);
            return;
          }
        }
      } catch (_) {}
    }
  }

  scanAnimationTimer = requestAnimationFrame(scanFrameLoop);
}

function extractTokenFromQr(scannedText) {
  if (!scannedText) return '';
  let str = scannedText.trim();
  const prefixes = [
    'https://durga.gov.in/case/secure/',
    'durga://case/secure/',
    '/case/secure/'
  ];
  for (const prefix of prefixes) {
    if (str.startsWith(prefix)) {
      return str.substring(prefix.length).trim();
    }
  }
  return str;
}

async function handleScannedQrResult(rawCode) {
  if (isVerifyingQr) return;
  isVerifyingQr = true;
  const cleanCode = extractTokenFromQr(rawCode);

  const feedback = document.getElementById('scan-feedback-message');
  if (feedback) {
    feedback.style.display = 'block';
    feedback.style.background = '#E0F2FE';
    feedback.style.color = '#0369A1';
    feedback.innerHTML = `🔍 Verifying Case QR: <code>${cleanCode}</code>...`;
  }

  try {
    const res = await fetch(`${API_BASE}/api/cases/lookup/qr?code=${encodeURIComponent(cleanCode)}`);
    const data = await res.json();

    if (!res.ok || !data.success) {
      if (feedback) {
        feedback.style.background = '#FEE2E2';
        feedback.style.color = '#B91C1C';
        feedback.innerHTML = `⚠️ <strong>Verification Failed:</strong> ${data.error || 'Case QR is inactive or not found.'}`;
      }
      isVerifyingQr = false;
      return;
    }

    const c = data.case;
    if (feedback) {
      feedback.style.background = '#DCFCE7';
      feedback.style.color = '#15803D';
      feedback.innerHTML = `
        ✅ <strong>Case Verified Successfully!</strong><br>
        <strong>Case:</strong> ${c.complaint_ref_no || c.id} &bull; 
        <strong>Status:</strong> ${data.currentStatus || c.current_status} &bull; 
        <strong>QR:</strong> ${data.qrStatus || 'ACTIVE'}
      `;
    }

    setTimeout(() => {
      closeQrScannerModal();
      isVerifyingQr = false;

      // Select case in officer portal view
      const existing = assignedCases.find(item => item.complaint_ref_no === c.complaint_ref_no || item.id === c.id);
      if (!existing) {
        assignedCases.unshift(c);
        document.getElementById('assigned-cases-count').textContent = `${assignedCases.length} assigned`;
      }
      selectCase(c.complaint_ref_no || (existing && existing.complaint_ref_no));
    }, 1200);

  } catch (err) {
    if (feedback) {
      feedback.style.background = '#FEE2E2';
      feedback.style.color = '#B91C1C';
      feedback.innerHTML = `⚠️ Lookup error: ${err.message}`;
    }
    isVerifyingQr = false;
  }
}

function verifyManualScannedCode() {
  const input = document.getElementById('manual-scan-token-input');
  const code = input ? input.value.trim() : '';
  if (!code) {
    alert('Please enter a Case Reference No or QR Token');
    return;
  }
  handleScannedQrResult(code);
}

document.addEventListener('DOMContentLoaded', () => {
  refreshOfficerCases();
});
