package com.sentori.iot.lambda.util;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Utilitaire pour construire les URLs Grafana
 * Équivalent de com.sentori.iot.util.GrafanaUrlBuilder
 */
@ApplicationScoped
public class GrafanaUrlBuilder {

    private static final Logger LOG = Logger.getLogger(GrafanaUrlBuilder.class);

    @ConfigProperty(name = "app.grafana.base-url")
    String grafanaBaseUrl;

    @ConfigProperty(name = "app.grafana.dashboard-path")
    String grafanaDashboardPath;

    @ConfigProperty(name = "app.grafana.org-id", defaultValue = "1")
    String grafanaOrgId;

    @ConfigProperty(name = "app.grafana.ds-prom-uid", defaultValue = "prometheus")
    String grafanaDsPromUid;

    @ConfigProperty(name = "app.grafana.timezone", defaultValue = "browser")
    String grafanaTimezone;

    @ConfigProperty(name = "app.grafana.default-from", defaultValue = "now-15m")
    String grafanaDefaultFrom;

    @ConfigProperty(name = "app.grafana.default-to", defaultValue = "now")
    String grafanaDefaultTo;

    @ConfigProperty(name = "app.grafana.default-refresh", defaultValue = "5s")
    String grafanaRefresh;

    /**
     * Construit une URL Grafana complète avec filtres pour un run et un user
     */
    public String buildUrl(String runId, String username) {
        try {
            StringBuilder url = new StringBuilder(grafanaBaseUrl);
            url.append(grafanaDashboardPath);
            url.append("?orgId=").append(grafanaOrgId);
            url.append("&from=").append(URLEncoder.encode(grafanaDefaultFrom, StandardCharsets.UTF_8.toString()));
            url.append("&to=").append(URLEncoder.encode(grafanaDefaultTo, StandardCharsets.UTF_8.toString()));
            url.append("&timezone=").append(grafanaTimezone);
            url.append("&refresh=").append(grafanaRefresh);
            
            // Variables Grafana
            if (runId != null) {
                url.append("&var-run_id=").append(URLEncoder.encode(runId, StandardCharsets.UTF_8.toString()));
            }
            if (username != null) {
                url.append("&var-username=").append(URLEncoder.encode(username, StandardCharsets.UTF_8.toString()));
            }
            
            LOG.infof("Built Grafana URL for runId=%s, username=%s", runId, username);
            return url.toString();
            
        } catch (UnsupportedEncodingException e) {
            LOG.error("Error encoding Grafana URL", e);
            return grafanaBaseUrl + grafanaDashboardPath;
        }
    }

    /**
     * Construit une URL Grafana simple sans filtres
     */
    public String buildSimpleUrl() {
        return grafanaBaseUrl + grafanaDashboardPath + 
               "?orgId=" + grafanaOrgId + 
               "&refresh=" + grafanaRefresh;
    }
}
