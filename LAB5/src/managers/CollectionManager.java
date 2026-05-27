package managers;

import models.City;
import util.IdGenerator;

import java.util.*;
import java.util.Stack;
import java.time.LocalDateTime;


public class CollectionManager {
    private static Stack<City> cityStack;
    private LocalDateTime initTime;
    private FileManager fileManager;

    public CollectionManager() {
        this.fileManager = fileManager;
        cityStack = new Stack<>();
        initTime = LocalDateTime.now();
    }

    public Stack<City> getCollection() {
        return cityStack;
    }

    public void setCollection(Stack<City> collection) {
        if (collection == null) {
            cityStack = new Stack<>();
            return;
        }
        cityStack = collection;
        int maxId = 0;
        for (City city : collection) {
            if (city.getId() > maxId) {
                maxId = city.getId();
            }
        }
        IdGenerator.updateMaxId(maxId);
    }

    public void addCity(City city) {
        cityStack.push(city);
    }

    public String showAllCities() {
        if (cityStack.isEmpty()) {
            return "Колекция пуста";
        }
        StringBuilder text = new StringBuilder();
        for (City city : cityStack) {
            text.append(city).append("\n");
        }
        return text.toString();
    }

    public boolean removeById(int id) {
        boolean removed = cityStack.removeIf(city -> city.getId() == id);
        return removed;
    }

    public String info() {
        return "Тип коллекции Stack\n" +
                "Дата инициализации: " + initTime + "\n" +
                "Количество элементов: " + cityStack.size();
    }

    public void clear() {
        cityStack.clear();
        IdGenerator.reset();
        System.out.println("Коллекция очищена");
    }

    public boolean updateCity(int id, City newCity) {
        for (int i = 0; i < cityStack.size(); i++) {
            if (cityStack.get(i).getId() == id) {
                cityStack.set(i, newCity);
                System.out.println("Город с id " + id + " обновлен");
                return true;
            }
        }
        System.out.println("Город не найден");
        return false;
    }

    public void insertAt(int index, City city) {
        if (index < 0 || index > cityStack.size()) {
            System.out.println("Индекс должен быть от 0 до " + cityStack.size());
            return;
        }
        City newCity = new City(
                city.getName(),
                city.getCoordinates(),
                city.getArea(),
                city.getPopulation(),
                city.getMetersAboveSeaLevel(),
                city.getCapital(),
                city.getGovernment(),
                city.getStandardOfLiving(),
                city.getGovernor()
        );
        cityStack.add(index, newCity);
        System.out.println("Город добавлен на позицию " + index);
    }

    public void removeGreater(City city) {
        City tempCity = new City(
                city.getName(),
                city.getCoordinates(),
                city.getArea(),
                city.getPopulation(),
                city.getMetersAboveSeaLevel(),
                city.getCapital(),
                city.getGovernment(),
                city.getStandardOfLiving(),
                city.getGovernor()
        );
        int before = cityStack.size();
        cityStack.removeIf(c -> c.compareTo(tempCity) > 0);
        int after = cityStack.size();
        System.out.println("Удалено элементв: " + (before - after));
    }

    public void reorder() {
        if (cityStack.isEmpty()) {
            System.out.println("Коллекция пуста, нечего переупорядочить");
            return;
        }
        java.util.List<City> tempList = new java.util.ArrayList<>(cityStack);
        java.util.Collections.reverse(tempList);
        cityStack.clear();
        cityStack.addAll(tempList);
        System.out.println("Коллекция отсортирована в обратном порядке");
    }

    public String maxById() {
        if (cityStack.isEmpty()) {
            return "коллекция пуста";
        }
        City max = Collections.max(cityStack);
        return max.toString();
    }

    public String printDescending() {
        if (cityStack.isEmpty()) {
            return "коллекция пуста";
        }
        List<City> sorted = new ArrayList<>(cityStack);
        Collections.sort(sorted, Collections.reverseOrder());
        StringBuilder text = new StringBuilder();
        for (City city : sorted) {
            text.append(city).append("\n");
        }
        return text.toString();
    }
    public String printFieldDescendingGovernor(){
        if(cityStack.isEmpty()){
            return "Коллекция пуста";
        }
        List<String> governors = new ArrayList<>();
        for(City city : cityStack){
            if(city.getGovernor() != null){
                governors.add(city.getGovernor().toString());
            }
        }
        Collections.sort(governors, Collections.reverseOrder());
        if(governors.isEmpty()){
            return "Нет губернаторов";
        }
        StringBuilder text = new StringBuilder();
        for(String g : governors){
            text.append(g).append("\n");
        }
        return text.toString();
    }
    public String save() {
        fileManager.saveCollection(cityStack);
        return "коллекция сохранена";
    }
}