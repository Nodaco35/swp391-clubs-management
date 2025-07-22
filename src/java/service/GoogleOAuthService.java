package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.GoogleUserInfo;
import util.GoogleOAuthConfig;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class to handle Google OAuth2 operations
 */
public class GoogleOAuthService {
    
    private static final Logger LOGGER = Logger.getLogger(GoogleOAuthService.class.getName());
    private static final Gson gson = new Gson();
    
    /**
     * Exchange authorization code for access token
     * @param authorizationCode The authorization code received from Google
     * @return Access token or null if failed
     */
    public static String getAccessToken(String authorizationCode) {
        try {
            HttpURLConnection connection = (HttpURLConnection) URI.create(GoogleOAuthConfig.GOOGLE_TOKEN_URL).toURL().openConnection();
            
            // Set request method and headers
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);
            
            // Build request parameters
            StringBuilder params = new StringBuilder();
            params.append("client_id=").append(URLEncoder.encode(GoogleOAuthConfig.getClientId(), StandardCharsets.UTF_8));
            params.append("&client_secret=").append(URLEncoder.encode(GoogleOAuthConfig.getClientSecret(), StandardCharsets.UTF_8));
            params.append("&redirect_uri=").append(URLEncoder.encode(GoogleOAuthConfig.getRedirectUri(), StandardCharsets.UTF_8));
            params.append("&code=").append(URLEncoder.encode(authorizationCode, StandardCharsets.UTF_8));
            params.append("&grant_type=authorization_code");
            
            // Send request
            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                writer.write(params.toString());
                writer.flush();
            }
            
            // Read response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    // Parse JSON response to extract access token
                    TypeToken<Map<String, Object>> typeToken = new TypeToken<Map<String, Object>>() {};
                    Map<String, Object> tokenResponse = gson.fromJson(response.toString(), typeToken.getType());
                    return (String) tokenResponse.get("access_token");
                }
            } else {
                LOGGER.log(Level.SEVERE, "Failed to get access token. Response code: {0}", responseCode);
                return null;
            }
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error getting access token from Google", e);
            return null;
        }
    }
    
    /**
     * Get user information from Google using access token
     * @param accessToken The access token obtained from Google
     * @return GoogleUserInfo object or null if failed
     */
    public static GoogleUserInfo getUserInfo(String accessToken) {
        try {
            String urlString = GoogleOAuthConfig.GOOGLE_USER_INFO_URL + "?access_token=" + accessToken;
            HttpURLConnection connection = (HttpURLConnection) URI.create(urlString).toURL().openConnection();
            
            // Set request method and headers
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            
            // Read response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    // Parse JSON response to GoogleUserInfo object
                    GoogleUserInfo userInfo = gson.fromJson(response.toString(), GoogleUserInfo.class);
                    LOGGER.log(Level.INFO, "Successfully retrieved user info for: {0}", userInfo.getEmail());
                    return userInfo;
                }
            } else {
                LOGGER.log(Level.SEVERE, "Failed to get user info. Response code: {0}", responseCode);
                return null;
            }
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error getting user info from Google", e);
            return null;
        }
    }
    
    /**
     * Complete OAuth2 flow: exchange code for token and get user info
     * @param authorizationCode The authorization code from Google callback
     * @return GoogleUserInfo object or null if any step failed
     */
    public static GoogleUserInfo processOAuthCallback(String authorizationCode) {
        LOGGER.log(Level.INFO, "Processing OAuth callback with code: {0}", 
                  authorizationCode != null ? authorizationCode.substring(0, Math.min(10, authorizationCode.length())) + "..." : "null");
        
        // Step 1: Exchange code for access token
        String accessToken = getAccessToken(authorizationCode);
        if (accessToken == null) {
            LOGGER.log(Level.SEVERE, "Failed to get access token");
            return null;
        }
        
        // Step 2: Get user info using access token
        GoogleUserInfo userInfo = getUserInfo(accessToken);
        if (userInfo == null) {
            LOGGER.log(Level.SEVERE, "Failed to get user info");
            return null;
        }
        
        // Step 3: Validate email domain
        if (!userInfo.isValidDomain()) {
            LOGGER.log(Level.WARNING, "Invalid email domain: {0}. Only @fpt.edu.vn is allowed.", userInfo.getEmail());
            return null;
        }
        
        return userInfo;
    }
}
