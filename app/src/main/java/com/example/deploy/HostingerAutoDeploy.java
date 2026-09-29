package com.example.deploy;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Cliente y manejador en Java para la integración de Git Auto Deployments en Hostinger hPanel
 * para el dominio/subdominio institucional: senda.fgdll.org
 *
 * Guía de referencia en hPanel:
 * https://hpanel.hostinger.com/git-auto-deployments-guide/senda.fgdll.org/github/
 */
public class HostingerAutoDeploy {

    private static final String TAG = "HostingerAutoDeploy";

    public static final String TARGET_DOMAIN = "https://senda.fgdll.org";
    public static final String HPANEL_GUIDE_URL = "https://hpanel.hostinger.com/git-auto-deployments-guide/senda.fgdll.org/github/";
    public static final String DEFAULT_BRANCH = "main";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private String webhookUrl;
    private String webhookSecretToken;
    private String gitRepositoryUrl;

    private void postToMain(Runnable runnable) {
        try {
            Looper looper = Looper.getMainLooper();
            if (looper != null) {
                new Handler(looper).post(runnable);
                return;
            }
        } catch (Throwable ignored) {
            // Fallback for non-Android JVM environments
        }
        runnable.run();
    }

    public interface DeployCallback {
        void onSuccess(int responseCode, String responseBody);
        void onError(Exception exception);
    }

    public interface HealthCallback {
        void onResult(boolean isOnline, int statusCode, long responseTimeMs);
    }

    public HostingerAutoDeploy() {
        this.webhookUrl = "";
        this.webhookSecretToken = "";
        this.gitRepositoryUrl = "https://github.com/senda-residencial/senda-erp.git";
    }

    public HostingerAutoDeploy(String webhookUrl, String webhookSecretToken) {
        this.webhookUrl = webhookUrl;
        this.webhookSecretToken = webhookSecretToken;
        this.gitRepositoryUrl = "https://github.com/senda-residencial/senda-erp.git";
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getWebhookSecretToken() {
        return webhookSecretToken;
    }

    public void setWebhookSecretToken(String webhookSecretToken) {
        this.webhookSecretToken = webhookSecretToken;
    }

    public String getGitRepositoryUrl() {
        return gitRepositoryUrl;
    }

    public void setGitRepositoryUrl(String gitRepositoryUrl) {
        this.gitRepositoryUrl = gitRepositoryUrl;
    }

    /**
     * Dispara el Webhook de despliegue automático configurado en Hostinger hPanel.
     * Envía una petición HTTP POST con el payload estructurado de GitHub.
     */
    public void triggerDeployment(final DeployCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    String targetEndpoint = (webhookUrl != null && !webhookUrl.trim().isEmpty())
                            ? webhookUrl
                            : "https://senda.fgdll.org/deploy-webhook.php";

                    Log.d(TAG, "Iniciando despliegue hacia: " + targetEndpoint);

                    URL url = new URL(targetEndpoint);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(15000);
                    connection.setReadTimeout(20000);
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    connection.setRequestProperty("User-Agent", "GitHub-Hookshot/HostingerAutoDeploy-Java");
                    connection.setRequestProperty("X-GitHub-Event", "push");

                    if (webhookSecretToken != null && !webhookSecretToken.isEmpty()) {
                        connection.setRequestProperty("X-Hub-Signature-256", "sha256=" + webhookSecretToken);
                    }

                    // Payload simulando GitHub Push Event para el branch main en senda.fgdll.org
                    String jsonPayload = "{"
                            + "\"ref\":\"refs/heads/" + DEFAULT_BRANCH + "\","
                            + "\"repository\":{"
                            + "\"name\":\"senda-erp\","
                            + "\"full_name\":\"senda.fgdll.org\","
                            + "\"clone_url\":\"" + gitRepositoryUrl + "\","
                            + "\"html_url\":\"https://senda.fgdll.org\""
                            + "},"
                            + "\"pusher\":{"
                            + "\"name\":\"senda-admin\","
                            + "\"email\":\"admin@senda.fgdll.org\""
                            + "},"
                            + "\"head_commit\":{"
                            + "\"message\":\"Deploy automático hPanel Hostinger para senda.fgdll.org\","
                            + "\"timestamp\":\"" + System.currentTimeMillis() + "\""
                            + "}"
                            + "}";

                    byte[] postData = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    connection.setFixedLengthStreamingMode(postData.length);

                    try (OutputStream os = connection.getOutputStream()) {
                        os.write(postData);
                        os.flush();
                    }

                    final int responseCode = connection.getResponseCode();
                    StringBuilder responseBuilder = new StringBuilder();

                    try (BufferedReader br = new BufferedReader(new InputStreamReader(
                            (responseCode >= 200 && responseCode < 400)
                                    ? connection.getInputStream()
                                    : connection.getErrorStream(),
                            StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            responseBuilder.append(line).append("\n");
                        }
                    }

                    final String responseBody = responseBuilder.toString().trim();
                    Log.i(TAG, "Despliegue completado con código HTTP: " + responseCode);

                    if (callback != null) {
                        postToMain(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(responseCode, responseBody);
                            }
                        });
                    }
                } catch (final Exception e) {
                    Log.e(TAG, "Fallo al ejecutar despliegue en Hostinger: " + e.getMessage(), e);
                    if (callback != null) {
                        postToMain(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError(e);
                            }
                        });
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        });
    }

    /**
     * Comprueba la disponibilidad en línea de https://senda.fgdll.org
     */
    public void checkDomainHealth(final HealthCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(TARGET_DOMAIN);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    connection.setRequestProperty("User-Agent", "HostingerAutoDeployHealthChecker/1.0");

                    int responseCode = connection.getResponseCode();
                    long elapsed = System.currentTimeMillis() - startTime;
                    final boolean isOnline = (responseCode >= 200 && responseCode < 400);

                    if (callback != null) {
                        final int finalCode = responseCode;
                        final long finalElapsed = elapsed;
                        postToMain(new Runnable() {
                            @Override
                            public void run() {
                                callback.onResult(isOnline, finalCode, finalElapsed);
                            }
                        });
                    }
                } catch (Exception e) {
                    final long elapsed = System.currentTimeMillis() - startTime;
                    if (callback != null) {
                        postToMain(new Runnable() {
                            @Override
                            public void run() {
                                callback.onResult(false, 0, elapsed);
                            }
                        });
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        });
    }

    /**
     * Retorna la guía paso a paso para configurar el Webhook en GitHub hacia Hostinger hPanel.
     */
    public static String getSetupInstructions() {
        return "=== GUÍA DE CONFIGURACIÓN GIT AUTO DEPLOYMENTS (HOSTINGER hPanel) ===\n\n"
                + "1. Accede a tu hPanel de Hostinger:\n"
                + "   " + HPANEL_GUIDE_URL + "\n\n"
                + "2. En la sección 'Avanzado' -> 'Git' selecciona el repositorio para el dominio 'senda.fgdll.org'.\n\n"
                + "3. Copia el 'Webhook URL' proporcionado por Hostinger.\n\n"
                + "4. Ve a tu repositorio en GitHub -> Settings -> Webhooks -> Add webhook:\n"
                + "   - Payload URL: Pega la URL obtenida de Hostinger hPanel\n"
                + "   - Content type: application/json\n"
                + "   - Secret: Ingresa tu token secreto si aplica\n"
                + "   - Which events would you like to trigger this webhook? Selecciona: 'Just the push event'\n"
                + "   - Active: [X] Habilitado\n\n"
                + "5. Haz clic en 'Add webhook'. A partir de este momento, cada commit en la rama '" + DEFAULT_BRANCH + "'\n"
                + "   se desplegará automáticamente en https://senda.fgdll.org\n";
    }
}
