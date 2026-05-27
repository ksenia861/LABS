package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import models.City;

import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Stack;
import java.time.LocalDate;

public class FileManager {
    private final String fileName;
    private final Gson gson;
    public FileManager(String fileName) {
        this.fileName = fileName;
        TypeAdapter<LocalDate> localDateAdapter = new TypeAdapter<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            @Override
            public void write(JsonWriter out, LocalDate value) throws IOException {
                if (value == null) {
                    out.nullValue();
                } else {
                    out.value(value.format(formatter));
                }
            }
            @Override
            public LocalDate read(JsonReader in) throws IOException {
                String dateStr = in.nextString();
                if (dateStr == null || dateStr.isEmpty()) {
                    return null;
                }
                return LocalDate.parse(dateStr, formatter);
            }
        };
        TypeAdapter<LocalDateTime> localDateTimeAdapter = new TypeAdapter<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            @Override
            public void write(JsonWriter out, LocalDateTime value) throws IOException {
                if (value == null) {
                    out.nullValue();
                } else {
                    out.value(value.format(formatter));
                }
            }
            @Override
            public LocalDateTime read(JsonReader in) throws IOException {
                String dateStr = in.nextString();
                if (dateStr == null || dateStr.isEmpty()) {
                    return null;
                }
                return LocalDateTime.parse(dateStr, formatter);
            }
        };
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, localDateAdapter)
                .registerTypeAdapter(LocalDateTime.class, localDateTimeAdapter)
                .create();
    }
    public String readFile() {
        StringBuilder data = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }
        return data.toString();
    }
    public void writeFile(String data) {
        try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(fileName))) {
            output.write(data.getBytes());
        } catch (IOException e) {
            System.out.println("Ошибка записи файла: " + e.getMessage());
        }
    }
    public void saveCollection(Stack<City> collection) {
        String json = gson.toJson(collection);
        writeFile(json);
        System.out.println("Коллекция сохранена в файл: " + fileName);
    }
    public Stack<City> loadCollection() {
        String json = readFile();
        if (json == null || json.trim().isEmpty()) {
            System.out.println("Файл пуст, создана новая коллекция");
            return new Stack<>();
        }
        try {
            Type collectionType = new TypeToken<Stack<City>>(){}.getType();
            Stack<City> collection = gson.fromJson(json, collectionType);
            if (collection == null) {
                return new Stack<>();
            }
            System.out.println("Коллекция загружена из файла: " + fileName);
            return collection;
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке коллекции из JSON: " + e.getMessage());
            e.printStackTrace();
            return new Stack<>();
        }
    }
}