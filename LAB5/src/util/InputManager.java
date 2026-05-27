package util;
import models.City;
import models.Coordinates;
import models.Government;
import models.Human;
import models.StandardOfLiving;

import java.io.BufferedReader;
import java.util.Scanner;
import models.*;
import java.time.LocalDateTime;

public class InputManager {
    private final Scanner scanner;

    public InputManager() {
        scanner = new Scanner(System.in);
    }
    public City createCity() {
        System.out.println("Новый город:");
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
    private String readName() {
        while(true) {
            System.out.println("Введите name: ");
            String input = scanner.nextLine();

            if (input == null || input.trim().isEmpty()) {
                System.out.println("Имя не может быть пустым");
                continue;
            }
            return input;
        }
    }
    private Coordinates readCoordinates() {
        double x;
        double y;

        while(true) {
            try {
                System.out.println("Введите coordinates.x (> -501): ");
                x = Double.parseDouble(scanner.nextLine());
                if (x <= -501) {
                    System.out.println("x должен быть больше -501");
                    continue;
                }
                break;
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
        return new Coordinates(x,y);
    }

    private Long readArea() {
        while(true) {
            try {
                System.out.print("Введите area (>0): ");
                long area = Long.parseLong(scanner.nextLine());

                if (area <= 0) {
                    System.out.println("area должен быть >0");
                    continue;
                }
                return area;
            } catch (Exception e) {
                System.out.println("Введите число");
            }
        }
    }
    private long readPopulation() {
        while(true) {
            try{
                System.out.print("Введите population (>0): ");
                long population = Long.parseLong(scanner.nextLine());
                if(population <= 0) {
                    System.out.println("population должен быть >0");
                    continue;
                }
                return population;
            } catch (Exception e) {
                System.out.println("Введите число");
            }
        }
    }
    private StandardOfLiving readStandardOfLiving() {
        System.out.println("Введите уровень жизни или пустую строку:");

        for (StandardOfLiving s : StandardOfLiving.values()) {
            System.out.println(s);
        }

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
        System.out.println("Введите government или пустую строку:");
        for (Government g : Government.values()) {
            System.out.println(g);
        }
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
        System.out.print("Высота над уровнем моря: ");
        return Float.parseFloat(scanner.nextLine());
    }
    private Boolean readCapital() {
        System.out.print("Столица? (true/false): ");
        return Boolean.parseBoolean(scanner.nextLine());
    }
    private Human readHuman() {
        System.out.println("Введите дату рождения губернатора (гггг-мм-ddTHH:mm:ss) или пустую строку:");
        String input = scanner.nextLine();
        if (input.isEmpty()) {
            return null;
        }
        try {
            LocalDateTime birthday = LocalDateTime.parse(input);
            return new Human(birthday);
        } catch (Exception e) {
            System.out.println("Неверный формат даты");
            return readHuman();
        }
    }
    public City readCityFromScript(BufferedReader reader) throws Exception {
        String name = reader.readLine();
        double x = Double.parseDouble(reader.readLine());
        Double y = Double.parseDouble(reader.readLine());
        Long area = Long.parseLong(reader.readLine());
        long population = Long.parseLong(reader.readLine());
        Float meters = Float.parseFloat(reader.readLine());
        Boolean capital = Boolean.parseBoolean(reader.readLine());
        Government government = Government.valueOf(reader.readLine());
        StandardOfLiving living = StandardOfLiving.valueOf(reader.readLine());
        LocalDateTime birthday = LocalDateTime.parse(reader.readLine());
        Coordinates coordinates = new Coordinates(x, y);
        Human governor = new Human(birthday);
        return new City(
                name,
                coordinates,
                area,
                population,
                meters,
                capital,
                government,
                living,
                governor
        );
    }


}
