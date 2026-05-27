package util;

public class Validator {
    public static Integer parseId(String arg) {
        if (arg == null || arg.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}