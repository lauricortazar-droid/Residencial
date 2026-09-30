// SENDA Residencial • Central Data Store matching Android App InitialData.kt

const SendaStore = {
  // Current user state (Default role: ADMIN for complete testing, can be toggled via Auth Dialog)
  currentUser: {
    uid: "admin-master",
    email: "direccion@senda.fgdll.org",
    displayName: "Dir. Gabriel Morales",
    role: "ADMIN", // ADMIN, CLINICO, OPERATIVO, FAMILIAR
    roleLabel: "Administrador General"
  },

  // Cloud Firestore Sync State
  cloudSyncState: {
    isOnline: true,
    isSyncing: false,
    lastSync: new Date().toLocaleTimeString('es-MX'),
    pendingChanges: 0,
    collectionsCount: {
      residents: 6,
      expedientes: 4,
      clinicalRecords: 8,
      medications: 5,
      finances: 10,
      agenda: 6,
      staff: 5,
      operationLogs: 6,
      administrativeRecords: 4
    }
  },

  // Residents (Preingreso, Internado, Egresado, Seguimiento)
  residents: [
    {
      id: 1,
      fullName: "Carlos Alberto Garza Vega",
      folio: "RES-2024-001",
      age: 34,
      status: "INTERNADO",
      bedNumber: "101-A",
      admissionDate: "2024-01-15",
      primaryReason: "Dependencia severa a Alcohol etílico (6 años)",
      tutorName: "Martha Vega",
      tutorPhone: "55-4123-8890",
      tutorRelationship: "Madre",
      monthlyFee: 8500,
      balanceDue: 0,
      hasSignedConsent: true,
      lastStatusUpdate: "2024-01-15"
    },
    {
      id: 2,
      fullName: "Mateo Sebastián Ríos Morales",
      folio: "RES-2024-002",
      age: 28,
      status: "INTERNADO",
      bedNumber: "102-B",
      admissionDate: "2024-02-01",
      primaryReason: "Dependencia a Metanfetamina / Cristal (3 años)",
      tutorName: "Esteban Ríos",
      tutorPhone: "55-9871-2345",
      tutorRelationship: "Hermano",
      monthlyFee: 9200,
      balanceDue: 4600,
      hasSignedConsent: true,
      lastStatusUpdate: "2024-02-01"
    },
    {
      id: 3,
      fullName: "Jorge Emilio Silva Navarro",
      folio: "RES-2024-003",
      age: 41,
      status: "INTERNADO",
      bedNumber: "103-A",
      admissionDate: "2024-02-20",
      primaryReason: "Consumo problemático de Alcohol y Benzodiacepinas",
      tutorName: "Lucía Navarro",
      tutorPhone: "55-1122-3344",
      tutorRelationship: "Esposa",
      monthlyFee: 8500,
      balanceDue: 0,
      hasSignedConsent: true,
      lastStatusUpdate: "2024-02-20"
    },
    {
      id: 4,
      fullName: "Rodrigo Damián Beltrán Cruz",
      folio: "RES-2024-004",
      age: 23,
      status: "PREINGRESO",
      bedNumber: "Sin Asignar",
      admissionDate: "2024-03-28",
      primaryReason: "Cannabis de alta potencia y crisis ansiosas",
      tutorName: "Mariana Cruz",
      tutorPhone: "55-7766-1234",
      tutorRelationship: "Madre",
      monthlyFee: 8000,
      balanceDue: 8000,
      hasSignedConsent: false,
      lastStatusUpdate: "2024-03-28"
    },
    {
      id: 5,
      fullName: "Alejandro Ruiz Peralta",
      folio: "RES-2023-089",
      age: 39,
      status: "EGRESADO",
      bedNumber: "Alta Médica",
      admissionDate: "2023-09-10",
      primaryReason: "Alcoholismo crónico en remisión completa (180 días)",
      tutorName: "Sofía Peralta",
      tutorPhone: "55-3344-5566",
      tutorRelationship: "Hermana",
      monthlyFee: 8500,
      balanceDue: 0,
      hasSignedConsent: true,
      lastStatusUpdate: "2024-03-10"
    },
    {
      id: 6,
      fullName: "Ignacio Daniel Cordero",
      folio: "RES-2023-094",
      age: 31,
      status: "SEGUIMIENTO",
      bedNumber: "Ambulatorio",
      admissionDate: "2023-10-05",
      primaryReason: "Poliadicciones en fase de reinserción social laboral",
      tutorName: "Héctor Cordero",
      tutorPhone: "55-6677-8899",
      tutorRelationship: "Padre",
      monthlyFee: 3500,
      balanceDue: 0,
      hasSignedConsent: true,
      lastStatusUpdate: "2024-03-15"
    }
  ],

  // Expedientes Clínicos Confidenciales NOM-028
  expedientes: [
    {
      id: 1,
      folioExpediente: "EXP-SND-2024-001",
      residentId: 1,
      residentName: "Carlos Alberto Garza Vega",
      fechaApertura: "2024-01-15",
      tipoIngreso: "VOLUNTARIO",
      diagnosticoPrincipal: "F10.2 Dependencia al Alcohol",
      sustanciaDeImpacto: "Alcohol etílico",
      tiempoDeConsumo: "6 años",
      planTratamiento: "Comunidad Terapéutica 6 meses (NOM-028) con psicoterapia cognitiva",
      medicoTratante: "Dr. Armando Valdés Soto (Céd. 4819203)",
      psicologoResponsable: "Lic. Elena Cárdenas (Céd. 8192301)",
      estatusExpediente: "ACTIVO",
      consentimientoFirmado: true,
      notasIngreso: "Ingreso voluntario ratificado por usuario y madre. Presenta síndrome de abstinencia leve superado."
    },
    {
      id: 2,
      folioExpediente: "EXP-SND-2024-002",
      residentId: 2,
      residentName: "Mateo Sebastián Ríos Morales",
      fechaApertura: "2024-02-01",
      tipoIngreso: "VOLUNTARIO",
      diagnosticoPrincipal: "F15.2 Dependencia a Metanfetaminas",
      sustanciaDeImpacto: "Metanfetamina / Cristal",
      tiempoDeConsumo: "3 años",
      planTratamiento: "Desintoxicación supervisada, estabilización psiquiátrica y TCC",
      medicoTratante: "Dr. Armando Valdés Soto",
      psicologoResponsable: "Lic. Roberto Mendoza",
      estatusExpediente: "ACTIVO",
      consentimientoFirmado: true,
      notasIngreso: "Fase 2 de reintegración comunitaria y prevención de recaídas."
    },
    {
      id: 3,
      folioExpediente: "EXP-SND-2024-003",
      residentId: 3,
      residentName: "Jorge Emilio Silva Navarro",
      fechaApertura: "2024-02-20",
      tipoIngreso: "VOLUNTARIO",
      diagnosticoPrincipal: "F19.2 Poliadicción (Alcohol y Ansiolíticos)",
      sustanciaDeImpacto: "Alcohol y Benzodiacepinas",
      tiempoDeConsumo: "8 años",
      planTratamiento: "Esquema de retiro gradual y psicoterapia grupal de 12 pasos",
      medicoTratante: "Dr. Armando Valdés Soto",
      psicologoResponsable: "Lic. Elena Cárdenas",
      estatusExpediente: "ACTIVO",
      consentimientoFirmado: true,
      notasIngreso: "Evolución favorable, apego terapéutico completo."
    },
    {
      id: 4,
      folioExpediente: "EXP-SND-2023-089",
      residentId: 5,
      residentName: "Alejandro Ruiz Peralta",
      fechaApertura: "2023-09-10",
      tipoIngreso: "VOLUNTARIO",
      diagnosticoPrincipal: "F10.2 Dependencia al Alcohol en Remisión",
      sustanciaDeImpacto: "Alcohol etílico",
      tiempoDeConsumo: "10 años",
      planTratamiento: "Programa residencial de 180 días completado con éxito",
      medicoTratante: "Dr. Armando Valdés Soto",
      psicologoResponsable: "Lic. Elena Cárdenas",
      estatusExpediente: "CERRADO_POR_ALTA",
      consentimientoFirmado: true,
      notasIngreso: "Egreso satisfactorio con certificado y plan de seguimiento ambulatorio."
    }
  ],

  // Clinical Records (Medicina, Psicología, Psiquiatría, Consejería, Incidentes)
  clinicalRecords: [
    {
      id: 1,
      residentId: 1,
      residentName: "Carlos Alberto Garza Vega",
      date: "2024-03-28",
      type: "MEDICINA", // MEDICINA, PSICOLOGIA, PSIQUIATRIA, CONSEJERIA, INCIDENTE
      professionalName: "Dr. Armando Valdés Soto",
      professionalRole: "Médico Cirujano",
      title: "Revisión General y Signos Vitales",
      notes: "TA: 120/80 mmHg, FC: 72 lpm, Temp: 36.6°C. Sin temblor distal ni craving reportado. Se mantiene complejo B.",
      severity: "NORMAL" // NORMAL, ALERTA, URGENTE
    },
    {
      id: 2,
      residentId: 2,
      residentName: "Mateo Sebastián Ríos Morales",
      date: "2024-03-27",
      type: "PSIQUIATRIA",
      professionalName: "Dr. Mauricio Herrera",
      professionalRole: "Médico Psiquiatra",
      title: "Control de Ansiedad y Patrón de Sueño",
      notes: "Refiere mejoría en la conciliación del sueño (7 horas continuas). Se reduce dosis de Clonazepam a rescate.",
      severity: "NORMAL"
    },
    {
      id: 3,
      residentId: 1,
      residentName: "Carlos Alberto Garza Vega",
      date: "2024-03-26",
      type: "PSICOLOGIA",
      professionalName: "Lic. Elena Cárdenas",
      professionalRole: "Psicóloga Clínica",
      title: "Sesión Cognitivo-Conductual: Reestructuración",
      notes: "El residente identifica disparadores emocionales asociados a conflictos familiares pasados. Excelente disposición.",
      severity: "NORMAL"
    },
    {
      id: 4,
      residentId: 2,
      residentName: "Mateo Sebastián Ríos Morales",
      date: "2024-03-25",
      type: "INCIDENTE",
      professionalName: "Enf. Rodrigo Montes",
      professionalRole: "Enfermería de Guardia",
      title: "Episodio de Ansiedad Nocturna Leve",
      notes: "Residente presentó inquietud psicomotriz a las 23:30 hrs. Se aplicó contención verbal y ejercicio de respiración diafragmática. Cedió sin medicación.",
      severity: "ALERTA"
    },
    {
      id: 5,
      residentId: 3,
      residentName: "Jorge Emilio Silva Navarro",
      date: "2024-03-24",
      type: "CONSEJERIA",
      professionalName: "Lic. Carlos Méndez",
      professionalRole: "Consejero en Adicciones",
      title: "Taller Paso 4: Inventario Moral",
      notes: "Participación activa en el grupo de reflexión. Manifiesta disposición a reparar daños a su cónyuge.",
      severity: "NORMAL"
    }
  ],

  // Medications Administration
  medications: [
    {
      id: 1,
      residentId: 1,
      residentName: "Carlos Alberto Garza Vega",
      medicationName: "Complejo B + Tiamina 100mg",
      dosage: "1 tableta cada 24 hrs",
      schedule: "08:00 hrs",
      isTakenToday: true,
      lastGiven: "Hoy 08:05",
      prescribedBy: "Dr. Armando Valdés Soto"
    },
    {
      id: 2,
      residentId: 2,
      residentName: "Mateo Sebastián Ríos Morales",
      medicationName: "Clonazepam 0.5mg",
      dosage: "1/2 tableta en la noche",
      schedule: "21:00 hrs",
      isTakenToday: false,
      lastGiven: "Ayer 21:00",
      prescribedBy: "Dr. Mauricio Herrera"
    },
    {
      id: 3,
      residentId: 2,
      residentName: "Mateo Sebastián Ríos Morales",
      medicationName: "Sertralina 50mg",
      dosage: "1 tableta por la mañana",
      schedule: "09:00 hrs",
      isTakenToday: true,
      lastGiven: "Hoy 09:10",
      prescribedBy: "Dr. Mauricio Herrera"
    },
    {
      id: 4,
      residentId: 3,
      residentName: "Jorge Emilio Silva Navarro",
      medicationName: "Omeprazol 20mg",
      dosage: "1 cápsula en ayunas",
      schedule: "07:30 hrs",
      isTakenToday: true,
      lastGiven: "Hoy 07:35",
      prescribedBy: "Dr. Armando Valdés Soto"
    },
    {
      id: 5,
      residentId: 4,
      residentName: "Rodrigo Damián Beltrán Cruz",
      medicationName: "Multivitamínico con Zinc",
      dosage: "1 tableta con alimentos",
      schedule: "14:00 hrs",
      isTakenToday: false,
      lastGiven: "Pendiente ingreso",
      prescribedBy: "Dr. Armando Valdés Soto"
    }
  ],

  // Finance Transactions (Caja, Ingresos, Egresos, Pagos)
  finances: [
    {
      id: 1,
      type: "INGRESO",
      category: "CUOTA_RECUPERACION",
      concept: "Pago de Cuota Mensual Residencial (Carlos Garza)",
      amount: 8500.0,
      date: "2024-03-01",
      residentName: "Carlos Alberto Garza Vega",
      receiptFolio: "REC-2024-041",
      paymentMethod: "Transferencia SPEI"
    },
    {
      id: 2,
      type: "INGRESO",
      category: "CUOTA_RECUPERACION",
      concept: "Anticipo de Mensualidad (Mateo Ríos)",
      amount: 4600.0,
      date: "2024-03-05",
      residentName: "Mateo Sebastián Ríos Morales",
      receiptFolio: "REC-2024-045",
      paymentMethod: "Efectivo en Caja"
    },
    {
      id: 3,
      type: "EGRESO",
      category: "INSUMOS_MEDICOS",
      concept: "Compra de Pruebas Antidoping 6 Parámetros y Material de Curación",
      amount: 2850.0,
      date: "2024-03-10",
      residentName: "Gasto General",
      receiptFolio: "FAC-PROV-118",
      paymentMethod: "Tarjeta Débito Clínica"
    },
    {
      id: 4,
      type: "EGRESO",
      category: "ALIMENTACION",
      concept: "Víveres semanales, frutas, verduras y carnes para comedor",
      amount: 4950.0,
      date: "2024-03-15",
      residentName: "Comedor Comunitario",
      receiptFolio: "FAC-CENTRAL-98",
      paymentMethod: "Transferencia"
    },
    {
      id: 5,
      type: "INGRESO",
      category: "CUOTA_RECUPERACION",
      concept: "Pago Mensualidad (Jorge Emilio Silva)",
      amount: 8500.0,
      date: "2024-03-20",
      residentName: "Jorge Emilio Silva Navarro",
      receiptFolio: "REC-2024-052",
      paymentMethod: "Transferencia SPEI"
    }
  ],

  // Agenda Events
  agenda: [
    {
      id: 1,
      title: "Sesión de Psicoterapia Individual",
      type: "TERAPIA_INDIVIDUAL",
      date: "2024-03-30",
      time: "10:00",
      location: "Consultorio 1 - Psicología",
      responsible: "Lic. Elena Cárdenas",
      resident: "Carlos Alberto Garza Vega"
    },
    {
      id: 2,
      title: "Terapia Grupal de Doce Pasos",
      type: "TERAPIA_GRUPAL",
      date: "2024-03-30",
      time: "16:00",
      location: "Salón Terapéutico Principal",
      responsible: "Lic. Carlos Méndez (Consejero)",
      resident: "Todos los Residentes"
    },
    {
      id: 3,
      title: "Visita Familiar Semanal Programada",
      type: "VISITA_FAMILIAR",
      date: "2024-03-31",
      time: "11:00",
      location: "Jardín Central y Terraza",
      responsible: "Trabajo Social y Seguridad",
      resident: "Familiares Autorizados"
    },
    {
      id: 4,
      title: "Valoración Psiquiátrica Periódica",
      type: "CONSULTA_MEDICA",
      date: "2024-04-02",
      time: "12:30",
      location: "Consultorio Médico",
      responsible: "Dr. Mauricio Herrera",
      resident: "Mateo Sebastián Ríos Morales"
    }
  ],

  // Personal y Staff
  staff: [
    {
      id: 1,
      fullName: "Dr. Armando Valdés Soto",
      role: "Director Médico y Cirujano",
      category: "MEDICINA", // MEDICINA, PSICOLOGIA, ENFERMERIA, CONSEJERIA, ADMINISTRACION
      phone: "55-4819-2030",
      email: "dr.valdes@senda.fgdll.org",
      shift: "Matutino (07:00 - 15:00)",
      license: "Céd. Prof. 4819203 UNAM",
      status: "ACTIVO"
    },
    {
      id: 2,
      fullName: "Lic. Elena Cárdenas Ruiz",
      role: "Psicóloga Clínica y Especialista en Adicciones",
      category: "PSICOLOGIA",
      phone: "55-8192-3010",
      email: "psic.cardenas@senda.fgdll.org",
      shift: "Mixto (09:00 - 17:00)",
      license: "Céd. Prof. 8192301",
      status: "ACTIVO"
    },
    {
      id: 3,
      fullName: "Enf. Rodrigo Montes Morales",
      role: "Enfermero Encargado de Fármacos",
      category: "ENFERMERIA",
      phone: "55-9210-3440",
      email: "enf.montes@senda.fgdll.org",
      shift: "Matutino (07:00 - 15:00)",
      license: "Céd. Técnica 9210344",
      status: "ACTIVO"
    },
    {
      id: 4,
      fullName: "Lic. Carlos Méndez",
      role: "Consejero Certificado en Adicciones",
      category: "CONSEJERIA",
      phone: "55-7766-5544",
      email: "consejeria@senda.fgdll.org",
      shift: "Vespertino (15:00 - 22:00)",
      license: "Certificación CONADIC-2021",
      status: "ACTIVO"
    },
    {
      id: 5,
      fullName: "Gabriel Morales Ramos",
      role: "Director General y Administrador",
      category: "ADMINISTRACION",
      phone: "55-9988-7711",
      email: "direccion@senda.fgdll.org",
      shift: "Tiempo Completo",
      license: "Representante Legal",
      status: "ACTIVO"
    }
  ],

  // Operación, Bitácoras, Llaves e Inventarios
  operationLogs: [
    {
      id: 1,
      shift: "MATUTINO (07:00 - 15:00)",
      responsible: "Enf. Rodrigo Montes",
      date: "2024-03-29 08:30",
      notes: "Pase de lista matutino con 100% de censo presente. Desayuno servido en orden. Suministro puntual de medicamentos.",
      category: "BITACORA"
    },
    {
      id: 2,
      shift: "VESPERTINO (15:00 - 22:00)",
      responsible: "Lic. Carlos Méndez",
      date: "2024-03-28 17:00",
      notes: "Taller terapéutico grupal concluido sin contratiempos. Residentes en patio en tiempo recreativo supervisado.",
      category: "BITACORA"
    },
    {
      id: 3,
      shift: "NOCTURNO (22:00 - 07:00)",
      responsible: "Vig. Alberto Salgado",
      date: "2024-03-28 23:45",
      notes: "Rondín perimetral completo. Portones asegurados y vitrina de medicamentos con candado en regla.",
      category: "BITACORA"
    }
  ],

  // Keys Inventory
  keyInventory: [
    { name: "Llave Maestra Portón Principal y Acceso", custody: "Vigilancia de Guardia en Caseta" },
    { name: "Llave Vitrina de Medicamentos Controlados", custody: "Enfermería / Dr. Armando Valdés" },
    { name: "Llave Almacén General de Despensa y Víveres", custody: "Jefatura de Cocina" },
    { name: "Llave de Vehículo Institucional (Van Traslados)", custody: "Administración / Chofer Asignado" }
  ],

  // Supply Inventory
  supplyInventory: [
    { item: "Pruebas Rápidas Multidroga 6 Parámetros (THC, COC, MET, AMP, BZO, OPI)", qty: "45 piezas", status: "Stock Óptimo" },
    { item: "Guantes de Látex Desechables", qty: "12 cajas (1200 pzas)", status: "Stock Óptimo" },
    { item: "Juegos de Sábanas y Cobijas Limpias", qty: "40 juegos", status: "Suficiente" },
    { item: "Víveres y Granos No Perecederos (Arroz, Frijol, Avena)", qty: "120 kg", status: "Abastecido" },
    { item: "Botiquín Antiséptico, Gasas y Vendas Elásticas", qty: "8 paquetes", status: "Reabastecer en 10 días" }
  ],

  // Administrative Records (Contracts, NOM-028 Consents, Digital Signatures)
  administrativeRecords: [
    {
      id: 1,
      residentId: 1,
      residentName: "Carlos Alberto Garza Vega",
      type: "CONSENTIMIENTO_NOM028",
      folio: "ADM-DOC-2024-001",
      date: "2024-01-15",
      signerName: "Martha Vega (Madre)",
      status: "RATIFICADO_FIRMADO",
      notes: "Consentimiento informado ratificado con huella y firma digital conforme a la NOM-028-SSA2."
    },
    {
      id: 2,
      residentId: 1,
      residentName: "Carlos Alberto Garza Vega",
      type: "CONTRATO_SERVICIOS",
      folio: "ADM-CTR-2024-001",
      date: "2024-01-15",
      signerName: "Martha Vega",
      status: "VIGENTE",
      notes: "Contrato de prestación de servicios residenciales por 180 días con cuota pactada de $8,500.00 MXN."
    },
    {
      id: 3,
      residentId: 2,
      residentName: "Mateo Sebastián Ríos Morales",
      type: "CONSENTIMIENTO_NOM028",
      folio: "ADM-DOC-2024-002",
      date: "2024-02-01",
      signerName: "Esteban Ríos (Hermano)",
      status: "RATIFICADO_FIRMADO",
      notes: "Aceptación de reglamento interno, confidencialidad y autorización de revisión médica periódica."
    },
    {
      id: 4,
      residentId: 5,
      residentName: "Alejandro Ruiz Peralta",
      type: "HOJA_EGRESO",
      folio: "ADM-EGR-2024-012",
      date: "2024-03-10",
      signerName: "Alejandro Ruiz y Sofía Peralta",
      status: "ALTA_TERAPEUTICA",
      notes: "Hoja de egreso voluntario por cumplimiento satisfactorio del plan terapéutico."
    }
  ],

  // Evidencias Fotográficas y Sanitarias
  evidencias: [
    {
      id: 1,
      residente: "Carlos Alberto Garza Vega",
      tipo: "ANTIDOPING_6_PANEL",
      resultado: "NEGATIVO",
      fecha: "2024-03-25",
      responsable: "Enf. Rodrigo Montes",
      observaciones: "Panel de 6 drogas negativo (THC, COC, MET, AMP, BZO, OPI). Residente estable y con excelente evolución."
    },
    {
      id: 2,
      residente: "Instalaciones Generales",
      tipo: "SUPERVISION_SANITARIA_COFEPRIS",
      resultado: "APROBADO_100%",
      fecha: "2024-02-18",
      responsable: "Dir. Gabriel Morales",
      observaciones: "Dictamen de supervisión higiénica favorable en cocina, dormitorios, consultorio y manejo de RPBI."
    },
    {
      id: 3,
      residente: "Mateo Sebastián Ríos Morales",
      tipo: "ANTIDOPING_CONTROL_RUTINARIO",
      resultado: "NEGATIVO",
      fecha: "2024-03-22",
      responsable: "Dr. Armando Valdés Soto",
      observaciones: "Tamizaje aleatorio negativo a todas las sustancias psicoactivas. Ausencia de craving."
    },
    {
      id: 4,
      residente: "Personal Senda",
      tipo: "CAPACITACION_NOM028",
      resultado: "CERTIFICACION_CONCLUIDA",
      fecha: "2024-01-20",
      responsable: "Consejo Estatal Contra las Adicciones (CECA)",
      observaciones: "Acreditación de todo el personal clínico y operativo en actualización de la NOM-028-SSA2."
    }
  ],

  // Communications & WhatsApp Templates
  whatsappTemplates: [
    {
      id: "reporte_semanal",
      title: "Reporte Semanal de Bienestar",
      text: "Hola estimado tutor. Le saludamos del Centro Residencial SENDA para informarle que su familiar se encuentra con excelente ánimo, participando activamente en sus terapias individuales y grupales. ¡Seguimos adelante un día a la vez!"
    },
    {
      id: "confirmacion_visita",
      title: "Confirmación de Visita Dominical",
      text: "Estimada familia: Se encuentra confirmada su visita para este domingo en horario de 11:00 a 14:00 hrs. Recuerde traer ropa cómoda, calzado cerrado y evitar alimentos enlatados o bebidas no autorizadas."
    },
    {
      id: "recordatorio_cuota",
      title: "Recordatorio de Cuota Mensual",
      text: "Apreciable familia: Le enviamos un cordial recordatorio sobre la cuota mensual de mantenimiento residencial correspondiente a este periodo. Agradecemos su puntual colaboración para continuar con la atención integral de su familiar."
    },
    {
      id: "notificacion_medica",
      title: "Notificación de Valoración Médica",
      text: "Estimada familia: Le informamos que el día de hoy su familiar tuvo su consulta médica periódica con el Dr. Armando Valdés, reportando signos vitales estables y un excelente apego al tratamiento."
    }
  ],

  // Centre Configuration
  config: {
    nombreCentro: "SENDA Residencial",
    lema: "Comunidad Terapéutica & ERP Clínico",
    registroSanitario: "CONASAMA-REG-2022-0941 / COFEPRIS",
    capacidadCamas: 30,
    diasPrograma: 180,
    diasVisita: "Sábados y Domingos (11:00 a 15:00 hrs)",
    telefonoEmergencia: "55-4819-2030",
    correoContacto: "contacto@senda.fgdll.org",
    alertaFarmacosHoras: 1
  }
};
