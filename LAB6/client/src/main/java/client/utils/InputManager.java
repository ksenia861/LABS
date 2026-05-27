package client.utils;

import common.models.*;

import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.io.IOException;

public class InputManager {
    public final Scanner scanner;
    public InputManager() {
        scanner = new Scanner(System.in);
    }
    public City createCity() {
        String name = readName();
        Coordinates coordinates = readCoordinates();
        Long area = readArea();
        long population = readPopulation();
        Government government = readGovernment();
        StandardOfLiving standardOfLiving = readStandardOfLiving();
        Human governor = readHuman();
        Float metersAboveSeaLevel = readMetersAboveSeaLevel();
        Boolean capital = readCapital();
        return new City(name, coordinates, area, population, metersAboveSeaLevel, capital, government, standardOfLiving, governor);
    }
    public City readCityFromScript(BufferedReader reader) throws IOException {
        try {
            String name = reader.readLine();
            if (name == null || name.trim().isEmpty()) {
                throw new IOException("Имя города не может быть пустым");
            }

            double x = Double.parseDouble(reader.readLine().trim());
            double y = Double.parseDouble(reader.readLine().trim());
            Long area = Long.parseLong(reader.readLine().trim());
            long population = Long.parseLong(reader.readLine().trim());
            Float metersAboveSeaLevel = Float.parseFloat(reader.readLine().trim());
            Boolean capital = Boolean.parseBoolean(reader.readLine().trim());
            Government government = Government.valueOf(reader.readLine().trim().toUpperCase());
            StandardOfLiving standardOfLiving = StandardOfLiving.valueOf(reader.readLine().trim().toUpperCase());
            LocalDateTime birthday = LocalDateTime.parse(reader.readLine().trim());

            Coordinates coordinates = new Coordinates(x, y);
            Human governor = new Human(birthday);

            return new City(name, coordinates, area, population, metersAboveSeaLevel,
                    capital, government, standardOfLiving, governor);

        } catch (NumberFormatException e) {
            throw new IOException("Неверный формат числа в скрипте: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IOException("Неверное значение enum в скрипте: " + e.getMessage());
        }
    }
    private String readName() {
        while (true) {
            System.out.print("Введите name: ");
            String input = scanner.nextLine();
            if (input != null && !input.trim().isEmpty()) {
                return input;
            }
            System.out.println("Имя не может быть пустым");
        }
    }
    private Coordinates readCoordinates() {
        double x;
        double y;
        while (true) {
            try {
                System.out.print("Введите coordinates.x (> -501): ");
                x = Double.parseDouble(scanner.nextLine());
                if (x > -501) break;
                System.out.println("x должен быть больше -501");
            } catch (Exception e) {
                System.out.println("Введите число");
            }
        }
        while (true) {
            try {
                System.out.print("Введите coordinates.y: ");
                y = Double.parseDouble(scanner.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("Введите число");
            }
        }
        return new Coordinates(x, y);
    }
    private Long readArea() {
        while (true) {
            try {
                System.out.print("Введите area (>0): ");
                long area = Long.parseLong(scanner.nextLine());
                if (area > 0) return area;
                System.out.println("area должен быть >0");
            } catch (Exception e) {
                System.out.println("Введите число");
            }
        }
    }
    private long readPopulation() {
        while (true) {
            try {
                System.out.print("Введите population (>0): ");
                long population = Long.parseLong(scanner.nextLine());
                if (population > 0) return population;
                System.out.println("population должен быть >0");
            } catch (Exception e) {
                System.out.println("Введите число");
            }
        }
    }
    private StandardOfLiving readStandardOfLiving() {
        System.out.println("Доступные значения: " + StandardOfLiving.getValues());
        System.out.print("Введите standardOfLiving (или пустую строку): ");
        String input = scanner.nextLine();
        if (input.isEmpty()) return null;
        try {
            return StandardOfLiving.valueOf(input.toUpperCase());
        } catch (Exception e) {
            System.out.println("Неверное значение");
            return readStandardOfLiving();
        }
    }
    private Government readGovernment() {
        System.out.println("Доступные значения: " + Government.getValues());
        System.out.print("Введите government (или пустую строку): ");
        String input = scanner.nextLine();
        if (input.isEmpty()) return null;
        try {
            return Government.valueOf(input.toUpperCase());
        } catch (Exception e) {
            System.out.println("Неверное значение");
            return readGovernment();
        }
    }
    private Float readMetersAboveSeaLevel() {
        System.out.print("Введите metersAboveSeaLevel: ");
        return Float.parseFloat(scanner.nextLine());
    }
    private Boolean readCapital() {
        System.out.print("Введите capital (true/false): ");
        return Boolean.parseBoolean(scanner.nextLine());
    }
    private Human readHuman() {
        System.out.print("Введите birthday (гггг-мм-ддTHH:MM:SS) или пустую строку: ");
        String input = scanner.nextLine();
        if (input.isEmpty()) return null;
        try {
            LocalDateTime birthday = LocalDateTime.parse(input);
            return new Human(birthday);
        } catch (Exception e) {
            System.out.println("Неверный формат даты");
            return readHuman();
        }
    }
}