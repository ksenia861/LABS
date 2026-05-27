package models;
public enum StandardOfLiving {
    LOW,
    VERY_LOW,
    ULTRA_LOW;

    public static String getValues() {
        StringBuilder values = new StringBuilder();
        for (StandardOfLiving s : values()) {
            values.append(s.name()).append(", ");
        }
        return values.substring(0, values.length() - 2);
    }
}