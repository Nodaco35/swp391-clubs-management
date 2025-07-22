package service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import models.Users;
import util.GoogleOAuthConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GoogleOAuthService {
    private static final Logger LOGGER = Logger.getLogger(GoogleOAuthService.class.getName());
    private static final Gson gson = new Gson();
    
    /**
     * Generate secure random state for CSRF protection
     */
    public static String generateState() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    /**
     * Exchange authorization code for access token
     */
    public static String getAccessToken(String authCode) {
        try {
            HttpURLConnection conn = (HttpURLConnection) URI.create(GoogleOAuthConfig.GOOGLE_TOKEN_URL).toURL().openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            
            // Build request body
            StringBuilder params = new StringBuilder();
            params.append("client_id=").append(URLEncoder.encode(GoogleOAuthConfig.getClientId(), StandardCharsets.UTF_8));
            params.append("&client_secret=").append(URLEncoder.encode(GoogleOAuthConfig.getClientSecret(), StandardCharsets.UTF_8));
            params.append("&code=").append(URLEncoder.encode(authCode, StandardCharsets.UTF_8));
            params.append("&grant_type=authorization_code");
            params.append("&redirect_uri=").append(URLEncoder.encode(GoogleOAuthConfig.getRedirectUri(), StandardCharsets.UTF_8));
            
            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = params.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // Read response
            int responseCode = conn.getResponseCode();
            LOGGER.log(Level.INFO, "Token exchange response code: {0}", responseCode);
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    
                    JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
                    return jsonResponse.get("access_token").getAsString();
                }
            } else {
                LOGGER.log(Level.SEVERE, "Failed to get access token. Response code: {0}", responseCode);
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        errorResponse.append(responseLine.trim());
                    }
                    LOGGER.log(Level.SEVERE, "Error response: {0}", errorResponse.toString());
                }
            }
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error exchanging code for token", e);
        }
        
        return null;
    }
    
    /**
     * Get user profile from Google using access token
     */
    public static Users getUserProfile(String accessToken) {
        try {
            String urlString = GoogleOAuthConfig.GOOGLE_USER_INFO_URL + "?access_token=" + accessToken;
            HttpURLConnection conn = (HttpURLConnection) URI.create(urlString).toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            
            int responseCode = conn.getResponseCode();
            LOGGER.log(Level.INFO, "User info response code: {0}", responseCode);
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    
                    JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
                    LOGGER.log(Level.INFO, "Google user info: {0}", jsonResponse.toString());
                    
                    Users user = new Users();
                    user.setFullName(jsonResponse.get("name").getAsString());
                    user.setEmail(jsonResponse.get("email").getAsString());
                    user.setAvatar(jsonResponse.get("picture").getAsString());
                    user.setStatus(true); // Google verified users are active
                    
                    return user;
                }
            } else {
                LOGGER.log(Level.SEVERE, "Failed to get user profile. Response code: {0}", responseCode);
            }
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error getting user profile", e);
        }
        
        return null;
    }
    
    /**
     * Complete OAuth2 flow: exchange code for token and get user profile
     * @param authorizationCode The authorization code from Google callback
     * @return Users object or null if any step failed
     */
    public static Users processCallback(String authorizationCode) {
        LOGGER.log(Level.INFO, "Processing OAuth callback with code: {0}", 
                  authorizationCode != null ? authorizationCode.substring(0, Math.min(10, authorizationCode.length())) + "..." : "null");
        
        // Bước 1: Đổi code lấy access token
        String accessToken = getAccessToken(authorizationCode);
        if (accessToken == null) {
            LOGGER.log(Level.SEVERE, "Không thể lấy access token");
            return null;
        }
        
        // Bước 2: Lấy thông tin user bằng access token
        Users userProfile = getUserProfile(accessToken);
        if (userProfile == null) {
            LOGGER.log(Level.SEVERE, "Không thể lấy thông tin user");
            return null;
        }
        
        // Bước 3: Validate email domain
        if (!userProfile.getEmail().endsWith("@fpt.edu.vn")) {
            LOGGER.log(Level.WARNING, "Email domain không hợp lệ: {0}. Chỉ cho phép @fpt.edu.vn.", userProfile.getEmail());
            return null;
        }
        
        return userProfile;
    }
}
