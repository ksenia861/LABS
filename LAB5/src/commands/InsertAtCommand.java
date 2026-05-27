package commands;

import managers.CollectionManager;
import models.City;
import util.Response;
import util.Req;

public class InsertAtCommand implements Command {
    private CollectionManager collectionManager;

    public InsertAtCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    public String getName() {
        return "insert_at";
    }
    public String getDescription() {
        return "добавить город по индексу";
    }
    public Response execute(Req req) {

        int index;
        try {
            index = Integer.parseInt(req.getArg());
        } catch (NumberFormatException e) {
            return new Response("Индекс должен быть числом");
        }
        City city = req.getCity();
        collectionManager.insertAt(index, city);
        return new Response("Город добавлен");
    }
}