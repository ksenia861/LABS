package models;
public enum Government {
    DESPOTISM,
    COMMUNISM,
    TECHNOCRACY;


    public static String getValues() {
        StringBuilder values = new StringBuilder();
        for (Government g : values()) {
            values.append(g.name()).append(", ");
        }
        return values.substring(0, values.length() - 2);
    }
}