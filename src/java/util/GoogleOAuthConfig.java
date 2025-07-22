package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuration class for Google OAuth2
 * Loads sensitive credentials from properties file or environment variables
 * NEVER commit actual credentials to git!
 */
public class GoogleOAuthConfig {
    
    private static final Logger LOGGER = Logger.getLogger(GoogleOAuthConfig.class.getName());
    private static Properties config;
    
    // Default values (placeholders only - will not work)
    public static final String DEFAULT_CLIENT_ID = "your-google-client-id.apps.googleusercontent.com";
    public static final String DEFAULT_CLIENT_SECRET = "your-google-client-secret"; 
    public static final String DEFAULT_REDIRECT_URI = "http://localhost:8080/clubs/oauth2callback";
    
    // Google OAuth2 URLs (public, safe to hardcode)
    public static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/auth";
    public static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    public static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";
    
    // OAuth2 Scopes
    public static final String SCOPE = "openid email profile";
    
    static {
        loadConfiguration();
    }
    
    /**
     * Load configuration from multiple sources (properties file, system props, env vars)
     */
    private static void loadConfiguration() {
        config = new Properties();
        
        // 1. Try to load from properties file first (recommended for development)
        try (InputStream inputStream = GoogleOAuthConfig.class.getClassLoader()
                .getResourceAsStream("conf/oauth.properties")) {
            if (inputStream != null) {
                config.load(inputStream);
                LOGGER.log(Level.INFO, "Loaded OAuth configuration from oauth.properties file");
                return;
            }
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "oauth.properties file not found, trying other sources...");
        }
        
        // 2. Try to load from system properties (for production deployment)
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
        
        // 3. Try environment variables as final fallback
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
        
        // 4. Use default placeholder values (will not work, but prevents crashes)
        config.setProperty("google.oauth.client.id", DEFAULT_CLIENT_ID);
        config.setProperty("google.oauth.client.secret", DEFAULT_CLIENT_SECRET);
        config.setProperty("google.oauth.redirect.uri", DEFAULT_REDIRECT_URI);
        LOGGER.log(Level.WARNING, "Using default OAuth configuration - Google Sign-In will NOT work! " +
                "Please configure oauth.properties file or environment variables.");
    }
    
    /**
     * Build Google OAuth authorization URL
     */
    public static String buildAuthUrl(String state) {
        StringBuilder url = new StringBuilder();
        url.append(GOOGLE_AUTH_URL)
           .append("?client_id=").append(getClientId())
           .append("&redirect_uri=").append(getRedirectUri())
           .append("&scope=").append(SCOPE.replace(" ", "%20"))
           .append("&response_type=code")
           .append("&state=").append(state)
           .append("&access_type=offline")
           .append("&prompt=consent")
           .append("&hd=fpt.edu.vn"); // Restrict to FPT domain
        return url.toString();
    }
    
    /**
     * Get Google OAuth2 Client ID
     */
    public static String getClientId() {
        return config.getProperty("google.oauth.client.id", DEFAULT_CLIENT_ID);
    }
    
    /**
     * Get Google OAuth2 Client Secret  
     */
    public static String getClientSecret() {
        return config.getProperty("google.oauth.client.secret", DEFAULT_CLIENT_SECRET);
    }
    
    /**
     * Get OAuth2 Redirect URI
     */
    public static String getRedirectUri() {
        return config.getProperty("google.oauth.redirect.uri", DEFAULT_REDIRECT_URI);
    }
    
    /**
     * Check if configuration is properly loaded (not using defaults)
     */
    public static boolean isConfigured() {
        String clientId = getClientId();
        String clientSecret = getClientSecret();
        
        LOGGER.log(Level.INFO, "OAuth Config Check - ClientID: {0}, ClientSecret: {1}", 
                new Object[]{clientId.substring(0, Math.min(10, clientId.length())) + "...", 
                            clientSecret.substring(0, Math.min(10, clientSecret.length())) + "..."});
        
        // Check if we have actual credentials (not placeholder values)
        boolean configured = !DEFAULT_CLIENT_ID.equals(clientId) && 
                            !DEFAULT_CLIENT_SECRET.equals(clientSecret) &&
                            clientId.contains("apps.googleusercontent.com") &&
                            clientSecret.startsWith("GOCSPX-");
        
        LOGGER.log(Level.INFO, "OAuth isConfigured: {0}", configured);
        return configured;
    }
    
    /**
     * Validate if email domain is allowed (only @fpt.edu.vn)
     */
    public static boolean isValidFptEmail(String email) {
        return email != null && email.toLowerCase().endsWith("@fpt.edu.vn");
    }
}
