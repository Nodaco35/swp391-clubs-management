package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuration class for Google OAuth2
 * Loads sensitive credentials from properties file
 */
public class GoogleOAuthConfig {
    
    private static final Logger LOGGER = Logger.getLogger(GoogleOAuthConfig.class.getName());
    private static Properties config;
    
    // Default values (fallback)
    public static final String DEFAULT_CLIENT_ID = "YOUR_GOOGLE_CLIENT_ID";
    public static final String DEFAULT_CLIENT_SECRET = "YOUR_GOOGLE_CLIENT_SECRET";
    public static final String DEFAULT_REDIRECT_URI = "http://localhost:8080/clubs/oauth2callback";
    
    // Google OAuth2 URLs (these are public, safe to hardcode)
    public static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/auth";
    public static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    public static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
    
    // OAuth2 Scopes
    public static final String SCOPE = "openid email profile";
    
    // Response type
    public static final String RESPONSE_TYPE = "code";
    
    static {
        loadConfiguration();
    }
    
    /**
     * Load configuration from properties file
     */
    private static void loadConfiguration() {
        config = new Properties();
        
        // Try to load from classpath first
        try (InputStream inputStream = GoogleOAuthConfig.class.getClassLoader().getResourceAsStream("conf/oauth.properties")) {
            if (inputStream != null) {
                config.load(inputStream);
                LOGGER.log(Level.INFO, "Loaded OAuth configuration from classpath");
                return;
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not load oauth.properties from classpath", e);
        }
        
        // Try to load from system properties as fallback
        String clientId = System.getProperty("google.oauth.client.id");
        String clientSecret = System.getProperty("google.oauth.client.secret");
        String redirectUri = System.getProperty("google.oauth.redirect.uri");
        
        if (clientId != null && clientSecret != null) {
            config.setProperty("google.oauth.client.id", clientId);
            config.setProperty("google.oauth.client.secret", clientSecret);
            config.setProperty("google.oauth.redirect.uri", redirectUri != null ? redirectUri : DEFAULT_REDIRECT_URI);
            LOGGER.log(Level.INFO, "Loaded OAuth configuration from system properties");
            return;
        }
        
        // Try environment variables as final fallback
        clientId = System.getenv("GOOGLE_OAUTH_CLIENT_ID");
        clientSecret = System.getenv("GOOGLE_OAUTH_CLIENT_SECRET");
        redirectUri = System.getenv("GOOGLE_OAUTH_REDIRECT_URI");
        
        if (clientId != null && clientSecret != null) {
            config.setProperty("google.oauth.client.id", clientId);
            config.setProperty("google.oauth.client.secret", clientSecret);
            config.setProperty("google.oauth.redirect.uri", redirectUri != null ? redirectUri : DEFAULT_REDIRECT_URI);
            LOGGER.log(Level.INFO, "Loaded OAuth configuration from environment variables");
            return;
        }
        
        // Use default values (will not work, but prevents crashes)
        config.setProperty("google.oauth.client.id", DEFAULT_CLIENT_ID);
        config.setProperty("google.oauth.client.secret", DEFAULT_CLIENT_SECRET);
        config.setProperty("google.oauth.redirect.uri", DEFAULT_REDIRECT_URI);
        LOGGER.log(Level.WARNING, "Using default OAuth configuration - Google Sign-In will not work!");
    }
    
    /**
     * Get Google OAuth2 Client ID
     * @return Client ID
     */
    public static String getClientId() {
        return config.getProperty("google.oauth.client.id", DEFAULT_CLIENT_ID);
    }
    
    /**
     * Get Google OAuth2 Client Secret
     * @return Client Secret
     */
    public static String getClientSecret() {
        return config.getProperty("google.oauth.client.secret", DEFAULT_CLIENT_SECRET);
    }
    
    /**
     * Get OAuth2 Redirect URI
     * @return Redirect URI
     */
    public static String getRedirectUri() {
        return config.getProperty("google.oauth.redirect.uri", DEFAULT_REDIRECT_URI);
    }
    
    /**
     * Build Google OAuth2 authorization URL
     * @param state CSRF protection state parameter
     * @return Complete authorization URL
     */
    public static String buildAuthorizationUrl(String state) {
        StringBuilder url = new StringBuilder();
        url.append(GOOGLE_AUTH_URL);
        url.append("?client_id=").append(getClientId());
        url.append("&redirect_uri=").append(getRedirectUri());
        url.append("&scope=").append(SCOPE.replace(" ", "%20"));
        url.append("&response_type=").append(RESPONSE_TYPE);
        url.append("&state=").append(state);
        return url.toString();
    }
}
