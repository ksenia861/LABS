package common.network;
import common.models.City;
import java.io.Serializable;

public class Req implements Serializable {
    private static final long serialVersionUID = 1L;
    private String argument;
    private City city;
    private String commandName;
    private String login;
    private String password;
    public Req(String commandName, City city, String argument,String login, String password) {
        this.commandName = commandName;
        this.argument = argument;
        this.city = city;
        this.login = login;
        this.password = password;
    }
    public String getArgument() {
        return argument;
    }
    public City getCity() {
        return city;
    }
    public String getCommandName() {return commandName; }
    public String getLogin() {return login;}
    public String getPassword() {return password;}
}