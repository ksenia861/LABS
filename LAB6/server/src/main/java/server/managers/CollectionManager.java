package server.managers;
import server.commands.*;
import common.models.City;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import common.utils.IdGenerator;
import java.util.stream.Collectors;

public class CollectionManager {
    private static final Logger logger = LogManager.getLogger(CollectionManager.class);

    private Stack<City> cityStack;
    private LocalDateTime initTime;
    private FileManager fileManager;

    public CollectionManager(FileManager fileManager) {
        this.fileManager = fileManager;
        this.cityStack = fileManager.loadCollection();
        this.initTime = LocalDateTime.now();

        int maxId = cityStack.stream()
                .mapToInt(City::getId)
                .max()
                .orElse(0);
        IdGenerator.updateMaxId(maxId);

        logger.info("Коллекция загружена. Элементов: " + cityStack.size());
    }

    public Stack<City> getCollection() {
        return cityStack;
    }

    public void addCity(City city) {
        city.setId(IdGenerator.generateId());
        city.setCreationDate(LocalDate.now());
        cityStack.push(city);
        logger.info("Добавлен город с id=" + city.getId());
    }

    public String showAllCities() {
        if (cityStack.isEmpty()) {
            return "Коллекция пуста";
        }
        return cityStack.stream()
                .map(City::toString)
                .collect(Collectors.joining("\n"));
    }

    public boolean removeById(int id) {
        int before = cityStack.size();
        cityStack.removeIf(city -> city.getId() == id);
        int after = cityStack.size();
        boolean removed = before > after;
        if (removed) {
            logger.info("Удален город с id=" + id);
        }
        return removed;
    }

    public String info() {
        return "Тип коллекции: Stack\n" +
                "Дата инициализации: " + initTime + "\n" +
                "Количество элементов: " + cityStack.size();
    }

    public void clear() {
        cityStack.clear();
        IdGenerator.reset();
        logger.info("Коллекция очищена");
    }

    public boolean updateCity(int id, City newCity) {
        Optional<City> found = cityStack.stream()
                .filter(city -> city.getId() == id)
                .findFirst();

        if (found.isPresent()) {
            newCity.setId(id);
            newCity.setCreationDate(found.get().getCreationDate());
            int index = cityStack.indexOf(found.get());
            cityStack.set(index, newCity);
            logger.info("Обновлен город с id=" + id);
            return true;
        }
        return false;
    }

    public void insertAt(int index, City city) {
        if (index < 0 || index > cityStack.size()) {
            throw new IndexOutOfBoundsException("Неверный индекс");
        }
        city.setId(IdGenerator.generateId());
        city.setCreationDate(LocalDate.now());
        cityStack.add(index, city);
        logger.info("Вставлен город на позицию " + index);
    }

    public void removeGreater(City city) {
        int before = cityStack.size();
        cityStack.removeIf(c -> c.compareTo(city) > 0);
        int after = cityStack.size();
        logger.info("Удалено элементов: " + (before - after));
    }

    public void reorder() {
        List<City> tempList = new ArrayList<>(cityStack);
        Collections.reverse(tempList);
        cityStack.clear();
        cityStack.addAll(tempList);
        logger.info("Коллекция перевернута");
    }

    public String maxById() {
        return cityStack.stream()
                .max(Comparator.comparingInt(City::getId))
                .map(City::toString)
                .orElse("Коллекция пуста");
    }

    public String printDescending() {
        return cityStack.stream()
                .sorted(Comparator.reverseOrder())
                .map(City::toString)
                .collect(Collectors.joining("\n"));
    }

    public String printFieldDescendingGovernor() {
        List<String> governors = cityStack.stream()
                .map(City::getGovernor)
                .filter(Objects::nonNull)
                .map(Object::toString)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        return governors.isEmpty() ? "Нет губернаторов" :
                governors.stream().collect(Collectors.joining("\n"));
    }

    public void save() {
        fileManager.saveCollection(cityStack);
        logger.info("Коллекция сохранена в файл");
    }

    public Collection<City> getSortedByLocation() {
        return cityStack.stream()
                .sorted(Comparator.comparing((City c) -> c.getCoordinates().getX())
                        .thenComparing(c -> c.getCoordinates().getY()))
                .collect(Collectors.toList());
    }
}