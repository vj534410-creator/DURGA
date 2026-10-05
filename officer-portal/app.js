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
    const priority = getCasePriorityLevel(activeCase);
    const idElem = document.getElementById('tooltip-case-id');
    const titleElem = document.getElementById('tooltip-case-title');
    const priorityElem = document.getElementById('tooltip-case-priority');
    const coordsElem = document.getElementById('tooltip-case-coords');

    if (idElem) idElem.textContent = activeCase.complaint_ref_no;
    if (titleElem) titleElem.textContent = activeCase.title || 'Incident Case Docket';
    if (priorityElem) {
      priorityElem.textContent = priority;
      priorityElem.className = `tooltip-badge badge-${priority.toLowerCase()}`;
    }
    if (coordsElem && activeCase.latitude && activeCase.longitude) {
      coordsElem.textContent = `📍 ${Number(activeCase.latitude).toFixed(4)}° N, ${Number(activeCase.longitude).toFixed(4)}° E`;
    }

    tooltip.style.display = 'block';
  } else {
    tooltip.style.display = 'none';
  }
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

  const latFormatted = `${Math.abs(lat).toFixed(4)}° ${lat >= 0 ? 'N' : 'S'}`;
  const lngFormatted = `${Math.abs(lng).toFixed(4)}° ${lng >= 0 ? 'E' : 'W'}`;

  if (coordsVal) {
    coordsVal.textContent = `${latFormatted}, ${lngFormatted}${isEstimated ? ' (Estimated)' : ''}`;
  }
  if (locAddress) {
    locAddress.textContent = caseData.incident_location || 'Jurisdiction Incident Zone';
  }
  if (extLink) {
    extLink.href = `https://maps.google.com/?q=${lat},${lng}`;
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

  // Render SVG static map with base layer and zoom level
  container.innerHTML = generateStaticMapSvg(lat, lng, caseData, currentMapZoom, currentMapLayer);
}

function generateStaticMapSvg(lat, lng, caseData, zoom = 15, layer = 'street') {
  const priority = getCasePriorityLevel(caseData);
  const isEmergency = priority === 'EMERGENCY';
  const isUrgent = priority === 'URGENT';
  
  const isSatellite = (layer === 'satellite');
  const pinColor = isEmergency ? '#DC2626' : (isUrgent ? '#EA580C' : (isSatellite ? '#38BDF8' : '#2563EB'));
  const priorityLabel = isEmergency ? '🚨 CRITICAL EMERGENCY PIN' : (isUrgent ? '⚠️ URGENT INCIDENT PIN' : '📍 INCIDENT PIN');

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
      <svg viewBox="0 0 700 220" width="100%" height="220" xmlns="http://www.w3.org/2000/svg" class="static-map-svg" style="display:block; width:100%; height:220px; border-radius: 6px; background: #0B132B;">
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

        <!-- Distinct Animated Active-Incident Pin Icon Centered on Coordinates -->
        <g id="active-incident-pin" class="active-incident-pin" transform="translate(350, 105)" onclick="toggleIncidentPinTooltip(event)" role="button" tabindex="0" aria-label="Incident Pin: Case ${caseData.complaint_ref_no}" filter="url(#pin-shadow-${uniqueId})">
          <!-- Animated Radar Beacon Ping Waves -->
          <circle cx="0" cy="0" r="10" fill="${pinColor}" opacity="0.4" class="pin-pulse-wave"/>

          <!-- Target Reticle Crosshair with Pulsing Ring -->
          <circle cx="0" cy="0" r="18" fill="none" stroke="${pinColor}" stroke-width="1.8" class="pin-reticle-ring"/>
          <line x1="-24" y1="0" x2="24" y2="0" stroke="${pinColor}" stroke-width="1.5"/>
          <line x1="0" y1="-24" x2="0" y2="24" stroke="${pinColor}" stroke-width="1.5"/>

          <!-- Pin Head Droplet with Animated Bounce -->
          <g class="pin-head-bounce">
            <path d="M 0,0 C -13,-13 -13,-32 0,-32 C 13,-32 13,-13 0,0 Z" fill="${pinColor}" stroke="#FFFFFF" stroke-width="2.5" class="pin-body"/>
            <!-- Inner Pin Symbol -->
            <circle cx="0" cy="-19" r="6" fill="#FFFFFF"/>
            <text x="0" y="-16" font-size="9" font-weight="900" text-anchor="middle" fill="${pinColor}">⚠</text>
          </g>

          <!-- Interactive Click-For-Tooltip Prompt Callout -->
          <g transform="translate(0, -44)" class="pin-label-callout">
            <rect x="-120" y="-12" width="240" height="20" rx="4" fill="#020617" stroke="${pinColor}" stroke-width="1.5"/>
            <text x="0" y="2" font-family="'Inter', sans-serif" font-size="8.5" font-weight="800" fill="#FFFFFF" text-anchor="middle">
              ${priorityLabel}: ${caseData.complaint_ref_no} ℹ️ (Click)
            </text>
          </g>
        </g>

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
        <text x="680" y="208" font-size="7.5" font-weight="600" fill="#94A3B8" text-anchor="end">
          SATELLITE RECON • ISRO/CARTOSAT [${lat.toFixed(4)}, ${lng.toFixed(4)}]
        </text>
      </svg>
    `;
  }

  // STREET VIEW BASE LAYER
  return `
    <svg viewBox="0 0 700 220" width="100%" height="220" xmlns="http://www.w3.org/2000/svg" class="static-map-svg" style="display:block; width:100%; height:220px; border-radius: 6px; background: #F8FAFC;">
      <defs>
        <!-- Map Grid Pattern -->
        <pattern id="tactical-grid-${uniqueId}" width="40" height="40" patternUnits="userSpaceOnUse">
          <path d="M 40 0 L 0 0 0 40" fill="none" stroke="#CBD5E1" stroke-width="0.7" opacity="0.6"/>
        </pattern>
        <!-- Animated Pin Radar Pulse Gradient -->
        <radialGradient id="pulse-grad-${uniqueId}" cx="50%" cy="50%" r="50%">
          <stop offset="0%" stop-color="${pinColor}" stop-opacity="0.6"/>
          <stop offset="60%" stop-color="${pinColor}" stop-opacity="0.2"/>
          <stop offset="100%" stop-color="${pinColor}" stop-opacity="0"/>
        </radialGradient>
        <!-- Drop Shadow for Pin -->
        <filter id="pin-shadow-${uniqueId}" x="-30%" y="-30%" width="160%" height="160%">
          <feDropShadow dx="0" dy="3" stdDeviation="3" flood-opacity="0.35"/>
        </filter>
      </defs>

      <!-- Base Ground & Land Texture -->
      <rect width="700" height="220" fill="#F1F5F9"/>
      
      <!-- Urban Land Parcels / City Blocks -->
      <rect x="20" y="20" width="140" height="65" rx="4" fill="#E2E8F0" opacity="0.75"/>
      <rect x="180" y="22" width="120" height="63" rx="4" fill="#E2E8F0" opacity="0.75"/>
      <rect x="320" y="15" width="160" height="70" rx="4" fill="#E2E8F0" opacity="0.75"/>
      <rect x="500" y="20" width="180" height="65" rx="4" fill="#E2E8F0" opacity="0.75"/>

      <rect x="30" y="130" width="130" height="65" rx="4" fill="#E2E8F0" opacity="0.75"/>
      <rect x="180" y="135" width="130" height="60" rx="4" fill="#E2E8F0" opacity="0.75"/>
      <rect x="330" y="130" width="150" height="65" rx="4" fill="#E2E8F0" opacity="0.75"/>
      <rect x="500" y="130" width="170" height="65" rx="4" fill="#E2E8F0" opacity="0.75"/>

      <!-- Park / Green Buffer Zone -->
      <path d="M 40,20 Q 90,15 130,45 L 120,75 Q 70,80 30,65 Z" fill="#DCFCE7" opacity="0.9"/>
      <text x="50" y="42" font-size="9" fill="#166534" font-weight="600" opacity="0.85">City Park Zone</text>

      <!-- River / Water Canal -->
      <path d="M 0,200 Q 180,180 350,195 T 700,185 L 700,220 L 0,220 Z" fill="#BFDBFE" opacity="0.6"/>
      <text x="610" y="210" font-size="8.5" fill="#1D4ED8" font-style="italic" opacity="0.8">Canal Corridor</text>

      <!-- Tactical Geographic Coordinate Grid -->
      <rect width="700" height="220" fill="url(#tactical-grid-${uniqueId})"/>

      <!-- Major Arterial Roads -->
      <!-- Horizontal Boulevard -->
      <line x1="0" y1="105" x2="700" y2="105" stroke="#FFFFFF" stroke-width="16"/>
      <line x1="0" y1="105" x2="700" y2="105" stroke="#FBBF24" stroke-width="1.5" stroke-dasharray="8,6"/>
      <line x1="0" y1="97" x2="700" y2="97" stroke="#94A3B8" stroke-width="1"/>
      <line x1="0" y1="113" x2="700" y2="113" stroke="#94A3B8" stroke-width="1"/>
      <text x="490" y="93" font-size="8.5" font-weight="700" fill="#475569" letter-spacing="0.05em">STATE HIGHWAY / JURISDICTION ARTERY</text>

      <!-- Vertical Connecting Avenue -->
      <line x1="350" y1="0" x2="350" y2="220" stroke="#FFFFFF" stroke-width="14"/>
      <line x1="350" y1="0" x2="350" y2="220" stroke="#E2E8F0" stroke-width="1.5" stroke-dasharray="6,4"/>
      <line x1="343" y1="0" x2="343" y2="220" stroke="#94A3B8" stroke-width="1"/>
      <line x1="357" y1="0" x2="357" y2="220" stroke="#94A3B8" stroke-width="1"/>

      <!-- Secondary Connecting Roads -->
      <line x1="160" y1="0" x2="160" y2="220" stroke="#FFFFFF" stroke-width="8"/>
      <line x1="160" y1="0" x2="160" y2="220" stroke="#CBD5E1" stroke-width="1"/>
      <line x1="480" y1="0" x2="480" y2="220" stroke="#FFFFFF" stroke-width="8"/>
      <line x1="480" y1="0" x2="480" y2="220" stroke="#CBD5E1" stroke-width="1"/>

      <!-- Tactical Patrol Perimeter Rings with Dynamic Zoom Radius -->
      <circle cx="350" cy="105" r="${z.r1}" fill="none" stroke="${pinColor}" stroke-width="1" stroke-dasharray="3,3" opacity="0.65"/>
      <text x="${350 + z.r1 + 5}" y="102" font-size="7.5" fill="${pinColor}" font-weight="600">${z.ring1}</text>
      
      <circle cx="350" cy="105" r="${z.r2}" fill="none" stroke="${pinColor}" stroke-width="1" stroke-dasharray="4,4" opacity="0.45"/>
      <text x="${350 + z.r2 + 5}" y="102" font-size="7.5" fill="${pinColor}" font-weight="600">${z.ring2}</text>

      <!-- Pulsing Radar Animation Circle -->
      <circle cx="350" cy="105" r="30" fill="url(#pulse-grad-${uniqueId})" class="animated-radar-circle">
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

      <!-- Distinct Animated Active-Incident Pin Icon Centered on Coordinates -->
      <g id="active-incident-pin" class="active-incident-pin" transform="translate(350, 105)" onclick="toggleIncidentPinTooltip(event)" role="button" tabindex="0" aria-label="Incident Pin: Case ${caseData.complaint_ref_no}" filter="url(#pin-shadow-${uniqueId})">
        <!-- Animated Radar Beacon Ping Waves -->
        <circle cx="0" cy="0" r="10" fill="${pinColor}" opacity="0.35" class="pin-pulse-wave"/>

        <!-- Target Reticle Crosshair with Pulsing Ring -->
        <circle cx="0" cy="0" r="18" fill="none" stroke="${pinColor}" stroke-width="1.8" class="pin-reticle-ring"/>
        <line x1="-24" y1="0" x2="24" y2="0" stroke="${pinColor}" stroke-width="1.5"/>
        <line x1="0" y1="-24" x2="0" y2="24" stroke="${pinColor}" stroke-width="1.5"/>

        <!-- Pin Head Droplet with Animated Bounce -->
        <g class="pin-head-bounce">
          <path d="M 0,0 C -13,-13 -13,-32 0,-32 C 13,-32 13,-13 0,0 Z" fill="${pinColor}" stroke="#FFFFFF" stroke-width="2.5" class="pin-body"/>
          <!-- Inner Pin Symbol -->
          <circle cx="0" cy="-19" r="6" fill="#FFFFFF"/>
          <text x="0" y="-16" font-size="9" font-weight="900" text-anchor="middle" fill="${pinColor}">⚠</text>
        </g>

        <!-- Interactive Click-For-Tooltip Prompt Callout -->
        <g transform="translate(0, -44)" class="pin-label-callout">
          <rect x="-115" y="-12" width="230" height="20" rx="4" fill="#0F172A" stroke="${pinColor}" stroke-width="1.5"/>
          <text x="0" y="2" font-family="'Inter', sans-serif" font-size="8.5" font-weight="800" fill="#FFFFFF" text-anchor="middle">
            ${priorityLabel}: ${caseData.complaint_ref_no} ℹ️ (Click)
          </text>
        </g>
      </g>

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
      <text x="680" y="208" font-size="7.5" font-weight="600" fill="#64748B" text-anchor="end">
        DURGA STREET GIS • WGS-84 [${lat.toFixed(4)}, ${lng.toFixed(4)}]
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
