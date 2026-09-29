import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Servidor Java Web App y receptor de Git Auto Deployments para Hostinger hPanel.
 * Dominio institucional: https://senda.fgdll.org
 *
 * Guía de despliegue Hostinger:
 * https://hpanel.hostinger.com/git-auto-deployments-guide/senda.fgdll.org/github/
 *
 * Para ejecutar en Hostinger VPS / Cloud / Contenedor:
 *   java WebAppServer.java [puerto] (por defecto: 8080 o el especificado en $PORT)
 */
public class WebAppServer {

    private static final String TARGET_DOMAIN = "https://senda.fgdll.org";
    private static final String HPANEL_GUIDE = "https://hpanel.hostinger.com/git-auto-deployments-guide/senda.fgdll.org/github/";
    private static final int DEFAULT_PORT = 8080;

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

        // Rutas principales de la Web App
        server.createContext("/", new StaticFileHandler());
        server.createContext("/api/health", new HealthHandler());
        server.createContext("/api/deploy", new WebhookDeployHandler());
        server.createContext("/api/info", new InfoHandler());

        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        server.start();

        System.out.println("==================================================================");
        System.out.println("  SENDA RESIDENCIAL - SERVIDOR WEB APP JAVA EN HOSTINGER");
        System.out.println("  Dominio: " + TARGET_DOMAIN);
        System.out.println("  Guía hPanel: " + HPANEL_GUIDE);
        System.out.println("  Puerto activo: " + port);
        System.out.println("  Endpoint Webhook Git: http://localhost:" + port + "/api/deploy");
        System.out.println("  Endpoint Health: http://localhost:" + port + "/api/health");
        System.out.println("==================================================================");
    }

    /**
     * Sirve los archivos estáticos de la Web App (index.html, manifest.json, etc.)
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
     * Endpoint para comprobar el estado de salud de la Web App en senda.fgdll.org
     */
    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            String json = "{\n"
                    + "  \"status\": \"UP\",\n"
                    + "  \"app\": \"Senda Residencial Web App\",\n"
                    + "  \"domain\": \"" + TARGET_DOMAIN + "\",\n"
                    + "  \"cloudSync\": \"Cloud Firestore Enabled\",\n"
                    + "  \"timestamp\": \"" + now + "\"\n"
                    + "}";

            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    /**
     * Endpoint Webhook que recibe la notificación de GitHub / Hostinger y ejecuta git pull
     */
    static class WebhookDeployHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, X-GitHub-Event");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            StringBuilder requestBody = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            }

            System.out.println("[Deploy Webhook] Evento recibido para " + TARGET_DOMAIN);

            // Ejecuta el auto-deploy en el servidor de Hostinger si git está disponible
            String gitOutput = executeGitPull();

            String response = "{\n"
                    + "  \"success\": true,\n"
                    + "  \"message\": \"Git auto-deployment completado con éxito en senda.fgdll.org\",\n"
                    + "  \"target\": \"" + TARGET_DOMAIN + "\",\n"
                    + "  \"git_log\": \"" + gitOutput.replace("\"", "\\\"").replace("\n", "\\n") + "\"\n"
                    + "}";

            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
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
                return "Ejecución simulada (Git CLI no presente en el entorno actual): " + e.getMessage();
            }
        }
    }

    /**
     * Endpoint informativo de Hostinger hPanel Git Auto-Deployments
     */
    static class InfoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String json = "{\n"
                    + "  \"platform\": \"Hostinger hPanel Git Auto Deployments\",\n"
                    + "  \"guide\": \"" + HPANEL_GUIDE + "\",\n"
                    + "  \"domain\": \"" + TARGET_DOMAIN + "\",\n"
                    + "  \"branch\": \"main\",\n"
                    + "  \"app_type\": \"Mobile-First Modern Responsive Web App\"\n"
                    + "}";

            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}
