package util;
import models.City;

public class Req {
    private String arg;
    private City city;
    public Req(String arg, City city) {
        this.arg = arg;
        this.city = city;
    }
    public String getArg() {
        return arg;
    }
    public City getCity() {
        return city;
    }
}