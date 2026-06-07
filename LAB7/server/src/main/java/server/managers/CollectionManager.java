package server.managers;
import server.commands.*;
import common.models.City;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CollectionManager {
    private static final Logger logger = LogManager.getLogger(CollectionManager.class);

    private List<City> cityStack = Collections.synchronizedList(new Stack<>());
    private LocalDateTime initTime;
    private DatabaseManager dbManager;

    public CollectionManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.initTime = LocalDateTime.now();

        this.cityStack.addAll(dbManager.loadAllCities());

        logger.info("Коллекция загружена из БД. Элементов: " + cityStack.size());
    }

    public Collection<City> getCollection() {
        return cityStack;
    }

    public boolean addCity(City city, String login) {
        city.setCreationDate(LocalDate.now());
        city.setOwnerLogin(login);
        int generatedId = dbManager.addCityToDB(city, login);
        if (generatedId != -1) {
            city.setId(generatedId);
            cityStack.add(city);
            logger.info("Пользователь" + login + "добавил город с id=" + generatedId);
            return true;
        }
        return false;
    }

    public boolean updateCity(int id, City newCity, String login) {
        synchronized (cityStack) {
            Optional<City> found = cityStack.stream()
                    .filter(city -> city.getId() == id)
                    .findFirst();

            if (found.isPresent()) {
                City oldCity = found.get();
                if (!oldCity.getOwnerLogin().equals(login)) {
                    logger.warn("Пользователь " + login + "попытался обновить чужой город!");
                    return false;
                }
                if (dbManager.updateCityInBD(id, newCity, login)) {
                    newCity.setId(id);
                    newCity.setCreationDate(oldCity.getCreationDate());
                    newCity.setOwnerLogin(login);
                    int index = cityStack.indexOf(oldCity);
                    cityStack.set(index, newCity);
                    logger.info("Обновлен город с id=" + id);
                    return true;
                }
            }
            return false;
        }
    }


    public String showAllCities() {
        synchronized (cityStack) {
            if (cityStack.isEmpty()) return "Коллекция пуста";
            return cityStack.stream()
                    .map(City::toString)
                    .collect(Collectors.joining("\n"));
        }
    }

    public boolean removeById(int id, String login) {
        synchronized (cityStack) {
            Optional<City> cityRemove = cityStack.stream()
                    .filter(c -> c.getId() == id).findFirst();
            if (cityRemove.isPresent() && cityRemove.get().getOwnerLogin().equals(login)) {
                if (dbManager.removeCityByIdFromDB(id, login)) {
                    cityStack.remove(cityRemove.get());
                    logger.info("Пользователь " + login + "удалил город id=" + id);
                    return true;
                }
            }
        }
        return false;
    }

    public String info() {
        return "Тип коллекции: Stack (потокобезопасный)\n" +
                "Дата инициализации: " + initTime + "\n" +
                "Количество элементов: " + cityStack.size();
    }

    public void clear(String login) {
        if (dbManager.clearCitiesOwnedByFromDb(login)) {
            synchronized (cityStack) {
                cityStack.removeIf(city -> city.getOwnerLogin().equals(login));
            }
            logger.info("Пользователь " + login + "очистил свои города");
        }

    }
    public boolean removeLast(String login) {
        synchronized (cityStack) {
            if (cityStack.isEmpty()) return false;
            City lastCity = cityStack.get(cityStack.size() - 1);

            if (!lastCity.getOwnerLogin().equals(login)) {
                logger.warn("Пользователь " + login + " попытался удалить чужой последний город!");
                return false;
            }

            if (dbManager.removeCityByIdFromDB(lastCity.getId(), login)) {
                cityStack.remove(cityStack.size() - 1);
                logger.info("Пользователь " + login + " удалил последний город id=" + lastCity.getId());
                return true;
            }
        }
        return false;
    }


    public void insertAt(int index, City city, String login) {
        if (index < 0 || index > cityStack.size()) throw new IndexOutOfBoundsException("Неверный индекс");
        city.setCreationDate(LocalDate.now());
        city.setOwnerLogin(login);
        int generatedId = dbManager.addCityToDB(city,login);
        if (generatedId != -1) {
            city.setId(generatedId);
            cityStack.add(index,city);
            logger.info("Пользователь " + login + "вставил город на позицию " + index);
        }
    }

    public void removeGreater(City city, String login) {
        synchronized (cityStack) {
            List<City> toRemove = cityStack.stream()
                    .filter(c -> c.compareTo(city) > 0 && c.getOwnerLogin().equals(login))
                    .collect(Collectors.toList());
            for (City c : toRemove) {
                if (dbManager.removeCityByIdFromDB(c.getId(), login)) {
                    cityStack.remove(c);
                }
            }
            logger.info("Пользователь " + login + " выполнил remove_greater");
        }

    }

    public void reorder() {
        List<City> tempList = new ArrayList<>(cityStack);
        Collections.reverse(tempList);
        cityStack.clear();
        cityStack.addAll(tempList);
        logger.info("Коллекция перевернута");
    }

    public String maxById() {
        synchronized (cityStack) {
            return cityStack.stream()
                    .max(Comparator.comparingInt(City::getId))
                    .map(City::toString)
                    .orElse("Коллекция пуста");
        }
    }

    public String printDescending() {
        synchronized (cityStack) {
            return cityStack.stream()
                    .sorted(Comparator.reverseOrder())
                    .map(City::toString)
                    .collect(Collectors.joining("\n"));
        }
    }

    public String printFieldDescendingGovernor() {
        synchronized (cityStack) {
            List<String> governors = cityStack.stream()
                    .map(City::getGovernor)
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());

            return governors.isEmpty() ? "Нет губернаторов" :
                    governors.stream().collect(Collectors.joining("\n"));
        }
    }

    public Collection<City> getSortedByLocation() {
        synchronized (cityStack) {
            return cityStack.stream()
                    .sorted(Comparator.comparing((City c) -> c.getCoordinates().getX())
                            .thenComparing(c -> c.getCoordinates().getY()))
                    .collect(Collectors.toList());
        }
    }
}