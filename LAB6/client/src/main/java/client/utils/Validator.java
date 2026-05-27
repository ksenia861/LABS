package client.utils;

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
    public static boolean isValidNumber(String arg) {
        if (arg == null || arg.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(arg);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}