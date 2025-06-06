package util;

public class StringEscapeUtils {

    /**
     * Escapes a string for safe use within JavaScript code, typically inside single quotes.
     * This method handles backslashes, single quotes, carriage returns, and newlines.
     *
     * @param input The string to escape.
     * @return The escaped string, or null if the input is null.
     */
    public static String escapeJavaScript(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '\\':
                    sb.append("\\\\"); // Replace \ with \\
                    break;
                case '\'':
                    sb.append("\\'");  // Replace ' with \'
                    break;
                case '\r':
                    sb.append("\\r");   // Replace carriage return with \r
                    break;
                case '\n':
                    sb.append("\\n");   // Replace newline with \n
                    break;
                // Bạn có thể thêm các trường hợp escape khác nếu cần, ví dụ:
                // case '"':
                //     sb.append("\\\""); // Replace " with \"
                //     break;
                // case '/':
                //     sb.append("\\/");  // Replace / with \/ (useful for </script> in JSON)
                //     break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }
}