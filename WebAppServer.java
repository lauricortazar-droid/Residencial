import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Servidor Web App Java Independiente y Backend REST para SENDA Residencial.
 * Diseñado como alternativa web completa para Hostinger (VPS / Cloud / Servidor Dedicado).
 *
 * Dominio institucional: https://senda.fgdll.org
 * Guía hPanel Git: https://hpanel.hostinger.com/git-auto-deployments-guide/senda.fgdll.org/github/
 *
 * Ejecución en Hostinger:
 *   java WebAppServer.java [puerto] (por defecto: 8080 o variable de entorno PORT)
 */
public class WebAppServer {

    private static final String TARGET_DOMAIN = "https://senda.fgdll.org";
    private static final String HPANEL_GUIDE = "https://hpanel.hostinger.com/git-auto-deployments-guide/senda.fgdll.org/github/";
    private static final int DEFAULT_PORT = 8080;

    // Almacenamiento en memoria concurrente para la Web App
    private static final List<Map<String, Object>> expedientesStore = new CopyOnWriteArrayList<>();
    private static final List<Map<String, Object>> residentsStore = new CopyOnWriteArrayList<>();
    private static final List<Map<String, Object>> farmacosStore = new CopyOnWriteArrayList<>();
    private static final List<Map<String, Object>> finanzasStore = new CopyOnWriteArrayList<>();
    private static final List<Map<String, Object>> bitacoraStore = new CopyOnWriteArrayList<>();

    static {
        // Semilla inicial de datos para la Web App (NOM-028)
        Map<String, Object> exp1 = new LinkedHashMap<>();
        exp1.put("id", 1);
        exp1.put("folioExpediente", "EXP-SND-2024-001");
        exp1.put("residentId", 1);
        exp1.put("residentName", "Carlos Alberto Garza Vega");
        exp1.put("fechaApertura", "2024-01-15");
        exp1.put("tipoIngreso", "VOLUNTARIO");
        exp1.put("diagnosticoPrincipal", "F10.2 Dependencia al Alcohol");
        exp1.put("sustanciaDeImpacto", "Alcohol etílico");
        exp1.put("tiempoDeConsumo", "6 años");
        exp1.put("planTratamiento", "Comunidad Terapéutica 6 meses (NOM-028)");
        exp1.put("medicoTratante", "Dr. Armando Valdés Soto");
        exp1.put("psicologoResponsable", "Lic. Elena Cárdenas");
        exp1.put("estatusExpediente", "ACTIVO");
        exp1.put("consentimientoFirmado", true);
        exp1.put("notasIngreso", "Ingreso voluntario acompañado de familiar responsable.");
        expedientesStore.add(exp1);

        Map<String, Object> exp2 = new LinkedHashMap<>();
        exp2.put("id", 2);
        exp2.put("folioExpediente", "EXP-SND-2024-002");
        exp2.put("residentId", 2);
        exp2.put("residentName", "Mateo Sebastián Ríos Morales");
        exp2.put("fechaApertura", "2024-02-01");
        exp2.put("tipoIngreso", "VOLUNTARIO");
        exp2.put("diagnosticoPrincipal", "F15.2 Dependencia a Metanfetaminas");
        exp2.put("sustanciaDeImpacto", "Metanfetamina / Cristal");
        exp2.put("tiempoDeConsumo", "3 años");
        exp2.put("planTratamiento", "Desintoxicación y Terapia Cognitivo-Conductual");
        exp2.put("medicoTratante", "Dr. Armando Valdés Soto");
        exp2.put("psicologoResponsable", "Lic. Roberto Mendoza");
        exp2.put("estatusExpediente", "ACTIVO");
        exp2.put("consentimientoFirmado", true);
        exp2.put("notasIngreso", "Fase 2 de reintegración social iniciada.");
        expedientesStore.add(exp2);

        // Residentes
        Map<String, Object> res1 = new LinkedHashMap<>();
        res1.put("id", 1);
        res1.put("nombre", "Carlos Alberto Garza Vega");
        res1.put("edad", 34);
        res1.put("cama", "101-A");
        res1.put("fechaIngreso", "2024-01-15");
        res1.put("estatus", "INTERNADO");
        res1.put("tutor", "Martha Vega (Madre) - Tel: 55-4123-8890");
        residentsStore.add(res1);

        Map<String, Object> res2 = new LinkedHashMap<>();
        res2.put("id", 2);
        res2.put("nombre", "Mateo Sebastián Ríos Morales");
        res2.put("edad", 28);
        res2.put("cama", "102-B");
        res2.put("fechaIngreso", "2024-02-01");
        res2.put("estatus", "INTERNADO");
        res2.put("tutor", "Esteban Ríos (Hermano) - Tel: 55-9871-2345");
        residentsStore.add(res2);

        Map<String, Object> res3 = new LinkedHashMap<>();
        res3.put("id", 3);
        res3.put("nombre", "Jorge Emilio Silva Navarro");
        res3.put("edad", 41);
        res3.put("cama", "103-A");
        res3.put("fechaIngreso", "2024-02-20");
        res3.put("estatus", "INTERNADO");
        res3.put("tutor", "Lucía Navarro (Esposa) - Tel: 55-1122-3344");
        residentsStore.add(res3);

        // Fármacos
        Map<String, Object> far1 = new LinkedHashMap<>();
        far1.put("id", 1);
        far1.put("residente", "Carlos Alberto Garza Vega");
        far1.put("medicamento", "Complejo B + Tiamina 100mg");
        far1.put("dosis", "1 tableta cada 24 hrs");
        far1.put("horario", "08:00");
        far1.put("estatus", "APLICADO");
        farmacosStore.add(far1);

        Map<String, Object> far2 = new LinkedHashMap<>();
        far2.put("id", 2);
        far2.put("residente", "Mateo Sebastián Ríos Morales");
        far2.put("medicamento", "Clonazepam 0.5mg");
        far2.put("dosis", "1/2 tableta en caso de ansiedad");
        far2.put("horario", "20:00");
        far2.put("estatus", "PENDIENTE");
        farmacosStore.add(far2);

        // Finanzas
        Map<String, Object> fin1 = new LinkedHashMap<>();
        fin1.put("id", 1);
        fin1.put("concepto", "Cuota Mensual Recuperación (Carlos Garza)");
        fin1.put("tipo", "INGRESO");
        fin1.put("monto", 8500.0);
        fin1.put("fecha", "2024-03-01");
        finanzasStore.add(fin1);

        Map<String, Object> fin2 = new LinkedHashMap<>();
        fin2.put("id", 2);
        fin2.put("concepto", "Insumos Médicos y Laboratorio Clínico");
        fin2.put("tipo", "EGRESO");
        fin2.put("monto", 2350.0);
        fin2.put("fecha", "2024-03-05");
        finanzasStore.add(fin2);

        // Bitácora
        Map<String, Object> bit1 = new LinkedHashMap<>();
        bit1.put("id", 1);
        bit1.put("turno", "MATUTINO (07:00 - 15:00)");
        bit1.put("responsable", "Enf. Rodrigo Montes");
        bit1.put("nota", "Pase de lista sin novedades. Residentes en sesión de psicoterapia grupal.");
        bit1.put("fecha", "2024-03-29 08:30");
        bitacoraStore.add(bit1);
    }

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.isEmpty()) {
            try {
                port = Integer.parseInt(envPort);
            } catch (NumberFormatException ignored) {}
        } else if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Rutas estáticas de la Web App
        server.createContext("/", new StaticFileHandler());

        // API REST Clínica y Administrativa en Java
        server.createContext("/api/health", new HealthHandler());
        server.createContext("/api/info", new InfoHandler());
        server.createContext("/api/deploy", new WebhookDeployHandler());
        server.createContext("/api/expedientes", new ExpedientesApiHandler());
        server.createContext("/api/expedientes/export", new ExpedientesExportHandler());
        server.createContext("/api/residents", new ResidentsApiHandler());
        server.createContext("/api/farmacos", new FarmacosApiHandler());
        server.createContext("/api/finanzas", new FinanzasApiHandler());
        server.createContext("/api/bitacora", new BitacoraApiHandler());

        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        server.start();

        System.out.println("==================================================================");
        System.out.println("  SENDA RESIDENCIAL - SERVIDOR WEB APP JAVA PARA HOSTINGER");
        System.out.println("  Dominio: " + TARGET_DOMAIN);
        System.out.println("  Guía hPanel Git: " + HPANEL_GUIDE);
        System.out.println("  Puerto activo: " + port);
        System.out.println("  Rutas REST:");
        System.out.println("    - GET /api/health");
        System.out.println("    - GET /api/expedientes (CRUD Clínico NOM-028)");
        System.out.println("    - GET /api/expedientes/export?format=csv|json");
        System.out.println("    - GET /api/residents (Censo)");
        System.out.println("    - GET /api/farmacos (Farmacia y Enfermería)");
        System.out.println("    - GET /api/finanzas (Ingresos y Egresos)");
        System.out.println("    - GET /api/bitacora (Guardia e Incidencias)");
        System.out.println("==================================================================");
    }

    /**
     * Handler para servir la Web App Desktop & Responsive
     */
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path == null || path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }

            File file = new File("public_html" + path);
            if (!file.exists()) {
                file = new File("." + path);
            }
            if (!file.exists()) {
                file = new File("index.html");
            }

            if (file.exists() && !file.isDirectory()) {
                byte[] bytes = Files.readAllBytes(file.toPath());
                String contentType = getContentType(file.getName());
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } else {
                String notFound = "<h1>404 Not Found - Senda Residencial Web App</h1>";
                byte[] bytes = notFound.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(404, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
        }

        private String getContentType(String filename) {
            if (filename.endsWith(".html")) return "text/html; charset=UTF-8";
            if (filename.endsWith(".css")) return "text/css; charset=UTF-8";
            if (filename.endsWith(".js")) return "application/javascript; charset=UTF-8";
            if (filename.endsWith(".json")) return "application/json; charset=UTF-8";
            if (filename.endsWith(".png")) return "image/png";
            if (filename.endsWith(".svg")) return "image/svg+xml";
            return "text/plain; charset=UTF-8";
        }
    }

    /**
     * API REST de Expedientes Clínicos
     */
    static class ExpedientesApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCORS(exchange);
            String method = exchange.getRequestMethod().toUpperCase();

            if ("OPTIONS".equals(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("GET".equals(method)) {
                String json = toJson(expedientesStore);
                sendJsonResponse(exchange, 200, json);
            } else if ("POST".equals(method)) {
                String body = readBody(exchange);
                Map<String, Object> nuevo = parseJsonMap(body);
                int newId = expedientesStore.size() + 1;
                nuevo.put("id", newId);
                if (!nuevo.containsKey("folioExpediente")) {
                    nuevo.put("folioExpediente", "EXP-SND-2024-" + String.format("%03d", newId));
                }
                if (!nuevo.containsKey("fechaApertura")) {
                    nuevo.put("fechaApertura", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
                }
                if (!nuevo.containsKey("estatusExpediente")) {
                    nuevo.put("estatusExpediente", "ACTIVO");
                }
                expedientesStore.add(0, nuevo);
                sendJsonResponse(exchange, 201, "{\"success\":true,\"message\":\"Expediente creado en Hostinger Web App\",\"id\":" + newId + "}");
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    /**
     * Exportador CSV y JSON en el servidor Java
     */
    static class ExpedientesExportHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCORS(exchange);
            String query = exchange.getRequestURI().getQuery();
            String format = "json";
            if (query != null && query.contains("format=csv")) {
                format = "csv";
            }

            if ("csv".equalsIgnoreCase(format)) {
                StringBuilder sb = new StringBuilder();
                sb.append("ID,Folio_Expediente,ID_Residente,Nombre_Residente,Fecha_Apertura,Tipo_Ingreso,Diagnostico_Principal_CIE,Sustancia_De_Impacto,Tiempo_De_Consumo,Plan_Tratamiento,Medico_Tratante,Psicologo_Responsable,Estatus_Expediente,Consentimiento_Firmado,Notas_Ingreso\r\n");
                for (Map<String, Object> exp : expedientesStore) {
                    sb.append(exp.getOrDefault("id", "")).append(",")
                      .append(escapeCsv((String) exp.getOrDefault("folioExpediente", ""))).append(",")
                      .append(exp.getOrDefault("residentId", "1")).append(",")
                      .append(escapeCsv((String) exp.getOrDefault("residentName", ""))).append(",")
                      .append(escapeCsv((String) exp.getOrDefault("fechaApertura", ""))).append(",")
                      .append(escapeCsv((String) exp.getOrDefault("tipoIngreso", "VOLUNTARIO"))).append(",")
                      .append(escapeCsv((String) exp.getOrDefault("diagnosticoPrincipal", ""))).append(",")
                      .append(escapeCsv((String) exp.getOrDefault("sustanciaDeImpacto", ""))).append(",")
                      .append(escapeCsv((String) exp.getOrDefault("tiempoDeConsumo", ""))).append(",")
                      .append(escapeCsv((String) exp.getOrDefault("planTratamiento", ""))).append(",")
                      .append(escapeCsv((String) exp.getOrDefault("medicoTratante", ""))).append(",")
                      .append(escapeCsv((String) exp.getOrDefault("psicologoResponsable", ""))).append(",")
                      .append(escapeCsv((String) exp.getOrDefault("estatusExpediente", "ACTIVO"))).append(",")
                      .append((Boolean.TRUE.equals(exp.get("consentimientoFirmado"))) ? "SI" : "NO").append(",")
                      .append(escapeCsv((String) exp.getOrDefault("notasIngreso", ""))).append("\r\n");
                }
                byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/csv; charset=UTF-8");
                exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"expedientes_senda_hostinger.csv\"");
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } else {
                Map<String, Object> exportMap = new LinkedHashMap<>();
                exportMap.put("institucion", "SENDA RESIDENCIAL - Hostinger Web App");
                exportMap.put("normativa", "NOM-028-SSA2-2009");
                exportMap.put("timestamp", new Date().toString());
                exportMap.put("totalRegistros", expedientesStore.size());
                exportMap.put("expedientes", expedientesStore);

                String json = toJson(exportMap);
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"expedientes_senda_hostinger.json\"");
                byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
        }
    }

    /**
     * API REST de Censo de Residentes
     */
    static class ResidentsApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCORS(exchange);
            String method = exchange.getRequestMethod().toUpperCase();
            if ("GET".equals(method)) {
                sendJsonResponse(exchange, 200, toJson(residentsStore));
            } else if ("POST".equals(method)) {
                String body = readBody(exchange);
                Map<String, Object> res = parseJsonMap(body);
                res.put("id", residentsStore.size() + 1);
                residentsStore.add(0, res);
                sendJsonResponse(exchange, 201, "{\"success\":true,\"message\":\"Residente registrado con éxito\"}");
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    /**
     * API REST de Control de Fármacos y Enfermería
     */
    static class FarmacosApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCORS(exchange);
            String method = exchange.getRequestMethod().toUpperCase();
            if ("GET".equals(method)) {
                sendJsonResponse(exchange, 200, toJson(farmacosStore));
            } else if ("POST".equals(method)) {
                String body = readBody(exchange);
                Map<String, Object> item = parseJsonMap(body);
                // Si viene id para marcar como aplicado
                if (item.containsKey("id")) {
                    int id = ((Number) item.get("id")).intValue();
                    for (Map<String, Object> f : farmacosStore) {
                        if (Objects.equals(f.get("id"), id)) {
                            f.put("estatus", "APLICADO");
                            f.put("horaAplicacion", new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                            break;
                        }
                    }
                    sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Dosis marcada como administrada\"}");
                } else {
                    item.put("id", farmacosStore.size() + 1);
                    farmacosStore.add(item);
                    sendJsonResponse(exchange, 201, "{\"success\":true,\"message\":\"Prescripción registrada\"}");
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    /**
     * API REST de Finanzas y Pagos
     */
    static class FinanzasApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCORS(exchange);
            String method = exchange.getRequestMethod().toUpperCase();
            if ("GET".equals(method)) {
                double totalIngresos = 0;
                double totalEgresos = 0;
                for (Map<String, Object> t : finanzasStore) {
                    double m = ((Number) t.getOrDefault("monto", 0.0)).doubleValue();
                    if ("INGRESO".equals(t.get("tipo"))) totalIngresos += m;
                    else totalEgresos += m;
                }
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("totalIngresos", totalIngresos);
                response.put("totalEgresos", totalEgresos);
                response.put("saldoActual", totalIngresos - totalEgresos);
                response.put("transacciones", finanzasStore);
                sendJsonResponse(exchange, 200, toJson(response));
            } else if ("POST".equals(method)) {
                String body = readBody(exchange);
                Map<String, Object> trans = parseJsonMap(body);
                trans.put("id", finanzasStore.size() + 1);
                trans.put("fecha", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
                finanzasStore.add(0, trans);
                sendJsonResponse(exchange, 201, "{\"success\":true,\"message\":\"Movimiento financiero registrado\"}");
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    /**
     * API REST de Bitácora de Guardia
     */
    static class BitacoraApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCORS(exchange);
            String method = exchange.getRequestMethod().toUpperCase();
            if ("GET".equals(method)) {
                sendJsonResponse(exchange, 200, toJson(bitacoraStore));
            } else if ("POST".equals(method)) {
                String body = readBody(exchange);
                Map<String, Object> nota = parseJsonMap(body);
                nota.put("id", bitacoraStore.size() + 1);
                nota.put("fecha", new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date()));
                bitacoraStore.add(0, nota);
                sendJsonResponse(exchange, 201, "{\"success\":true,\"message\":\"Nota de guardia registrada\"}");
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    /**
     * Health check endpoint
     */
    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCORS(exchange);
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            String json = "{\n"
                    + "  \"status\": \"UP\",\n"
                    + "  \"app\": \"Senda Residencial - Web App Java Alternativa\",\n"
                    + "  \"domain\": \"" + TARGET_DOMAIN + "\",\n"
                    + "  \"platform\": \"Hostinger Cloud / VPS\",\n"
                    + "  \"totalExpedientes\": " + expedientesStore.size() + ",\n"
                    + "  \"totalResidentes\": " + residentsStore.size() + ",\n"
                    + "  \"timestamp\": \"" + now + "\"\n"
                    + "}";
            sendJsonResponse(exchange, 200, json);
        }
    }

    /**
     * Webhook de auto-deploy en Hostinger hPanel
     */
    static class WebhookDeployHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCORS(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            System.out.println("[Deploy Webhook] Evento recibido para " + TARGET_DOMAIN);
            String gitOutput = executeGitPull();

            String response = "{\n"
                    + "  \"success\": true,\n"
                    + "  \"message\": \"Git auto-deployment completado con éxito en senda.fgdll.org\",\n"
                    + "  \"target\": \"" + TARGET_DOMAIN + "\",\n"
                    + "  \"git_log\": \"" + gitOutput.replace("\"", "\\\"").replace("\n", "\\n") + "\"\n"
                    + "}";

            sendJsonResponse(exchange, 200, response);
        }

        private String executeGitPull() {
            try {
                ProcessBuilder pb = new ProcessBuilder("git", "pull", "origin", "main");
                pb.redirectErrorStream(true);
                Process process = pb.start();
                StringBuilder output = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }
                process.waitFor();
                return output.toString().trim();
            } catch (Exception e) {
                return "Auto-deploy procesado: " + e.getMessage();
            }
        }
    }

    /**
     * Información institucional y guía hPanel
     */
    static class InfoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCORS(exchange);
            String json = "{\n"
                    + "  \"platform\": \"Hostinger hPanel Git Auto Deployments\",\n"
                    + "  \"guide\": \"" + HPANEL_GUIDE + "\",\n"
                    + "  \"domain\": \"" + TARGET_DOMAIN + "\",\n"
                    + "  \"branch\": \"main\",\n"
                    + "  \"type\": \"Java Full-Stack ERP Web Workstation\"\n"
                    + "}";
            sendJsonResponse(exchange, 200, json);
        }
    }

    // ==========================================
    // MÉTODOS DE APOYO HTTP Y JSON EN JAVA PURO
    // ==========================================

    private static void setCORS(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization, X-GitHub-Event");
    }

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private static String escapeCsv(String val) {
        if (val == null) return "";
        if (val.contains(",") || val.contains("\"") || val.contains("\n") || val.contains("\r")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    @SuppressWarnings("unchecked")
    private static String toJson(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String) return "\"" + escapeJson((String) obj) + "\"";
        if (obj instanceof Number || obj instanceof Boolean) return obj.toString();
        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                sb.append(toJson(list.get(i)));
                if (i < list.size() - 1) sb.append(",");
            }
            sb.append("]");
            return sb.toString();
        }
        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            StringBuilder sb = new StringBuilder("{");
            int count = 0;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                sb.append("\"").append(escapeJson(entry.getKey().toString())).append("\":");
                sb.append(toJson(entry.getValue()));
                count++;
                if (count < map.size()) sb.append(",");
            }
            sb.append("}");
            return sb.toString();
        }
        return "\"" + escapeJson(obj.toString()) + "\"";
    }

    /**
     * Parser JSON simple en Java puro para peticiones REST
     */
    private static Map<String, Object> parseJsonMap(String json) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (json == null || json.trim().isEmpty()) return map;
        String trimmed = json.trim();
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1).trim();
        }

        // Divide propiedades de primer nivel simples
        String[] pairs = trimmed.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            String[] kv = pair.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", 2);
            if (kv.length == 2) {
                String key = kv[0].trim().replace("\"", "");
                String rawVal = kv[1].trim();
                if (rawVal.startsWith("\"") && rawVal.endsWith("\"")) {
                    map.put(key, rawVal.substring(1, rawVal.length() - 1));
                } else if ("true".equalsIgnoreCase(rawVal)) {
                    map.put(key, true);
                } else if ("false".equalsIgnoreCase(rawVal)) {
                    map.put(key, false);
                } else {
                    try {
                        if (rawVal.contains(".")) {
                            map.put(key, Double.parseDouble(rawVal));
                        } else {
                            map.put(key, Integer.parseInt(rawVal));
                        }
                    } catch (NumberFormatException e) {
                        map.put(key, rawVal);
                    }
                }
            }
        }
        return map;
    }
}
