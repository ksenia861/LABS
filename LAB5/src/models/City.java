package models;

import java.time.LocalDate;
import util.IdGenerator;

public class City implements Comparable<City> {
    private String name;
    private Coordinates coordinates;
    private LocalDate creationDate;
    private Long area;
    private long population;
    private Float metersAboveSeaLevel;
    private Boolean capital;
    private Government government;
    private StandardOfLiving standardOfLiving;
    private Human governor;
    private int id;

    public City(String name, Coordinates coordinates, Long area, long population,
                Float metersAboveSeaLevel, Boolean capital, Government government, StandardOfLiving standardOfLiving, Human governor) {

        this.id = IdGenerator.generateId();
        this.creationDate = LocalDate.now();
        this.name = name;
        this.coordinates = coordinates;
        this.area = area;
        this.population = population;
        this.metersAboveSeaLevel = metersAboveSeaLevel;
        this.capital = capital;
        this.government = government;
        this.standardOfLiving = standardOfLiving;
        this.governor = governor;

    }
    public LocalDate getCreationDate(){
        return creationDate;
    }
    public Float getMetersAboveSeaLevel(){
        return metersAboveSeaLevel;
    }
    public Boolean getCapital() {
        return capital;
    }
    public StandardOfLiving getStandardOfLiving() {
        return standardOfLiving;
    }
    public String getName() {
        return name;
    }
    public Coordinates getCoordinates() {
        return coordinates;
    }
    public Long getArea(){
        return area;
    }
    public long getPopulation() {
        return population;
    }
    public Government getGovernment() {
        return government;
    }
    public Human getGovernor() {
        return governor;
    }

    public int getId(){
        return id;
    }
    @Override
    public int compareTo(City other) {
        return Integer.compare(this.id, other.id);
    }

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", area=" + area +
                ", population=" + population +
                ", metersAboveSeaLevel=" + metersAboveSeaLevel +
                ", capital=" + capital +
                ", government=" + government +
                ", standardOfLiving=" + standardOfLiving +
                ", governor=" + governor + '}';
    }
}