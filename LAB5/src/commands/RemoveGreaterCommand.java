package commands;

import managers.CollectionManager;
import models.City;
import util.Response;
import util.Req;

public class RemoveGreaterCommand implements Command {
    private CollectionManager collectionManager;
    public RemoveGreaterCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    public String getName() {
        return "remove_greater";
    }
    public String getDescription() {
        return "удалить города больше заданного";
    }
    public Response execute(Req req) {
        City city = req.getCity();
        collectionManager.removeGreater(city);
        return new Response("Удалены элементы больше заданного");
    }
}