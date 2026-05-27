package commands;

import managers.CollectionManager;
import models.City;
import util.Req;
import util.Response;

public class AddCommand implements Command{
    private final CollectionManager collectionManager;

    public AddCommand(CollectionManager collectionManager){
        this.collectionManager = collectionManager;
    }
    public String getName() {
        return "add";
    }
    public String getDescription() {
        return "добавить новый город";
    }
    public Response execute(Req req) {
        City city = req.getCity();
        if(city == null) {
            return new Response("Город не передан");
        }
        collectionManager.addCity(city);
        return new Response("город добавлен");
    }
}