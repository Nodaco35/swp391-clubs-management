package util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for handling JSON operations in the application
 * Used for parsing and formatting JSON data in form fields
 */
public class JsonUtils {

    /**
     * Check if a string is valid JSON (object or array)
     * @param str String to check
     * @return true if valid JSON, false otherwise
     */
    public static boolean isValidJson(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        
        str = str.trim();
        try {
            if (str.startsWith("{")) {
                new JSONObject(str);
                return true;
            } else if (str.startsWith("[")) {
                new JSONArray(str);
                return true;
            }
        } catch (JSONException e) {
            // Not valid JSON
        }
        return false;
    }

    /**
     * Parse JSON string to a list of string values if it's a JSON array
     * @param jsonString JSON string to parse
     * @return List of strings, or empty list if parsing fails
     */
    public static List<String> parseJsonArray(String jsonString) {
        List<String> result = new ArrayList<>();
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return result;
        }
        
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                result.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            // Return empty list on error
        }
        
        return result;
    }    /**
     * Parse form builder options from various formats (JSON array, JSON objects, or delimited strings)
     * This handles all possible formats for options in form fields like radio, checkbox, select
     * 
     * @param options The options string (JSON array, JSON objects with value, or delimited string)
     * @return List of option values parsed from the input
     */
    public static List<String> parseFormBuilderOptions(String options) {
        List<String> optionsList = new ArrayList<>();
        
        if (options == null || options.trim().isEmpty()) {
            return optionsList;
        }
        
        // Debug log
        System.out.println("Debug - parseFormBuilderOptions input: " + options);
        
        // Try to parse as JSON first
        if (isValidJson(options)) {
            try {
                if (options.trim().startsWith("[")) {
                    // It's a JSON array
                    JSONArray jsonArray = new JSONArray(options);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        // Check if each item is a JSON object with 'value' property
                        if (jsonArray.get(i) instanceof JSONObject) {
                            JSONObject optObj = jsonArray.getJSONObject(i);
                            if (optObj.has("value")) {
                                optionsList.add(optObj.getString("value"));
                            }
                        } else {
                            // If it's a simple string item
                            optionsList.add(jsonArray.getString(i));
                        }
                    }
                } else if (options.trim().startsWith("{")) {
                    // It's a JSON object, check if it has an 'options' array
                    JSONObject jsonObj = new JSONObject(options);
                    if (jsonObj.has("options") && jsonObj.get("options") instanceof JSONArray) {
                        JSONArray optionsArray = jsonObj.getJSONArray("options");
                        for (int i = 0; i < optionsArray.length(); i++) {
                            if (optionsArray.get(i) instanceof JSONObject) {
                                JSONObject optObj = optionsArray.getJSONObject(i);
                                if (optObj.has("value")) {
                                    optionsList.add(optObj.getString("value"));
                                }
                            } else {
                                optionsList.add(optionsArray.getString(i));
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                System.out.println("Debug - Error parsing JSON options: " + e.getMessage());
                // If JSON parsing fails, fall through to string delimiter parsing
            }
        }
        
        // If we got options from JSON, return them
        if (!optionsList.isEmpty()) {
            System.out.println("Debug - Options parsed from JSON: " + optionsList.size());
            return optionsList;
        }
        
        // Try parsing as delimited string
        if (options.contains(";;")) {
            // Split by ";;" delimiter
            String[] optionsArray = options.split(";;");
            for (String option : optionsArray) {
                optionsList.add(option.trim());
            }
            System.out.println("Debug - Options parsed with ';;' delimiter: " + optionsList.size());
        } else if (options.contains(",")) {
            // Split by "," delimiter
            String[] optionsArray = options.split(",");
            for (String option : optionsArray) {
                optionsList.add(option.trim());
            }
            System.out.println("Debug - Options parsed with ',' delimiter: " + optionsList.size());
        } else {
            // If no delimiter found, treat as a single option
            optionsList.add(options.trim());
            System.out.println("Debug - Added as single option: " + options.trim());
        }
        
        return optionsList;
    }

    /**
     * Extract a string value from a JSON object with the given key
     * @param jsonString JSON string to parse
     * @param key Key to extract
     * @return The string value, or null if not found or parsing fails
     */
    public static String getStringFromJsonObject(String jsonString, String key) {
        if (jsonString == null || jsonString.trim().isEmpty() || key == null) {
            return null;
        }
        
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.has(key)) {
                return jsonObject.getString(key);
            }
        } catch (JSONException e) {
            // Return null on error
        }
        
        return null;
    }
    
    /**
     * Extract image data from a JSON object, typically used for info fields
     * @param jsonString JSON string to parse
     * @return The image data as a string (data URL format) or null if not found
     */
    public static String getImageFromJsonObject(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.has("image")) {
                return jsonObject.getString("image");
            }
        } catch (JSONException e) {
            // Return null on error
        }
        
        return null;
    }

    /**
     * Format JSON string for display (pretty print)
     * @param jsonString JSON string to format
     * @return Formatted JSON string, or original string if parsing fails
     */    public static String formatJsonForDisplay(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return jsonString;
        }
        
        try {
            if (jsonString.trim().startsWith("{")) {
                JSONObject json = new JSONObject(jsonString);
                return json.toString(2); // 2 spaces for indentation
            } else if (jsonString.trim().startsWith("[")) {
                JSONArray json = new JSONArray(jsonString);
                return json.toString(2); // 2 spaces for indentation
            }
        } catch (JSONException e) {
            // Return original on error
        }
        
        return jsonString;
    }
    
    /**
     * Extract date constraints from a JSON object
     * @param jsonString JSON string potentially containing date constraints
     * @return A two-element String array with [minDate, maxDate], can contain null values
     */    public static String[] extractDateConstraints(String jsonString) {
        String[] result = new String[2]; // [minDate, maxDate]
        
        if (!isValidJson(jsonString) || !jsonString.trim().startsWith("{")) {
            return result;
        }
        
        try {
            JSONObject json = new JSONObject(jsonString);
            if (json.has("minDate")) {
                result[0] = json.getString("minDate");
            }
            if (json.has("maxDate")) {
                result[1] = json.getString("maxDate");
            }
        } catch (JSONException e) {
            // Return array with nulls on error
        }
        
        return result;
    }
    
    /**
     * Process JSON options for different field types
     * @param jsonString JSON string to parse
     * @param fieldType Type of the form field (info, radio, checkbox, etc.)
     * @return Processed data appropriate for the field type
     */
    public static Object processJsonForFieldType(String jsonString, String fieldType) {
        if (!isValidJson(jsonString)) {
            return jsonString; // Return original if not valid JSON
        }
        
        try {
            switch(fieldType.toLowerCase()) {
                case "info":
                    // For info fields, return a map with content and possibly image
                    JSONObject infoObj = new JSONObject(jsonString);
                    JSONObject result = new JSONObject();
                    
                    if (infoObj.has("content")) {
                        result.put("content", infoObj.getString("content"));
                    }
                    
                    if (infoObj.has("image")) {
                        result.put("image", infoObj.getString("image"));
                    }
                    
                    return result.toString();
                    
                case "radio":
                case "checkbox":
                case "select":
                    // For option-based fields, extract options array
                    if (jsonString.trim().startsWith("[")) {
                        // Already an array, just return formatted
                        return new JSONArray(jsonString).toString();
                    } else if (jsonString.trim().startsWith("{")) {
                        // Object with options property
                        JSONObject obj = new JSONObject(jsonString);
                        if (obj.has("options") && obj.get("options") instanceof JSONArray) {
                            return obj.getJSONArray("options").toString();
                        }
                    }
                    break;
                    
                case "date":
                    // For date fields, extract constraints
                    JSONObject dateObj = new JSONObject();
                    String[] constraints = extractDateConstraints(jsonString);
                    
                    if (constraints[0] != null) {
                        dateObj.put("minDate", constraints[0]);
                    }
                    
                    if (constraints[1] != null) {
                        dateObj.put("maxDate", constraints[1]);
                    }
                    
                    return dateObj.toString();
            }
        } catch (JSONException e) {
            // Return original on error
        }
        
        return jsonString;
    }
}
