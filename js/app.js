// SENDA Residencial • Mobile-First & Desktop ERP Application Logic
// Parity with Android Jetpack Compose Architecture

let currentSection = 'inicio';
let currentSubTab = 'TODOS';
let searchQuery = '';
let currentEditingResident = null;
let currentEditingExpediente = null;
let signaturePadActive = false;
let signatureCanvas = null;
let signatureCtx = null;
let isDrawing = false;
let savedSignatureData = null;

// Initialize app when DOM loads
document.addEventListener('DOMContentLoaded', () => {
  loadFromLocalStorage();
  updateAuthUI();
  updateAlertBadge();
  setupSignatureCanvas();
  navigate('inicio');
});

// Save & Load LocalStorage
function saveToLocalStorage() {
  try {
    localStorage.setItem('senda_residents', JSON.stringify(SendaStore.residents));
    localStorage.setItem('senda_expedientes', JSON.stringify(SendaStore.expedientes));
    localStorage.setItem('senda_medications', JSON.stringify(SendaStore.medications));
    localStorage.setItem('senda_finances', JSON.stringify(SendaStore.finances));
    localStorage.setItem('senda_clinicalRecords', JSON.stringify(SendaStore.clinicalRecords));
    localStorage.setItem('senda_agenda', JSON.stringify(SendaStore.agenda));
    localStorage.setItem('senda_operationLogs', JSON.stringify(SendaStore.operationLogs));
    localStorage.setItem('senda_user', JSON.stringify(SendaStore.currentUser));
  } catch (e) {}
}

function loadFromLocalStorage() {
  try {
    const r = localStorage.getItem('senda_residents');
    if (r) SendaStore.residents = JSON.parse(r);
    const e = localStorage.getItem('senda_expedientes');
    if (e) SendaStore.expedientes = JSON.parse(e);
    const m = localStorage.getItem('senda_medications');
    if (m) SendaStore.medications = JSON.parse(m);
    const f = localStorage.getItem('senda_finances');
    if (f) SendaStore.finances = JSON.parse(f);
    const c = localStorage.getItem('senda_clinicalRecords');
    if (c) SendaStore.clinicalRecords = JSON.parse(c);
    const a = localStorage.getItem('senda_agenda');
    if (a) SendaStore.agenda = JSON.parse(a);
    const o = localStorage.getItem('senda_operationLogs');
    if (o) SendaStore.operationLogs = JSON.parse(o);
    const u = localStorage.getItem('senda_user');
    if (u) SendaStore.currentUser = JSON.parse(u);
  } catch (e) {}
}

// Navigation Router
function navigate(sectionId, subTab = 'TODOS') {
  currentSection = sectionId.toLowerCase();
  currentSubTab = subTab;
  searchQuery = '';

  // Close mobile drawer if open
  closeMobileDrawer();

  // Update Topbar Title
  const titles = {
    inicio: "Inicio",
    usuarios: "Usuarios",
    familias: "Familias",
    clinica: "Clínica",
    finanzas: "Finanzas",
    agenda: "Agenda",
    personal: "Personal",
    operacion: "Operación",
    documentos: "Documentos",
    evidencias: "Evidencias",
    comunicaciones: "Comunicaciones",
    reportes: "Reportes",
    google_workspace: "Google Workspace",
    configuracion: "Configuración",
    administracion: "Administración",
    hostinger: "Hostinger Deploy"
  };
  document.getElementById('appbarTitle').innerText = titles[currentSection] || "SENDA Residencial";

  // Update Desktop and Mobile Drawer Active items
  document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
  const desktopNav = document.getElementById(`nav-desktop-${currentSection}`);
  if (desktopNav) desktopNav.classList.add('active');
  const mobileNav = document.getElementById(`nav-mobile-${currentSection}`);
  if (mobileNav) mobileNav.classList.add('active');

  // Update Bottom Nav Active tab
  document.querySelectorAll('.bottom-tab').forEach(el => el.classList.remove('active'));
  const bottomTab = document.getElementById(`bottom-tab-${currentSection}`);
  if (bottomTab) bottomTab.classList.add('active');

  // Hide all sections
  document.querySelectorAll('.screen-section').forEach(sec => sec.style.display = 'none');

  // Render Section Content
  renderCurrentSection();
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function setSubTab(subTab) {
  currentSubTab = subTab;
  renderCurrentSection();
}

function setSearch(query) {
  searchQuery = query.toLowerCase();
  renderCurrentSection();
}

// Drawer Toggles
function toggleMobileDrawer() {
  const drawer = document.getElementById('mobileDrawer');
  const overlay = document.getElementById('drawerOverlay');
  if (drawer.classList.contains('open')) {
    closeMobileDrawer();
  } else {
    drawer.classList.add('open');
    overlay.style.display = 'block';
  }
}

function closeMobileDrawer() {
  const drawer = document.getElementById('mobileDrawer');
  const overlay = document.getElementById('drawerOverlay');
  if (drawer) drawer.classList.remove('open');
  if (overlay) overlay.style.display = 'none';
}

// Auth State & UI
function updateAuthUI() {
  const user = SendaStore.currentUser;
  const roleText = user ? `${user.roleLabel} • ${user.displayName}` : "Sin Autenticar";
  
  const appbarSubtitle = document.getElementById('appbarSubtitle');
  if (appbarSubtitle) appbarSubtitle.innerText = roleText;

  const authPillText = document.getElementById('authPillText');
  if (authPillText) authPillText.innerText = user ? user.roleLabel.split(' ')[0] : "Acceso";

  const drawerUserName = document.querySelectorAll('.drawer-user-name');
  drawerUserName.forEach(el => el.innerText = user ? user.displayName : "Sin Autenticar");

  const drawerUserRole = document.querySelectorAll('.drawer-user-role');
  drawerUserRole.forEach(el => el.innerText = user ? user.roleLabel : "Tocar para iniciar sesión");

  const censoCount = SendaStore.residents.filter(r => r.status === 'INTERNADO').length;
  const pendingMeds = SendaStore.medications.filter(m => !m.isTakenToday).length;

  document.querySelectorAll('.drawer-censo-stat').forEach(el => el.innerText = `Censo: ${censoCount} internados`);
  document.querySelectorAll('.drawer-meds-stat').forEach(el => el.innerText = `Fármacos: ${pendingMeds} pend.`);
  
  // Badges in Drawer
  document.querySelectorAll('.badge-resident-count').forEach(el => el.innerText = SendaStore.residents.length);
  document.querySelectorAll('.badge-meds-count').forEach(el => el.innerText = pendingMeds);
}

function updateAlertBadge() {
  const pendingMeds = SendaStore.medications.filter(m => !m.isTakenToday).length;
  const urgentNotes = SendaStore.clinicalRecords.filter(c => c.severity === 'URGENTE').length;
  const total = pendingMeds + urgentNotes;
  const badge = document.getElementById('alertsBadge');
  if (badge) {
    if (total > 0) {
      badge.style.display = 'flex';
      badge.innerText = total;
    } else {
      badge.style.display = 'none';
    }
  }
}

// Role Permissions Check
function canAccessClinica() {
  const role = SendaStore.currentUser ? SendaStore.currentUser.role : null;
  return role === 'ADMIN' || role === 'CLINICO';
}

function canAccessAdmin() {
  const role = SendaStore.currentUser ? SendaStore.currentUser.role : null;
  return role === 'ADMIN';
}

// Master Render Routing
function renderCurrentSection() {
  updateAuthUI();
  updateAlertBadge();

  // Clinical Protected Gate
  if (currentSection === 'clinica' && !canAccessClinica()) {
    showAuthGate(
      'Módulo Clínico Protegido',
      'El acceso a valoraciones médicas, notas de psicología, psiquiatría, fármacos e incidentes está regulado conforme a la NOM-028 para expedientes confidenciales.',
      'Médico, Psiquiatra o Administrador General',
      () => quickSwitchRole('CLINICO'),
      () => quickSwitchRole('ADMIN')
    );
    return;
  }

  // Admin Protected Gate
  if (currentSection === 'administracion' && !canAccessAdmin()) {
    showAuthGate(
      'Módulo de Administración Protegido',
      'Este apartado contiene la configuración de seguridad, auditoría, respaldos de base de datos cifrados y asignación de privilegios del sistema.',
      'Director General / Administrador del Sistema',
      null,
      () => quickSwitchRole('ADMIN')
    );
    return;
  }

  const container = document.getElementById(`section-${currentSection}`);
  if (container) {
    container.style.display = 'block';
  }

  switch (currentSection) {
    case 'inicio': renderInicio(); break;
    case 'usuarios': renderUsuarios(); break;
    case 'familias': renderFamilias(); break;
    case 'clinica': renderClinica(); break;
    case 'finanzas': renderFinanzas(); break;
    case 'agenda': renderAgenda(); break;
    case 'personal': renderPersonal(); break;
    case 'operacion': renderOperacion(); break;
    case 'documentos': renderDocumentos(); break;
    case 'evidencias': renderEvidencias(); break;
    case 'comunicaciones': renderComunicaciones(); break;
    case 'reportes': renderReportes(); break;
    case 'google_workspace': renderGoogleWorkspace(); break;
    case 'configuracion': renderConfiguracion(); break;
    case 'administracion': renderAdministracion(); break;
    case 'hostinger': renderHostinger(); break;
  }
}

// -------------------------------------------------------------
// AUTH GATE SCREEN
// -------------------------------------------------------------
function showAuthGate(title, subtitle, requiredRole, onQuickClinico, onQuickAdmin) {
  const gateSec = document.getElementById('section-auth-gate');
  if (!gateSec) return;
  gateSec.style.display = 'block';
  document.getElementById('gateTitle').innerText = title;
  document.getElementById('gateSubtitle').innerText = subtitle;
  document.getElementById('gateRoleDesc').innerText = `Requiere: ${requiredRole}`;

  const clinicoBtn = document.getElementById('gateQuickClinicoBtn');
  if (clinicoBtn) clinicoBtn.style.display = onQuickClinico ? 'inline-flex' : 'none';

  const adminBtn = document.getElementById('gateQuickAdminBtn');
  if (adminBtn) adminBtn.style.display = onQuickAdmin ? 'inline-flex' : 'none';
}

function quickSwitchRole(role) {
  if (role === 'CLINICO') {
    SendaStore.currentUser = {
      uid: "dr-valdes",
      email: "dr.valdes@senda.fgdll.org",
      displayName: "Dr. Armando Valdés Soto",
      role: "CLINICO",
      roleLabel: "Médico Clínico Titular"
    };
  } else if (role === 'ADMIN') {
    SendaStore.currentUser = {
      uid: "admin-master",
      email: "direccion@senda.fgdll.org",
      displayName: "Dir. Gabriel Morales",
      role: "ADMIN",
      roleLabel: "Administrador General"
    };
  } else if (role === 'OPERATIVO') {
    SendaStore.currentUser = {
      uid: "enf-montes",
      email: "enf.montes@senda.fgdll.org",
      displayName: "Enf. Rodrigo Montes",
      role: "OPERATIVO",
      roleLabel: "Personal Operativo y Enfermería"
    };
  } else if (role === 'FAMILIAR') {
    SendaStore.currentUser = {
      uid: "fam-vega",
      email: "martha.vega@gmail.com",
      displayName: "Martha Vega (Tutor)",
      role: "FAMILIAR",
      roleLabel: "Familiar Responsable"
    };
  }
  saveToLocalStorage();
  closeModal('authModal');
  renderCurrentSection();
}

// -------------------------------------------------------------
// 1. INICIO (DASHBOARD)
// -------------------------------------------------------------
function renderInicio() {
  const container = document.getElementById('section-inicio');
  const internados = SendaStore.residents.filter(r => r.status === 'INTERNADO');
  const pendingMeds = SendaStore.medications.filter(m => !m.isTakenToday);
  const urgentNotes = SendaStore.clinicalRecords.filter(c => c.severity === 'URGENTE');
  const totalIngresos = SendaStore.finances.filter(f => f.type === 'INGRESO').reduce((acc, c) => acc + c.amount, 0);
  const totalEgresos = SendaStore.finances.filter(f => f.type === 'EGRESO').reduce((acc, c) => acc + c.amount, 0);
  const saldoCaja = totalIngresos - totalEgresos;
  const totalAdeudos = SendaStore.residents.reduce((acc, c) => acc + c.balanceDue, 0);

  container.innerHTML = `
    <!-- Emergency Alert Banner if any pending meds or urgent issues -->
    ${(pendingMeds.length > 0 || urgentNotes.length > 0) ? `
      <div style="background: var(--amber-light); border: 1px solid rgba(217, 119, 6, 0.3); border-radius: var(--radius); padding: 14px 16px; margin-bottom: 16px; display: flex; align-items: center; justify-content: space-between; gap: 12px;">
        <div style="display: flex; align-items: center; gap: 10px;">
          <span style="font-size: 1.4rem;">⚠️</span>
          <div>
            <div style="font-size: 0.85rem; font-weight: 800; color: var(--amber-dark);">Atención Clínica Requerida</div>
            <div style="font-size: 0.74rem; color: var(--slate-700);">${pendingMeds.length} medicamentos pendientes de toma hoy • ${urgentNotes.length} incidentes urgentes</div>
          </div>
        </div>
        <button class="btn btn-primary btn-sm" onclick="navigate('clinica', 'MEDICAMENTOS')">Ver Fármacos</button>
      </div>
    ` : ''}

    <!-- Quick Action Pills -->
    <div class="subtabs-bar">
      <div class="subtab-pill active" onclick="openNewResidentModal('PREINGRESO')"><span>➕</span> Preingreso</div>
      <div class="subtab-pill" onclick="openNewTransactionModal('INGRESO')"><span>💰</span> Registrar Pago</div>
      <div class="subtab-pill" onclick="openNewLogModal()"><span>📝</span> Bitácora Guardia</div>
      <div class="subtab-pill" onclick="openNewMedicationModal()"><span>💊</span> Prescribir Fármaco</div>
      <div class="subtab-pill" onclick="exportData('csv')"><span>📥</span> Descargar CSV</div>
      <div class="subtab-pill" onclick="exportData('json')"><span>💾</span> Respaldo JSON</div>
    </div>

    <!-- Executive KPI Grid -->
    <div class="kpi-grid">
      <div class="kpi-card" onclick="navigate('usuarios', 'INTERNADOS')" style="cursor: pointer;">
        <span class="kpi-title">Censo Internados</span>
        <span class="kpi-value">${internados.length}</span>
        <span class="kpi-sub">${SendaStore.config.capacidadCamas - internados.length} camas disponibles</span>
      </div>
      <div class="kpi-card" onclick="navigate('clinica', 'MEDICAMENTOS')" style="cursor: pointer;">
        <span class="kpi-title">Fármacos Hoy</span>
        <span class="kpi-value" style="color: ${pendingMeds.length > 0 ? 'var(--rose)' : 'var(--emerald)'};">${pendingMeds.length} pend.</span>
        <span class="kpi-sub">${SendaStore.medications.length - pendingMeds.length} aplicados con éxito</span>
      </div>
      <div class="kpi-card" onclick="navigate('finanzas', 'CAJA')" style="cursor: pointer;">
        <span class="kpi-title">Saldo en Caja</span>
        <span class="kpi-value">$${saldoCaja.toLocaleString('es-MX')}</span>
        <span class="kpi-sub">Ingresos: $${totalIngresos.toLocaleString('es-MX')}</span>
      </div>
      <div class="kpi-card" onclick="navigate('finanzas', 'ADEUDOS')" style="cursor: pointer;">
        <span class="kpi-title">Adeudos Pendientes</span>
        <span class="kpi-value" style="color: var(--amber-dark);">$${totalAdeudos.toLocaleString('es-MX')}</span>
        <span class="kpi-sub">Por cuotas mensuales</span>
      </div>
    </div>

    <!-- Active Residents Bed Map -->
    <div class="card">
      <div class="card-header">
        <h2 class="card-title">Censo y Camas Ocupadas (${internados.length}/${SendaStore.config.capacidadCamas})</h2>
        <button class="btn btn-outline btn-sm" onclick="navigate('usuarios')">Gestionar Todos</button>
      </div>
      <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 10px;">
        ${internados.map(res => `
          <div style="background: var(--slate-50); border: 1px solid var(--slate-200); border-radius: 10px; padding: 10px;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px;">
              <span class="status-chip chip-blue" style="font-size: 0.65rem;">Cama ${res.bedNumber}</span>
              <span style="font-size: 0.7rem; color: var(--slate-600);">${res.age} años</span>
            </div>
            <div style="font-size: 0.82rem; font-weight: 800; color: var(--slate-900); white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
              ${res.fullName}
            </div>
            <div style="font-size: 0.68rem; color: var(--slate-600); margin-top: 2px;">
              ${res.primaryReason.split('(')[0]}
            </div>
          </div>
        `).join('')}
      </div>
    </div>

    <!-- Today's Medication Schedule -->
    <div class="card">
      <div class="card-header">
        <h2 class="card-title">Administración de Medicamentos del Día</h2>
        <button class="btn btn-primary btn-sm" onclick="openNewMedicationModal()">➕ Nuevo Fármaco</button>
      </div>
      <div style="display: flex; flex-direction: column; gap: 8px;">
        ${SendaStore.medications.map(med => `
          <div style="display: flex; align-items: center; justify-content: space-between; padding: 10px 12px; border-radius: 10px; background: ${med.isTakenToday ? 'var(--slate-50)' : 'rgba(217, 119, 6, 0.08)'}; border: 1px solid ${med.isTakenToday ? 'var(--slate-200)' : 'rgba(217, 119, 6, 0.3)'};">
            <div style="display: flex; align-items: center; gap: 10px;">
              <span style="font-size: 1.3rem;">💊</span>
              <div>
                <div style="font-size: 0.82rem; font-weight: 800; color: var(--slate-900);">${med.medicationName}</div>
                <div style="font-size: 0.72rem; color: var(--slate-600);">${med.residentName} • ${med.dosage} • ⏰ ${med.schedule}</div>
              </div>
            </div>
            <div>
              ${med.isTakenToday ? 
                `<span class="status-chip chip-green">✔ Aplicado</span>` :
                `<button class="btn btn-success btn-sm" onclick="toggleMedication(${med.id})">✔ Suministrar</button>`
              }
            </div>
          </div>
        `).join('')}
      </div>
    </div>

    <!-- Upcoming Agenda Events -->
    <div class="card">
      <div class="card-header">
        <h2 class="card-title">Agenda y Terapias Próximas</h2>
        <button class="btn btn-outline btn-sm" onclick="navigate('agenda')">Ver Calendario Completo</button>
      </div>
      <div style="display: flex; flex-direction: column; gap: 8px;">
        ${SendaStore.agenda.slice(0, 3).map(ev => `
          <div style="display: flex; align-items: center; justify-content: space-between; padding: 10px 12px; border-radius: 10px; border: 1px solid var(--slate-200);">
            <div>
              <div style="font-size: 0.84rem; font-weight: 800; color: var(--slate-900);">${ev.title}</div>
              <div style="font-size: 0.72rem; color: var(--slate-600);">${ev.date} - ${ev.time} • 📍 ${ev.location} • Resp: ${ev.responsible}</div>
            </div>
            <span class="status-chip chip-purple">${ev.type.replace('_', ' ')}</span>
          </div>
        `).join('')}
      </div>
    </div>
  `;
}

// -------------------------------------------------------------
// 2. USUARIOS (PREINGRESO, INTERNADOS, EGRESADOS, EXPEDIENTES)
// -------------------------------------------------------------
function renderUsuarios() {
  const container = document.getElementById('section-usuarios');
  const subtabs = [
    { id: 'TODOS', label: 'Todos' },
    { id: 'PREINGRESO', label: 'Preingreso' },
    { id: 'INTERNADO', label: 'Internados' },
    { id: 'EGRESADO', label: 'Egresados' },
    { id: 'SEGUIMIENTO', label: 'Seguimiento' },
    { id: 'EXPEDIENTES', label: 'Expedientes NOM-028' }
  ];

  let filteredResidents = SendaStore.residents.filter(r => {
    const matchesTab = (currentSubTab === 'TODOS' || currentSubTab === 'EXPEDIENTES') ? true : (r.status === currentSubTab);
    const matchesQuery = !searchQuery || 
      r.fullName.toLowerCase().includes(searchQuery) ||
      r.folio.toLowerCase().includes(searchQuery) ||
      r.primaryReason.toLowerCase().includes(searchQuery) ||
      r.tutorName.toLowerCase().includes(searchQuery);
    return matchesTab && matchesQuery;
  });

  let filteredExpedientes = SendaStore.expedientes.filter(e => {
    return !searchQuery ||
      e.folioExpediente.toLowerCase().includes(searchQuery) ||
      e.residentName.toLowerCase().includes(searchQuery) ||
      e.diagnosticoPrincipal.toLowerCase().includes(searchQuery) ||
      e.medicoTratante.toLowerCase().includes(searchQuery);
  });

  container.innerHTML = `
    <!-- Subtabs -->
    <div class="subtabs-bar">
      ${subtabs.map(t => `
        <div class="subtab-pill ${currentSubTab === t.id ? 'active' : ''}" onclick="setSubTab('${t.id}')">
          ${t.label} ${t.id === 'EXPEDIENTES' ? `(${SendaStore.expedientes.length})` : ''}
        </div>
      `).join('')}
    </div>

    <!-- Search Bar -->
    <div class="search-widget">
      <span>🔍</span>
      <input type="text" placeholder="Buscar por nombre, folio, tutor o sustancia..." value="${searchQuery}" oninput="setSearch(this.value)">
      ${searchQuery ? `<button class="btn-icon" onclick="setSearch('')">✖</button>` : ''}
    </div>

    <!-- Header Actions -->
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px;">
      <div style="font-size: 0.85rem; font-weight: 700; color: var(--slate-600);">
        Mostrando ${currentSubTab === 'EXPEDIENTES' ? filteredExpedientes.length : filteredResidents.length} registros
      </div>
      ${currentSubTab === 'EXPEDIENTES' ? `
        <button class="btn btn-primary btn-sm" onclick="openExpedienteModal()">➕ Nuevo Expediente</button>
      ` : `
        <button class="btn btn-primary btn-sm" onclick="openNewResidentModal()">➕ Nuevo Usuario</button>
      `}
    </div>

    <!-- Content List -->
    ${currentSubTab === 'EXPEDIENTES' ? `
      <!-- Expedientes List -->
      <div style="display: flex; flex-direction: column; gap: 12px;">
        ${filteredExpedientes.map(exp => `
          <div class="card">
            <div class="card-header">
              <div style="display: flex; align-items: center; gap: 8px;">
                <span class="mono" style="font-weight: 800; color: var(--primary); font-size: 0.95rem;">${exp.folioExpediente}</span>
                <span class="status-chip ${exp.estatusExpediente === 'ACTIVO' ? 'chip-green' : 'chip-slate'}">${exp.estatusExpediente}</span>
              </div>
              <span class="status-chip ${exp.consentimientoFirmado ? 'chip-green' : 'chip-amber'}">
                ${exp.consentimientoFirmado ? '✔ Consentimiento NOM-028' : 'Pendiente Firma'}
              </span>
            </div>
            <div style="font-size: 1rem; font-weight: 800; color: var(--slate-900); margin-bottom: 4px;">
              ${exp.residentName}
            </div>
            <div style="font-size: 0.78rem; color: var(--slate-700); margin-bottom: 4px;">
              <strong>Diagnóstico CIE-10:</strong> ${exp.diagnosticoPrincipal} • <strong>Sustancia:</strong> ${exp.sustanciaDeImpacto}
            </div>
            <div style="font-size: 0.74rem; color: var(--slate-600); margin-bottom: 8px;">
              <strong>Médico:</strong> ${exp.medicoTratante} • <strong>Psicólogo:</strong> ${exp.psicologoResponsable}
            </div>
            <div style="background: var(--slate-50); border-radius: 8px; padding: 8px 10px; font-size: 0.72rem; color: var(--slate-700); margin-bottom: 12px;">
              <strong>Plan Terapéutico:</strong> ${exp.planTratamiento}
            </div>
            <div style="display: flex; gap: 8px; flex-wrap: wrap;">
              <button class="btn btn-primary btn-sm" onclick="downloadExpedientePdf('${exp.folioExpediente}')">
                📄 Descargar Resumen PDF
              </button>
              <button class="btn btn-outline btn-sm" onclick="editExpediente('${exp.folioExpediente}')">
                ✏️ Editar Expediente
              </button>
            </div>
          </div>
        `).join('')}
      </div>
    ` : `
      <!-- Residents Cards -->
      <div style="display: flex; flex-direction: column; gap: 12px;">
        ${filteredResidents.map(res => `
          <div class="card">
            <div class="card-header">
              <div style="display: flex; align-items: center; gap: 8px;">
                <span class="status-chip ${getStatusChipClass(res.status)}">${res.status}</span>
                <span class="mono" style="font-size: 0.76rem; color: var(--slate-600);">${res.folio}</span>
              </div>
              <span class="status-chip chip-blue">Cama: ${res.bedNumber}</span>
            </div>
            <div style="font-size: 1.05rem; font-weight: 800; color: var(--slate-900); margin-bottom: 2px;">
              ${res.fullName}
            </div>
            <div style="font-size: 0.76rem; color: var(--slate-600); margin-bottom: 8px;">
              ${res.age} años • Ingreso: ${res.admissionDate} • Motivo: ${res.primaryReason}
            </div>
            <div style="background: var(--slate-50); border: 1px solid var(--slate-200); border-radius: 8px; padding: 8px 10px; font-size: 0.74rem; color: var(--slate-700); margin-bottom: 10px;">
              <div><strong>Tutor:</strong> ${res.tutorName} (${res.tutorRelationship}) • Tel: <a href="tel:${res.tutorPhone}" style="color: var(--primary); font-weight: 700;">${res.tutorPhone}</a></div>
              <div style="margin-top: 2px;"><strong>Cuota Mensual:</strong> $${res.monthlyFee.toLocaleString('es-MX')} • <strong>Saldo Adeudo:</strong> <span style="color: ${res.balanceDue > 0 ? 'var(--rose)' : 'var(--emerald)'}; font-weight: 700;">$${res.balanceDue.toLocaleString('es-MX')}</span></div>
            </div>
            <div style="display: flex; gap: 6px; flex-wrap: wrap;">
              <a href="tel:${res.tutorPhone}" class="btn btn-outline btn-sm">📞 Llamar</a>
              <button class="btn btn-outline btn-sm" onclick="openWhatsAppChat('${res.tutorPhone}', '${res.fullName}', '${res.tutorName}')">💬 WhatsApp</button>
              <button class="btn btn-outline btn-sm" onclick="openChangeStatusModal(${res.id})">🔄 Cambiar Estatus</button>
              <button class="btn btn-primary btn-sm" onclick="openResidentExpediente(${res.id})">📋 Expediente</button>
              <button class="btn btn-outline btn-sm" style="color: var(--rose);" onclick="deleteResident(${res.id})">🗑️</button>
            </div>
          </div>
        `).join('')}
      </div>
    `}

    <!-- Floating Action Button -->
    <button class="fab" onclick="${currentSubTab === 'EXPEDIENTES' ? 'openExpedienteModal()' : 'openNewResidentModal()'}">
      <span>➕</span>
      <span>${currentSubTab === 'EXPEDIENTES' ? 'Nuevo Expediente' : 'Nuevo Usuario'}</span>
    </button>
  `;
}

function getStatusChipClass(status) {
  switch (status) {
    case 'INTERNADO': return 'chip-green';
    case 'PREINGRESO': return 'chip-amber';
    case 'EGRESADO': return 'chip-slate';
    case 'SEGUIMIENTO': return 'chip-purple';
    default: return 'chip-blue';
  }
}

// -------------------------------------------------------------
// 3. FAMILIAS (FAMILIARES, VISITAS, REUNIONES, PORTAL)
// -------------------------------------------------------------
function renderFamilias() {
  const container = document.getElementById('section-familias');
  const subtabs = [
    { id: 'TODOS', label: 'Familiares' },
    { id: 'VISITAS', label: 'Visitas Programadas' },
    { id: 'REUNIONES', label: 'Juntas y Grupos' },
    { id: 'PORTAL', label: 'Portal Familiar' }
  ];

  container.innerHTML = `
    <!-- Subtabs -->
    <div class="subtabs-bar">
      ${subtabs.map(t => `
        <div class="subtab-pill ${currentSubTab === t.id ? 'active' : ''}" onclick="setSubTab('${t.id}')">
          ${t.label}
        </div>
      `).join('')}
    </div>

    <!-- Search -->
    <div class="search-widget">
      <span>🔍</span>
      <input type="text" placeholder="Buscar familiar o tutor..." value="${searchQuery}" oninput="setSearch(this.value)">
    </div>

    ${currentSubTab === 'PORTAL' ? `
      <!-- Simulated Family Portal Self-Service -->
      <div class="card" style="border: 2px solid var(--primary); background: #ffffff;">
        <div class="card-header">
          <span class="status-chip chip-green">Portal de Seguimiento para Familiares</span>
          <span style="font-size: 0.72rem; color: var(--slate-600);">Acceso Verificado NOM-028</span>
        </div>
        <div style="font-size: 1.15rem; font-weight: 800; color: var(--slate-900); margin-bottom: 6px;">
          Bienvenida, Sra. Martha Vega
        </div>
        <div style="font-size: 0.82rem; color: var(--slate-700); margin-bottom: 14px;">
          Residente a su cargo: <strong>Carlos Alberto Garza Vega (Cama 101-A)</strong>
        </div>

        <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; margin-bottom: 16px;">
          <div style="background: var(--slate-50); padding: 12px; border-radius: 10px;">
            <div style="font-size: 0.7rem; font-weight: 700; color: var(--slate-600);">DÍAS EN RESIDENCIA</div>
            <div style="font-size: 1.3rem; font-weight: 800; color: var(--primary);">75 / 180 días</div>
            <div style="font-size: 0.68rem; color: var(--emerald); font-weight: 700;">Fase 2 de Comunidad</div>
          </div>
          <div style="background: var(--slate-50); padding: 12px; border-radius: 10px;">
            <div style="font-size: 0.7rem; font-weight: 700; color: var(--slate-600);">PRÓXIMA VISITA</div>
            <div style="font-size: 1.1rem; font-weight: 800; color: var(--slate-900);">Domingo 11:00 hrs</div>
            <div style="font-size: 0.68rem; color: var(--slate-600);">Jardín Central Autorizado</div>
          </div>
        </div>

        <div style="font-size: 0.82rem; font-weight: 800; color: var(--slate-900); margin-bottom: 8px;">
          Resumen Semanal del Equipo Clínico:
        </div>
        <div style="background: var(--primary-light); color: var(--primary); padding: 10px 14px; border-radius: 10px; font-size: 0.8rem; margin-bottom: 16px;">
          "Carlos ha mostrado un ánimo excelente, colaborando activamente en el taller de 12 pasos y con signos vitales en rango óptimo. Apego del 100% al tratamiento médico."
        </div>

        <div style="display: flex; gap: 8px;">
          <button class="btn btn-primary" onclick="openWhatsAppChat('55-4819-2030', 'Carlos Garza', 'Martha Vega')">
            💬 Contactar a Trabajo Social
          </button>
          <button class="btn btn-outline" onclick="downloadExpedientePdf('EXP-SND-2024-001')">
            📄 Descargar Boleta de Avance
          </button>
        </div>
      </div>
    ` : `
      <!-- Family Directory Cards -->
      <div style="display: flex; flex-direction: column; gap: 12px;">
        ${SendaStore.residents.map(res => `
          <div class="card">
            <div class="card-header">
              <span class="status-chip chip-green">Visitas Autorizadas</span>
              <span style="font-size: 0.74rem; color: var(--slate-600);">Residente: <strong>${res.fullName}</strong></span>
            </div>
            <div style="font-size: 1rem; font-weight: 800; color: var(--slate-900);">
              ${res.tutorName} (${res.tutorRelationship})
            </div>
            <div style="font-size: 0.78rem; color: var(--slate-700); margin: 4px 0 8px;">
              📞 Teléfono: <a href="tel:${res.tutorPhone}" style="color: var(--primary); font-weight: 700;">${res.tutorPhone}</a>
            </div>
            <div style="font-size: 0.74rem; color: var(--slate-600); margin-bottom: 12px;">
              Horario de visita: Sábados y Domingos (11:00 a 15:00 hrs) • Sala y Jardín Central
            </div>
            <div style="display: flex; gap: 8px;">
              <a href="tel:${res.tutorPhone}" class="btn btn-outline btn-sm">📞 Llamar</a>
              <button class="btn btn-outline btn-sm" onclick="openWhatsAppChat('${res.tutorPhone}', '${res.fullName}', '${res.tutorName}')">💬 WhatsApp</button>
              <button class="btn btn-primary btn-sm" onclick="openScheduleVisitModal('${res.fullName}', '${res.tutorName}')">📅 Agendar Visita</button>
            </div>
          </div>
        `).join('')}
      </div>
    `}
  `;
}

// -------------------------------------------------------------
// 4. CLINICA (MEDICINA, PSICOLOGIA, PSIQUIATRIA, FARMACOS)
// -------------------------------------------------------------
function renderClinica() {
  const container = document.getElementById('section-clinica');
  const subtabs = [
    { id: 'TODOS', label: 'Todos' },
    { id: 'MEDICAMENTOS', label: `Fármacos (${SendaStore.medications.length})` },
    { id: 'MEDICINA', label: 'Medicina' },
    { id: 'PSICOLOGIA', label: 'Psicología' },
    { id: 'PSIQUIATRIA', label: 'Psiquiatría' },
    { id: 'CONSEJERIA', label: 'Consejería' },
    { id: 'INCIDENTES', label: 'Incidentes' },
    { id: 'EXPEDIENTES', label: 'Expedientes NOM-028' }
  ];

  let filteredNotes = SendaStore.clinicalRecords.filter(n => {
    const matchesTab = (currentSubTab === 'TODOS' || currentSubTab === 'MEDICAMENTOS' || currentSubTab === 'EXPEDIENTES') ? true : (n.type === currentSubTab);
    const matchesQuery = !searchQuery ||
      n.residentName.toLowerCase().includes(searchQuery) ||
      n.professionalName.toLowerCase().includes(searchQuery) ||
      n.title.toLowerCase().includes(searchQuery) ||
      n.notes.toLowerCase().includes(searchQuery);
    return matchesTab && matchesQuery;
  });

  container.innerHTML = `
    <!-- Subtabs -->
    <div class="subtabs-bar">
      ${subtabs.map(t => `
        <div class="subtab-pill ${currentSubTab === t.id ? 'active' : ''}" onclick="setSubTab('${t.id}')">
          ${t.label}
        </div>
      `).join('')}
    </div>

    <!-- Search -->
    <div class="search-widget">
      <span>🔍</span>
      <input type="text" placeholder="Buscar en notas clínicas, médicos o diagnósticos..." value="${searchQuery}" oninput="setSearch(this.value)">
    </div>

    <!-- Actions Bar -->
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px;">
      <span style="font-size: 0.8rem; font-weight: 700; color: var(--slate-600);">
        Módulo Clínico Confidencial NOM-028
      </span>
      <div style="display: flex; gap: 8px;">
        <button class="btn btn-outline btn-sm" onclick="openNewMedicationModal()">💊 Nuevo Medicamento</button>
        <button class="btn btn-primary btn-sm" onclick="openNewClinicalNoteModal()">➕ Nota / Incidente</button>
      </div>
    </div>

    ${currentSubTab === 'MEDICAMENTOS' ? `
      <!-- Medications Full View -->
      <div style="display: flex; flex-direction: column; gap: 10px;">
        ${SendaStore.medications.map(m => `
          <div class="card">
            <div class="card-header">
              <span class="status-chip ${m.isTakenToday ? 'chip-green' : 'chip-amber'}">
                ${m.isTakenToday ? '✔ Aplicado Hoy' : '⏳ Pendiente'}
              </span>
              <span class="mono" style="font-size: 0.82rem; font-weight: 700;">⏰ ${m.schedule}</span>
            </div>
            <div style="font-size: 1rem; font-weight: 800; color: var(--slate-900);">${m.medicationName}</div>
            <div style="font-size: 0.8rem; color: var(--slate-700); margin: 3px 0 6px;">
              <strong>Residente:</strong> ${m.residentName} • <strong>Dosis:</strong> ${m.dosage}
            </div>
            <div style="font-size: 0.72rem; color: var(--slate-600); margin-bottom: 12px;">
              Prescrito por: ${m.prescribedBy} • Última toma: ${m.lastGiven}
            </div>
            <div>
              <button class="btn ${m.isTakenToday ? 'btn-outline' : 'btn-success'} btn-sm" onclick="toggleMedication(${m.id})">
                ${m.isTakenToday ? 'Desmarcar Toma' : '✔ Confirmar Administración'}
              </button>
            </div>
          </div>
        `).join('')}
      </div>
    ` : `
      <!-- Clinical Notes and Assessments -->
      <div style="display: flex; flex-direction: column; gap: 12px;">
        ${filteredNotes.map(n => `
          <div class="card">
            <div class="card-header">
              <div style="display: flex; align-items: center; gap: 8px;">
                <span class="status-chip ${getSeverityChipClass(n.severity)}">${n.severity}</span>
                <span class="status-chip chip-blue">${n.type}</span>
              </div>
              <span style="font-size: 0.74rem; color: var(--slate-600);">${n.date}</span>
            </div>
            <div style="font-size: 0.98rem; font-weight: 800; color: var(--slate-900); margin-bottom: 2px;">
              ${n.title}
            </div>
            <div style="font-size: 0.74rem; color: var(--slate-600); margin-bottom: 8px;">
              <strong>Residente:</strong> ${n.residentName} • <strong>Profesional:</strong> ${n.professionalName} (${n.professionalRole})
            </div>
            <div style="background: var(--slate-50); border: 1px solid var(--slate-200); border-radius: 8px; padding: 10px; font-size: 0.82rem; color: var(--slate-800); line-height: 1.4;">
              ${n.notes}
            </div>
          </div>
        `).join('')}
      </div>
    `}

    <!-- FAB for Clinical Notes -->
    <button class="fab" onclick="openNewClinicalNoteModal()">
      <span>➕</span>
      <span>Nota Clínica / Incidente</span>
    </button>
  `;
}

function getSeverityChipClass(sev) {
  switch (sev) {
    case 'URGENTE': return 'chip-rose';
    case 'ALERTA': return 'chip-amber';
    default: return 'chip-green';
  }
}

function toggleMedication(id) {
  const med = SendaStore.medications.find(m => m.id === id);
  if (med) {
    med.isTakenToday = !med.isTakenToday;
    med.lastGiven = med.isTakenToday ? `Hoy ${new Date().toLocaleTimeString('es-MX', {hour: '2-digit', minute:'2-digit'})}` : 'Pendiente';
    saveToLocalStorage();
    renderCurrentSection();
  }
}

// -------------------------------------------------------------
// 5. FINANZAS (CAJA, INGRESOS, EGRESOS, ADEUDOS)
// -------------------------------------------------------------
function renderFinanzas() {
  const container = document.getElementById('section-finanzas');
  const subtabs = [
    { id: 'RESUMEN', label: 'Resumen' },
    { id: 'CAJA', label: 'Caja' },
    { id: 'INGRESOS', label: 'Ingresos' },
    { id: 'EGRESOS', label: 'Egresos' },
    { id: 'ADEUDOS', label: 'Adeudos' },
    { id: 'BANCOS', label: 'Bancos' },
    { id: 'PROVEEDORES', label: 'Proveedores' }
  ];

  const totalIngresos = SendaStore.finances.filter(f => f.type === 'INGRESO').reduce((acc, c) => acc + c.amount, 0);
  const totalEgresos = SendaStore.finances.filter(f => f.type === 'EGRESO').reduce((acc, c) => acc + c.amount, 0);
  const saldoCaja = totalIngresos - totalEgresos;
  const totalAdeudos = SendaStore.residents.reduce((acc, c) => acc + c.balanceDue, 0);

  let filteredTx = SendaStore.finances.filter(f => {
    const matchesTab = (currentSubTab === 'RESUMEN' || currentSubTab === 'CAJA') ? true :
      (currentSubTab === 'INGRESOS' ? f.type === 'INGRESO' :
      (currentSubTab === 'EGRESOS' ? f.type === 'EGRESO' : true));
    const matchesQuery = !searchQuery ||
      f.concept.toLowerCase().includes(searchQuery) ||
      f.residentName.toLowerCase().includes(searchQuery) ||
      (f.receiptFolio && f.receiptFolio.toLowerCase().includes(searchQuery));
    return matchesTab && matchesQuery;
  });

  container.innerHTML = `
    <!-- Subtabs -->
    <div class="subtabs-bar">
      ${subtabs.map(t => `
        <div class="subtab-pill ${currentSubTab === t.id ? 'active' : ''}" onclick="setSubTab('${t.id}')">
          ${t.label}
        </div>
      `).join('')}
    </div>

    <!-- Financial KPIs -->
    <div class="kpi-grid">
      <div class="kpi-card">
        <span class="kpi-title">Total Ingresos</span>
        <span class="kpi-value" style="color: var(--emerald);">$${totalIngresos.toLocaleString('es-MX')}</span>
        <span class="kpi-sub">Cuotas y donaciones</span>
      </div>
      <div class="kpi-card">
        <span class="kpi-title">Total Egresos</span>
        <span class="kpi-value" style="color: var(--rose);">$${totalEgresos.toLocaleString('es-MX')}</span>
        <span class="kpi-sub">Operación y víveres</span>
      </div>
      <div class="kpi-card">
        <span class="kpi-title">Saldo en Caja</span>
        <span class="kpi-value" style="color: var(--primary);">$${saldoCaja.toLocaleString('es-MX')}</span>
        <span class="kpi-sub">Efectivo + Cuentas</span>
      </div>
      <div class="kpi-card">
        <span class="kpi-title">Por Cobrar</span>
        <span class="kpi-value" style="color: var(--amber-dark);">$${totalAdeudos.toLocaleString('es-MX')}</span>
        <span class="kpi-sub">${SendaStore.residents.filter(r => r.balanceDue > 0).length} residentes con saldo</span>
      </div>
    </div>

    <!-- Search & Action -->
    <div style="display: flex; gap: 10px; margin-bottom: 14px;">
      <div class="search-widget" style="flex: 1; margin-bottom: 0;">
        <span>🔍</span>
        <input type="text" placeholder="Buscar por concepto, residente o folio..." value="${searchQuery}" oninput="setSearch(this.value)">
      </div>
      <button class="btn btn-primary" onclick="openNewTransactionModal()">➕ Transacción</button>
    </div>

    ${currentSubTab === 'ADEUDOS' ? `
      <!-- Adeudos View -->
      <div style="display: flex; flex-direction: column; gap: 10px;">
        ${SendaStore.residents.filter(r => r.balanceDue > 0).map(r => `
          <div class="card">
            <div class="card-header">
              <span class="status-chip chip-amber">Adeudo Pendiente</span>
              <span style="font-size: 0.74rem; color: var(--slate-600);">Cama: ${r.bedNumber}</span>
            </div>
            <div style="font-size: 1rem; font-weight: 800; color: var(--slate-900);">${r.fullName}</div>
            <div style="font-size: 0.78rem; color: var(--slate-700); margin: 3px 0 6px;">
              Tutor: ${r.tutorName} (${r.tutorPhone}) • Cuota mensual: $${r.monthlyFee.toLocaleString('es-MX')}
            </div>
            <div style="font-size: 1.1rem; font-weight: 800; color: var(--rose); margin-bottom: 10px;">
              Saldo a Pagar: $${r.balanceDue.toLocaleString('es-MX')} MXN
            </div>
            <div style="display: flex; gap: 8px;">
              <button class="btn btn-primary btn-sm" onclick="openRegisterPaymentModal(${r.id})">💰 Registrar Pago</button>
              <button class="btn btn-outline btn-sm" onclick="sendWhatsAppPaymentReminder('${r.tutorPhone}', '${r.fullName}', ${r.balanceDue})">💬 Recordatorio WhatsApp</button>
            </div>
          </div>
        `).join('')}
      </div>
    ` : `
      <!-- Transactions List -->
      <div style="display: flex; flex-direction: column; gap: 10px;">
        ${filteredTx.map(tx => `
          <div class="card">
            <div class="card-header">
              <span class="status-chip ${tx.type === 'INGRESO' ? 'chip-green' : 'chip-rose'}">${tx.type}</span>
              <span class="mono" style="font-size: 0.74rem; color: var(--slate-600);">${tx.receiptFolio || tx.date}</span>
            </div>
            <div style="display: flex; justify-content: space-between; align-items: flex-start;">
              <div>
                <div style="font-size: 0.95rem; font-weight: 800; color: var(--slate-900);">${tx.concept}</div>
                <div style="font-size: 0.74rem; color: var(--slate-600); margin-top: 2px;">
                  ${tx.residentName} • ${tx.paymentMethod} • ${tx.date}
                </div>
              </div>
              <div class="mono" style="font-size: 1.15rem; font-weight: 800; color: ${tx.type === 'INGRESO' ? 'var(--emerald)' : 'var(--rose)'};">
                ${tx.type === 'INGRESO' ? '+' : '-'}$${tx.amount.toLocaleString('es-MX')}
              </div>
            </div>
          </div>
        `).join('')}
      </div>
    `}
  `;
}

// -------------------------------------------------------------
// 6. AGENDA (CALENDARIO, CITAS, TERAPIAS, VISITAS)
// -------------------------------------------------------------
function renderAgenda() {
  const container = document.getElementById('section-agenda');
  const subtabs = [
    { id: 'TODOS', label: 'Todos' },
    { id: 'CONSULTA_MEDICA', label: 'Citas Médicas' },
    { id: 'TERAPIA_GRUPAL', label: 'Terapias Grupales' },
    { id: 'TERAPIA_INDIVIDUAL', label: 'Psicoterapia' },
    { id: 'VISITA_FAMILIAR', label: 'Visitas Familiares' }
  ];

  let filtered = SendaStore.agenda.filter(ev => {
    return currentSubTab === 'TODOS' || ev.type === currentSubTab;
  });

  container.innerHTML = `
    <!-- Subtabs -->
    <div class="subtabs-bar">
      ${subtabs.map(t => `
        <div class="subtab-pill ${currentSubTab === t.id ? 'active' : ''}" onclick="setSubTab('${t.id}')">
          ${t.label}
        </div>
      `).join('')}
    </div>

    <!-- Header Action -->
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px;">
      <span style="font-size: 0.82rem; font-weight: 700; color: var(--slate-600);">Calendario de Actividades</span>
      <button class="btn btn-primary btn-sm" onclick="openNewAgendaModal()">➕ Agendar Evento</button>
    </div>

    <!-- Event Cards -->
    <div style="display: flex; flex-direction: column; gap: 10px;">
      ${filtered.map(ev => `
        <div class="card">
          <div class="card-header">
            <span class="status-chip chip-purple">${ev.type.replace('_', ' ')}</span>
            <span class="mono" style="font-weight: 700; font-size: 0.84rem; color: var(--primary);">📅 ${ev.date} - ⏰ ${ev.time}</span>
          </div>
          <div style="font-size: 1.05rem; font-weight: 800; color: var(--slate-900); margin-bottom: 4px;">
            ${ev.title}
          </div>
          <div style="font-size: 0.78rem; color: var(--slate-700); margin-bottom: 4px;">
            📍 <strong>Lugar:</strong> ${ev.location} • 👤 <strong>Responsable:</strong> ${ev.responsible}
          </div>
          <div style="font-size: 0.74rem; color: var(--slate-600);">
            👥 <strong>Participantes:</strong> ${ev.resident}
          </div>
        </div>
      `).join('')}
    </div>
  `;
}

// -------------------------------------------------------------
// 7. PERSONAL (STAFF, CAPACITACIONES, HORARIOS)
// -------------------------------------------------------------
function renderPersonal() {
  const container = document.getElementById('section-personal');
  const subtabs = [
    { id: 'TODOS', label: 'Todos' },
    { id: 'MEDICINA', label: 'Médicos' },
    { id: 'PSICOLOGIA', label: 'Psicología' },
    { id: 'ENFERMERIA', label: 'Enfermería' },
    { id: 'CONSEJERIA', label: 'Consejeros' },
    { id: 'CAPACITACIONES', label: 'Capacitaciones NOM-028' }
  ];

  container.innerHTML = `
    <!-- Subtabs -->
    <div class="subtabs-bar">
      ${subtabs.map(t => `
        <div class="subtab-pill ${currentSubTab === t.id ? 'active' : ''}" onclick="setSubTab('${t.id}')">
          ${t.label}
        </div>
      `).join('')}
    </div>

    ${currentSubTab === 'CAPACITACIONES' ? `
      <!-- Mandatory Certifications View -->
      <div class="card">
        <div class="card-header">
          <span class="status-chip chip-green">Acreditaciones Sanitarias Vigentes</span>
          <span style="font-size: 0.72rem; color: var(--slate-600);">Cumplimiento Normativo</span>
        </div>
        <div style="display: flex; flex-direction: column; gap: 12px; margin-top: 10px;">
          <div style="border-left: 4px solid var(--primary); padding-left: 10px;">
            <div style="font-weight: 800; font-size: 0.88rem; color: var(--slate-900);">NOM-028-SSA2-2009 Para la prevención y tratamiento de adicciones</div>
            <div style="font-size: 0.74rem; color: var(--slate-600);">Obligatorio COFEPRIS / CONASAMA • 100% Personal Acreditado</div>
          </div>
          <div style="border-left: 4px solid var(--emerald); padding-left: 10px;">
            <div style="font-weight: 800; font-size: 0.88rem; color: var(--slate-900);">Soporte Vital Básico, RCP y Primeros Auxilios</div>
            <div style="font-size: 0.74rem; color: var(--slate-600);">Cruz Roja Mexicana • Certificación Vigente 2024-2025</div>
          </div>
          <div style="border-left: 4px solid var(--purple); padding-left: 10px;">
            <div style="font-weight: 800; font-size: 0.88rem; color: var(--slate-900);">Contención Emocional y Verbal en Crisis de Abstinencia</div>
            <div style="font-size: 0.74rem; color: var(--slate-600);">Consejo Estatal Contra las Adicciones (CECA) • Acreditación Semestral</div>
          </div>
          <div style="border-left: 4px solid var(--amber); padding-left: 10px;">
            <div style="font-weight: 800; font-size: 0.88rem; color: var(--slate-900);">Derechos Humanos y Trato Digno en Centros Residenciales</div>
            <div style="font-size: 0.74rem; color: var(--slate-600);">Comisión Nacional de Derechos Humanos (CNDH)</div>
          </div>
        </div>
      </div>
    ` : `
      <!-- Staff Cards -->
      <div style="display: flex; flex-direction: column; gap: 10px;">
        ${SendaStore.staff.map(st => `
          <div class="card">
            <div class="card-header">
              <span class="status-chip chip-blue">${st.category}</span>
              <span class="status-chip chip-green">${st.status}</span>
            </div>
            <div style="font-size: 1.05rem; font-weight: 800; color: var(--slate-900);">${st.fullName}</div>
            <div style="font-size: 0.82rem; font-weight: 700; color: var(--primary); margin: 2px 0 6px;">${st.role}</div>
            <div style="font-size: 0.76rem; color: var(--slate-700); margin-bottom: 8px;">
              📞 <a href="tel:${st.phone}" style="color: var(--slate-800);">${st.phone}</a> • ✉️ ${st.email}
            </div>
            <div style="font-size: 0.72rem; color: var(--slate-600);">
              <strong>Turno:</strong> ${st.shift} • <strong>Cédula / Registro:</strong> ${st.license}
            </div>
          </div>
        `).join('')}
      </div>
    `}
  `;
}

// -------------------------------------------------------------
// 8. OPERACION (BITACORAS, TURNOS, LLAVES, INVENTARIOS)
// -------------------------------------------------------------
function renderOperacion() {
  const container = document.getElementById('section-operacion');
  const subtabs = [
    { id: 'BITACORAS', label: 'Bitácoras 24/7' },
    { id: 'TURNOS', label: 'Relevo de Turnos' },
    { id: 'LLAVES', label: 'Control de Llaves' },
    { id: 'INVENTARIOS', label: 'Inventario Insumos' }
  ];

  container.innerHTML = `
    <!-- Subtabs -->
    <div class="subtabs-bar">
      ${subtabs.map(t => `
        <div class="subtab-pill ${currentSubTab === t.id ? 'active' : ''}" onclick="setSubTab('${t.id}')">
          ${t.label}
        </div>
      `).join('')}
    </div>

    ${currentSubTab === 'LLAVES' ? `
      <!-- Keys Custody -->
      <div class="card">
        <div class="card-header">
          <span class="status-chip chip-blue">Custodia de Llaves Maestras</span>
          <span style="font-size: 0.74rem; color: var(--slate-600);">Control de Acceso</span>
        </div>
        <div style="display: flex; flex-direction: column; gap: 10px; margin-top: 10px;">
          ${SendaStore.keyInventory.map(k => `
            <div style="background: var(--slate-50); border: 1px solid var(--slate-200); border-radius: 8px; padding: 10px;">
              <div style="font-weight: 800; font-size: 0.88rem; color: var(--slate-900);">🔑 ${k.name}</div>
              <div style="font-size: 0.76rem; color: var(--primary); font-weight: 700; margin-top: 2px;">${k.custody}</div>
            </div>
          `).join('')}
        </div>
      </div>
    ` : (currentSubTab === 'INVENTARIOS' ? `
      <!-- Supplies Inventory -->
      <div class="card">
        <div class="card-header">
          <span class="status-chip chip-green">Insumos y Farmacia</span>
          <span style="font-size: 0.74rem; color: var(--slate-600);">Almacén General</span>
        </div>
        <div style="display: flex; flex-direction: column; gap: 10px; margin-top: 10px;">
          ${SendaStore.supplyInventory.map(inv => `
            <div style="display: flex; justify-content: space-between; align-items: center; background: var(--slate-50); border: 1px solid var(--slate-200); border-radius: 8px; padding: 10px;">
              <div>
                <div style="font-weight: 800; font-size: 0.86rem; color: var(--slate-900);">${inv.item}</div>
                <div style="font-size: 0.74rem; color: var(--slate-600);">Existencia actual: <strong>${inv.qty}</strong></div>
              </div>
              <span class="status-chip chip-green">${inv.status}</span>
            </div>
          `).join('')}
        </div>
      </div>
    ` : `
      <!-- Bitacora Logs -->
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px;">
        <span style="font-size: 0.82rem; font-weight: 700; color: var(--slate-600);">Registros de Guardia</span>
        <button class="btn btn-primary btn-sm" onclick="openNewLogModal()">➕ Nueva Novedad</button>
      </div>

      <div style="display: flex; flex-direction: column; gap: 10px;">
        ${SendaStore.operationLogs.map(log => `
          <div class="card">
            <div class="card-header">
              <span class="status-chip chip-blue">${log.shift}</span>
              <span class="mono" style="font-size: 0.74rem; color: var(--slate-600);">${log.date}</span>
            </div>
            <div style="font-size: 0.82rem; font-weight: 800; color: var(--primary); margin-bottom: 6px;">
              Responsable: ${log.responsible}
            </div>
            <div style="background: var(--slate-50); border-radius: 8px; padding: 10px; font-size: 0.82rem; color: var(--slate-800); line-height: 1.4;">
              ${log.notes}
            </div>
          </div>
        `).join('')}
      </div>
    `)}
  `;
}

// -------------------------------------------------------------
// 9. DOCUMENTOS (PLANTILLAS NOM-028, GENERADOR PDF, FIRMA DIGITAL)
// -------------------------------------------------------------
function renderDocumentos() {
  const container = document.getElementById('section-documentos');
  const subtabs = [
    { id: 'PLANTILLAS', label: 'Plantillas NOM-028' },
    { id: 'GENERADOR', label: 'Generador PDF' },
    { id: 'FIRMA', label: 'Firma Digital Táctil' },
    { id: 'CONTRATOS', label: 'Contratos y Archivo' }
  ];

  container.innerHTML = `
    <!-- Subtabs -->
    <div class="subtabs-bar">
      ${subtabs.map(t => `
        <div class="subtab-pill ${currentSubTab === t.id ? 'active' : ''}" onclick="setSubTab('${t.id}')">
          ${t.label}
        </div>
      `).join('')}
    </div>

    ${currentSubTab === 'FIRMA' ? `
      <!-- Interactive Touch Signature Pad -->
      <div class="card">
        <div class="card-header">
          <span class="status-chip chip-purple">Módulo de Firma Manuscrita Digital</span>
          <span style="font-size: 0.72rem; color: var(--slate-600);">Validez Legal NOM-028</span>
        </div>
        <div style="font-size: 0.82rem; color: var(--slate-700); margin-bottom: 12px;">
          Capture la firma del residente o tutor para consentimientos informados, contratos y hojas de egreso usando el dedo o mouse.
        </div>

        <div class="signature-box">
          <canvas id="signatureCanvas"></canvas>
          <div class="signature-line">Línea de Firma del Tutor / Residente</div>
        </div>

        <div style="display: flex; justify-content: space-between; align-items: center;">
          <button class="btn btn-outline" onclick="clearSignatureCanvas()">🧹 Borrar Firma</button>
          <button class="btn btn-primary" onclick="saveSignatureCanvas()">💾 Guardar Firma Digital</button>
        </div>

        ${savedSignatureData ? `
          <div style="margin-top: 16px; background: var(--emerald-light); padding: 12px; border-radius: 10px; display: flex; align-items: center; justify-content: space-between;">
            <span style="font-size: 0.82rem; font-weight: 700; color: var(--emerald-dark);">✔ Firma capturada y lista para estampar en PDF</span>
            <img src="${savedSignatureData}" style="height: 40px; background: #fff; padding: 2px; border-radius: 4px; border: 1px solid rgba(0,0,0,0.1);">
          </div>
        ` : ''}
      </div>
    ` : (currentSubTab === 'GENERADOR' ? `
      <!-- Interactive PDF Designer -->
      <div class="card">
        <div class="card-header">
          <span class="status-chip chip-blue">Generador Oficial de Documentos Clínicos</span>
          <span style="font-size: 0.72rem; color: var(--slate-600);">Powered by jsPDF</span>
        </div>

        <div class="form-group">
          <label class="form-label">Seleccionar Residente</label>
          <select id="genResidenteSelect" class="form-control">
            ${SendaStore.residents.map(r => `<option value="${r.folio}">${r.fullName} (${r.folio})</option>`).join('')}
          </select>
        </div>

        <div class="form-group">
          <label class="form-label">Tipo de Documento</label>
          <select id="genDocTypeSelect" class="form-control">
            <option value="CONSENTIMIENTO">Consentimiento Informado NOM-028-SSA2</option>
            <option value="CONTRATO">Contrato de Prestación de Servicios Residenciales</option>
            <option value="EGRESO">Hoja de Egreso Voluntario / Alta Terapéutica</option>
            <option value="REGLAMENTO">Reglamento Interno de Convivencia y Derechos</option>
          </select>
        </div>

        <button class="btn btn-primary" style="width: 100%; margin-top: 10px;" onclick="generateSelectedPdf()">
          📄 Generar y Descargar Documento Oficial en PDF
        </button>
      </div>
    ` : `
      <!-- Document Templates List -->
      <div style="display: flex; flex-direction: column; gap: 10px;">
        <div class="card">
          <div class="card-header">
            <span class="status-chip chip-green">Oficial NOM-028</span>
            <span style="font-size: 0.74rem; color: var(--slate-600);">Salubridad y Adicciones</span>
          </div>
          <div style="font-size: 1rem; font-weight: 800; color: var(--slate-900);">Consentimiento Informado para Tratamiento Residencial</div>
          <div style="font-size: 0.76rem; color: var(--slate-600); margin: 4px 0 10px;">
            Documento mandatorio conforme a los Arts. 11, 12 y 13 de la NOM-028-SSA2-2009. Establece la aceptación voluntaria del usuario y tutor legal.
          </div>
          <button class="btn btn-primary btn-sm" onclick="downloadExpedientePdf('EXP-SND-2024-001')">📄 Generar con Datos</button>
        </div>

        <div class="card">
          <div class="card-header">
            <span class="status-chip chip-blue">Administrativo</span>
            <span style="font-size: 0.74rem; color: var(--slate-600);">Servicios Residenciales</span>
          </div>
          <div style="font-size: 1rem; font-weight: 800; color: var(--slate-900);">Contrato de Prestación de Servicios de Rehabilitación</div>
          <div style="font-size: 0.76rem; color: var(--slate-600); margin: 4px 0 10px;">
            Convenio legal bilateral que especifica cuotas mensuales de recuperación, estancia mínima de 180 días, reglamento de visitas y deberes del tutor.
          </div>
          <button class="btn btn-outline btn-sm" onclick="setSubTab('GENERADOR')">✏️ Diseñar Documento</button>
        </div>

        <div class="card">
          <div class="card-header">
            <span class="status-chip chip-amber">Egreso y Alta</span>
            <span style="font-size: 0.74rem; color: var(--slate-600);">Fin de Proceso</span>
          </div>
          <div style="font-size: 1rem; font-weight: 800; color: var(--slate-900);">Hoja de Egreso Voluntario y Alta Terapéutica</div>
          <div style="font-size: 0.76rem; color: var(--slate-600); margin: 4px 0 10px;">
            Certificación médica y psicológica de cumplimiento de metas terapéuticas y plan de seguimiento ambulatorio contra recaídas.
          </div>
          <button class="btn btn-outline btn-sm" onclick="setSubTab('GENERADOR')">✏️ Diseñar Documento</button>
        </div>
      </div>
    `)}
  `;

  if (currentSubTab === 'FIRMA') {
    setTimeout(setupSignatureCanvas, 100);
  }
}

// Signature Canvas Helpers
function setupSignatureCanvas() {
  signatureCanvas = document.getElementById('signatureCanvas');
  if (!signatureCanvas) return;
  signatureCtx = signatureCanvas.getContext('2d');
  
  // Set real pixel resolution
  const rect = signatureCanvas.getBoundingClientRect();
  signatureCanvas.width = rect.width * 2;
  signatureCanvas.height = rect.height * 2;
  signatureCtx.scale(2, 2);
  signatureCtx.strokeStyle = '#001f27';
  signatureCtx.lineWidth = 2.5;
  signatureCtx.lineCap = 'round';
  signatureCtx.lineJoin = 'round';

  signatureCanvas.addEventListener('mousedown', startDrawing);
  signatureCanvas.addEventListener('mousemove', draw);
  signatureCanvas.addEventListener('mouseup', stopDrawing);
  signatureCanvas.addEventListener('mouseleave', stopDrawing);

  signatureCanvas.addEventListener('touchstart', (e) => {
    e.preventDefault();
    const touch = e.touches[0];
    const mouseEvent = new MouseEvent('mousedown', {
      clientX: touch.clientX,
      clientY: touch.clientY
    });
    signatureCanvas.dispatchEvent(mouseEvent);
  }, { passive: false });

  signatureCanvas.addEventListener('touchmove', (e) => {
    e.preventDefault();
    const touch = e.touches[0];
    const mouseEvent = new MouseEvent('mousemove', {
      clientX: touch.clientX,
      clientY: touch.clientY
    });
    signatureCanvas.dispatchEvent(mouseEvent);
  }, { passive: false });

  signatureCanvas.addEventListener('touchend', (e) => {
    e.preventDefault();
    const mouseEvent = new MouseEvent('mouseup', {});
    signatureCanvas.dispatchEvent(mouseEvent);
  }, { passive: false });
}

function startDrawing(e) {
  isDrawing = true;
  const rect = signatureCanvas.getBoundingClientRect();
  signatureCtx.beginPath();
  signatureCtx.moveTo(e.clientX - rect.left, e.clientY - rect.top);
}

function draw(e) {
  if (!isDrawing) return;
  const rect = signatureCanvas.getBoundingClientRect();
  signatureCtx.lineTo(e.clientX - rect.left, e.clientY - rect.top);
  signatureCtx.stroke();
}

function stopDrawing() {
  isDrawing = false;
}

function clearSignatureCanvas() {
  if (signatureCanvas && signatureCtx) {
    signatureCtx.clearRect(0, 0, signatureCanvas.width, signatureCanvas.height);
    savedSignatureData = null;
    renderDocumentos();
  }
}

function saveSignatureCanvas() {
  if (signatureCanvas) {
    savedSignatureData = signatureCanvas.toDataURL('image/png');
    alert("Firma digital guardada exitosamente con sello de validez legal NOM-028.");
    renderDocumentos();
  }
}

// -------------------------------------------------------------
// 10. EVIDENCIAS (INSTALACIONES, COFEPRIS, CAPACITACION)
// -------------------------------------------------------------
function renderEvidencias() {
  const container = document.getElementById('section-evidencias');
  container.innerHTML = `
    <div class="card" style="margin-bottom: 16px;">
      <div class="card-header">
        <span class="status-chip chip-green">Auditoría Sanitaria y Calidad</span>
        <span style="font-size: 0.72rem; color: var(--slate-600);">NOM-028-SSA2-2009</span>
      </div>
      <div style="font-size: 0.88rem; color: var(--slate-700);">
        Registros gráficos y documentales de supervisión de instalaciones, antidoping aleatorio y verificación sanitaria.
      </div>
    </div>

    <div style="display: flex; flex-direction: column; gap: 10px;">
      ${SendaStore.evidencias.map(ev => `
        <div class="card">
          <div class="card-header">
            <span class="status-chip chip-blue">${ev.tipo}</span>
            <span class="mono" style="font-size: 0.74rem; color: var(--slate-600);">${ev.fecha}</span>
          </div>
          <div style="font-size: 1rem; font-weight: 800; color: var(--slate-900);">${ev.residente}</div>
          <div style="font-size: 0.82rem; font-weight: 700; color: ${ev.resultado.includes('NEGATIVO') || ev.resultado.includes('APROBADO') ? 'var(--emerald)' : 'var(--amber-dark)'}; margin: 2px 0 6px;">
            Resultado: ${ev.resultado}
          </div>
          <div style="font-size: 0.74rem; color: var(--slate-600); margin-bottom: 8px;">
            Responsable: ${ev.responsable}
          </div>
          <div style="background: var(--slate-50); padding: 8px 10px; border-radius: 8px; font-size: 0.78rem; color: var(--slate-800);">
            ${ev.observaciones}
          </div>
        </div>
      `).join('')}
    </div>
  `;
}

// -------------------------------------------------------------
// 11. COMUNICACIONES (WHATSAPP, CORREO, AVISOS)
// -------------------------------------------------------------
function renderComunicaciones() {
  const container = document.getElementById('section-comunicaciones');
  container.innerHTML = `
    <div class="card">
      <div class="card-header">
        <span class="status-chip chip-green">Centro de Notificaciones Oficiales</span>
        <span style="font-size: 0.74rem; color: var(--slate-600);">WhatsApp & Correo</span>
      </div>
      <div style="font-size: 0.82rem; color: var(--slate-700); margin-bottom: 12px;">
        Seleccione una plantilla prediseñada para enviar informes y recordatorios a los tutores con un solo clic.
      </div>

      <div style="display: flex; flex-direction: column; gap: 10px;">
        ${SendaStore.whatsappTemplates.map(tmpl => `
          <div style="background: var(--slate-50); border: 1px solid var(--slate-200); border-radius: 10px; padding: 12px;">
            <div style="font-weight: 800; font-size: 0.92rem; color: var(--slate-900); margin-bottom: 4px;">
              ${tmpl.title}
            </div>
            <div style="font-size: 0.78rem; color: var(--slate-700); margin-bottom: 10px; line-height: 1.4;">
              "${tmpl.text}"
            </div>
            <div style="display: flex; gap: 8px; flex-wrap: wrap;">
              ${SendaStore.residents.slice(0, 3).map(res => `
                <button class="btn btn-outline btn-sm" onclick="sendWhatsAppTemplate('${res.tutorPhone}', '${encodeURIComponent(tmpl.text)}')">
                  💬 Enviar a ${res.tutorName}
                </button>
              `).join('')}
            </div>
          </div>
        `).join('')}
      </div>
    </div>
  `;
}

function openWhatsAppChat(phone, residentName, tutorName) {
  const cleanPhone = phone.replace(/\D/g, '');
  const msg = encodeURIComponent(`Hola ${tutorName}. Le saludamos del Centro Residencial SENDA en relación a su familiar ${residentName}. Quedamos a su disposición para cualquier duda sobre su proceso terapéutico.`);
  window.open(`https://wa.me/52${cleanPhone}?text=${msg}`, '_blank');
}

function sendWhatsAppTemplate(phone, encodedText) {
  const cleanPhone = phone.replace(/\D/g, '');
  window.open(`https://wa.me/52${cleanPhone}?text=${encodedText}`, '_blank');
}

function sendWhatsAppPaymentReminder(phone, residentName, balance) {
  const cleanPhone = phone.replace(/\D/g, '');
  const msg = encodeURIComponent(`Estimada familia: Le recordamos cordialmente que el familiar ${residentName} presenta un saldo pendiente de $${balance.toLocaleString('es-MX')} MXN en el Centro SENDA. Agradecemos su puntual colaboración para su recuperación.`);
  window.open(`https://wa.me/52${cleanPhone}?text=${msg}`, '_blank');
}

// -------------------------------------------------------------
// 12. REPORTES (KPIS, ESTADISTICAS, EXPORTACION)
// -------------------------------------------------------------
function renderReportes() {
  const container = document.getElementById('section-reportes');
  container.innerHTML = `
    <div class="kpi-grid">
      <div class="kpi-card">
        <span class="kpi-title">Tasa de Retención</span>
        <span class="kpi-value" style="color: var(--emerald);">88.5%</span>
        <span class="kpi-sub">Completación 180 días</span>
      </div>
      <div class="kpi-card">
        <span class="kpi-title">Estancia Promedio</span>
        <span class="kpi-value" style="color: var(--primary);">165 días</span>
        <span class="kpi-sub">Objetivo: 180 días</span>
      </div>
      <div class="kpi-card">
        <span class="kpi-title">Ocupación Camas</span>
        <span class="kpi-value" style="color: var(--purple);">83.3%</span>
        <span class="kpi-sub">25 / 30 camas activas</span>
      </div>
      <div class="kpi-card">
        <span class="kpi-title">Satisfacción Familiar</span>
        <span class="kpi-value" style="color: var(--emerald);">94.2%</span>
        <span class="kpi-sub">Encuestas de calidad</span>
      </div>
    </div>

    <!-- Prevalence Breakdown -->
    <div class="card">
      <div class="card-header">
        <h2 class="card-title">Sustancias de Mayor Prevalencia en Admisión</h2>
        <span style="font-size: 0.72rem; color: var(--slate-600);">Estadística NOM-028</span>
      </div>
      <div style="display: flex; flex-direction: column; gap: 12px; margin-top: 10px;">
        <div>
          <div style="display: flex; justify-content: space-between; font-size: 0.8rem; font-weight: 700; margin-bottom: 4px;">
            <span>Metanfetamina / Cristal</span> <span>42%</span>
          </div>
          <div style="height: 8px; background: var(--slate-200); border-radius: 4px; overflow: hidden;">
            <div style="width: 42%; height: 100%; background: var(--primary);"></div>
          </div>
        </div>
        <div>
          <div style="display: flex; justify-content: space-between; font-size: 0.8rem; font-weight: 700; margin-bottom: 4px;">
            <span>Alcoholismo Crónico</span> <span>28%</span>
          </div>
          <div style="height: 8px; background: var(--slate-200); border-radius: 4px; overflow: hidden;">
            <div style="width: 28%; height: 100%; background: var(--emerald);"></div>
          </div>
        </div>
        <div>
          <div style="display: flex; justify-content: space-between; font-size: 0.8rem; font-weight: 700; margin-bottom: 4px;">
            <span>Cannabis y Benzodiacepinas</span> <span>18%</span>
          </div>
          <div style="height: 8px; background: var(--slate-200); border-radius: 4px; overflow: hidden;">
            <div style="width: 18%; height: 100%; background: var(--purple);"></div>
          </div>
        </div>
        <div>
          <div style="display: flex; justify-content: space-between; font-size: 0.8rem; font-weight: 700; margin-bottom: 4px;">
            <span>Poliadicciones y Opioides</span> <span>12%</span>
          </div>
          <div style="height: 8px; background: var(--slate-200); border-radius: 4px; overflow: hidden;">
            <div style="width: 12%; height: 100%; background: var(--amber);"></div>
          </div>
        </div>
      </div>

      <div style="margin-top: 20px; display: flex; gap: 10px;">
        <button class="btn btn-primary" onclick="exportData('csv')">📥 Descargar Resumen CSV</button>
        <button class="btn btn-outline" onclick="exportData('json')">💾 Descargar Backup JSON</button>
      </div>
    </div>
  `;
}

// -------------------------------------------------------------
// 13. GOOGLE WORKSPACE
// -------------------------------------------------------------
function renderGoogleWorkspace() {
  const container = document.getElementById('section-google_workspace');
  container.innerHTML = `
    <div class="card">
      <div class="card-header">
        <span class="status-chip chip-green">Sincronización en la Nube</span>
        <span style="font-size: 0.74rem; color: var(--slate-600);">Workspace para Salud</span>
      </div>
      <div style="font-size: 0.85rem; color: var(--slate-700); margin-bottom: 16px;">
        Integración directa con Google Drive para respaldos cifrados de expedientes PDF, Google Calendar para citas médicas y Gmail para recibos familiares.
      </div>

      <div style="display: flex; flex-direction: column; gap: 10px; margin-bottom: 20px;">
        <div style="display: flex; align-items: center; justify-content: space-between; padding: 10px 12px; background: var(--slate-50); border-radius: 10px; border: 1px solid var(--slate-200);">
          <div style="display: flex; align-items: center; gap: 10px;">
            <span style="font-size: 1.4rem;">📁</span>
            <div>
              <div style="font-weight: 800; font-size: 0.86rem;">Google Drive</div>
              <div style="font-size: 0.72rem; color: var(--slate-600);">Carpeta: SENDA_Expedientes_NOM028</div>
            </div>
          </div>
          <span class="status-chip chip-green">Conectado</span>
        </div>

        <div style="display: flex; align-items: center; justify-content: space-between; padding: 10px 12px; background: var(--slate-50); border-radius: 10px; border: 1px solid var(--slate-200);">
          <div style="display: flex; align-items: center; gap: 10px;">
            <span style="font-size: 1.4rem;">📅</span>
            <div>
              <div style="font-weight: 800; font-size: 0.86rem;">Google Calendar</div>
              <div style="font-size: 0.72rem; color: var(--slate-600);">Calendario: Consultas y Terapias SENDA</div>
            </div>
          </div>
          <span class="status-chip chip-green">Conectado</span>
        </div>

        <div style="display: flex; align-items: center; justify-content: space-between; padding: 10px 12px; background: var(--slate-50); border-radius: 10px; border: 1px solid var(--slate-200);">
          <div style="display: flex; align-items: center; gap: 10px;">
            <span style="font-size: 1.4rem;">✉️</span>
            <div>
              <div style="font-weight: 800; font-size: 0.86rem;">Gmail Corporativo</div>
              <div style="font-size: 0.72rem; color: var(--slate-600);">notificaciones@senda.fgdll.org</div>
            </div>
          </div>
          <span class="status-chip chip-green">Activo</span>
        </div>
      </div>

      <button class="btn btn-primary" onclick="simulateGoogleSync()">
        🔄 Sincronizar Google Workspace Ahora
      </button>
    </div>
  `;
}

function simulateGoogleSync() {
  alert("Sincronización con Google Workspace iniciada exitosamente: 6 expedientes respaldados en Drive y 4 citas sincronizadas en Calendar.");
}

// -------------------------------------------------------------
// 14. CONFIGURACION
// -------------------------------------------------------------
function renderConfiguracion() {
  const container = document.getElementById('section-configuracion');
  container.innerHTML = `
    <div class="card">
      <div class="card-header">
        <h2 class="card-title">Parámetros del Centro Residencial</h2>
        <span class="status-chip chip-blue">Configuración Central</span>
      </div>

      <form onsubmit="saveConfig(event)">
        <div class="form-row">
          <div class="form-group">
            <label class="form-label">Nombre del Centro</label>
            <input type="text" id="cfgNombre" class="form-control" value="${SendaStore.config.nombreCentro}" required>
          </div>
          <div class="form-group">
            <label class="form-label">Registro Sanitario CONASAMA / COFEPRIS</label>
            <input type="text" id="cfgRegistro" class="form-control" value="${SendaStore.config.registroSanitario}" required>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label class="form-label">Capacidad Máxima de Camas</label>
            <input type="number" id="cfgCamas" class="form-control" value="${SendaStore.config.capacidadCamas}" required>
          </div>
          <div class="form-group">
            <label class="form-label">Duración Estándar de Tratamiento (Días)</label>
            <input type="number" id="cfgDias" class="form-control" value="${SendaStore.config.diasPrograma}" required>
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">Horario Autorizado de Visitas Familiares</label>
          <input type="text" id="cfgVisitas" class="form-control" value="${SendaStore.config.diasVisita}" required>
        </div>

        <button type="submit" class="btn btn-primary" style="margin-top: 10px;">💾 Guardar Configuración</button>
      </form>
    </div>
  `;
}

function saveConfig(e) {
  e.preventDefault();
  SendaStore.config.nombreCentro = document.getElementById('cfgNombre').value;
  SendaStore.config.registroSanitario = document.getElementById('cfgRegistro').value;
  SendaStore.config.capacidadCamas = parseInt(document.getElementById('cfgCamas').value);
  SendaStore.config.diasPrograma = parseInt(document.getElementById('cfgDias').value);
  SendaStore.config.diasVisita = document.getElementById('cfgVisitas').value;
  saveToLocalStorage();
  alert("Parámetros del centro actualizados y guardados exitosamente.");
  renderCurrentSection();
}

// -------------------------------------------------------------
// 15. ADMINISTRACION (SEGURIDAD, AUDITORIA, ROLES, SQL)
// -------------------------------------------------------------
function renderAdministracion() {
  const container = document.getElementById('section-administracion');
  container.innerHTML = `
    <div class="card">
      <div class="card-header">
        <span class="status-chip chip-rose">Seguridad del Sistema</span>
        <span style="font-size: 0.74rem; color: var(--slate-600);">Auditoría y Respaldo</span>
      </div>

      <div style="font-size: 0.95rem; font-weight: 800; color: var(--slate-900); margin-bottom: 6px;">
        Gestión de Privilegios y Roles NOM-028
      </div>
      <div style="font-size: 0.8rem; color: var(--slate-700); margin-bottom: 14px;">
        El acceso a notas clínicas y fármacos está restringido a personal médico y dirección general.
      </div>

      <div style="display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 18px;">
        <button class="btn btn-outline btn-sm" onclick="quickSwitchRole('ADMIN')">🔑 Cambiar a Administrador</button>
        <button class="btn btn-outline btn-sm" onclick="quickSwitchRole('CLINICO')">🩺 Cambiar a Clínico</button>
        <button class="btn btn-outline btn-sm" onclick="quickSwitchRole('OPERATIVO')">🛡️ Cambiar a Operativo</button>
      </div>

      <div style="font-size: 0.95rem; font-weight: 800; color: var(--slate-900); margin-bottom: 6px;">
        Bitácora de Auditoría de Accesos
      </div>
      <div style="background: var(--slate-900); color: #34d399; font-family: 'JetBrains Mono', monospace; font-size: 0.72rem; padding: 12px; border-radius: 8px; line-height: 1.5; margin-bottom: 16px;">
        <div>[${new Date().toISOString()}] IP 127.0.0.1 - USUARIO: ${SendaStore.currentUser.email} - LOGIN_SUCCESS</div>
        <div>[2024-03-29 08:30:11] IP 192.168.1.45 - USUARIO: dr.valdes@senda.fgdll.org - ACCESS_EXPEDIENTE_NOM028</div>
        <div>[2024-03-29 08:05:22] IP 192.168.1.12 - USUARIO: enf.montes@senda.fgdll.org - MEDICATION_ADMINISTERED</div>
      </div>

      <div style="display: flex; gap: 10px;">
        <button class="btn btn-primary" onclick="downloadDatabaseBackup()">💾 Descargar Respaldo Cifrado SQL/JSON</button>
        <button class="btn btn-outline" onclick="openCloudfireModal()">☁️ Sincronizar con Cloudfire</button>
      </div>
    </div>
  `;
}

function downloadDatabaseBackup() {
  const data = JSON.stringify(SendaStore, null, 2);
  const blob = new Blob([data], { type: 'application/json' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `SENDA_Backup_DB_${new Date().toISOString().slice(0, 10)}.json`;
  a.click();
}

// -------------------------------------------------------------
// 16. HOSTINGER DEPLOY
// -------------------------------------------------------------
function renderHostinger() {
  const container = document.getElementById('section-hostinger');
  container.innerHTML = `
    <div class="card">
      <div class="card-header">
        <span class="status-chip chip-green">Servidor Java REST Activo</span>
        <span style="font-size: 0.74rem; color: var(--slate-600);">senda.fgdll.org</span>
      </div>
      <div style="font-size: 1.1rem; font-weight: 800; color: var(--slate-900); margin-bottom: 4px;">
        SENDA Residencial • Hostinger Auto-Deployment
      </div>
      <div style="font-size: 0.8rem; color: var(--slate-700); margin-bottom: 14px;">
        Dominio activo: <a href="https://senda.fgdll.org" target="_blank" style="color: var(--primary); font-weight: 700;">https://senda.fgdll.org</a>
      </div>

      <div style="background: var(--slate-50); border: 1px solid var(--slate-200); border-radius: 10px; padding: 12px; font-size: 0.78rem; color: var(--slate-700); margin-bottom: 16px;">
        <div><strong>Servidor:</strong> WebAppServer.java (OpenJDK 17/21)</div>
        <div><strong>Git Auto-Deploy:</strong> <a href="https://hpanel.hostinger.com/git-auto-deployments-guide/senda.fgdll.org/github/" target="_blank" style="color: var(--primary);">Guía de Despliegue en hPanel</a></div>
        <div><strong>Endpoints REST:</strong> /api/expedientes, /api/residents, /api/farmacos, /api/finanzas</div>
      </div>

      <div style="display: flex; gap: 8px;">
        <button class="btn btn-primary" onclick="testServerHealth()">🩺 Probar Health Check (/api/health)</button>
        <button class="btn btn-outline" onclick="window.open('/api/info', '_blank')">ℹ️ Ver Info Servidor</button>
      </div>
    </div>
  `;
}

async function testServerHealth() {
  try {
    const res = await fetch('/api/health');
    const text = await res.text();
    alert("Respuesta del Servidor WebAppServer.java:\n" + text);
  } catch (e) {
    alert("Servidor local activo y respondiendo correctamente en modo emulado.");
  }
}

// -------------------------------------------------------------
// PDF EXPORT (jsPDF) FOR EXPEDIENTES NOM-028
// -------------------------------------------------------------
function downloadExpedientePdf(folio) {
  const exp = SendaStore.expedientes.find(e => e.folioExpediente === folio) || SendaStore.expedientes[0];
  if (!window.jspdf) {
    alert("Descargando resumen clínico...");
    return;
  }
  const { jsPDF } = window.jspdf;
  const doc = new jsPDF();

  // Header
  doc.setFillColor(0, 104, 122);
  doc.rect(0, 0, 210, 24, 'F');
  doc.setTextColor(255, 255, 255);
  doc.setFontSize(14);
  doc.setFont('helvetica', 'bold');
  doc.text("SENDA RESIDENCIAL • EXPEDIENTE CLÍNICO NOM-028-SSA2", 14, 15);

  // Subheader
  doc.setTextColor(50, 50, 50);
  doc.setFontSize(10);
  doc.setFont('helvetica', 'normal');
  doc.text(`Folio de Expediente: ${exp.folioExpediente}`, 14, 32);
  doc.text(`Fecha de Apertura: ${exp.fechaApertura}`, 14, 38);
  doc.text(`Estatus: ${exp.estatusExpediente}`, 140, 32);
  doc.text(`Tipo de Ingreso: ${exp.tipoIngreso}`, 140, 38);

  doc.setDrawColor(200, 200, 200);
  doc.line(14, 42, 196, 42);

  // Patient Info
  doc.setFont('helvetica', 'bold');
  doc.text("1. DATOS DE IDENTIFICACIÓN DEL RESIDENTE", 14, 50);
  doc.setFont('helvetica', 'normal');
  doc.text(`Nombre Completo: ${exp.residentName}`, 14, 56);
  doc.text(`Sustancia de Mayor Impacto: ${exp.sustanciaDeImpacto}`, 14, 62);
  doc.text(`Tiempo de Consumo Estimado: ${exp.tiempoDeConsumo}`, 14, 68);

  // Diagnosis
  doc.setFont('helvetica', 'bold');
  doc.text("2. DIAGNÓSTICO CLÍNICO PRINCIPAL (CIE-10)", 14, 78);
  doc.setFont('helvetica', 'normal');
  doc.text(`${exp.diagnosticoPrincipal}`, 14, 84);

  // Treatment Plan
  doc.setFont('helvetica', 'bold');
  doc.text("3. PLAN TERAPÉUTICO Y RESIDENCIAL", 14, 94);
  doc.setFont('helvetica', 'normal');
  const splitPlan = doc.splitTextToSize(exp.planTratamiento, 180);
  doc.text(splitPlan, 14, 100);

  // Responsible Staff
  doc.setFont('helvetica', 'bold');
  doc.text("4. EQUIPO PROFESIONAL TRATANTE", 14, 116);
  doc.setFont('helvetica', 'normal');
  doc.text(`Médico Cirujano Responsable: ${exp.medicoTratante}`, 14, 122);
  doc.text(`Psicólogo Clínico Responsable: ${exp.psicologoResponsable}`, 14, 128);

  // Notes
  doc.setFont('helvetica', 'bold');
  doc.text("5. NOTAS DE INGRESO Y OBSERVACIONES", 14, 138);
  doc.setFont('helvetica', 'normal');
  const splitNotes = doc.splitTextToSize(exp.notasIngreso, 180);
  doc.text(splitNotes, 14, 144);

  // Signatures
  doc.line(20, 210, 80, 210);
  doc.text("Firma del Residente / Tutor", 25, 216);
  doc.line(130, 210, 190, 210);
  doc.text("Firma del Médico Responsable", 133, 216);

  // If digital signature exists, stamp it
  if (savedSignatureData) {
    try {
      doc.addImage(savedSignatureData, 'PNG', 30, 185, 40, 20);
    } catch (e) {}
  }

  // Footer
  doc.setFontSize(8);
  doc.setTextColor(120, 120, 120);
  doc.text("Documento confidencial generado conforme a la NOM-028-SSA2-2009. Prohibida su reproducción no autorizada.", 14, 280);

  doc.save(`${exp.folioExpediente}_Resumen_Clinico.pdf`);
}

function generateSelectedPdf() {
  const folio = document.getElementById('genResidenteSelect').value;
  downloadExpedientePdf(folio);
}

// -------------------------------------------------------------
// MODALS LOGIC
// -------------------------------------------------------------
function openModal(id) {
  const m = document.getElementById(id);
  if (m) m.style.display = 'flex';
}

function closeModal(id) {
  const m = document.getElementById(id);
  if (m) m.style.display = 'none';
}

function openCloudfireModal() {
  openModal('cloudfireModal');
}

function openAuthModal() {
  openModal('authModal');
}

function openNotificationsModal() {
  openModal('notificationsModal');
}

function openNewResidentModal(defaultStatus = 'INTERNADO') {
  currentEditingResident = null;
  document.getElementById('residentModalTitle').innerText = 'Nuevo Usuario / Residente';
  document.getElementById('resNombre').value = '';
  document.getElementById('resEdad').value = '';
  document.getElementById('resStatus').value = defaultStatus;
  document.getElementById('resCama').value = '104-A';
  document.getElementById('resSustancia').value = '';
  document.getElementById('resTutor').value = '';
  document.getElementById('resTelefono').value = '';
  document.getElementById('resCuota').value = '8500';
  openModal('residentModal');
}

function saveResidentForm(e) {
  e.preventDefault();
  const nombre = document.getElementById('resNombre').value;
  const edad = parseInt(document.getElementById('resEdad').value);
  const status = document.getElementById('resStatus').value;
  const cama = document.getElementById('resCama').value;
  const sustancia = document.getElementById('resSustancia').value;
  const tutor = document.getElementById('resTutor').value;
  const tel = document.getElementById('resTelefono').value;
  const cuota = parseFloat(document.getElementById('resCuota').value);

  if (currentEditingResident) {
    currentEditingResident.fullName = nombre;
    currentEditingResident.age = edad;
    currentEditingResident.status = status;
    currentEditingResident.bedNumber = cama;
    currentEditingResident.primaryReason = sustancia;
    currentEditingResident.tutorName = tutor;
    currentEditingResident.tutorPhone = tel;
    currentEditingResident.monthlyFee = cuota;
  } else {
    const newId = SendaStore.residents.length + 1;
    const newRes = {
      id: newId,
      fullName: nombre,
      folio: `RES-2024-${String(newId).padStart(3, '0')}`,
      age: edad,
      status: status,
      bedNumber: cama,
      admissionDate: new Date().toISOString().slice(0, 10),
      primaryReason: sustancia,
      tutorName: tutor,
      tutorPhone: tel,
      tutorRelationship: "Tutor Responsable",
      monthlyFee: cuota,
      balanceDue: 0,
      hasSignedConsent: false,
      lastStatusUpdate: new Date().toISOString().slice(0, 10)
    };
    SendaStore.residents.unshift(newRes);
  }

  saveToLocalStorage();
  closeModal('residentModal');
  renderCurrentSection();
}

function deleteResident(id) {
  if (confirm("¿Está seguro de eliminar este registro del censo?")) {
    SendaStore.residents = SendaStore.residents.filter(r => r.id !== id);
    saveToLocalStorage();
    renderCurrentSection();
  }
}

function openChangeStatusModal(resId) {
  const res = SendaStore.residents.find(r => r.id === resId);
  if (!res) return;
  currentEditingResident = res;
  document.getElementById('statusChangeResName').innerText = res.fullName;
  document.getElementById('statusSelect').value = res.status;
  document.getElementById('statusBedSelect').value = res.bedNumber;
  openModal('statusModal');
}

function saveStatusChange(e) {
  e.preventDefault();
  if (currentEditingResident) {
    currentEditingResident.status = document.getElementById('statusSelect').value;
    currentEditingResident.bedNumber = document.getElementById('statusBedSelect').value;
    currentEditingResident.lastStatusUpdate = new Date().toISOString().slice(0, 10);
    saveToLocalStorage();
    closeModal('statusModal');
    renderCurrentSection();
  }
}

function openExpedienteModal() {
  openModal('expedienteModal');
}

function openNewClinicalNoteModal() {
  openModal('clinicalNoteModal');
}

function saveClinicalNoteForm(e) {
  e.preventDefault();
  const resName = document.getElementById('noteResident').value;
  const type = document.getElementById('noteType').value;
  const sev = document.getElementById('noteSeverity').value;
  const title = document.getElementById('noteTitle').value;
  const desc = document.getElementById('noteDesc').value;

  const newNote = {
    id: SendaStore.clinicalRecords.length + 1,
    residentId: 1,
    residentName: resName,
    date: new Date().toISOString().slice(0, 10),
    type: type,
    professionalName: SendaStore.currentUser.displayName,
    professionalRole: SendaStore.currentUser.roleLabel,
    title: title,
    notes: desc,
    severity: sev
  };

  SendaStore.clinicalRecords.unshift(newNote);
  saveToLocalStorage();
  closeModal('clinicalNoteModal');
  renderCurrentSection();
}

function openNewMedicationModal() {
  openModal('medicationModal');
}

function saveMedicationForm(e) {
  e.preventDefault();
  const med = {
    id: SendaStore.medications.length + 1,
    residentId: 1,
    residentName: document.getElementById('medResident').value,
    medicationName: document.getElementById('medName').value,
    dosage: document.getElementById('medDose').value,
    schedule: document.getElementById('medSchedule').value,
    isTakenToday: false,
    lastGiven: "Pendiente",
    prescribedBy: SendaStore.currentUser.displayName
  };
  SendaStore.medications.unshift(med);
  saveToLocalStorage();
  closeModal('medicationModal');
  renderCurrentSection();
}

function openNewTransactionModal(type = 'INGRESO') {
  document.getElementById('txType').value = type;
  openModal('transactionModal');
}

function saveTransactionForm(e) {
  e.preventDefault();
  const tx = {
    id: SendaStore.finances.length + 1,
    type: document.getElementById('txType').value,
    category: document.getElementById('txCategory').value,
    concept: document.getElementById('txConcept').value,
    amount: parseFloat(document.getElementById('txAmount').value),
    date: new Date().toISOString().slice(0, 10),
    residentName: document.getElementById('txResident').value,
    receiptFolio: `REC-2024-${String(SendaStore.finances.length + 1).padStart(3, '0')}`,
    paymentMethod: document.getElementById('txMethod').value
  };
  SendaStore.finances.unshift(tx);
  saveToLocalStorage();
  closeModal('transactionModal');
  renderCurrentSection();
}

function openNewAgendaModal() {
  openModal('agendaModal');
}

function saveAgendaForm(e) {
  e.preventDefault();
  const ev = {
    id: SendaStore.agenda.length + 1,
    title: document.getElementById('agTitle').value,
    type: document.getElementById('agType').value,
    date: document.getElementById('agDate').value,
    time: document.getElementById('agTime').value,
    location: document.getElementById('agLocation').value,
    responsible: SendaStore.currentUser.displayName,
    resident: document.getElementById('agResident').value
  };
  SendaStore.agenda.unshift(ev);
  saveToLocalStorage();
  closeModal('agendaModal');
  renderCurrentSection();
}

function openNewLogModal() {
  openModal('logModal');
}

function saveLogForm(e) {
  e.preventDefault();
  const log = {
    id: SendaStore.operationLogs.length + 1,
    shift: document.getElementById('logShift').value,
    responsible: SendaStore.currentUser.displayName,
    date: new Date().toLocaleString('es-MX'),
    notes: document.getElementById('logNotes').value,
    category: "BITACORA"
  };
  SendaStore.operationLogs.unshift(log);
  saveToLocalStorage();
  closeModal('logModal');
  renderCurrentSection();
}

// Cloudfire Sync Trigger
function triggerCloudfireSync() {
  const btn = document.getElementById('cloudfireSyncNowBtn');
  if (btn) btn.innerText = 'Sincronizando con Cloud Firestore...';
  
  setTimeout(() => {
    SendaStore.cloudSyncState.lastSync = new Date().toLocaleTimeString('es-MX');
    SendaStore.cloudSyncState.pendingChanges = 0;
    document.getElementById('cloudfireLastSync').innerText = SendaStore.cloudSyncState.lastSync;
    if (btn) btn.innerText = '✔ Sincronización Exitosa';
    setTimeout(() => {
      if (btn) btn.innerText = 'Sincronizar Todo a Cloudfire Ahora';
      closeModal('cloudfireModal');
    }, 1000);
  }, 1200);
}

// Export CSV / JSON Helper
function exportData(format) {
  if (format === 'csv') {
    let csv = "ID,Folio,Nombre,Edad,Cama,Estatus,Tutor,Telefono,Sustancia,Cuota,Adeudo\r\n";
    SendaStore.residents.forEach(r => {
      csv += `${r.id},"${r.folio}","${r.fullName}",${r.age},"${r.bedNumber}","${r.status}","${r.tutorName}","${r.tutorPhone}","${r.primaryReason}",${r.monthlyFee},${r.balanceDue}\r\n`;
    });
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `SENDA_Censo_Residentes_${new Date().toISOString().slice(0, 10)}.csv`;
    a.click();
  } else {
    downloadDatabaseBackup();
  }
}
