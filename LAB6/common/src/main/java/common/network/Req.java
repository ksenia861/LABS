package common.network;
import common.models.City;
import java.io.Serializable;

public class Req implements Serializable {
    private static final long serialVersionUID = 1L;
    private String argument;
    private City city;
    private String commandName;
    public Req(String commandName, City city, String argument) {
        this.commandName = commandName;
        this.argument = argument;
        this.city = city;
    }
    public String getArgument() {
        return argument;
    }
    public City getCity() {
        return city;
    }
    public String getCommandName() {return commandName; }
}