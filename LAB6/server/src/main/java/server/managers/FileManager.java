package server.managers;

import common.models.City;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Stack;

public class FileManager {
    private final String fileName;
    private final Gson gson;

    public FileManager(String fileName) {
        this.fileName = fileName;

        TypeAdapter<LocalDate> localDateAdapter = new TypeAdapter<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            public void write(JsonWriter out, LocalDate value) throws IOException {
                out.value(value == null ? null : value.format(formatter));
            }
            public LocalDate read(JsonReader in) throws IOException {
                String dateStr = in.nextString();
                return dateStr == null ? null : LocalDate.parse(dateStr, formatter);
            }
        };

        TypeAdapter<LocalDateTime> localDateTimeAdapter = new TypeAdapter<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            public void write(JsonWriter out, LocalDateTime value) throws IOException {
                out.value(value == null ? null : value.format(formatter));
            }
            public LocalDateTime read(JsonReader in) throws IOException {
                String dateStr = in.nextString();
                return dateStr == null ? null : LocalDateTime.parse(dateStr, formatter);
            }
        };

        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, localDateAdapter)
                .registerTypeAdapter(LocalDateTime.class, localDateTimeAdapter)
                .create();
    }

    public void saveCollection(Stack<City> collection) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            String json = gson.toJson(collection);
            writer.write(json);
        } catch (IOException e) {
            System.err.println("Ошибка записи файла: " + e.getMessage());
        }
    }

    public Stack<City> loadCollection() {
        File file = new File(fileName);
        if (!file.exists()) {
            return new Stack<>();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            StringBuilder data = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }

            String json = data.toString().trim();
            if (json.isEmpty()) {
                return new Stack<>();
            }

            Type collectionType = new TypeToken<Stack<City>>(){}.getType();
            Stack<City> collection = gson.fromJson(json, collectionType);
            return collection == null ? new Stack<>() : collection;
        } catch (Exception e) {
            System.err.println("Ошибка загрузки коллекции: " + e.getMessage());
            return new Stack<>();
        }
    }
}