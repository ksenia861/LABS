package common.utils;

public class IdGenerator {
    private static int currentId = 1;

    public static int generateId() {
        return currentId++;
    }
    public static void updateMaxId(int id) {
        if(id >= currentId) {
            currentId = id + 1;
        }
    }
    public static void reset() {
        currentId = 1;
    }
}