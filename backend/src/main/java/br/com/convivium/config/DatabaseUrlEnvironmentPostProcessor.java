package br.com.convivium.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Converte DATABASE_URL (formato Render/Heroku: postgresql://user:pass@host:port/dbname)
 * em spring.datasource.url, username e password, para não precisar configurar as 3 variáveis em separado.
 */
public class DatabaseUrlEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String DATABASE_URL = "DATABASE_URL";
    private static final String PROPERTY_SOURCE_NAME = "databaseUrlProperties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String databaseUrl = environment.getProperty(DATABASE_URL);
        if (databaseUrl == null || databaseUrl.isBlank()) {
            return;
        }
        databaseUrl = databaseUrl.trim();
        if (!databaseUrl.startsWith("postgresql://") && !databaseUrl.startsWith("postgres://")) {
            return;
        }

        try {
            // URI não lida bem com user:password se password tiver caracteres especiais; usar parsing manual
            String jdbcUrl = parseJdbcUrl(databaseUrl);
            String username = parseUsername(databaseUrl);
            String password = parsePassword(databaseUrl);

            if (jdbcUrl != null && username != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("spring.datasource.url", jdbcUrl);
                map.put("spring.datasource.username", username);
                map.put("spring.datasource.password", password != null ? password : "");
                environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, map));
            }
        } catch (Exception ignored) {
            // Se falhar o parse, deixa as outras configs (SPRING_DATASOURCE_*) valerem
        }
    }

    private static String parseJdbcUrl(String url) {
        try {
            // Usar último @ para separar user:pass do host:port/path (senha pode conter @)
            int lastAt = url.lastIndexOf('@');
            if (lastAt < 0) return null;
            String hostPart = url.substring(lastAt + 1);
            String path = hostPart.contains("/") ? hostPart.substring(hostPart.indexOf('/')) : "/convivium";
            String hostPort = hostPart.contains("/") ? hostPart.substring(0, hostPart.indexOf('/')) : hostPart;
            String host = hostPort.split(":")[0];
            int port = hostPort.contains(":") ? Integer.parseInt(hostPort.substring(hostPort.indexOf(':') + 1).replaceAll("[^0-9].*", "")) : 5432;
            String db = path.startsWith("/") ? path.substring(1).split("\\?")[0] : "convivium";
            if (db.isEmpty()) db = "convivium";
            return "jdbc:postgresql://" + host + ":" + port + "/" + db;
        } catch (Exception e) {
            return null;
        }
    }

    private static String parseUsername(String url) {
        try {
            int protoEnd = url.indexOf("://") + 3;
            int lastAt = url.lastIndexOf('@');
            if (lastAt <= protoEnd) return null;
            String userInfo = url.substring(protoEnd, lastAt);
            int colon = userInfo.indexOf(':');
            if (colon <= 0) return userInfo;
            return userInfo.substring(0, colon);
        } catch (Exception e) {
            return null;
        }
    }

    private static String parsePassword(String url) {
        try {
            int protoEnd = url.indexOf("://") + 3;
            int lastAt = url.lastIndexOf('@');
            if (lastAt <= protoEnd) return null;
            String userInfo = url.substring(protoEnd, lastAt);
            int colon = userInfo.indexOf(':');
            if (colon < 0 || colon == userInfo.length() - 1) return null;
            return userInfo.substring(colon + 1);
        } catch (Exception e) {
            return null;
        }
    }
}
