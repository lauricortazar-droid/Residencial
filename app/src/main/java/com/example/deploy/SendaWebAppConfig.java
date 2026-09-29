package com.example.deploy;

/**
 * Configuración en Java para la Web App móvil desplegada en Hostinger
 * bajo el dominio https://senda.fgdll.org
 *
 * Guía hPanel: https://hpanel.hostinger.com/git-auto-deployments-guide/senda.fgdll.org/github/
 */
public class SendaWebAppConfig {

    public static final String PRODUCTION_WEBAPP_URL = "https://senda.fgdll.org";
    public static final String HPANEL_DEPLOY_GUIDE = "https://hpanel.hostinger.com/git-auto-deployments-guide/senda.fgdll.org/github/";
    public static final String REPOSITORY_NAME = "senda-erp";
    public static final String DEFAULT_BRANCH = "main";
    public static final String HEALTH_CHECK_ENDPOINT = "https://senda.fgdll.org/api/health";
    public static final String DEPLOY_WEBHOOK_ENDPOINT = "https://senda.fgdll.org/api/deploy";

    private final String customDomain;
    private boolean isPwaEnabled;

    public SendaWebAppConfig() {
        this(PRODUCTION_WEBAPP_URL);
    }

    public SendaWebAppConfig(String customDomain) {
        this.customDomain = (customDomain != null && !customDomain.isEmpty())
                ? customDomain
                : PRODUCTION_WEBAPP_URL;
        this.isPwaEnabled = true;
    }

    public String getProductionWebappUrl() {
        return customDomain;
    }

    public boolean isPwaEnabled() {
        return isPwaEnabled;
    }

    public void setPwaEnabled(boolean pwaEnabled) {
        isPwaEnabled = pwaEnabled;
    }

    public static String getHpanelGuideUrl() {
        return HPANEL_DEPLOY_GUIDE;
    }

    public static String getDeployInstructions() {
        return "Para desplegar como Web App en Hostinger:\n"
                + "1. Accede a: " + HPANEL_DEPLOY_GUIDE + "\n"
                + "2. Vincula el repositorio de GitHub con la rama '" + DEFAULT_BRANCH + "'.\n"
                + "3. El servidor WebAppServer.java o el directorio public_html/index.html servirán la Web App en " + PRODUCTION_WEBAPP_URL + ".\n"
                + "4. Configura el Webhook en GitHub apuntando a la URL generada por Hostinger hPanel.\n";
    }
}
