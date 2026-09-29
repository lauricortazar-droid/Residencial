package com.example.data.local

import com.example.data.model.*

object InitialData {
    val residents = listOf(
        ResidentEntity(
            id = 1,
            folio = "SND-2024-001",
            fullName = "Rodrigo Morales Alarcón",
            age = 29,
            gender = "Masculino",
            status = ResidentStatus.INTERNADO.name,
            admissionDate = "2024-07-15",
            bedNumber = "Cama 04",
            roomName = "Dormitorio A - Esperanza",
            primaryReason = "Dependencia a Sustancias Psicoactivas (Alcohol y Estimulantes)",
            currentPhase = "3. Reestructuración Cognitiva",
            tutorName = "Elena Alarcón Vega (Madre)",
            tutorPhone = "+52 55 4192 8831",
            tutorRelationship = "Madre",
            monthlyFee = 8500.0,
            balanceDue = 0.0,
            bloodType = "O+",
            allergies = "Penicilina",
            clinicalNotes = "Buena integración grupal. Asiste puntual a talleres de 12 pasos. Exámenes toxicológicos limpios.",
            counselorAssigned = "Lic. Carlos Méndez"
        ),
        ResidentEntity(
            id = 2,
            folio = "SND-2024-002",
            fullName = "Alejandro Herrera Gómez",
            age = 34,
            gender = "Masculino",
            status = ResidentStatus.INTERNADO.name,
            admissionDate = "2024-08-01",
            bedNumber = "Cama 02",
            roomName = "Dormitorio B - Serenidad",
            primaryReason = "Consumo problemático de Cristal / Metanfetamina",
            currentPhase = "2. Deshabituación y Hábitos",
            tutorName = "Maricela Gómez (Esposa)",
            tutorPhone = "+52 55 7823 4910",
            tutorRelationship = "Esposa",
            monthlyFee = 8500.0,
            balanceDue = 4250.0,
            bloodType = "A+",
            allergies = "Ninguna conocida",
            clinicalNotes = "Presenta mejoría en patrón de sueño. Seguimiento psiquiátrico por ligera ansiedad vespertina.",
            counselorAssigned = "Padrino Roberto Silva"
        ),
        ResidentEntity(
            id = 3,
            folio = "SND-2024-003",
            fullName = "Valeria Ríos Santillán",
            age = 26,
            gender = "Femenino",
            status = ResidentStatus.INTERNADO.name,
            admissionDate = "2024-08-18",
            bedNumber = "Cama 01",
            roomName = "Dormitorio Femenil - Luz",
            primaryReason = "Dependencia a Benzodiacepinas y Alcohol",
            currentPhase = "2. Deshabituación y Hábitos",
            tutorName = "Dr. Javier Ríos (Padre)",
            tutorPhone = "+52 55 3309 1120",
            tutorRelationship = "Padre",
            monthlyFee = 9000.0,
            balanceDue = 0.0,
            bloodType = "B+",
            allergies = "Sulfas",
            clinicalNotes = "Participa en terapia de arte y consejería individual. Buena adherencia al plan alimentario.",
            counselorAssigned = "Psic. Mariana Flores"
        ),
        ResidentEntity(
            id = 4,
            folio = "SND-2024-004",
            fullName = "Mateo Estrada Quintana",
            age = 22,
            gender = "Masculino",
            status = ResidentStatus.PREINGRESO.name,
            admissionDate = "2024-09-24",
            bedNumber = "Por asignar",
            roomName = "Área de Recepción",
            primaryReason = "Cannabis y Ludopatía con crisis de conducta familiar",
            currentPhase = "1. Desintoxicación y Valoración",
            tutorName = "Patricia Quintana (Madre)",
            tutorPhone = "+52 55 6712 9044",
            tutorRelationship = "Madre",
            monthlyFee = 8500.0,
            balanceDue = 8500.0,
            bloodType = "O+",
            allergies = "Ninguna",
            clinicalNotes = "En entrevista inicial de preingreso. Pendiente valoración médica y firma de consentimiento.",
            counselorAssigned = "Lic. Carlos Méndez"
        ),
        ResidentEntity(
            id = 5,
            folio = "SND-2024-005",
            fullName = "Fernando Castillo Nava",
            age = 41,
            gender = "Masculino",
            status = ResidentStatus.EGRESADO.name,
            admissionDate = "2024-02-10",
            dischargeDate = "2024-08-10",
            bedNumber = "Alta Concluida",
            roomName = "Egresados",
            primaryReason = "Alcoholismo Crónico - Programa Residencial 6 meses",
            currentPhase = "4. Reinserción y Proyecto de Vida",
            tutorName = "Claudia Nava (Hermana)",
            tutorPhone = "+52 55 8891 0021",
            tutorRelationship = "Hermana",
            monthlyFee = 8500.0,
            balanceDue = 0.0,
            bloodType = "O-",
            allergies = "Ibuprofeno",
            clinicalNotes = "Alta satisfactoria tras 180 días. Cumplió las 4 fases. Proyecto de vida laboral aprobado.",
            counselorAssigned = "Padrino Roberto Silva"
        ),
        ResidentEntity(
            id = 6,
            folio = "SND-2024-006",
            fullName = "Daniel Orozco Beltrán",
            age = 31,
            gender = "Masculino",
            status = ResidentStatus.SEGUIMIENTO.name,
            admissionDate = "2024-01-15",
            dischargeDate = "2024-07-15",
            bedNumber = "Ambulatorio",
            roomName = "Grupo de Egresados",
            primaryReason = "Poliadicciones en sobriedad continua",
            currentPhase = "4. Reinserción y Proyecto de Vida",
            tutorName = "Beatriz Beltrán (Madre)",
            tutorPhone = "+52 55 1290 8472",
            tutorRelationship = "Madre",
            monthlyFee = 0.0,
            balanceDue = 0.0,
            bloodType = "A+",
            allergies = "Ninguna",
            clinicalNotes = "Asiste puntualmente cada jueves al grupo de apoyo de seguimiento. 2 meses y medio sobrio en casa.",
            counselorAssigned = "Lic. Carlos Méndez"
        )
    )

    val clinicalRecords = listOf(
        ClinicalRecordEntity(
            residentId = 1,
            residentName = "Rodrigo Morales Alarcón",
            category = "PSICOLOGIA",
            title = "Sesión TCC: Manejo de Impulsos y Creencias Nucleares",
            notes = "El residente muestra introspección sobre los detonantes del consumo en el entorno laboral. Se trabajó reestructuración de pensamientos automáticos.",
            professionalName = "Psic. Mariana Flores",
            date = "2024-09-26",
            severity = "NORMAL"
        ),
        ClinicalRecordEntity(
            residentId = 2,
            residentName = "Alejandro Herrera Gómez",
            category = "PSIQUIATRIA",
            title = "Ajuste de esquema ansiolítico",
            notes = "Evolución favorable del cuadro depresivo reactivo. Se reduce Sertralina y se mantiene buena respuesta sin efectos adversos.",
            professionalName = "Dr. Guillermo Garza (Psiquiatra)",
            date = "2024-09-25",
            severity = "MODERADO"
        ),
        ClinicalRecordEntity(
            residentId = 1,
            residentName = "Rodrigo Morales Alarcón",
            category = "MEDICINA",
            title = "Chequeo de Signos Vitales y Prueba Rápida",
            notes = "TA: 120/80 mmHg, FC: 72 lpm, Glucosa: 94 mg/dL. Examen general de orina y panel de 5 drogas: NEGATIVO completo.",
            professionalName = "Dr. Armando Valdés (Médico General)",
            date = "2024-09-24",
            severity = "NORMAL"
        ),
        ClinicalRecordEntity(
            residentId = 2,
            residentName = "Alejandro Herrera Gómez",
            category = "INCIDENTE",
            title = "Desacato leve en hora de aseo de dormitorios",
            notes = "Discusión verbal con compañero sobre el turno de limpieza. Se intervino de inmediato con el consejero. Se llegó a acuerdo y reparación.",
            professionalName = "Padrino Roberto Silva",
            date = "2024-09-22",
            severity = "MODERADO"
        )
    )

    val medications = listOf(
        MedicationEntity(
            residentId = 2,
            residentName = "Alejandro Herrera Gómez",
            medicationName = "Sertralina 50mg",
            dosage = "1 tableta",
            scheduleTime = "08:00 AM",
            instructions = "Tomar después del desayuno con abundante agua",
            isTakenToday = true,
            prescribedBy = "Dr. Guillermo Garza"
        ),
        MedicationEntity(
            residentId = 2,
            residentName = "Alejandro Herrera Gómez",
            medicationName = "Complejo B + Tiamina 300mg",
            dosage = "1 cápsula",
            scheduleTime = "02:00 PM",
            instructions = "Para soporte neurotrófico de deshabituación",
            isTakenToday = false,
            prescribedBy = "Dr. Armando Valdés"
        ),
        MedicationEntity(
            residentId = 3,
            residentName = "Valeria Ríos Santillán",
            medicationName = "Clonazepam 0.5mg (Reducción gradual)",
            dosage = "1/2 tableta",
            scheduleTime = "08:00 PM",
            instructions = "Protocolo de retiro supervisado por enfermería",
            isTakenToday = false,
            prescribedBy = "Dr. Guillermo Garza"
        )
    )

    val transactions = listOf(
        FinanceTransactionEntity(
            type = "INGRESO",
            category = "PAGO_MENSUALIDAD",
            concept = "Mensualidad Septiembre - Residente Rodrigo Morales",
            amount = 8500.0,
            residentId = 1,
            residentName = "Rodrigo Morales Alarcón",
            date = "2024-09-15",
            paymentMethod = "TRANSFERENCIA",
            receiptNumber = "REC-2024-098"
        ),
        FinanceTransactionEntity(
            type = "INGRESO",
            category = "CUOTA_INGRESO",
            concept = "Pago anticipado 50% Mensualidad - Alejandro Herrera",
            amount = 4250.0,
            residentId = 2,
            residentName = "Alejandro Herrera Gómez",
            date = "2024-09-18",
            paymentMethod = "EFECTIVO",
            receiptNumber = "REC-2024-099"
        ),
        FinanceTransactionEntity(
            type = "EGRESO",
            category = "ALIMENTOS",
            concept = "Despensa y abarrotes para comedor semanal",
            amount = 5420.0,
            date = "2024-09-20",
            paymentMethod = "TARJETA",
            receiptNumber = "FAC-A4891"
        ),
        FinanceTransactionEntity(
            type = "EGRESO",
            category = "FARMACIA",
            concept = "Insumos médicos, pruebas antidoping y botiquín",
            amount = 2150.0,
            date = "2024-09-22",
            paymentMethod = "TRANSFERENCIA",
            receiptNumber = "FAC-B102"
        )
    )

    val agendaEvents = listOf(
        AgendaEventEntity(
            title = "Visita Familiar Dominical General",
            type = "VISITA_FAMILIAR",
            date = "2024-10-06",
            time = "10:00 AM - 02:00 PM",
            location = "Jardín Central y Sala de Usos Múltiples",
            description = "Convivencia familiar supervisada de residentes con permiso fase 2+",
            personInvolved = "Familias acreditadas"
        ),
        AgendaEventEntity(
            title = "Junta de Padres y Escuela para Familias",
            type = "REUNION",
            date = "2024-10-05",
            time = "11:00 AM",
            location = "Auditorio de Conferencias",
            description = "Tema: Límites sanos y codependencia en la recuperación",
            personInvolved = "Terapeutas y tutores"
        ),
        AgendaEventEntity(
            title = "Valoración Psiquiátrica Quincenal",
            type = "CITA_MEDICA",
            date = "2024-10-02",
            time = "04:00 PM",
            location = "Consultorio Médico",
            description = "Revisión de recetas controladas e historial clínico",
            personInvolved = "Dr. Garza & Residentes"
        ),
        AgendaEventEntity(
            title = "Supervisión Sanitaria y Protección Civil",
            type = "EVENTO",
            date = "2024-10-10",
            time = "09:30 AM",
            location = "Instalaciones Generales",
            description = "Verificación de bitácoras, salidas de emergencia y botiquines",
            personInvolved = "Inspector estatal"
        )
    )

    val staff = listOf(
        StaffMemberEntity(
            fullName = "Lic. Carlos Méndez Ortiz",
            role = "Director Clínico y Consejero Certificado",
            category = "PROFESIONAL",
            phone = "+52 55 1100 2233",
            email = "direccion@sendaclinica.org",
            shift = "Matutino / Tiempo Completo",
            cedulaProf = "CED-981240"
        ),
        StaffMemberEntity(
            fullName = "Dr. Armando Valdés Soto",
            role = "Médico Cirujano General",
            category = "PROFESIONAL",
            phone = "+52 55 4455 6677",
            email = "medicina@sendaclinica.org",
            shift = "Lunes a Viernes 08:00 - 14:00",
            cedulaProf = "CED-451299"
        ),
        StaffMemberEntity(
            fullName = "Psic. Mariana Flores Peñaloza",
            role = "Psicóloga Clínica y Adicciones",
            category = "PROFESIONAL",
            phone = "+52 55 7788 9900",
            email = "psicologia@sendaclinica.org",
            shift = "Martes, Jueves y Sábados",
            cedulaProf = "CED-839211"
        ),
        StaffMemberEntity(
            fullName = "Roberto Silva Tapia (Padrino)",
            role = "Consejero en Adicciones y Padrino Residente",
            category = "SERVIDOR",
            phone = "+52 55 6611 3344",
            email = "consejeria@sendaclinica.org",
            shift = "Residencia 24/7",
            cedulaProf = "Cert. CONADIC 2022"
        ),
        StaffMemberEntity(
            fullName = "Guadalupe Domínguez",
            role = "Jefa de Cocina y Nutrición",
            category = "EMPLEADO",
            phone = "+52 55 9922 4411",
            email = "cocina@sendaclinica.org",
            shift = "Matutino 07:00 - 15:30",
            cedulaProf = "Manejo Higiénico NMX"
        )
    )

    val operationLogs = listOf(
        OperationLogEntity(
            logType = "BITACORA_GUARDIA",
            title = "Rondín Nocturno 03:00 AM",
            staffName = "Roberto Silva",
            timestamp = "2024-09-28 03:15 AM",
            shift = "NOCTURNO",
            details = "Dormitorio A y B en completo orden y descanso. Perímetro y cerraduras aseguradas sin novedad.",
            status = "OK"
        ),
        OperationLogEntity(
            logType = "CONTROL_LLAVES",
            title = "Entrega de Llaves de Farmacia y Consultorio",
            staffName = "Enf. Lucía Pérez",
            timestamp = "2024-09-27 08:00 AM",
            shift = "MATUTINO",
            details = "Se reciben llaves de botiquín de controlados de manos de la guardia nocturna con inventario cuadrado.",
            status = "OK"
        ),
        OperationLogEntity(
            logType = "MANTENIMIENTO",
            title = "Fuga menor en tarja de cocina",
            staffName = "Guadalupe Domínguez",
            timestamp = "2024-09-26 12:40 PM",
            shift = "VESPERTINO",
            details = "Empaque de manguera flexible dañado. Se solicitó reemplazo con proveedor de ferretería.",
            status = "RESUELTO"
        )
    )

    val expedientes = listOf(
        ExpedienteEntity(
            id = 1,
            folioExpediente = "EXP-SND-2024-001",
            residentId = 1,
            residentName = "Rodrigo Morales Alarcón",
            fechaApertura = "2024-07-15",
            tipoIngreso = "VOLUNTARIO",
            diagnosticoPrincipal = "F10.2 Trastorno por dependencia al alcohol y F15.2 estimulantes",
            antecedentesPatologicos = "Alergia a penicilina. Gastritis reactiva controlada.",
            sustanciaDeImpacto = "Alcohol de alta graduación y estimulantes",
            tiempoDeConsumo = "5 años continuos con incremento en últimos 12 meses",
            planTratamiento = "Programa Residencial 180 días. Fases 1 a 4. TCC y 12 Pasos.",
            medicoTratante = "Dr. Armando Valdés Soto",
            psicologoResponsable = "Psic. Mariana Flores Peñaloza",
            estatusExpediente = "ACTIVO",
            consentimientoFirmado = true,
            notasIngreso = "Tutor responsable: Elena Alarcón Vega (Madre). Documentos de identidad y contrato validados."
        ),
        ExpedienteEntity(
            id = 2,
            folioExpediente = "EXP-SND-2024-002",
            residentId = 2,
            residentName = "Alejandro Herrera Gómez",
            fechaApertura = "2024-08-01",
            tipoIngreso = "VOLUNTARIO",
            diagnosticoPrincipal = "F15.2 Síndrome de dependencia a metanfetaminas (Cristal)",
            antecedentesPatologicos = "Sin antecedentes crónicos. Insomnio de conciliación en remisión.",
            sustanciaDeImpacto = "Metanfetamina / Cristal",
            tiempoDeConsumo = "2 años con episodios de pérdida de control",
            planTratamiento = "Desintoxicación médica supervisada, reestructuración cognitiva y manejo psiquiátrico.",
            medicoTratante = "Dr. Guillermo Garza (Psiquiatra)",
            psicologoResponsable = "Psic. Mariana Flores Peñaloza",
            estatusExpediente = "ACTIVO",
            consentimientoFirmado = true,
            notasIngreso = "Tutor responsable: Maricela Gómez (Esposa). Carta de consentimiento y responsiva firmada."
        ),
        ExpedienteEntity(
            id = 3,
            folioExpediente = "EXP-SND-2024-003",
            residentId = 3,
            residentName = "Valeria Ríos Santillán",
            fechaApertura = "2024-08-18",
            tipoIngreso = "VOLUNTARIO",
            diagnosticoPrincipal = "F13.2 Dependencia a sedantes/benzodiacepinas y alcohol",
            antecedentesPatologicos = "Alergia a sulfas. Sin otras comorbilidades.",
            sustanciaDeImpacto = "Clonazepam automedicado y consumo de alcohol",
            tiempoDeConsumo = "3 años",
            planTratamiento = "Desescalamiento farmacológico gradual, arteterapia y prevención de recaídas.",
            medicoTratante = "Dr. Armando Valdés Soto",
            psicologoResponsable = "Psic. Mariana Flores Peñaloza",
            estatusExpediente = "ACTIVO",
            consentimientoFirmado = true,
            notasIngreso = "Tutor responsable: Dr. Javier Ríos (Padre). Protocolo clínico de confidencialidad aceptado."
        ),
        ExpedienteEntity(
            id = 4,
            folioExpediente = "EXP-SND-2024-004",
            residentId = 4,
            residentName = "Mateo Estrada Quintana",
            fechaApertura = "2024-09-24",
            tipoIngreso = "VOLUNTARIO",
            diagnosticoPrincipal = "F12.2 Consumo perjudicial de cannabis con alteración conductual",
            antecedentesPatologicos = "Negados.",
            sustanciaDeImpacto = "Cannabis y apuestas en línea",
            tiempoDeConsumo = "2 años",
            planTratamiento = "Valoración integral en preingreso, deshabituación y terapia familiar.",
            medicoTratante = "Dr. Armando Valdés Soto",
            psicologoResponsable = "Lic. Carlos Méndez",
            estatusExpediente = "EN_REVISION",
            consentimientoFirmado = true,
            notasIngreso = "En proceso de entrevista inicial y recepción de analíticas sanguíneas."
        )
    )

    val administrativeRecords = listOf(
        AdministrativeRecordEntity(
            id = 1,
            folio = "ADM-CNT-2024-001",
            category = "CONTRATO_INGRESO",
            residentId = 1,
            residentName = "Rodrigo Morales Luna",
            title = "Contrato de Prestación de Servicios Residenciales",
            description = "Contrato formal de 180 días que ampara estancia, alimentación, atención médica, psiquiátrica y consejería.",
            responsibleStaff = "Lic. Carlos Méndez (Director)",
            date = "2024-06-15",
            status = "FIRMADO",
            documentNumber = "NOM-028-SND-01",
            notes = "Firmado en original por el residente y su tutora Sra. Elena Morales."
        ),
        AdministrativeRecordEntity(
            id = 2,
            folio = "ADM-RES-2024-001",
            category = "RESGUARDO_VALORES",
            residentId = 1,
            residentName = "Rodrigo Morales Luna",
            title = "Inventario y Resguardo de Pertenencias y Valores",
            description = "Custodia de teléfono móvil, cartera con identificaciones y reloj de pulso en caja de seguridad institucional.",
            responsibleStaff = "Padrino Roberto Silva (Consejero)",
            date = "2024-06-15",
            status = "VIGENTE",
            documentNumber = "VAL-2024-88",
            notes = "Artículos inventariados y sellados en bolsa numerada #088."
        ),
        AdministrativeRecordEntity(
            id = 3,
            folio = "ADM-CST-2024-002",
            category = "CONSENTIMIENTO_TUTOR",
            residentId = 2,
            residentName = "Alejandro Herrera Gómez",
            title = "Consentimiento Informado y Carta Compromiso Familiar",
            description = "Aceptación de reglamento interno, visitas dominicales quincenales y corresponsabilidad económica.",
            responsibleStaff = "Lic. Carlos Méndez (Director)",
            date = "2024-08-01",
            status = "FIRMADO",
            documentNumber = "NOM-028-SND-02",
            notes = "Aceptado y firmado por Maricela Gómez (Esposa y tutora legal)."
        ),
        AdministrativeRecordEntity(
            id = 4,
            folio = "ADM-ACT-2024-003",
            category = "SUPERVISION_OFICIAL",
            residentId = null,
            residentName = "Centro Senda Residencial",
            title = "Acta de Verificación Sanitaria y Protección Civil",
            description = "Dictamen favorable de instalaciones, dormitorios, área médica y plan de protección civil anual.",
            responsibleStaff = "Dr. Armando Valdés Soto",
            date = "2024-09-10",
            status = "VIGENTE",
            documentNumber = "COFEPRIS-PC-2024-541",
            notes = "Inspección aprobada sin observaciones críticas. Próxima revisión anual Sep 2025."
        )
    )
}
